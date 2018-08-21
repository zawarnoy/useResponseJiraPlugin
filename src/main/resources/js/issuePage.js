urlParam = function(name){
    var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
    if (results==null){
        return null;
    }
    else{
        return decodeURI(results[1]) || 0;
    }
};

AJS.$(document).ready(function () {

    var syncStatus = urlParam('sync_status');

    console.log(syncStatus);

    if(syncStatus !== null) {
        if(syncStatus === '1') {
            JIRA.Messages.showSuccessMsg('Issue was successfully sync!');
        } else {
            JIRA.Messages.showErrorMsg('An error was occurred while sync!');
        }
    }

    // AJS.$("#useResponselinkbutton").on('click', function (event) {
    //     event.preventDefault();
    //
    //     $.get(AJS.$("#useResponselinkbutton").attr('href'), {async: true}, function (receivedData) {
    //             console.log(receivedData);
    //             receivedData = JSON.parse(receivedData);
    //             swal(receivedData.message, "", receivedData.status);
    //         }
    //     );
    // });
});
