AJS.$(document).ready(function () {

    AJS.$(".radio_check").on('click', function () {
        var neededRadio = AJS.$("#" + AJS.$(this).attr("for"));

        AJS.$("input[name=" + neededRadio.attr("name") + "]").each(function () {
            console.log(AJS.$(this).attr("id"));
            AJS.$(this).removeAttr('checked');
        });

        neededRadio.attr('checked', 'checked');
    });

    AJS.$("#submit-button").on('click', function (event) {

        event.preventDefault();
        var data = {};

        AJS.$('form input, form select').each(
            function () {
                var field = AJS.$(this);
                if (field.attr("type") === 'radio') {
                    if (field.is(':checked')) {
                        data[field.attr('name')] = field.attr('value');
                    }
                } else {
                    data[field.attr('name')] = field.attr('value');
                }
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
                swal(response.message, "", "success");
            } else {
                swal(response.message, "", "error");
            }
        });


    });
});
