package useresponse.atlassian.plugins.jira.action.listener.comment;

import com.atlassian.jira.issue.comments.Comment;
import useresponse.atlassian.plugins.jira.action.listener.AbstractListenerAction;
import useresponse.atlassian.plugins.jira.manager.CommentLinkManager;
import useresponse.atlassian.plugins.jira.manager.UseResponseObjectManager;

public abstract class AbstractCommentAction extends AbstractListenerAction {

    protected CommentLinkManager commentLinkManager;
    protected UseResponseObjectManager useResponseObjectManager;
    protected Comment comment;
}
