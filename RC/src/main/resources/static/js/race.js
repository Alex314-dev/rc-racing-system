$(window).on('load', function() {

    var dataRaces;
    var datatable;
    var userInfo;
    getCredentials ();
    let startTime;
    let elapsedTime = 0;
    let timerInterval;

    getMyRaces();

    console.log(dataRaces);

    datatable = $("#table_races").DataTable( {
        "pageLength": 20,
        "searching": false,
        "lengthChange": false,
        "data" : dataRaces,
        columns: [
            { "data" : "date" },
            { "data" : "overallTime" }
        ]
    });


    $("#start-race").on('click', function() {
        $('#table_races_wrapper').css('display','none');
        $('#loading-window').css('display','flex');
        $('#race_text').text('Ongoing');
        delay(1500).then(() => startTimer());
        startRaceRequest();

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

        function getMyRaces () {
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
            xmlhttpraces.onreadystatechange = function() {
                if (this.readyState == 4 && this.status == 200) {

                    var response = xmlhttpraces.responseText;

                    var result = parseInt(response);

                    if (result == -1) {
                        Swal.fire({
                          icon: 'error',
                          title: 'Invalid Race',
                          text: '',
                        });
                    } if (result == -2) {
                        Swal.fire({
                          icon: 'error',
                          title: 'There is an outgoing race',
                          text: 'Please wait for it to finish!',
                        });
                    } else {
                        Swal.fire(
                          'Race Finished. Good job!',
                          'Your time:' + result,
                          'success'
                        )
                    }
                    endOfRace();
                }
            }
                xmlhttpraces.open("GET", "/rest/race", true);
                xmlhttpraces.send();
        };

        function endOfRace () {
            $('#table_races_wrapper').css('display','block');
            $('#loading-window').css('display','none');
            $('#race_text').text('Start Race');
            reset();
        }

    	// Convert time to a format of hours, minutes, seconds, and milliseconds

        function timeToString(time) {
          let diffInHrs = time / 3600000;
          let hh = Math.floor(diffInHrs);

          let diffInMin = (diffInHrs - hh) * 60;
          let mm = Math.floor(diffInMin);

          let diffInSec = (diffInMin - mm) * 60;
          let ss = Math.floor(diffInSec);

          let diffInMs = (diffInSec - ss) * 100;
          let ms = Math.floor(diffInMs);

          let formattedMM = mm.toString().padStart(2, "0");
          let formattedSS = ss.toString().padStart(2, "0");
          let formattedMS = ms.toString().padStart(2, "0");

          return `${formattedMM}:${formattedSS}:${formattedMS}`;
        }

        function print(txt) {
          document.getElementById("display").innerHTML = txt;
        }

        function startTimer() {
          startTime = Date.now() - elapsedTime;
          timerInterval = setInterval(function printTime() {
            elapsedTime = Date.now() - startTime;
            print(timeToString(elapsedTime));
          }, 10);
        }

        function pause() {
          clearInterval(timerInterval);
        }

        function reset() {
          clearInterval(timerInterval);
          print("00:00:00");
          elapsedTime = 0;
        }

        function delay(time) {
          return new Promise(resolve => setTimeout(resolve, time));
        }


});