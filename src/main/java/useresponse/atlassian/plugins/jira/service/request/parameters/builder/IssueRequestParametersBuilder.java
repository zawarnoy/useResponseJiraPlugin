package useresponse.atlassian.plugins.jira.service.request.parameters.builder;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.entity.WithId;
import com.atlassian.jira.issue.AttachmentManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.RendererManager;
import com.atlassian.jira.issue.attachment.Attachment;
import com.atlassian.jira.issue.label.Label;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import useresponse.atlassian.plugins.jira.manager.IssueFileLinkManager;
import useresponse.atlassian.plugins.jira.manager.PriorityLinkManager;
import useresponse.atlassian.plugins.jira.manager.StatusesLinkManager;
import useresponse.atlassian.plugins.jira.manager.UseResponseObjectManager;
import useresponse.atlassian.plugins.jira.model.IssueFileLink;
import useresponse.atlassian.plugins.jira.model.StatusesLink;
import useresponse.atlassian.plugins.jira.service.converter.content.ContentConverter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class IssueRequestParametersBuilder extends RequestParametersBuilder {
    Logger logger = LoggerFactory.getLogger(IssueRequestParametersBuilder.class);
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

    public IssueRequestParametersBuilder addUseResponseObjectId(Issue issue) {
        int useResponseObjectId = useResponseObjectManager.findByJiraId(issue.getId().intValue()).getUseResponseId();
        requestMap.put("useresponse_object_id", useResponseObjectId);
        return this;
    }

    public IssueRequestParametersBuilder addCreatedAt(Issue issue) {
        requestMap.put("created_at", (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(issue.getCreated()));
        return this;
    }

    public IssueRequestParametersBuilder addObjectTypeToMap() {
        requestMap.put("object_type", "ticket");
        return this;
    }

    public IssueRequestParametersBuilder addStatusToMap(Issue issue) {
        StatusesLink statusesLink = statusesLinkManager.findByJiraStatusName((issue.getStatus().getSimpleStatus().getName()));
        if (statusesLink != null) {
            requestMap.put("status", statusesLink.getUseResponseStatusSlug());
        }
        return this;
    }

    public IssueRequestParametersBuilder addStandardParametersToMap(Issue issue) throws IOException {
        requestMap = addHtmlTreat(requestMap);
        requestMap = addContentToRequest(requestMap, issue);
        requestMap = addTitleToRequest(requestMap, issue);
        requestMap = addReporterToMap(requestMap, issue);
        requestMap = addPriorityToMap(requestMap, issue);
        requestMap = addLabelsToMap(requestMap, issue);
        requestMap = addAttachmentsToMap(requestMap, issue);
        requestMap = addResponsibleToMap(requestMap, issue);
        requestMap = addJiraIssueIdToMap(requestMap, issue);
        requestMap = addDueOnToMap(requestMap, issue);
        requestMap = addProjectKeyToMap(requestMap, issue);
        return this;
    }

    private Map<Object, Object> addLabelsToMap(Map<Object, Object> map, Issue issue) {
        if (issue.getLabels() != null) {
            map.put("tags", getTagsFromLabels(issue.getLabels()));
        }
        return map;
    }

    private Map<Object, Object> addProjectKeyToMap(Map<Object, Object> map, Issue issue) {
        if (issue.getProjectObject() != null) {
            map.put("project_key", issue.getProjectObject().getKey());
        }
        return map;
    }

    private Map<Object, Object> addDueOnToMap(Map<Object, Object> map, Issue issue) {
        if (issue.getDueDate() != null) {
            map.put("due_on", issue.getDueDate().getTime());
        }
        return map;
    }

    private Map<Object, Object> addJiraIssueIdToMap(Map<Object, Object> map, Issue issue) {
        map.put("jira_issue_id", issue.getKey());
        return map;
    }

    private Map<Object, Object> addTitleToRequest(Map<Object, Object> map, Issue issue) {
        map.put("title", issue.getSummary());
        return map;
    }

    private Map<Object, Object> addContentToRequest(Map<Object, Object> map, Issue issue) {
        map.put("content", ContentConverter.convert(issue));
        return map;
    }

    private Map<Object, Object> addReporterToMap(Map<Object, Object> map, Issue issue) {
        if (issue.getReporterUser() != null) {
            map.put("force_author", issue.getReporter().getEmailAddress());
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
        List<String> deletedAttachmentsList = getDeletedAttachmentsList(issue);

        ArrayList<Map> attachmentsData = new ArrayList<Map>();

        if(!deletedAttachmentsList.isEmpty()) {
            map.put("deleted_attachments", deletedAttachmentsList);
        }

        if (attachments.isEmpty()) {
            return map;
        }

        String filename;
        int issueId = issue.getId().intValue();

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

    private List<String> getDeletedAttachmentsList(Issue issue) {
        List<IssueFileLink> links = issueFileLinkManager.findByJiraIssueId(issue.getId().intValue());
        List<Attachment> existedAttachments = ComponentAccessor.getAttachmentManager().getAttachments(issue);
        List<String> result = new ArrayList<>();

        for (IssueFileLink link : links) {
            if(!isLinkInList(link, existedAttachments)) {
                result.add(link.getSentFilename());
                issueFileLinkManager.delete(link);
            }
        }
        return result;
    }

    private boolean isLinkInList(IssueFileLink link, Iterable<Attachment> attachments) {
        boolean flag = false;
        for (Attachment attachment : attachments) {
            if (link.getSentFilename().equals(attachment.getFilename())) {
                flag = true;
                break;
            }
        }
        return flag;
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

    @Override
    public <T extends WithId> RequestParametersBuilder addAuthorToRequest(T entity) {
        requestMap.put("force_author", ((Issue) entity).getCreator().getEmailAddress());
        return this;
    }
}
