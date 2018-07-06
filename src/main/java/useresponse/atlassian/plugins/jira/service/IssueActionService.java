package useresponse.atlassian.plugins.jira.service;

import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.attachment.Attachment;
import com.atlassian.jira.issue.attachment.AttachmentStore;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.label.Label;
import com.atlassian.jira.util.AttachmentUtils;
import com.atlassian.jira.util.io.InputStreamConsumer;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import useresponse.atlassian.plugins.jira.manager.CommentLinkManager;
import useresponse.atlassian.plugins.jira.manager.PriorityLinkManager;
import useresponse.atlassian.plugins.jira.manager.StatusesLinkManager;
import useresponse.atlassian.plugins.jira.manager.UseResponseObjectManager;
import useresponse.atlassian.plugins.jira.model.UseResponseObject;
import useresponse.atlassian.plugins.jira.request.DeleteRequest;
import useresponse.atlassian.plugins.jira.request.PostRequest;
import useresponse.atlassian.plugins.jira.request.PutRequest;
import useresponse.atlassian.plugins.jira.request.Request;
import useresponse.atlassian.plugins.jira.settings.PluginSettings;
import useresponse.atlassian.plugins.jira.settings.PluginSettingsImpl;
import useresponse.atlassian.plugins.jira.storage.ConstStorage;
import com.atlassian.jira.issue.AttachmentManager;

import org.apache.commons.io.IOUtils;


import java.io.*;
import java.util.*;

public class IssueActionService {

    private CommentLinkManager commentLinkManager;
    private UseResponseObjectManager useResponseObjectManager;
    private StatusesLinkManager statusesLinkManager;
    private PluginSettingsFactory pluginSettingsFactory;
    private PriorityLinkManager priorityLinkManager;
    private AttachmentManager attachmentManager;

    public IssueActionService(PluginSettingsFactory pluginSettingsFactory, CommentLinkManager commentLinkManager,
                              UseResponseObjectManager useResponseObjectManager, StatusesLinkManager statusesLinkManager,
                              PriorityLinkManager priorityLinkManager, AttachmentManager attachmentManager) {
        this.statusesLinkManager = statusesLinkManager;
        this.useResponseObjectManager = useResponseObjectManager;
        this.commentLinkManager = commentLinkManager;
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.priorityLinkManager = priorityLinkManager;
        this.attachmentManager = attachmentManager;
    }

    private List<String> getTagsFromLabels(Set<Label> labels) {
        Iterator<Label> iterator = labels.iterator();
        List<String> result = new ArrayList<String>();
        while (iterator.hasNext()) {
            result.add(iterator.next().getLabel());
        }
        return result;
    }

    public void createAction(Issue issue) throws Exception {
        Request request = new PostRequest();
        request = prepareRequest(request);

        request.addParameter("ownership", "helpdesk");
        request.addParameter("object_type", "ticket");
        request.addParameter("content", issue.getDescription());
        request.addParameter("title", issue.getSummary());
        request.addParameter("force_author", issue.getReporterUser().getEmailAddress());
        request.addParameter("tags", getTagsFromLabels(issue.getLabels()));
        request.addParameter("priority", priorityLinkManager.findByJiraPriorityName(issue.getPriority().getName()).getUseResponsePriority().getUseResponsePrioritySlug());
//        request.addParameter("responsible_id", issue.getAssignee().getEmailAddress());

        request = addAttachmentsToRequest(issue.getAttachments(), request);

        String response = request.sendRequest(createPostIssueRequestUrl());

        useResponseObjectManager.add(getIdFromResponse(response), issue.getId().intValue());
    }

