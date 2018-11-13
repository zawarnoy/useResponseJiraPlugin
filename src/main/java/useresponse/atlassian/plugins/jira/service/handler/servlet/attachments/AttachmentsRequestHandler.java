package useresponse.atlassian.plugins.jira.service.handler.servlet.attachments;


import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.attachment.Attachment;
import com.atlassian.jira.issue.attachment.CreateAttachmentParamsBean;
import com.atlassian.jira.web.util.AttachmentException;
import com.google.gson.Gson;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import useresponse.atlassian.plugins.jira.manager.IssueFileLinkManager;
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
        HashMap<String, Object> response = new HashMap<>();

        try {
            issueKey = (String) data.get("issueKey");
        } catch (NullPointerException e) {
            try {
                String commentId = (String) data.get("comment_id");
                issueKey = ComponentAccessor.getCommentManager().getCommentById(Long.valueOf(commentId)).getIssue().getKey();
            } catch (NullPointerException ex) {
                response.put("status", "error");
                response.put("message", "");
                return (new Gson()).toJson(response);
            }
        }

        issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey(issueKey);
        addAttachments(issue, (List<Map<String, String>>) data.get("attachments"));

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
        for(Attachment existedAttachment : existedAttachments) {
            if(existedAttachment.getFilename().equals(attachment.get("filename"))) {
                return false;
            }
        }
        return true;
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
        } catch (IOException e) {
            log.error("An exception thrown while file " + filename + "was loaded!");
        }
        try {
            getAttachmentManager().createAttachment(bean);
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

    private boolean isZipFile(String filename) {
        Pattern pattern = Pattern.compile("\\.zip$");
        Matcher matcher = pattern.matcher(filename);
        return matcher.find();
    }

}
