package useresponse.atlassian.plugins.jira.action.listener.issue;

import com.atlassian.jira.issue.AttachmentManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.RendererManager;
import useresponse.atlassian.plugins.jira.action.listener.AbstractListenerAction;
import useresponse.atlassian.plugins.jira.manager.IssueFileLinkManager;
import useresponse.atlassian.plugins.jira.manager.PriorityLinkManager;
import useresponse.atlassian.plugins.jira.manager.UseResponseObjectManager;
import useresponse.atlassian.plugins.jira.service.request.parameters.builder.IssueRequestBuilder;

public abstract class AbstractIssueAction extends AbstractListenerAction {

    protected Issue issue;
    protected RendererManager rendererManager;
    protected PriorityLinkManager priorityLinkManager;

    protected AttachmentManager attachmentManager;
    protected IssueFileLinkManager issueFileLinkManager;
    protected IssueRequestBuilder builder;
    }
