$(window).on('load', function() {

    const params = new URLSearchParams(document.location.search);
    const error = params.get("error");

    if (error == "exists") {
        Swal.fire({
          icon: 'error',
          title: 'Duplicate usernames',
          text: 'Please choose another username!',
        });
    } else if (error == "illegal") {
        Swal.fire({
          icon: 'error',
          title: 'Illegal username',
          text: 'Username can only contain alphabetic and numberic characters, underscores, dashes and dots',
        });
    } else if (error == "wrong") {
        Swal.fire({
          icon: 'error',
          title: 'Oops...',
          text: 'Something went wrong!',
        });
    }

    getName ();
    function getName () {
    	var xmlhttp = new XMLHttpRequest();
    	xmlhttp.onreadystatechange = function() {
    		if(this.readyState == 4 && this.status == 200) {

    			var response = xmlhttp.responseText;

    			name = response;
    			console.log(name);
    			$("#wellcome").html("Welcome " + "<h3>" + name + "<h3>");
    			}
    		}
    		xmlhttp.open("GET", "/rest/myname", true);
            xmlhttp.send();
    	};


});