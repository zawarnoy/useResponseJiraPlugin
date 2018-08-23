package useresponse.atlassian.plugins.jira.action.listener.comment;

import com.atlassian.jira.entity.WithId;
import com.atlassian.jira.issue.comments.Comment;
import org.json.simple.parser.ParseException;
import useresponse.atlassian.plugins.jira.action.listener.AbstractListenerAction;
import useresponse.atlassian.plugins.jira.manager.CommentLinkManager;
import useresponse.atlassian.plugins.jira.manager.UseResponseObjectManager;
import useresponse.atlassian.plugins.jira.service.request.parameters.builder.CommentRequestBuilder;
import useresponse.atlassian.plugins.jira.service.request.parameters.builder.CommentRequestParametersBuilder;

/**
 * Parent of all comments transfer action.
 *
 * Contains fields which are needed for all comments action
 *
 */
public abstract class AbstractCommentAction extends AbstractListenerAction {

    protected CommentRequestBuilder parametersBuilder;

    protected WithId comment;

    @Override
    protected int getIdFromResponse(String response) throws ParseException {
        return super.getIdFromResponse(response);
    }
}
