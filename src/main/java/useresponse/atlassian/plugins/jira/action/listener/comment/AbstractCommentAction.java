package useresponse.atlassian.plugins.jira.action.listener.comment;

import com.atlassian.jira.entity.WithId;
import org.json.simple.parser.ParseException;
import useresponse.atlassian.plugins.jira.action.listener.AbstractListenerAction;
import useresponse.atlassian.plugins.jira.service.request.parameters.builder.CommentRequestBuilder;

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