    public void updateAction(Issue issue) throws Exception {
        Request request = new PutRequest();
        request = prepareRequest(request);

        request.addParameter("title", issue.getSummary());
        request.addParameter("content", issue.getDescription());
        request.addParameter("status", findUseResponseStatusFromJiraStatus(issue.getStatus().getSimpleStatus().getName()));
        request.addParameter("priority", priorityLinkManager.findByJiraPriorityName(issue.getPriority().getName()).getUseResponsePriority().getUseResponsePrioritySlug());
//        request.addParameter("responsible_id", issue.getAssignee().getEmailAddress());


        UseResponseObject object = useResponseObjectManager.findByJiraId(issue.getId().intValue());

        String response = request.sendRequest(createPutIssueRequestUrl(object.getUseResponseId()));
    }

    public void createCommentAction(Comment comment) throws Exception {
        Request request = new PostRequest();
        request = prepareRequest(request);

        int id = useResponseObjectManager.findByJiraId(comment.getIssue().getId().intValue()).getUseResponseId();
        request.addParameter("object_id", String.valueOf(id));
        request.addParameter("content", comment.getBody());

        String response = request.sendRequest(createPostCommentRequestUrl());

        commentLinkManager.findOrAdd(getIdFromResponse(response), comment.getId().intValue());
    }

    public void updateCommentAction(Comment comment) throws Exception {
        Request request = new PostRequest();
        request = prepareRequest(request);


        int id = commentLinkManager.findByJiraId(comment.getId().intValue()).getUseResponseCommentId();
        request.addParameter("content", comment.getBody());
        String response = request.sendRequest(createPutCommentRequestUrl(id));
    }

    public void deleteAction(Issue issue) throws Exception {
        Request request = new DeleteRequest();
        request = prepareRequest(request);

        int id = useResponseObjectManager.findByJiraId(issue.getId().intValue()).getUseResponseId();
        String response = request.sendRequest(createDeleteIssueRequestUrl(id));
    }

    public void deleteCommentAction(IssueEvent issueEvent) throws Exception {
        // DOESN'T WORK
    }


    private String createPostIssueRequestUrl() {
        return collectUrl("objects.json");
    }

    private String createPutIssueRequestUrl(int id) {
        return collectUrl("objects/" + id + ".json");
    }

    private String createDeleteIssueRequestUrl(int id) {
        return collectUrl("objects/" + id + "/trash.json");
    }

    private String createPostCommentRequestUrl() {
        return collectUrl("comments.json");
    }

    private String createPutCommentRequestUrl(int id) {
        return collectUrl("comments/" + id + "/edit.json");
    }

    private String createDeleteCommentRequestUrl(int id) {
        return collectUrl("comments/" + id + "/trash.json");
    }

    private String collectUrl(String requestString) {
        PluginSettings pluginSettings = new PluginSettingsImpl(pluginSettingsFactory);
        return pluginSettings.getUseResponseDomain() + ConstStorage.API_STRING + requestString + "?apiKey=" + pluginSettings.getUseResponseApiKey();
    }

    private int getIdFromResponse(String response) throws ParseException {
        JSONObject object = (JSONObject) new JSONParser().parse(response);
        return ((Long) ((JSONObject) object.get("success")).get("id")).intValue();
    }

    private Request prepareRequest(Request request) {
        request.addParameter("jira", "1");
        return request;
    }

    private String findUseResponseStatusFromJiraStatus(String jiraStatus) {
        return statusesLinkManager.findByJiraStatusName(jiraStatus).getUseResponseStatusSlug();
    }

    private Request addAttachmentsToRequest(Collection<Attachment> attachments, Request request) throws IOException {

        OutputStream outputStream = new FileOutputStream();

        for (Attachment attachment : attachments) {
//            attachmentManager.streamAttachmentContent(attachment, new InputStreamConsumer<Void>() {
//                @Override
//                public Void withInputStream(InputStream inputStream) throws IOException {
//                    try {
//                        IOUtils.copy(inputStream, outputStream);
//                    } finally {
//                        IOUtils.copy();
//                    }
//
//                    return null;
//                }
//            });
        }



        return request;
    }

}
