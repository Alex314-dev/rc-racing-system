$(window).on('load', function() {

    const params = new URLSearchParams(document.location.search);
    const error = params.get("expired");

    if (error == "true") {
        Swal.fire({
          icon: 'error',
          title: 'Session Expired',
          text: 'Only one session could be active per person',
        });
    }

    $('#google').click(function() {
        window.location.href='/oauth2/authorization/google';
    })

    $('#facebook').click(function() {
        window.location.href='/oauth2/authorization/facebook';
    })


});