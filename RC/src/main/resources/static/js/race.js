$(window).on('load', function() {

    var dataRaces;
    var datatable;
    var userInfo;
    var interval;
    var mil = 0;
    var sec = 0;
    var min = 0;
    var timerFlag = false;
    getCredentials();
    getMyRaces();

    datatable = $("#table_races").DataTable( {
        "pageLength": 20,
        "searching": false,
        "lengthChange": false,
        "responsive": true,
        "scrollY": "calc(100vh - 475px)",
        "scrollCollapse": true,
        "order": [[ 0, "desc" ]],
        "data" : dataRaces,
        columns: [
            {"data": "null", "render": function ( data, type, row ) {
                if (row.date == "" || row.date == null) {
                    return "";
                }
                if ( type === 'display' || type === 'filter' ) {
                    var date = row.date.split("T")[0];
                    var day = date.split("-")[2];
                    var month = date.split("-")[1];
                    var year = date.split("-")[0];
                    var time = row.date.split("T")[1].substring(0, 5);
                    return time+" "+day+"/"+month+"/"+year;
                }

                return row.date;
            }},
            {"data": "null", "render": function ( data, type, row ) {
                if ( type === 'display' || type === 'filter' ) {
                    var minutes = Math.floor( row.sectorTime[0] / 60);
                    var seconds = row.sectorTime[0] - minutes * 60;

                    return minutes+"m "+seconds+"s"
                }

                return row.sectorTime[0]
            }},
            {"data": "null", "render": function ( data, type, row ) {
                if ( type === 'display' || type === 'filter' ) {
                    var minutes = Math.floor( row.sectorTime[1] / 60);
                    var seconds = row.sectorTime[1] - minutes * 60;

                    return minutes+"m "+seconds+"s"
                }

                return row.sectorTime[1]
            }},
            {"data": "null", "render": function ( data, type, row ) {
                if ( type === 'display' || type === 'filter' ) {
                    var minutes = Math.floor( row.sectorTime[2] / 60);
                    var seconds = row.sectorTime[2] - minutes * 60;

                    return minutes+"m "+seconds+"s"
                }

                return row.sectorTime[2]
            }},
            {"data": "null", "render": function ( data, type, row ) {
                if ( type === 'display' || type === 'filter' ) {
                    var minutes = Math.floor( row.overallTime / 60);
                    var seconds = row.overallTime - minutes * 60;

                    return minutes+"m "+seconds+"s"
                }

                return row.overallTime;
            }}
        ]
    });

    $("#start-race").on('click', function() {
        $('#table_races_wrapper').css('display','none');
        $('#loading-window').css('display','flex');
        $('#race_text').text('Ongoing');
        timerFlag = false;
        startRaceRequest();
        checkTimer();
    });

    $("#delete_account").on('click', async function() {

        const { value: username } = await Swal.fire({
          input: 'text',
          inputPlaceholder: 'Enter your username here...',
          inputAttributes: {
            'aria-label': 'Enter your username here'
          },
          confirmButtonText: 'Yes, delete it!',
          icon: 'warning',
          title: 'Are you sure you want to delete your account?',
          text: "You won't be able to revert this!",
          showCancelButton: true
        })

        if (username) {
            if (username === userInfo.username) {
                fetch('/rest/removeAccount', {method: 'DELETE', redirect: 'follow'}).then(function(response) {
                    if (response.redirected) {
                        window.location.href = response.url;
                    }
                return response.json();
                }).then(function(data) {
                    if (data == 1) {
                        window.location.href = "rest/logout";
                    } else {
                        Swal.fire({
                            icon: 'error',
                            title: 'Unsuccessful Account Removal',
                            text: 'Something Went Wrong',
                        });
                    }
                }).catch(function(error) {
                    console.log("Something Went Wrong");
                    console.log(error);
                    window.location.href = "/"
                });
            } else {
                Swal.fire({
                    icon: 'error',
                    title: 'Unsuccessful Account Removal',
                    text: 'The input did not match your username',
                });
            }
        }
    })

    function getCredentials() {
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

    function getMyRaces() {
        var xmlhttpraces = new XMLHttpRequest();
        xmlhttpraces.onreadystatechange = function() {
            if(this.readyState == 4 && this.status == 200) {
                var response = xmlhttpraces.responseText;
                dataRaces = JSON.parse(response);
            }
        }
        xmlhttpraces.open("GET", "/rest/myraces", false);
        xmlhttpraces.send();
    };


    function startRaceRequest () {
        var xmlhttpraces = new XMLHttpRequest();
        xmlhttpraces.onreadystatechange = function() {
            if (this.readyState == 4 && this.status == 200) {
                var response = xmlhttpraces.responseText;

                if (response == -1.0) {
                    Swal.fire({
                        icon: 'error',
                        title: 'Invalid Race',
                        text: 'You were too slow or something went wrong!',
                    });
                } else if (response == -2.0) {
                    Swal.fire({
                        icon: 'error',
                        title: 'There is an ongoing race',
                        text: 'Please wait for it to finish!',
                    });
                } else if (response == -4.0) {
                    Swal.fire({
                        icon: 'error',
                        title: 'Server Error',
                        text: 'Internal error occurred.',
                    });
                } else if (response > 0.0) {
                    Swal.fire({
                        title: "Race Finished. Good job!",
                        text: "Your time:" + response,
                        type: "success"
                    }).then(function() { location.reload(); });
                } else {
                    Swal.fire({
                        icon: 'error',
                        title: 'Unexpected Error',
                        text: 'Oh shoot, run!',
                    }).then(function() { location.reload(); });
                }

                endOfRace();
                timerFlag = true;
            } else if (this.readyState == 4 && this.status != 200) {
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: 'Cannot establish connection to the server',
                });

                endOfRace();
                timerFlag = true;
            }
        }
        xmlhttpraces.open("GET", "/rest/race", true);
        xmlhttpraces.send();
    }

    function endOfRace() {
        resetTimer();
        pauseTimer();
        $('#table_races_wrapper').css('display','block');
        $('#loading-window').css('display','none');
        $('#race_text').text('Start Race');
    }

    function delay(time) {
      return new Promise(resolve => setTimeout(resolve, time));
    }

    async function checkTimer() {
        while (!timerFlag) {
            await delay(500);
            sendTimerRequest();
        }
    }

    function sendTimerRequest() {
        fetch('/rest/timer', {method: 'GET', redirect: 'follow'}).then(function(response) {
            return response.json();
        }).then(function(data) {
            if (data === true) {
                startTimer();
                timerFlag = true;
            }
        }).catch(function(error) {
            console.log(error);
        });
    }

    function startTimer() {
            clearInterval(interval);
            interval = setInterval(start, 10);
        }

        function stopTimer() {
            clearInterval(interval);
            $("#display").html("00:00:00");
        }

        function resetTimer() {
            clearInterval(interval);
            mil = 0;
            sec = 0;
            min = 0;
            $("#display").html("00:00:00");
        }

        function start() {

            var milString;
            var secString;
            var minString;

            mil++;

            if (mil <= 9) {
                milString = "0" + mil;
            } else if (mil > 99) {
                mil = 0;
                milString = "00";
                sec++;
            } else {
                milString = "" + mil;
            }

            if (sec <= 9) {
                secString = "0" + sec;
            } else if (sec > 59) {
                sec = 0;
                secString = "00";
                min ++;
            } else {
                secString = "" + sec;
            }

            if (min <= 9) {
                minString = "0" + min;
            } else {
                minString = min;
            }

            var timeString = minString + ":" + secString + ":" + milString;
            $("#display").html(timeString);

        }
});
