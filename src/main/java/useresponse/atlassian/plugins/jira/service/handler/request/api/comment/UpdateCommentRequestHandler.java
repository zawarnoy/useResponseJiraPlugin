package useresponse.atlassian.plugins.jira.service.handler.request.api.comment;

import com.atlassian.jira.issue.comments.Comment;
import useresponse.atlassian.plugins.jira.request.Request;
import useresponse.atlassian.plugins.jira.service.handler.request.api.RequestHandler;

public class UpdateCommentRequestHandler extends CommentRequestHandler{

    public UpdateCommentRequestHandler(Comment comment, RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
        this.comment = comment;
    }

    @Override
    protected Request currentAddition(Request request) {
        return null;
    }
}
