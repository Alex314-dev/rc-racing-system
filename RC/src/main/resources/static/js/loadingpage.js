window.onload = function() {
    /**
    var path = anime.path('.loading svg path');

    anime({
      targets: '.loading .loadingcar',
      translateX: path('x'),
      translateY: path('y'),
      rotate: path('angle'),
      easing: 'linear',
      duration: 2500,
      loop: true
    });

    $(".loading-wrapper").fadeOut("slow");
    **/
    anime({
        targets: '.loading svg path',
        strokeDashoffset: [anime.setDashoffset, 0],
        easing: 'easeInOutCubic',
        duration: 1200,
        begin: function(anim) {
            $(".loading").css("display", "block");
        },
        complete: function(anim) {
            $(".loader-wrapper").css("display", "none");
        },
        loop: false
    });

    setTimeout(
        function() {
            $(".loader-wrapper").fadeOut(200, "swing");
        }, 400);

};

