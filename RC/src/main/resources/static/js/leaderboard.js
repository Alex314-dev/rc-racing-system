$(window).on('load', function() {

    var userInfo;
    getCredentials ();
    let startTime;
    let elapsedTime = 0;
    let timerInterval;
    let position = 0;

    var datatable;
    var dataSet;
    getMyRaces ();

    datatable = $("#table_races").DataTable( {
        "pageLength": 20,
        "lengthChange": false,
        "responsive": true,
        "scrollY": "calc(100vh - 460px)",
        "scrollCollapse": true,
        "order": [[ 0, "asc" ]],
        data: dataSet,
        columns: [
            {"data": "position"},
            {"data": "player"},
            {"data": "null", "render": function ( data, type, row ) {
                if ( type === 'display' || type === 'filter' ) {
                    var minutes = Math.floor( row.overallTime / 60);
                    var seconds = row.overallTime - minutes * 60;

                    return minutes+"m "+seconds+"s"
                }

                return row.overallTime; } },
            {"data": "null", "render": function ( data, type, row ) {
                if ( type === 'display' || type === 'filter' ) {
                    var minutes = Math.floor( row.sectorTime[0] / 60);
                    var seconds = row.sectorTime[0] - minutes * 60;

                    return minutes+"m "+seconds+"s"
                }

                return row.sectorTime[0]} },
            {"data": "null", "render": function ( data, type, row ) {
                if ( type === 'display' || type === 'filter' ) {
                    var minutes = Math.floor( row.sectorTime[1] / 60);
                    var seconds = row.sectorTime[1] - minutes * 60;

                    return minutes+"m "+seconds+"s"
                }

                return row.sectorTime[1]} },
            {"data": "null", "render": function ( data, type, row ) {
                if ( type === 'display' || type === 'filter' ) {
                    var minutes = Math.floor( row.sectorTime[2] / 60);
                    var seconds = row.sectorTime[2] - minutes * 60;

                    return minutes+"m "+seconds+"s"
                }

                return row.sectorTime[2]} },
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

                return row.date;} }
                ],
                "createdRow": function( row, data, dataIndex) {
                    if (data.position == 1) {
                        $(row).addClass('first');
                    } else if (data.position == 2) {
                        $(row).addClass('second');
                    } else if (data.position == 3) {
                        $(row).addClass('third');
                    }
                }
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
                    dataSet = JSON.parse(response);
                    dataSet.forEach(function (race) {
                        position += 1;
                        race.position = position;
                    });
                    position = 0;
                    }
                }
                xmlhttpraces.open("GET", "/rest/allraces", false);
                xmlhttpraces.send();
            };

});