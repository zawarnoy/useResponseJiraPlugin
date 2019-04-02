package useresponse.atlassian.plugins.jira.action.listener.issue;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.AttachmentManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.RendererManager;
import com.atlassian.jira.issue.managers.DefaultAttachmentManager;
import org.springframework.beans.factory.annotation.Autowired;
import useresponse.atlassian.plugins.jira.action.listener.AbstractListenerAction;
import useresponse.atlassian.plugins.jira.manager.IssueFileLinkManager;
import useresponse.atlassian.plugins.jira.manager.PriorityLinkManager;
import useresponse.atlassian.plugins.jira.service.request.parameters.builder.IssueRequestBuilder;

import javax.inject.Inject;
import javax.inject.Named;

public abstract class AbstractIssueAction extends AbstractListenerAction {

    protected Issue issue;

    @Inject
    protected RendererManager rendererManager;

    @Inject
    @Named("priorityLinkManager")
    protected PriorityLinkManager priorityLinkManager;

    protected AttachmentManager attachmentManager = ComponentAccessor.getComponent(DefaultAttachmentManager.class);

    @Inject
    @Named("issueFileLinkManager")
    protected IssueFileLinkManager issueFileLinkManager;

    @Autowired
    protected IssueRequestBuilder builder;
}
