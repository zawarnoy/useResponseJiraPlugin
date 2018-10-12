package useresponse.atlassian.plugins.jira.servlet;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.DefaultStatusManager;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.status.Status;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.UserUtils;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import useresponse.atlassian.plugins.jira.service.converter.content.ContentConverter;
import useresponse.atlassian.plugins.jira.service.handler.Handler;
import useresponse.atlassian.plugins.jira.service.handler.servlet.attachments.AttachmentsRequestHandler;
import useresponse.atlassian.plugins.jira.service.request.ServletService;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class IssueServlet extends HttpServlet {

    Logger log = LoggerFactory.getLogger(IssueServlet.class);

    public IssueServlet() {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


        DefaultStatusManager statusManager = ComponentAccessor.getComponent(DefaultStatusManager.class);

        String issueKey = null;
        String statusName = null;
        String authorEmail = null;
        String content = null;
        String attachmentsHandleResponse = null;

        String json = ServletService.getJsonFromRequest(req);

        Map<String, Object> data = (new Gson()).fromJson(json, Map.class);



        if (data != null) {

            try {
                issueKey = (String) data.get("issueKey");
            } catch (NullPointerException exception) {
                log.error("Exception IssueServlet. Message: " + exception.getMessage());
                exception.printStackTrace();
            }
            try {
                statusName = (String) data.get("statusName");
            } catch (NullPointerException exception) {
                log.error("Exception IssueServlet. Message: " + exception.getMessage());
                exception.printStackTrace();
            }
            try {
                authorEmail = (String) data.get("authorEmail");
            } catch (NullPointerException exception) {
                log.error("Exception IssueServlet. Message: " + exception.getMessage());
                exception.printStackTrace();
            }
            try {
                content = (String) data.get("content");
            } catch (NullPointerException exception) {
                log.error("Exception IssueServlet. Message: " + exception.getMessage());
                exception.printStackTrace();
            }
            try {
                Handler<String, String> handler = new AttachmentsRequestHandler();
                attachmentsHandleResponse = handler.handle(json);
            } catch (Exception exception) {
                log.error("Exception IssueServlet. Message: " + exception.getMessage());
                exception.printStackTrace();
            }
        }

        if (issueKey == null) {
            return;
        }

        IssueManager issueManager = ComponentAccessor.getIssueManager();
        MutableIssue issue = issueManager.getIssueByCurrentKey(issueKey);

        issue = setDescription(issue, content);
        issue = setStatusByStatusName(issue, statusName);
        issue = setReporterByEmail(issue, authorEmail);
        issue.store();
    }

    private MutableIssue setStatusByStatusName(MutableIssue issue, String statusName) {
        if (statusName != null) {
            DefaultStatusManager statusManager = ComponentAccessor.getComponent(DefaultStatusManager.class);
            for (Status status : statusManager.getStatuses()) {
                if (status.getSimpleStatus().getName().equals(statusName)) {
                    issue.setStatus(status);
                    break;
                }
            }
        }
        return issue;
    }

    private MutableIssue setDescription(MutableIssue issue, String content) {
        if (content != null) {
            content = ContentConverter.convertForJira(content, issue);
            issue.setDescription(content);
        }
        return issue;
    }

    private MutableIssue setReporterByEmail(MutableIssue issue, String reporterEmail) {
        if (reporterEmail != null) {
            ApplicationUser user = UserUtils.getUserByEmail(reporterEmail);

            issue.setReporter(user);
        }
        return issue;
    }
}