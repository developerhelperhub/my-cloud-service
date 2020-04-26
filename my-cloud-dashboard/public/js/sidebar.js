jQuery(function ($) {

    $(".sidebar-dropdown > a").click(function () {
        $(".sidebar-submenu").slideUp(200);
        if (
            $(this)
                .parent()
                .hasClass("active")
        ) {
            $(".sidebar-dropdown").removeClass("active");
            $(this)
                .parent()
                .removeClass("active");
        } else {
            $(".sidebar-dropdown").removeClass("active");
            $(this)
                .next(".sidebar-submenu")
                .slideDown(200);
            $(this)
                .parent()
                .addClass("active");
        }
    });


    $("#navbar-top-close-button").click(function () {
        if ($(".main-wrapper").hasClass("toggled")) {
            $(".main-wrapper").removeClass("toggled");

            $("#navbar-top-close-button i").removeClass("fa fa-arrow-left");
            $("#navbar-top-close-button i").addClass("fa fa-bars");

            $("#navbar-top-close-button").removeClass("navbar-top-close-button");
            $("#navbar-top-close-button").addClass("navbar-top-close-button");
        } else {
            $(".main-wrapper").addClass("toggled");

            $("#navbar-top-close-button i").removeClass("fa fa-bars");
            $("#navbar-top-close-button i").addClass("fa fa-arrow-left");
        }
    });


    $("#menu-toggle").click(function (e) {
        e.preventDefault();
        $("#wrapper").toggleClass("toggled");
    });
});