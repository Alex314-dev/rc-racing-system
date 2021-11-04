$(window).on('load', function() {

    var userInfo;
    var doneData = [{"challenger": "No Data Available", "challengerTime": 0,  "challengee": "", "challengeeTime": ""}];
    var waitingData= [{"challenger": "No Data Available", "challengerTime": 0, "challengeID": ""}];
    var sentData = [{"username": "No Data Available", "wins": 0,  "losses": 0}];
    var challengeData = [{"username": "No Data Available", "wins": 0,  "losses": 0}];
    getCredentials ();
    getDoneData();

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
            {"data": "challengerTime", "width": "20%"},
            {"data": "challengee", "width": "30%"},
            {"data": "challengeeTime", "width": "20%"}
            ]
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
                {"data": "challenger", "width": "40%"},
                {"data": "challengerTime", "width": "25%"},
                {"data": null, "orderable": false, "width": "35%",
                render: function ( data, type, row ) {
                    var challenger = row.challenger;
                    var challengeID = row.challengeID;
                    // challenger name and challengeID
                    return '<div id="challengeAccept" data-value="'+challengeID+','+challenger+'" id="'+challengeID+'"   " >Accept</div>';} }
                ]
            });
            getWaitingData();
    /**
        datatableSent = $("#table_sent").DataTable( {
            "pageLength": challengeData.length,
            "lengthChange": false,
            "paging": false,
            "responsive": true,
            "orderCellsTop": true,
            "scrollY": "calc(100vh - 425px)",
            "scrollCollapse": true,
            "searching": false,
            "order": [[ 1, "asc" ]],
            data: challengeData,
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
                    if (username == "No Data Available") {
                        return "";
                    }
                    return '<i class="fas fa-user-times remove" id="remove" data-value='+username+'></i>';} }
                ]
            });

        datatableChallenge = $("#table_challenge").DataTable( {
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
                    if (username == "No Data Available") {
                        return "";
                    }
                    return '<i class="fas fa-user-times remove" id="remove" data-value='+username+'></i>';} }
                ]
            });
    **/


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
          fetch('/rest/getDoneChallenges').then(function(response) {
            return response.json();
          }).then(function(data) {
            doneData = data;
            if ($.fn.dataTable.isDataTable("#table_done")) {
                datatableDone.clear().draw();
                datatableDone.rows.add(doneData);
                datatableDone.columns.adjust().draw();
                }
          }).catch(function() {
            console.log("Something Went Wrong");
          });
    }

    function getWaitingData () {
          fetch('/rest/getPendingChallengeRequests').then(function(response) {
            return response.json();
          }).then(function(data) {
            waitingData = data;
            if ($.fn.dataTable.isDataTable("#table_waiting")) {
                datatableWaiting.clear().draw();
                datatableWaiting.rows.add(waitingData);
                datatableWaiting.columns.adjust().draw();
                }
          }).catch(function() {
            console.log("Something Went Wrong");
          });
    }

    $('.done').on( "click", function() {
        getDoneData();
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

    $(document).on("click", "#challengeAccept", function() {
        var value = $(this).attr("data-value");
        var id = value.split(",")[0];
        var challenger = value.split(",")[1];
        acceptChallenge(id, challenger);
    })

    function acceptChallenge(id, challenger) {

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
                  headers: {
                    'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'
                  },
                  body: formBody
                }).then((resp) => {
                     return resp.json(); // or resp.text() or whatever the server sends
                }).then((body) => {
                    console.log(body);
                     if (body == -2) {
                         Swal.fire({
                           icon: 'error',
                           title: 'Ongoing race/Server error',
                           text: 'There is an ongoing race!',
                         })
                        getOutgoingRequests();
                        getPendingRequests();
                     } else if (body == -1) {
                         Swal.fire({
                           icon: 'error',
                           title: 'Server error',
                           text: 'Something went wrong!',
                         })
                     }  else if (body >= 0){
                        Swal.fire(
                          'Done',
                          'Your time: ' + body + 'seconds',
                          'success'
                        )
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

    }

    function showDone() {
        getDoneData();
        $('.waiting-wrapper').css('display','none');
        $('.sent-wrapper').css('display','none');
        $('.challenge-wrapper').css('display','none');
        $('.done-wrapper').css('display','block');
        //datatable.columns.adjust().draw();
        $('.waiting').removeClass('activechallenge');
        $('.sent').removeClass('activechallenge');
        $('.challenge').removeClass('activechallenge');
        $('.done').addClass('activechallenge');
    }

    function showWaiting() {
        getWaitingData();
        $('.done-wrapper').css('display','none');
        $('.sent-wrapper').css('display','none');
        $('.challenge-wrapper').css('display','none');
        $('.waiting-wrapper').css('display','block');
        //datatable.columns.adjust().draw();
        $('.done').removeClass('activechallenge');
        $('.sent').removeClass('activechallenge');
        $('.challenges').removeClass('activechallenge');
        $('.waiting').addClass('activechallenge');
    }

    function showSent() {
        //TODO get DONE data
        $('.waiting-wrapper').css('display','none');
        $('.done-wrapper').css('display','none');
        $('.challenge-wrapper').css('display','none');
        $('.sent-wrapper').css('display','block');
        //datatable.columns.adjust().draw();
        $('.waiting').removeClass('activechallenge');
        $('.done').removeClass('activechallenge');
        $('.challenges').removeClass('activechallenge');
        $('.sent').addClass('activechallenge');
    }

    function showChallenges() {
        //TODO get DONE data
        $('.waiting-wrapper').css('display','none');
        $('.sent-wrapper').css('display','none');
        $('.done-wrapper').css('display','none');
        $('.challenge-wrapper').css('display','block');
        //datatable.columns.adjust().draw();
        $('.waiting').removeClass('activechallenge');
        $('.sent').removeClass('activechallenge');
        $('.done').removeClass('activechallenge');
        $('.challenges').addClass('activechallenge');
    }


});