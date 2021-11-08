$(window).on('load', function() {

    var userInfo = {"username": "username"};
    var doneData = [{"challenger": "No Data Available", "challengerTime": 0,  "challengee": "", "challengeeTime": ""}];
    var waitingData= [{"challenger": "No Data Available", "challengerTime": 0, "challengeID": ""}];
    var sentData = [{"challengee": "No Data Available", "challengerTime": 0}];
    var challengeData = [{"username": "No Data Available", "wins": 0,  "losses": 0}];
    getCredentials ();
    getDoneData ();

    //Initialize done table
    datatableDone = $("#table_done").DataTable( {
        "pageLength": doneData.length,
        "lengthChange": false,
        "paging": false,
        "responsive": true,
        "orderCellsTop": true,
        "scrollY": "calc(100vh - 425px)",
        "scrollCollapse": true,
        "searching": false,
        "order": [[ 1, "asc" ]],
        data: doneData,
        columns: [
            {"data": "challenger", "width": "30%"},
            {"data": "null", "width": "20%",
            "render": function ( data, type, row ) {
                if ( type === 'display' || type === 'filter' ) {
                    var minutes = Math.floor( row.challengerTime / 60);
                    var seconds = row.challengerTime - minutes * 60;

                    return minutes+"m "+seconds+"s"
                }

                return row.challengerTime} },
            {"data": "challengee", "width": "30%"},
            {"data": "null", "width": "20%",
            "render": function ( data, type, row ) {
                if ( type === 'display' || type === 'filter' ) {
                    if (row.challengeeTime == 0) {
                        return "DNF";
                    }
                    var minutes = Math.floor( row.challengeeTime / 60);
                    var seconds = row.challengeeTime - minutes * 60;
                    return minutes+"m "+seconds+"s"
                }
                return row.challengeeTime} }
            ],
            "createdRow": function( row, data, dataIndex) {
                console.log("My Username: "+userInfo.username)
                console.log("challenger: "+  data.challenger);
                console.log("challengee: "+  data.challengee);
                console.log((userInfo.username == data.challenger));
                console.log((userInfo.username == data.challengee));
                console.log("-----")
                if (userInfo.username == data.challenger) {
                    if (data.challengeeTime == 0) {
                        $(row).addClass('win');
                        console.log('win');
                    } else if (data.challengeeTime > data.challengerTime) {
                        $(row).addClass('win');
                        console.log('win');
                    } else if (data.challengeeTime < data.challengerTime) {
                        $(row).addClass('lost');
                        console.log('lost');
                    }
                } else if (userInfo.username == data.challengee) {
                    if (data.challengeeTime == 0) {
                        $(row).addClass('lost');
                        console.log('lost');
                    } else if (data.challengeeTime > data.challengerTime) {
                        $(row).addClass('lost');
                        console.log('lost');
                  } else if (data.challengeeTime < data.challengerTime) {
                        $(row).addClass('win');
                        console.log('win');
                  }
                }
            }
        });

        datatableWaiting = $("#table_waiting").DataTable( {
            "pageLength": waitingData.length,
            "lengthChange": false,
            "paging": false,
            "responsive": true,
            "orderCellsTop": true,
            "scrollY": "calc(100vh - 425px)",
            "scrollCollapse": true,
            "searching": false,
            "order": [[ 1, "asc" ]],
            data: waitingData,
            columns: [
                {"data": "challenger", "width": "35%"},
                {"data": "null", "width": "15%",
                "render": function ( data, type, row ) {
                    if ( type === 'display' || type === 'filter' ) {
                        var minutes = Math.floor( row.challengerTime / 60);
                        var seconds = row.challengerTime - minutes * 60;

                        return minutes+"m "+seconds+"s"
                    }

                    return row.challengerTime} },
                {"data": null, "orderable": false, "width": "25%",
                render: function ( data, type, row ) {
                    var challenger = row.challenger;
                    var challengeID = row.challengeID;
                    // challenger name and challengeID
                    return '<div id="challengeAccept" data-value="'+challengeID+','+challenger+'">Accept</div>';} },
                {"data": null, "orderable": false, "width": "25%",
                render: function ( data, type, row ) {
                    var challenger = row.challenger;
                    var challengeID = row.challengeID;
                    return '<div id="challengeReject" data-value="'+challengeID+','+challenger+'">Reject</div>';} }
                ]
            });

        datatableSent = $("#table_sent").DataTable( {
            "pageLength": sentData.length,
            "lengthChange": false,
            "paging": false,
            "responsive": true,
            "orderCellsTop": true,
            "scrollY": "calc(100vh - 425px)",
            "scrollCollapse": true,
            "searching": false,
            "order": [[ 1, "asc" ]],
            data: sentData,
            columns: [
                {"data": "challengee", "width": "40%"},
                {"data": "null", "width": "15%",
                "render": function ( data, type, row ) {
                    if ( type === 'display' || type === 'filter' ) {
                        var minutes = Math.floor( row.challengerTime / 60);
                        var seconds = row.challengerTime - minutes * 60;

                        return minutes+"m "+seconds+"s"
                    }

                    return row.challengerTime} },
                ]
            });

        datatableChallenge = $("#table_challenge").DataTable( {
        "pageLength": challengeData.length,
        "lengthChange": false,
        "paging": false,
        "responsive": true,
        "scrollY": "calc(100vh - 425px)",
        "scrollCollapse": true,
        "searching": false,
        "order": [[ 1, "asc" ]],
        data: challengeData,
        columns: [
            {"data": "username", "width": "40%"},
			{"data": "losses", "width": "15%"},
			{"data": "wins", "width": "15%"},
			{"data": null, "orderable": false, "width": "15%",
			render: function ( data, type, row ) {
				var challengee = row.username;
				if (challengee == "No Data Available") {
				    return "";
				}
				return '<div id="challengeCreate" data-value="'+challengee+'">Challenge</div>';} }
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

    function getDoneData () {
          fetch('/rest/getDoneChallenges', {method: 'GET', redirect: 'follow'}).then(function(response) {
                if (response.redirected) {
                    window.location.href = response.url;
                }
            return response.json();
          }).then(function(data) {
            doneData = data;
            if ($.fn.dataTable.isDataTable("#table_done")) {
                datatableDone.clear().draw();
                datatableDone.rows.add(doneData);
                datatableDone.columns.adjust().draw();
                }
          }).catch(function(error) {
           console.log("Something Went Wrong");
           console.log(error);
           //window.location.href = "/"
          });
    }

    function getWaitingData () {
          fetch('/rest/getPendingChallengeRequests', {method: 'GET', redirect: 'follow'}).then(function(response) {
                if (response.redirected) {
                  window.location.href = response.url;
                }
            return response.json();
          }).then(function(data) {
          /**
          if (response.redirected) {
              window.location.href = response.url;
          }
          **/
            waitingData = data;
            if ($.fn.dataTable.isDataTable("#table_waiting")) {
                datatableWaiting.clear().draw();
                datatableWaiting.rows.add(waitingData);
                datatableWaiting.columns.adjust().draw();
                }
          }).catch(function(error) {
            console.log("Something Went Wrong");
            window.location.href = "/"
          });
    }

    function getSentData() {
          fetch('/rest/getSentChallengeRequests', {method: 'GET', redirect: 'follow'}).then(function(response) {
                if (response.redirected) {
                    window.location.href = response.url;
                }
            return response.json();
          }).then(function(data) {
            sentData = data;
            if ($.fn.dataTable.isDataTable("#table_sent")) {
                datatableSent.clear().draw();
                datatableSent.rows.add(sentData);
                datatableSent.columns.adjust().draw();
                }
          }).catch(function() {
            console.log("Something Went Wrong");
            window.location.href = "/"
          });
    }

    function getChallengeData() {
          fetch('/rest/getFriendsWinsLosses', {method: 'GET', redirect: 'follow'}).then(function(response) {
                if (response.redirected) {
                  window.location.href = response.url;
                }
            return response.json();
          }).then(function(data) {
            challengeData = data;
            if ($.fn.dataTable.isDataTable("#table_challenge")) {
                datatableChallenge.clear().draw();
                datatableChallenge.rows.add(challengeData);
                datatableChallenge.columns.adjust().draw();
                }
          }).catch(function() {
            console.log("Something Went Wrong");
            window.location.href = "/"
          });
    }

    $('.done').on( "click", function() {
        showDone();
    });

    $('.sent').on( "click", function() {
        showSent ();
    });

    $('.challenges').on( "click", function() {
        showChallenges ();
    });

    $('.waiting').on( "click", function() {
        showWaiting ();
    });

    $(document).on("click", "#challengeCreate", function() {
        var challengee = $(this).attr("data-value");
        createChallenge(challengee);
    })

    function createChallenge(challengee) {
            Swal.fire(
              'Challenge Created',
              'GO GO GO',
              'success'
            )
            var acceptChallengeData = {'challengee': challengee};
                    var formBody = [];
                    for (var property in acceptChallengeData) {
                      var encodedKey = encodeURIComponent(property);
                      var encodedValue = encodeURIComponent(acceptChallengeData[property]);
                      formBody.push(encodedKey + "=" + encodedValue);
                    }
                    formBody = formBody.join("&");

                    fetch('/rest/challengeRequest', {
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
                         if (body == -2) {
                             Swal.fire({
                               icon: 'error',
                               title: 'Ongoing race',
                               text: 'There is an ongoing race!',
                             })
                            getOutgoingRequests();
                            getPendingRequests();
                         } else if (body == -1) {
                            Swal.fire({
                              icon: 'error',
                              title: 'Invalid Race',
                              text: 'You were too slow or something went wrong!',
                            });
                         } else if (body == -3) {
                          Swal.fire({
                               icon: 'error',
                               title: 'Ongoing Challenge',
                               text: 'There is an ongoing challenge between you and this user.',
                             });

                         } else if (body == -4) {
                          Swal.fire({
                               icon: 'error',
                               title: 'Server Error',
                               text: 'Internal error occurred.',
                             });

                         } else if (body >= 0){
                            Swal.fire(
                              'Done',
                              'Your time: ' + body + 'seconds',
                              'success'
                            )
                         } else {
                           Swal.fire({
                             icon: 'error',
                             title: 'Unexpected Error',
                             text: 'Oh shoot, run!',
                             }).then(function() {
                                   location.reload();
                               } );
                         }
                         getWaitingData();
                    }).catch((error) => {
                         Swal.fire({
                           icon: 'error',
                           title: 'Server error',
                           text: 'Something went wrong!',
                         })
                         window.location.href = "/"
                 });
        }

    $(document).on("click", "#challengeReject", function() {
        var value = $(this).attr("data-value");
        var id = value.split(",")[0];
        var challenger = value.split(",")[1];
        rejectChallenge(id, challenger);
    })

    function rejectChallenge(id, challenger) {
        var rejectChallengeData = {'challenger': challenger, 'id': id};
                var formBody = [];
                for (var property in rejectChallengeData) {
                  var encodedKey = encodeURIComponent(property);
                  var encodedValue = encodeURIComponent(rejectChallengeData[property]);
                  formBody.push(encodedKey + "=" + encodedValue);
                }
                formBody = formBody.join("&");

        fetch('/rest/rejectChallenge', {
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
             if (body == true) {
                Swal.fire(
                  'Rejected Challenge',
                  'You got an automatic loss you pussy',
                  'success'
                )
                getWaitingData();
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
    }

    $(document).on("click", "#challengeAccept", function() {

        var value = $(this).attr("data-value");
        var id = value.split(",")[0];
        var challenger = value.split(",")[1];
        acceptChallenge(id, challenger);
    })

    function acceptChallenge(id, challenger) {
        Swal.fire(
          'Challenge Started',
          'GO GO GO',
          'success'
        )
        var acceptChallengeData = {'challenger': challenger, 'id': id};
                var formBody = [];
                for (var property in acceptChallengeData) {
                  var encodedKey = encodeURIComponent(property);
                  var encodedValue = encodeURIComponent(acceptChallengeData[property]);
                  formBody.push(encodedKey + "=" + encodedValue);
                }
                formBody = formBody.join("&");

                fetch('/rest/acceptChallenge', {
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
                     if (body == -2) {
                          Swal.fire({
                            icon: 'error',
                            title: 'Ongoing race',
                            text: 'There is an ongoing race!',
                          })
                         getOutgoingRequests();
                         getPendingRequests();
                      } else if (body == -1) {
                         Swal.fire({
                           icon: 'error',
                           title: 'Invalid Race',
                           text: 'You were too slow or something went wrong!',
                         });
                      } else if (body == -3) {
                       Swal.fire({
                            icon: 'error',
                            title: 'Ongoing Challenge',
                            text: 'There is an ongoing challenge between you and this user.',
                          });

                      } else if (body == -4) {
                       Swal.fire({
                            icon: 'error',
                            title: 'Server Error',
                            text: 'Internal error occurred.',
                          });

                      } else if (body >= 0){
                         Swal.fire(
                           'Done',
                           'Your time: ' + body + 'seconds',
                           'success'
                         )
                      } else {
                        Swal.fire({
                          icon: 'error',
                          title: 'Unexpected Error',
                          text: 'Oh shoot, run!',
                          }).then(function() {
                                location.reload();
                            } );
                      }
                     getChallengeData();
                }).catch((error) => {
                     Swal.fire({
                       icon: 'error',
                       title: 'Server error',
                       text: 'Something went wrong!',
                     })
                     window.location.href = "/"
             });
    }

    function showDone() {
        getDoneData();
        $('.waiting-wrapper').css('display','none');
        $('.sent-wrapper').css('display','none');
        $('.challenge-wrapper').css('display','none');
        $('.done-wrapper').css('display','block');
        $('.waiting').removeClass('activechallenge');
        $('.sent').removeClass('activechallenge');
        $('.challenges').removeClass('activechallenge');
        $('.done').addClass('activechallenge');
    }

    function showWaiting() {
        getWaitingData();
        $('.done-wrapper').css('display','none');
        $('.sent-wrapper').css('display','none');
        $('.challenge-wrapper').css('display','none');
        $('.waiting-wrapper').css('display','block');
        $('.done').removeClass('activechallenge');
        $('.sent').removeClass('activechallenge');
        $('.challenges').removeClass('activechallenge');
        $('.waiting').addClass('activechallenge');
    }

    function showSent() {
        getSentData();
        $('.waiting-wrapper').css('display','none');
        $('.done-wrapper').css('display','none');
        $('.challenge-wrapper').css('display','none');
        $('.sent-wrapper').css('display','block');
        $('.waiting').removeClass('activechallenge');
        $('.done').removeClass('activechallenge');
        $('.challenges').removeClass('activechallenge');
        $('.sent').addClass('activechallenge');
    }

    function showChallenges() {
        getChallengeData();
        $('.waiting-wrapper').css('display','none');
        $('.sent-wrapper').css('display','none');
        $('.done-wrapper').css('display','none');
        $('.challenge-wrapper').css('display','block');
        $('.waiting').removeClass('activechallenge');
        $('.sent').removeClass('activechallenge');
        $('.done').removeClass('activechallenge');
        $('.challenges').addClass('activechallenge');
    }


});