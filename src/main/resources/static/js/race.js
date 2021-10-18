$(window).on('load', function() {

    getCredentials ();

    var userInfo;
    function getCredentials () {
    	var xmlhttp = new XMLHttpRequest();
    	xmlhttp.onreadystatechange = function() {
    		if(this.readyState == 4 && this.status == 200) {

    			var response = xmlhttp.responseText;

    			userInfo = JSON.parse(response);
    			$("#Name").text("Name: " + userInfo.name);
    			$("#Email").text("Email: " + userInfo.email);
    			$("#Username").text("Username: " + userInfo.username);
    			}
    		}
    		xmlhttp.open("GET", "/rest/player", true);
            xmlhttp.send();
    	};


});