var scrollEvent = false;
var count = 0;

$("html, body").on('mousewheel', function (c) {
    c.preventDefault();
    c.passive = false;

    var m = c.originalEvent.wheelDelta;
    var sb = $(".page-section").height();

    if (m > 1 && scrollEvent == false && count >= 1) {
        scrollEvent = true;
        count--;

        $("html, body").stop().animate({scrollTop: sb * count * 1.1},
            {duration: 800, complete: function() {
                scrollEvent = false;}
            });
    }
    else if(m < 1 && scrollEvent == false && count < 3) {
        scrollEvent = true;
        count++;
        $("html, body").stop().animate({scrollTop: sb * count * 1.1},
            {duration: 800, complete: function() {
                scrollEvent = false;}
            });
    }
});