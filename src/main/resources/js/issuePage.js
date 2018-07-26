AJS.$(document).ready(function () {

    AJS.$("#useResponselinkbutton").on('click', function (event) {
        event.preventDefault();

        $.get(AJS.$("#useResponselinkbutton").attr('href'), {async: true}, function (receivedData) {
                console.log(receivedData);
                receivedData = JSON.parse(receivedData);

                if(receivedData.status === "success") {
                    swal(receivedData.message, "", "success");
                } else {
                    swal(receivedData.message, receivedData.slug, "error");
                }
            }
        );

        return false;
    });
});

