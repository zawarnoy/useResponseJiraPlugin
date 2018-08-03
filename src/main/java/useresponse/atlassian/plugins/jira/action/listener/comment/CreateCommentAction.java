package useresponse.atlassian.plugins.jira.action.listener.comment;

import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.json.simple.parser.ParseException;
import useresponse.atlassian.plugins.jira.action.listener.type.ActionType;
import useresponse.atlassian.plugins.jira.manager.CommentLinkManager;
import useresponse.atlassian.plugins.jira.manager.UseResponseObjectManager;
import useresponse.atlassian.plugins.jira.request.PostRequest;
import useresponse.atlassian.plugins.jira.request.Request;
import useresponse.atlassian.plugins.jira.service.handler.servlet.binder.RequestHandler;
import useresponse.atlassian.plugins.jira.service.request.parameters.builder.CommentRequestBuilder;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateCommentAction extends AbstractCommentAction {

    public CreateCommentAction(Comment comment, CommentLinkManager commentLinkManager, UseResponseObjectManager useResponseObjectManager, PluginSettingsFactory pluginSettingsFactory, CommentRequestBuilder builder) {
        this.comment = comment;
        this.commentLinkManager = commentLinkManager;
        this.useResponseObjectManager = useResponseObjectManager;
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.parametersBuilder = builder;
        this.request = new PostRequest();
        this.actionType = ActionType.CREATE_COMMENT_ID;
    }

    @Override
    public String createUrl() {
        return getSpecialApiPath();
    }

    @Override
    public void handleResponse(String response) {
        (new RequestHandler(useResponseObjectManager, commentLinkManager)).handle(response);
    }

    @Override
    public Request addParameters(Request request) {

        Map<Object, Object> commentData = parametersBuilder.build(comment);
        List<Map> commentsData = new ArrayList<>();
        commentsData.add(commentData);
        Map<Object, Object> dataForSending = new HashMap<>();
        dataForSending.put("comments", commentsData);
        request.addParameter(dataForSending);
        return request;
    }
}
