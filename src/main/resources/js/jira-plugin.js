var hellopreloader = document.getElementById("hellopreloader_preload");

function fadeOutnojquery(el) {
    el.style.opacity = 0.5;
    var interhellopreloader = setInterval(function () {
        el.style.opacity = el.style.opacity - 0.05;
        if (el.style.opacity <= 0.05) {
            clearInterval(interhellopreloader);
            hellopreloader.style.display = "none";
        }
    }, 16);
}


$(document).ready(function () {

    AJS.$("#submit-button").on('click', function (event) {

        event.preventDefault();
        var data = {};

        AJS.$('form input, form select').each(
            function () {
                var container = AJS.$(this);
                data[container.attr('name')] = container.attr('value');
            }
        );

        AJS.$('#hellopreloader_preload').css('display', 'block');

        AJS.$.post('', data, function (response) {
            AJS.$('#hellopreloader_preload').fadeOut(200, function () {
            });
            console.log(response);
            response = JSON.parse(response);

            if (response.status === "success") {
                AJS.$('#link-settings').html(response.linkTemplate);
                swal("Settings was changed!", "", "success");
            } else {
                swal(response.message, "", "error");
            }
        });


    });
});
