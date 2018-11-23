package useresponse.atlassian.plugins.jira.service.handler.servlet.attachments;


import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.event.type.EventDispatchOption;
import com.atlassian.jira.exception.RemoveException;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.attachment.Attachment;
import com.atlassian.jira.issue.attachment.CreateAttachmentParamsBean;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.issue.comments.MutableComment;
import com.atlassian.jira.issue.history.ChangeItemBean;
import com.atlassian.jira.web.util.AttachmentException;
import com.google.gson.Gson;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import useresponse.atlassian.plugins.jira.manager.IssueFileLinkManager;
import useresponse.atlassian.plugins.jira.service.converter.content.ContentConverter;
import useresponse.atlassian.plugins.jira.service.handler.Handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.atlassian.jira.component.ComponentAccessor.getAttachmentManager;

public class AttachmentsRequestHandler implements Handler<String, String> {

    private static final Logger log = LoggerFactory.getLogger(AttachmentsRequestHandler.class);

    private IssueFileLinkManager fileLinkManager;

    public void setFileLinkManager(IssueFileLinkManager fileLinkManager) {
        this.fileLinkManager = fileLinkManager;
    }

    @Override
    public String handle(String s) throws IOException, ParseException {
        Map data = (new Gson()).fromJson(s, Map.class);

        String issueKey = null;
        MutableIssue issue = null;
        Boolean delete;
        HashMap<String, Object> response = new HashMap<>();
        int attachmentsType;


        try {
            delete = (boolean) data.get("delete");
        } catch (Exception e) {
            delete = false;
        }

        try {
            issueKey = (String) data.get("issueKey");
            if (issueKey == null) {
                throw new NullPointerException();
            }
            attachmentsType = AttachmentsType.ISSUE_ATTACHMENT;
        } catch (NullPointerException e) {
            try {
                String commentId = (String) data.get("comment_id");
                issueKey = ComponentAccessor.getCommentManager().getCommentById(Long.valueOf(commentId)).getIssue().getKey();
                attachmentsType = AttachmentsType.COMMENT_ATTACHMENT;
            } catch (NullPointerException ex) {
                response.put("status", "error");
                response.put("message", ex.getMessage());
                return (new Gson()).toJson(response);
            }
        }

        issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey(issueKey);
        List<Map<String, String>> attachmentsList = (List<Map<String, String>>) data.get("attachments");


        if (delete) {
            deleteAttachments(issue, attachmentsList);
        } else {
            addAttachments(issue, attachmentsList);

            switch (attachmentsType) {
                case AttachmentsType.ISSUE_ATTACHMENT:
                    issue.setDescription(ContentConverter.convertImages(issue));
                    ComponentAccessor.getIssueManager().updateIssue(ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser(), issue, EventDispatchOption.DO_NOT_DISPATCH, false);
                    break;
                case AttachmentsType.COMMENT_ATTACHMENT:
                    String comment_id = (String) data.get("comment_id");
                    CommentManager manager = ComponentAccessor.getCommentManager();
                    MutableComment comment = manager.getMutableComment(Long.valueOf(comment_id));
                    comment.setBody(ContentConverter.convertImages(comment.getIssue(), comment.getBody()));
                    manager.update(comment, false);
                    break;
            }
        }
        response.put("status", "success");

        return (new Gson()).toJson(response);
    }

    private MutableIssue addAttachments(MutableIssue issue, List<Map<String, String>> attachments) {
        if (attachments == null) {
            log.error("Attachments array is empty!");
            return issue;
        }
        for (Map<String, String> attachment : attachments) {
            if (checkNeedToAdd(issue.getAttachments(), attachment)) {
                addOneAttachment(issue, attachment);
            }
        }
        return issue;
    }

    private boolean checkNeedToAdd(Collection<Attachment> existedAttachments, Map<String, String> attachment) {
        for (Attachment existedAttachment : existedAttachments) {
            if (existedAttachment.getFilename().equals(attachment.get("filename"))) {
                return false;
            }
        }
        return true;
    }

    private void deleteAttachments(MutableIssue issue, List<Map<String, String>> attachmentsList) {
        List<Attachment> attachments = getAttachmentManager().getAttachments(issue);
        Attachment attachment;
        for (Map<String, String> attachmentMap : attachmentsList) {
            try {
                attachment = getAttachmentByFilename(attachments, attachmentMap.get("filename"));
                if (attachment != null) {
                    getAttachmentManager().deleteAttachment(attachment);
                    this.fileLinkManager.delete(issue.getId().intValue(), attachmentMap.get("filename"));
                }
            } catch (RemoveException | NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    private void addOneAttachment(MutableIssue issue, Map<String, String> data) {
        String filename = data.get("filename");

        CreateAttachmentParamsBean bean = null;
        try {
            bean = new CreateAttachmentParamsBean(
                    downloadFileToDisk(data, issue),
                    filename,
                    null,
                    issue.getReporter(),
                    issue,
                    isZipFile(filename),
                    null,
                    null,
                    new Date(),
                    false
            );

            fileLinkManager.add(issue.getId().intValue(), filename);

        } catch (IOException e) {
            log.error("An exception thrown while file " + filename + "was loaded!");
        }
        try {
            ChangeItemBean changeItemBean = getAttachmentManager().createAttachment(bean);
        } catch (AttachmentException e) {
            log.error("Adding Attachment error" + e.getMessage());
        }
    }

    private File downloadFileToDisk(Map<String, String> data, MutableIssue issue) throws IOException {
        String attachmentsPath = ComponentAccessor.getAttachmentPathManager().getAttachmentPath();

        String pathToFile = attachmentsPath + "\\" + issue.getProjectObject().getKey() + issue.getKey() + RandomStringUtils.randomAlphabetic(10);
        byte[] fileData = Base64.getDecoder().decode(data.get("content"));

        File file = new File(pathToFile);

        if (file.createNewFile()) {
            try (OutputStream stream = new FileOutputStream(pathToFile)) {
                stream.write(fileData);
            }
        }

        return file;
    }

    private Attachment getAttachmentByFilename(List<Attachment> attachments, String filename) {
        for (Attachment attachment : attachments) {
            if (attachment.getFilename().equals(filename)) {
                return attachment;
            }
        }
        return null;
    }

    private boolean isZipFile(String filename) {
        Pattern pattern = Pattern.compile("\\.zip$");
        Matcher matcher = pattern.matcher(filename);
        return matcher.find();
    }
}
