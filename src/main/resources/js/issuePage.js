urlParam = function (name) {
    var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
    if (results == null) {
        return null;
    }
    else {
        return decodeURI(results[1]) || 0;
    }
};

function removeParam(key, sourceURL) {
    var rtn = sourceURL.split("?")[0],
        param,
        params_arr = [],
        queryString = (sourceURL.indexOf("?") !== -1) ? sourceURL.split("?")[1] : "";
    if (queryString !== "") {
        params_arr = queryString.split("&");
        for (var i = params_arr.length - 1; i >= 0; i -= 1) {
            param = params_arr[i].split("=")[0];
            if (param === key) {
                params_arr.splice(i, 1);
            }
        }
        rtn = rtn + "?" + params_arr.join("&");
    }
    return rtn;
}

AJS.$(document).ready(function () {

    var syncStatus = urlParam('sync_status');

    if (syncStatus !== null) {
        if (syncStatus === '1') {
            JIRA.Messages.showSuccessMsg('Object was successfully synchronized!');
        } else {
            JIRA.Messages.showErrorMsg('An error was occurred while sync!');
        }
        var url = removeParam("sync_status", document.location.href);
        history.pushState({}, '', url);
    }
});
