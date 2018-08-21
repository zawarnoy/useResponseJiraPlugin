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
            JIRA.Messages.showSuccessMsg('Object was successfully synchronized!');
        } else {
            JIRA.Messages.showErrorMsg('An error was occurred while sync!');
        }
    }
});
