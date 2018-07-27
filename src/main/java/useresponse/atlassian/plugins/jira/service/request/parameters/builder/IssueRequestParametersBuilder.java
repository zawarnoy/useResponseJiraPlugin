package useresponse.atlassian.plugins.jira.service.request.parameters.builder;

import com.atlassian.jira.entity.WithId;
import com.atlassian.jira.issue.AttachmentManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.RendererManager;
import com.atlassian.jira.issue.attachment.Attachment;
import com.atlassian.jira.issue.fields.renderer.IssueRenderContext;
import com.atlassian.jira.issue.fields.renderer.JiraRendererPlugin;
import com.atlassian.jira.issue.label.Label;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.apache.commons.io.IOUtils;
import useresponse.atlassian.plugins.jira.manager.IssueFileLinkManager;
import useresponse.atlassian.plugins.jira.manager.PriorityLinkManager;
import useresponse.atlassian.plugins.jira.manager.StatusesLinkManager;
import useresponse.atlassian.plugins.jira.manager.UseResponseObjectManager;
import useresponse.atlassian.plugins.jira.model.StatusesLink;
import useresponse.atlassian.plugins.jira.model.UseResponseObject;

import java.io.IOException;
import java.util.*;

public class IssueRequestParametersBuilder extends RequestParametersBuilder {

    protected RendererManager rendererManager;
    protected PriorityLinkManager priorityLinkManager;

    protected AttachmentManager attachmentManager;
    protected IssueFileLinkManager issueFileLinkManager;
    protected PluginSettingsFactory pluginSettingsFactory;
    protected StatusesLinkManager statusesLinkManager;

    public IssueRequestParametersBuilder(RendererManager rendererManager,
                                         PriorityLinkManager priorityLinkManager,
                                         UseResponseObjectManager useResponseObjectManager,
                                         AttachmentManager attachmentManager,
                                         IssueFileLinkManager issueFileLinkManager,
                                         PluginSettingsFactory pluginSettingsFactory,
                                         StatusesLinkManager statusesLinkManager
    ) {
        this.rendererManager = rendererManager;
        this.priorityLinkManager = priorityLinkManager;
        this.useResponseObjectManager = useResponseObjectManager;
        this.attachmentManager = attachmentManager;
        this.issueFileLinkManager = issueFileLinkManager;
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.statusesLinkManager = statusesLinkManager;
    }

    public IssueRequestParametersBuilder addOwnershipToMap() {
        requestMap.put("ownership", "helpdesk");
        return this;
    }

    public IssueRequestParametersBuilder addUseResponseId(Issue issue) {
        requestMap.put("useresponse_id", useResponseObjectManager.findByJiraId(issue.getId().intValue()));
        return this;
    }

    public IssueRequestParametersBuilder addObjectTypeToMap() {
        requestMap.put("object_type", "ticket");
        return this;
    }

    public IssueRequestParametersBuilder addStatusToMap(Issue issue) {
        StatusesLink statusesLink = statusesLinkManager.findByJiraStatusName((issue.getStatus().getName()));
        if(statusesLink != null) {
            requestMap.put("status", statusesLink.getUseResponseStatusSlug());
        }
        return this;
    }

    public IssueRequestParametersBuilder addStandardParametersToMap(Issue issue) throws IOException {
        IssueRenderContext renderContext = new IssueRenderContext(issue);
        JiraRendererPlugin renderer = rendererManager.getRendererForType("atlassian-wiki-renderer");
        String html = renderer.render(issue.getDescription(), renderContext);

        requestMap.put("content", html);
        requestMap.put("title", issue.getSummary());
        requestMap = addReporterToMap(requestMap, issue);
        requestMap = addPriorityToMap(requestMap, issue);
        requestMap = addLabelsToMap(requestMap, issue);
        requestMap = addAttachmentsToMap(requestMap, issue);
        requestMap = addAttachmentsToMap(requestMap, issue);

        return this;
    }

    private Map<Object, Object> addLabelsToMap(Map<Object, Object> map, Issue issue) {
        if (issue.getLabels() != null) {
            map.put("tags", getTagsFromLabels(issue.getLabels()));
        }
        return map;
    }

    private Map<Object, Object> addReporterToMap(Map<Object, Object> map, Issue issue) {
        if (issue.getReporterUser() != null) {
            map.put("force_author", issue.getReporterUser().getEmailAddress());
        }
        return map;
    }

    private Map<Object, Object> addPriorityToMap(Map<Object, Object> map, Issue issue) {
        if (priorityLinkManager.findByJiraPriorityName(issue.getPriority().getName()) != null) {
            map.put("priority", priorityLinkManager.findByJiraPriorityName(issue.getPriority().getName()).getUseResponsePriority().getUseResponsePrioritySlug());
        }
        return map;
    }

    private Map<Object, Object> addAttachmentsToMap(Map<Object, Object> map, Issue issue) throws IOException {
        Collection<Attachment> attachments = attachmentManager.getAttachments(issue);
        int issueId = issue.getId().intValue();
        ArrayList<Map> attachmentsData = new ArrayList<Map>();

        String filename;

        if (attachments.isEmpty()) {
            return map;
        }

        for (Attachment attachment : attachments) {
            filename = attachment.getFilename();
            if (checkNeedToSentAttachment(issueId, filename)) {
                attachmentsData.add(transformAttachmentForMap(attachment));
                issueFileLinkManager.add(issueId, filename);
            }
        }
        map.put("attachments", attachmentsData);
        return map;
    }

    private boolean checkNeedToSentAttachment(int issueId, String attachmentName) {
        return issueFileLinkManager.find(issueId, attachmentName) == null;
    }

    private Map<String, String> transformAttachmentForMap(Attachment attachment) throws IOException {
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

    private Map<Object, Object> addResponsibleToMap(Map<Object, Object> map, Issue issue) {
        if (issue.getAssignee() != null) {
            map.put("responsible_email", issue.getAssignee().getEmailAddress());
        }
        return map;
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
