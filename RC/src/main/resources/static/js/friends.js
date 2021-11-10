$(window).on('load', function() {

    var userInfo;
	var friendList = [{"username": "No Data Available", "wins": 0,  "losses": 0}];
    var pendingReqData;
    var outgoingReqData;
    getFriends ();
    getCredentials ();

	datatable = $("#table_friends").DataTable( {
        "pageLength": friendList.length,
        "lengthChange": false,
        "paging": false,
        "responsive": true,
        "orderCellsTop": true,
        "scrollY": "calc(100vh - 460px)",
        "scrollCollapse": true,
        "searching": false,
        aaSorting: [],
        data: friendList,
        columns: [
            {"data": "username", "width": "40%"},
			{"data": "losses", "width": "15%"},
			{"data": "wins", "width": "15%"},
			{"data": null, "width": "15%",
            render: function ( data, type, row ) {
                var total = row.wins + row.losses;
                return total;} },
			{"data": null, "orderable": false, "width": "15%",
			render: function ( data, type, row ) {
				var username = row.username;
				if (username == "No Data Available") {
				    return "";
				}
				return '<i class="fas fa-user-times remove" id="remove" data-value='+username+'></i>';} }
			]
		});


    function getCredentials () {
    	var xmlhttp = new XMLHttpRequest();
    	xmlhttp.onreadystatechange = function() {
    		if(this.readyState == 4 && this.status == 200) {

    			var response = xmlhttp.responseText;

    			userInfo = JSON.parse(response);
    			$("#username").text(userInfo.username);
    			}
    		}
    		xmlhttp.open("GET", "/rest/player", true);
            xmlhttp.send();
    	};


    $('.list').on( "click", function() {
        showFriendsList();
    });

    $('.requests').on( "click", function() {
        showRequests ();
    });


	function showFriendsList () {
	    getFriends();
		$('.add-friends-wrapper').css('display','none');
        $('.friends-list-wrapper').css('display','block');
        datatable.columns.adjust().draw();
        $('.requests').removeClass('freindsactive');
        $('.list').addClass('freindsactive');
	}

	function showRequests () {
	    getOutgoingRequests ();
	    getPendingRequests ();
	    $('.friends-list-wrapper').css('display','none');
        $('.add-friends-wrapper').css('display','block');
        $('.list').removeClass('freindsactive');
        $('.requests').addClass('freindsactive');
	}

	//Add friend (Handles response from the server)
	//using fetch (cooler than xml for REST API handling)
	document.forms['add_friend_form'].addEventListener('submit', (event) => {
        event.preventDefault();
        //do something here to show user that form is being submitted
        fetch(event.target.action, {
            method: 'POST',
            redirect: 'follow',
            body: new URLSearchParams(new FormData(event.target)) // event.target is the form
        }).then((resp) => {
                if (resp.redirected) {
                    window.location.href = resp.url;
                }
            return resp.json(); // or resp.text() or whatever the server sends
        }).then((body) => {
            console.log(body);
            if (body == 1) {
                Swal.fire({
                  icon: 'error',
                  title: 'Invalid Request',
                  text: 'You are already friends with that user!',
                })
            } else if (body == 2) {
                Swal.fire({
                  icon: 'error',
                  title: 'Invalid Request',
                  text: 'There is already an ongoing friend request with that user!',
                })
            } else if (body == 3) {
                 Swal.fire({
                   icon: 'error',
                   title: 'Invalid Request',
                   text: 'There is no user with this name!',
                 })
             } else if (body == 0) {
                Swal.fire(
                  'Success',
                  'Friend Request has been sent!',
                  'success'
                )
                getOutgoingRequests();
            } else if (body == -1) {
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: 'Something went wrong!',
                })
            }
        }).catch((error) => {
            Swal.fire({
              icon: 'error',
              title: 'Server error',
              text: 'Something went wrong!',
            })
            window.location.href = "/"
        });
    });

    //if we click on the decline/delete button (pending or outgoing requests)
    $(document).on("click", '#remove', function() {
        var usernameForRemove = $(this).attr("data-value");
        console.log("remove: " + usernameForRemove);

        var deleteData = {
            'friendToDelete': usernameForRemove,
        };
        var formBody = [];
        for (var property in deleteData) {
          var encodedKey = encodeURIComponent(property);
          var encodedValue = encodeURIComponent(deleteData[property]);
          formBody.push(encodedKey + "=" + encodedValue);
        }
        formBody = formBody.join("&");

        fetch('/rest/deleteFriend', {
          method: 'DELETE',
          redirect: 'follow',
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'
          },
          body: formBody
        }).then((resp) => {
                if (resp.redirected) {
                    window.location.href = resp.url;
                }
             return resp.json(); // or resp.text() or whatever the server sends
        }).then((body) => {
             console.log(body);
             if (body == 0) {
                Swal.fire(
                  'Success',
                  'This Friend has been deleted!',
                  'success'
                )
                getOutgoingRequests();
                getPendingRequests();
                getFriends ();
             } else {
                 Swal.fire({
                   icon: 'error',
                   title: 'Server error',
                   text: 'Something went wrong!',
                 })
             }
        }).catch((error) => {
             Swal.fire({
               icon: 'error',
               title: 'Server error',
               text: 'Something went wrong!',
             })
     });
    });


    //if we click on the accept button
    $(document).on("click", '#accept', function() {
        var usernameForAccept = $(this).attr("data-value");
        console.log("Accept: " + usernameForAccept);

        var acceptData = {'friendToAccept': usernameForAccept,};
        var formBody = [];
        for (var property in acceptData) {
          var encodedKey = encodeURIComponent(property);
          var encodedValue = encodeURIComponent(acceptData[property]);
          formBody.push(encodedKey + "=" + encodedValue);
        }
        formBody = formBody.join("&");

        fetch('/rest/acceptFriendRequest', {
          method: 'POST',
          redirect: 'follow',
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'
          },
          body: formBody
        }).then((resp) => {
                if (resp.redirected) {
                    window.location.href = resp.url;
                }
             return resp.json(); // or resp.text() or whatever the server sends
        }).then((body) => {
             console.log(body);
             if (body == 0) {
                Swal.fire(
                  'Success',
                  'Friend Request Has Accepted!',
                  'success'
                )
                getOutgoingRequests();
                getPendingRequests();
             } else {
                 Swal.fire({
                   icon: 'error',
                   title: 'Server error',
                   text: 'Something went wrong!',
                 })
             }
        }).catch((error) => {
             Swal.fire({
               icon: 'error',
               title: 'Server error',
               text: 'Something went wrong!',
             })
             window.location.href = "/"
     });

    });

	function getFriends () {
      fetch('/rest/getFriendsWinsLosses', {method: 'GET', redirect: 'follow'}).then(function(response) {
          if (response.redirected) {
              window.location.href = response.url;
          }
        return response.json();
      }).then(function(data) {
        friendList = data;
        if ($.fn.dataTable.isDataTable("#table_friends")) {
            datatable.clear().draw();
            datatable.rows.add(friendList);
            datatable.columns.adjust().draw();
            }
      }).catch(function() {
        console.log("Something Went Wrong");
        window.location.href = "/"
      });
	}

	function getPendingRequests () {

        $("#pending_table tbody").empty();

          fetch('/rest/getPendingRequests', {method: 'GET', redirect: 'follow'}).then(function(response) {
              if (response.redirected) {
                  window.location.href = response.url;
              }
            return response.json();
          }).then(function(data) {
            pendingReqData = data;
            for (i=0;pendingReqData.length>i;i++) {
                var username = pendingReqData[i];
                var row = "<tr>"
                    +"<td>"+username+"</td>"
                    +"<td><i class='fas fa-user-check accept' id='accept' data-value="+username+"></i></td>"
                    +"<td><i class='far fa-times-circle remove' id='remove' data-value="+username+"></i></td>"
                +"</tr>";
                $("#pending_table tbody").append(row);
            }
          }).catch(function() {
            console.log("Something Went Wrong");
            window.location.href = "/"
          });
	}

	function getOutgoingRequests () {
	    $("#outgoing_table tbody").empty()

          fetch('/rest/getSentRequests', {method: 'GET', redirect: 'follow'}).then(function(response) {
                  if (response.redirected) {
                      window.location.href = response.url;
                  }
            return response.json();
          }).then(function(data) {
            outgoingReqData = data;
            for (i=0;outgoingReqData.length>i;i++) {
                var username = outgoingReqData[i];
                console.log(username);
                var row = "<tr>"
                    +"<td>"+username+"</td>"
                    +"<td><i class='fas fa-trash remove' id='remove' data-value="+username+"></i></td>"
                +"</tr>";
                $("#outgoing_table tbody").append(row);
            }
          }).catch(function() {
            console.log("Something Went Wrong");
            window.location.href = "/"
          });
	}

});