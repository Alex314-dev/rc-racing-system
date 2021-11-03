$(window).on('load', function() {

    var userInfo;
	var friendList =   [{"username":"kris", "wins":"1", "losses":"3"},
                       {"username":"test", "wins":"2", "losses":"2"},
                       {"username":"friends", "wins":"3", "losses":"1"},
                       {"username":"kris1", "wins":"1", "losses":"3"},
                       {"username":"test1", "wins":"2", "losses":"2"},
                       {"username":"friends1", "wins":"3", "losses":"1"},
                       {"username":"kris2", "wins":"1", "losses":"3"},
                       {"username":"test2", "wins":"2", "losses":"2"},
                       {"username":"friends2", "wins":"3", "losses":"1"},
                      {"username":"kris3", "wins":"1", "losses":"3"},
                      {"username":"test3", "wins":"2", "losses":"2"},
                      {"username":"friends3", "wins":"3", "losses":"1"}];


    var pendingReqData = [{"username":"kris"}, {"username":"kris1"}, {"username":"kris2"}, {"username":"kris3"},
    {"username":"kris4"}, {"username":"kris5"}, {"username":"kris6"}, {"username":"kris7"}, {"username":"kris8"}];

    var outgoingReqData = [{"username":"kris"}, {"username":"kris1"}, {"username":"kris2"}, {"username":"kris3"},
    {"username":"kris4"}, {"username":"kris5"}, {"username":"kris6"}, {"username":"kris7"}, {"username":"kris8"}];

    getFriends ();
    getPendingRequests();
    getOutgoingRequests();
    getCredentials ();

	datatable = $("#table_friends").DataTable( {
        "pageLength": friendList.length,
        "lengthChange": false,
        "paging": false,
        "responsive": true,
        "orderCellsTop": true,
        "scrollY": "calc(100vh - 425px)",
        "scrollCollapse": true,
        "searching": false,
        "order": [[ 1, "asc" ]],
        data: friendList,
        columns: [
            {"data": "username", "width": "40%"},
			{"data": "wins", "width": "15%"},
			{"data": "losses", "width": "15%"},
			{"data": null, "width": "15%",
            render: function ( data, type, row ) {
                var total = row.wins + row.losses;
                return total;} },
			{"data": null, "orderable": false, "width": "15%",
			render: function ( data, type, row ) {
				var username = row.username;
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
	    reloadFriendsList();
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
        // TODO do something here to show user that form is being submitted
        fetch(event.target.action, {
            method: 'POST',
            body: new URLSearchParams(new FormData(event.target)) // event.target is the form
        }).then((resp) => {
            return resp.json(); // or resp.text() or whatever the server sends
        }).then((body) => {
            // TODO handle body
            console.log(body);
        }).catch((error) => {
            console.log(error);
        });
    });

    //if we click on the decline/delete button (pending or outgoing requests)
    $(document).on("click", '#remove', function() {
        var usernameForRemove = $(this).attr("data-value");
        console.log("remove: " + usernameForRemove);
        //TODO REST API Call to remove friend
    });

    //if we click on the decline/delete button (pending or outgoing requests)
    $(document).on("click", '#accept', function() {
        var usernameForAccept = $(this).attr("data-value");
        console.log("Accept: " + usernameForAccept);
        //TODO REST API Call to remove friend
    });

	function getFriends () {
        fetch('/rest/friends')
          .then(response => response.json())
          .then(data => friendList = data);
	}

	function reloadFriendsList() {
        getFriends();
        datatable.clear().draw();
        datatable.rows.add(friendList);
        datatable.columns.adjust().draw();
	}

	function getPendingRequests () {

        $("#pending_table tbody").empty();

        fetch('/rest/pending')
          .then(response => response.json())
          .then(data => friendList = data);

        for (i=0;pendingReqData.length>i;i++) {
            var username = pendingReqData[i].username;
            var row = "<tr>"
                +"<td>"+username+"</td>"
                +"<td><i class='fas fa-user-check accept' id='accept' data-value="+username+"></i></td>"
                +"<td><i class='far fa-times-circle remove' id='remove' data-value="+username+"></i></td>"
            +"</tr>";
            $("#pending_table tbody").append(row);
        }
	}

	function getOutgoingRequests () {
	    $("#outgoing_table tbody").empty()

        fetch('/rest/outgoing')
          .then(response => response.json())
          .then(data => friendList = data);

        for (i=0;outgoingReqData.length>i;i++) {
            var username = outgoingReqData[i].username;
            console.log(username);
            var row = "<tr>"
                +"<td>"+username+"</td>"
                +"<td><i class='fas fa-trash remove' id='remove' data-value="+username+"></i></td>"
            +"</tr>";
            $("#outgoing_table tbody").append(row);
        }
	}

});