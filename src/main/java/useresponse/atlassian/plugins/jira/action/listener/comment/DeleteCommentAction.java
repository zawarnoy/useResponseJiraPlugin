package useresponse.atlassian.plugins.jira.action.listener.comment;

import useresponse.atlassian.plugins.jira.request.Request;

public class DeleteCommentAction extends AbstractCommentAction {
    @Override
    protected Request addParameters(Request request) {
        return null;
    }

    @Override
    protected String createUrl() {
        return null;
    }

    @Override
    protected void handleResponse(String response) throws Exception {

    }

    @Override
    public void run() {
        // TODO How to get deleted comment id?
    }
}
