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

import java.io.IOException;
import java.util.*;

public abstract class AbstractIssueAction extends AbstractListenerAction {

    protected Issue issue;
    protected RendererManager rendererManager;
    protected PriorityLinkManager priorityLinkManager;
    protected UseResponseObjectManager useResponseObjectManager;
    protected AttachmentManager attachmentManager;
    protected IssueFileLinkManager issueFileLinkManager;


    protected Request addStandardParametersToRequest(Request request, Issue issue) throws IOException {
        IssueRenderContext renderContext = new IssueRenderContext(issue);
        JiraRendererPlugin renderer = rendererManager.getRendererForType("atlassian-wiki-renderer");
        String html = renderer.render(issue.getDescription(), renderContext);
        request.addParameter("content", html);
        request.addParameter("title", issue.getSummary());
        request = addReporterToRequest(request, issue);
        request = addPriorityToRequest(request, issue);
        request = addLabelsToRequest(request, issue);
        request = addAttachmentsToRequest(request, issue);
        request = addResponsibleToRequest(request, issue);

        return request;
    }

    private Request addLabelsToRequest(Request request, Issue issue) {
        if (issue.getLabels() != null) {
            request.addParameter("tags", getTagsFromLabels(issue.getLabels()));
        }
        return request;
    }

    private Request addReporterToRequest(Request request, Issue issue) {
        if (issue.getReporterUser() != null) {
            request.addParameter("force_author", issue.getReporterUser().getEmailAddress());
        }
        return request;
    }

    private Request addPriorityToRequest(Request request, Issue issue) {
        if (priorityLinkManager.findByJiraPriorityName(issue.getPriority().getName()) != null) {
            request.addParameter("priority", priorityLinkManager.findByJiraPriorityName(issue.getPriority().getName()).getUseResponsePriority().getUseResponsePrioritySlug());
        }
        return request;
    }

    private Request addAttachmentsToRequest(Request request, Issue issue) throws IOException {
        Collection<Attachment> attachments = attachmentManager.getAttachments(issue);
        int issueId = issue.getId().intValue();
        ArrayList<Map> attachmentsData = new ArrayList<Map>();

        String filename;

        if (attachments.isEmpty()) {
            return request;
        }

        for (Attachment attachment : attachments) {
            filename = attachment.getFilename();
            if (checkNeedToSentAttachment(issueId, filename)) {
                attachmentsData.add(transformAttachmentForRequest(attachment));
                issueFileLinkManager.add(issueId, filename);
            }
        }
        request.addParameter("attachments", attachmentsData);
        return request;
    }

    private boolean checkNeedToSentAttachment(int issueId, String attachmentName) {
        return issueFileLinkManager.find(issueId, attachmentName) == null;
    }

    private Map<String, String> transformAttachmentForRequest(Attachment attachment) throws IOException {
        String body = null;
        Map<String, String> attachmentData = new HashMap<>();
        body = attachmentManager.streamAttachmentContent(attachment, inputStream -> {
            byte[] file = IOUtils.toByteArray(inputStream);
            return Base64.getEncoder().encodeToString(file);
        });
        attachmentData.put("name", attachment.getFilename());
        attachmentData.put("body", body);
        return attachmentData;
    }

    private Request addResponsibleToRequest(Request request, Issue issue) {
        if (issue.getAssignee() != null) {
            request.addParameter("responsible_email", issue.getAssignee().getEmailAddress());
        }
        return request;
    }

    private Object getTagsFromLabels(Set<Label> labels) {
        Iterator<Label> iterator = labels.iterator();
        List<String> result = new ArrayList<String>();
        while (iterator.hasNext()) {
            result.add(iterator.next().getLabel());
        }
        return result;
    }

}