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

import java.util.*;

public abstract class AbstractIssueAction extends AbstractListenerAction {

    protected Issue issue;
    protected RendererManager rendererManager;
    protected PriorityLinkManager priorityLinkManager;
    protected UseResponseObjectManager useResponseObjectManager;
    protected AttachmentManager attachmentManager;
    protected IssueFileLinkManager issueFileLinkManager;


    protected Request addStandardParametersToRequest(Request request, Issue issue) {
        try {
            IssueRenderContext renderContext = new IssueRenderContext(issue);
            JiraRendererPlugin renderer = rendererManager.getRendererForType("atlassian-wiki-renderer");
            String html = renderer.render(issue.getDescription(), renderContext);
            request.addParameter("content", html);
            request.addParameter("title", issue.getSummary());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (issue.getReporterUser() != null) {
            try {
                request.addParameter("force_author", issue.getReporterUser().getEmailAddress());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (issue.getLabels() != null) {
            try {
                request.addParameter("tags", getTagsFromLabels(issue.getLabels()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (priorityLinkManager.findByJiraPriorityName(issue.getPriority().getName()) != null) {
            try {
                request.addParameter("priority", priorityLinkManager.findByJiraPriorityName(issue.getPriority().getName()).getUseResponsePriority().getUseResponsePrioritySlug());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            request = addResponsibleToRequest(request, issue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            request = addAttachmentsToRequest(request, issue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return request;
    }

    private Request addAttachmentsToRequest(Request request, Issue issue) throws Exception {
        Collection<Attachment> attachments = attachmentManager.getAttachments(issue);
        int issueId = issue.getId().intValue();
        ArrayList<Map> attachmentsData = new ArrayList<Map>();

        String filename;

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

    private Map<String, String> transformAttachmentForRequest(Attachment attachment) throws Exception {
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
