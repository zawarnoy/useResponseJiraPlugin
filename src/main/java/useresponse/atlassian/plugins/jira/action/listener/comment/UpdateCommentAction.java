package useresponse.atlassian.plugins.jira.action.listener.comment;

import com.atlassian.jira.issue.comments.Comment;
import org.springframework.stereotype.Component;
import useresponse.atlassian.plugins.jira.action.listener.ListenerActionType;
import useresponse.atlassian.plugins.jira.request.PostRequest;
import useresponse.atlassian.plugins.jira.request.Request;

@Component("updateCommentAction")
public class UpdateCommentAction extends AbstractCommentAction {

    public UpdateCommentAction() {
        this.request = new PostRequest();
        this.actionType = ListenerActionType.UPDATE_COMMENT_ID;
    }

    @Override
    protected Request addParameters(Request request) {
        request.addParameter(parametersBuilder.build((Comment) comment));
        return request;
    }

    @Override
    protected String createUrl() {
        int useResponseId = commentLinkManager.findByJiraId(comment.getId().intValue()).getUseResponseCommentId();
        return collectUrl("comments/" + useResponseId + "/edit.json");
    }

    @Override
    protected void handleResponse(String response) {

    }
}
