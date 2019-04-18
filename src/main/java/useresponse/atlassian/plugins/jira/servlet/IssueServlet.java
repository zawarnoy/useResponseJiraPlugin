package useresponse.atlassian.plugins.jira.servlet;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.event.type.EventDispatchOption;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import useresponse.atlassian.plugins.jira.manager.UseResponseObjectManager;
import useresponse.atlassian.plugins.jira.model.UseResponseObject;
import useresponse.atlassian.plugins.jira.service.IssueService;
import useresponse.atlassian.plugins.jira.service.request.ServletService;
import useresponse.atlassian.plugins.jira.settings.PluginSettingsImpl;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class IssueServlet extends HttpServlet {

    Logger log = LoggerFactory.getLogger(IssueServlet.class);

    @Autowired
    private UseResponseObjectManager objectManager;

    @Autowired
    private PluginSettingsImpl pluginSettings;

    public IssueServlet() {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String issueKey = null;
        String statusName = null;
        String authorEmail = null;
        String content = null;
        String assigneeEmail = null;

        String json = ServletService.getJsonFromRequest(req);

        Map<String, Object> data = (new Gson()).fromJson(json, Map.class);


        if (data != null) {
            try {
                issueKey = (String) data.get("issueKey");
            } catch (NullPointerException exception) {
                log.error("Empty Issue Key");
//                exception.printStackTrace();
            }

            if (issueKey == null) {
                return;
            }

            IssueManager issueManager = ComponentAccessor.getIssueManager();
            MutableIssue issue = issueManager.getIssueByCurrentKey(issueKey);

            UseResponseObject useResponseObject = objectManager.findByJiraId(issue.getId().intValue());

            String objectType = (String) data.get("object_type");

            if(objectType != null) {
                useResponseObject.setObjectType(objectType);
                useResponseObject.save();
            }

            try {
                statusName = (String) data.get("statusName");
                issue = IssueService.setStatusByStatusName(issue, statusName);
            } catch (NullPointerException exception) {
                log.error("Empty Status Name");
//                exception.printStackTrace();
            }
            try {
                authorEmail = (String) data.get("authorEmail");
                issue = IssueService.setReporterByEmail(issue, authorEmail);
            } catch (NullPointerException exception) {
                log.error("Empty author Email");
//                exception.printStackTrace();
            }
            try {
                content = (String) data.get("content");
                issue = IssueService.setDescription(issue, content);
            } catch (NullPointerException exception) {
                log.error("Empty content");
//                exception.printStackTrace();
            }
            try {
                assigneeEmail = (String) data.get("responsibleEmail");
                issue = IssueService.setAssigneeByEmail(issue, assigneeEmail);
            } catch (NullPointerException exception) {
                log.error("Empty responsible Email");
//                exception.printStackTrace();
            }

            pluginSettings.setNeedExecute(false);

            try {
                ComponentAccessor.getIssueManager().updateIssue(ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser(), issue, EventDispatchOption.DO_NOT_DISPATCH, false);
                issue.store();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                pluginSettings.setNeedExecute(true);
            }
        }
    }
}