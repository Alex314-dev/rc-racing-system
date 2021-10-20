$(window).on('load', function() {

    var userInfo;
    getCredentials ();
    let startTime;
    let elapsedTime = 0;
    let timerInterval;

    var datatable;
    var dataSet;

    getMyRaces ();

    datatable = $("#table_races").DataTable( {
        "pageLength": 20,
        "lengthChange": false,
        data: dataSet,
        columns: [
            { "data" : "date" },
            { "data" : "player" },
            { "data" : "overallTime" }
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
    		xmlhttp.open("GET", "/rest/allraces", true);
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
                xmlhttpraces.open("GET", "/rest/allraces", false);
                xmlhttpraces.send();
            };


});