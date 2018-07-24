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

        AJS.$.post('', data, function (response) {
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
