$(window).on('load', function() {

    var userInfo;
    getCredentials ();


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

});