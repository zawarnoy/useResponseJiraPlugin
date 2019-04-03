package useresponse.atlassian.plugins.jira.action.listener.comment;

import com.atlassian.jira.issue.comments.Comment;
import org.springframework.stereotype.Component;
import useresponse.atlassian.plugins.jira.action.listener.ListenerActionType;
import useresponse.atlassian.plugins.jira.request.PostRequest;
import useresponse.atlassian.plugins.jira.request.Request;
import useresponse.atlassian.plugins.jira.service.handler.servlet.binder.IssueBinderServletRequestHandler;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("createCommentAction")
public class CreateCommentAction extends AbstractCommentAction {

    @Inject
    @Named("issueBinderRequestHandler")
    private IssueBinderServletRequestHandler issueBinderServletRequestHandler;

    public CreateCommentAction() {
        this.request = new PostRequest();
        this.actionType = ListenerActionType.CREATE_COMMENT_ID;
    }

    @Override
    public String createUrl() {
        return getSpecialApiPath();
    }

    @Override
    public void handleResponse(String response) {
        issueBinderServletRequestHandler.handle(response);
    }

    @Override
    public Request addParameters(Request request) {
        Map<Object, Object> commentData = parametersBuilder.build((Comment) comment);
        List<Map> commentsData = new ArrayList<>();
        commentsData.add(commentData);
        Map<Object, Object> dataForSending = new HashMap<>();
        dataForSending.put("comments", commentsData);
        request.addParameter(dataForSending);
        return request;
    }
}
