urlParam = function (name) {
    var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
    if (results == null) {
        return null;
    }
    else {
        return decodeURI(results[1]) || 0;
    }
};

function removeURLParameter(url, parameter) {
    //prefer to use l.search if you have a location/link object
    var urlparts = url.split('?');
    if (urlparts.length >= 2) {

        var prefix = encodeURIComponent(parameter) + '=';
        var pars = urlparts[1].split(/[&;]/g);

        //reverse iteration as may be destructive
        for (var i = pars.length; i-- > 0;) {
            //idiom for string.startsWith
            if (pars[i].lastIndexOf(prefix, 0) !== -1) {
                pars.splice(i, 1);
            }
        }

        url = urlparts[0] + (pars.length > 0 ? '?' + pars.join('&') : "");
        return url;
    } else {
        return url;
    }
}

AJS.$(document).ready(function () {

    var syncStatus = urlParam('sync_status');

    if (syncStatus !== null) {
        if (syncStatus === '1') {
            JIRA.Messages.showSuccessMsg('Object was successfully synchronized!');
        } else {
            JIRA.Messages.showErrorMsg('An error was occurred while sync!');
        }
    }

    // console.log( removeURLParameter(window.location.href, 'sync_status'));

    // window.history.pushState({}, removeURLParameter(window.location.href, 'sync_status'));

});
