package useresponse.atlassian.plugins.jira.action.listener.issue;

import com.atlassian.jira.issue.AttachmentManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.RendererManager;
import com.atlassian.jira.issue.attachment.Attachment;
import com.atlassian.jira.issue.fields.renderer.IssueRenderContext;
import com.atlassian.jira.issue.fields.renderer.JiraRendererPlugin;
import com.atlassian.jira.issue.label.Label;
import org.apache.commons.io.IOUtils;
import useresponse.atlassian.plugins.jira.action.listener.AbstractListenerAction;
import useresponse.atlassian.plugins.jira.manager.IssueFileLinkManager;
import useresponse.atlassian.plugins.jira.manager.PriorityLinkManager;
import useresponse.atlassian.plugins.jira.manager.UseResponseObjectManager;
import useresponse.atlassian.plugins.jira.request.Request;
import useresponse.atlassian.plugins.jira.service.request.parameters.builder.IssueRequestBuilder;

import java.io.IOException;
import java.util.*;

public abstract class AbstractIssueAction extends AbstractListenerAction {

    protected Issue issue;
    protected RendererManager rendererManager;
    protected PriorityLinkManager priorityLinkManager;
    protected UseResponseObjectManager useResponseObjectManager;
    protected AttachmentManager attachmentManager;
    protected IssueFileLinkManager issueFileLinkManager;
    protected IssueRequestBuilder builder;
    }
