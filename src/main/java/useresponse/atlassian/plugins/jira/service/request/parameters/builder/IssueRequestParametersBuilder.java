package useresponse.atlassian.plugins.jira.service.request.parameters.builder;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.entity.WithId;
import com.atlassian.jira.issue.AttachmentManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.attachment.Attachment;
import com.atlassian.jira.issue.label.Label;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import useresponse.atlassian.plugins.jira.manager.IssueFileLinkManager;
import useresponse.atlassian.plugins.jira.manager.PriorityLinkManager;
import useresponse.atlassian.plugins.jira.manager.StatusesLinkManager;
import useresponse.atlassian.plugins.jira.model.IssueFileLink;
import useresponse.atlassian.plugins.jira.model.StatusesLink;
import useresponse.atlassian.plugins.jira.service.converter.content.ContentConverter;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.atlassian.jira.component.ComponentAccessor.getAttachmentManager;

public class IssueRequestParametersBuilder extends RequestParametersBuilder {
    private Logger logger = LoggerFactory.getLogger(IssueRequestParametersBuilder.class);

    @Inject
    @Named("priorityLinkManager")
    protected PriorityLinkManager priorityLinkManager;

    protected AttachmentManager attachmentManager;

    @Inject
    @Named("issueFileLinkManager")
    protected IssueFileLinkManager issueFileLinkManager;

    @Inject
    protected PluginSettingsFactory pluginSettingsFactory;

    @Inject
    @Named("statusesLinkManager")
    protected StatusesLinkManager statusesLinkManager;

    public IssueRequestParametersBuilder() {
        this.attachmentManager = getAttachmentManager();
    }

    public IssueRequestParametersBuilder addOwnershipToMap() {
        requestMap.put("ownership", "helpdesk");
        return this;
    }

    public IssueRequestParametersBuilder addNewOldIssueKeysToMap(Set<String> issueKeys) {
        String oldKey = "";
        String newKey = "";
        for (String key : issueKeys) {
            oldKey = newKey;
            newKey = key;
        }
        requestMap.put("old_jira_issue_id", oldKey);
        requestMap.put("jira_issue_id", newKey);
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
        try {
            StatusesLink statusesLink = statusesLinkManager.findByJiraStatusName((issue.getStatus().getName()));
            if (statusesLink != null) {
                requestMap.put("status", statusesLink.getUseResponseStatusSlug());
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return this;
    }

    public IssueRequestParametersBuilder addStandardParametersToMap(Issue issue) throws IOException {
        requestMap = addHtmlTreat(requestMap);
        requestMap = addContentToRequest(requestMap, issue);
        requestMap = addTitleToRequest(requestMap, issue);
        requestMap = addCreatorToMap(requestMap, issue);
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

    public Map<Object, Object> addJiraIssueIdToMap(Map<Object, Object> map, Issue issue) {
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

    private Map<Object, Object> addCreatorToMap(Map<Object, Object> map, Issue issue) {
        if (issue.getReporter() != null) {
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

        if (!deletedAttachmentsList.isEmpty()) {
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
        List<Attachment> existedAttachments = getAttachmentManager().getAttachments(issue);
        List<String> result = new ArrayList<>();

        for (IssueFileLink link : links) {
            if (!isLinkInList(link, existedAttachments)) {
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
        requestMap.put("force_author", ((Issue) entity).getReporter().getEmailAddress());
        return this;
    }
}
