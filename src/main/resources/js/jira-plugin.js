$(document).ready(function () {
    alert("123");
    $("#useResponselinkbutton").on("click", function () {

        var requestUrl = this.attr("href");

        $.ajax({
                url: requestUrl
            }
        ).done(function () {
            alert("Succesfully moved to UseResponse")
        }).error(function () {
            alert("Sync error")
        });
        return false;
    })
});