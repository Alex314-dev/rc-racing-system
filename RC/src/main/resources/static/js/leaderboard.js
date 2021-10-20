$(window).on('load', function() {

    var userInfo;
    getCredentials ();
    let startTime;
    let elapsedTime = 0;
    let timerInterval;

    var datatable;
    var dataSet;

    $(document).ready(function() {
        datatable = $("#table_races").DataTable( {
            "pageLength": 20,
            "lengthChange": false,
            data: dataSet,
            columns: [
                { title: "Position" },
                { title: "Date" },
                { title: "Player" },
                { title: "Time" },
            ]
        });
    } );

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

                    dataSet = JSON.parse(response);
                    }
                }
                xmlhttpraces.open("GET", "/rest/player", true);
                xmlhttpraces.send();
            };


});