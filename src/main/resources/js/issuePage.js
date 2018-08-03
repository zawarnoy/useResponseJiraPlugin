AJS.$(document).ready(function () {

    AJS.$("#useResponselinkbutton").on('click', function (event) {
        event.preventDefault();

        $.get(AJS.$("#useResponselinkbutton").attr('href'), {async: true}, function (receivedData) {
                console.log(receivedData);
                receivedData = JSON.parse(receivedData);
                swal(receivedData.message, "", receivedData.status);
            }
        );
    });
});

