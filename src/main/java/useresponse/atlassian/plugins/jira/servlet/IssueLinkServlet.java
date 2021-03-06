package useresponse.atlassian.plugins.jira.servlet;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.user.ApplicationUser;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import useresponse.atlassian.plugins.jira.exception.MissingParameterException;
import useresponse.atlassian.plugins.jira.exception.NotLoggedException;
import useresponse.atlassian.plugins.jira.manager.impl.UseResponseObjectManagerImpl;
import useresponse.atlassian.plugins.jira.service.IssueService;
import useresponse.atlassian.plugins.jira.service.request.ServletService;
import useresponse.atlassian.plugins.jira.storage.Storage;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class IssueLinkServlet extends HttpServlet {

    Logger log = LoggerFactory.getLogger(IssueLinkServlet.class);

    @Autowired
    private UseResponseObjectManagerImpl useResponseObjectManager;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String json = ServletService.getJsonFromRequest(req);

        Map<String, Object> data = (new Gson()).fromJson(json, Map.class);

        String useresponseId = (String) data.get("useresponse_id");
        String jiraKey = (String) data.get("jira_key");
        String objectType = (String) data.get("object_type");
        Boolean sync = (Boolean) data.get("sync");
        String responsibleEmail = (String) data.get("responsible_email");
        String creatorEmail = (String) data.get("creator_email");
        String statusName = (String) data.get("status_name");

        Map<String, String> responseMap = new HashMap<>();

        ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();

        try {
            if (user == null) {
                throw new NotLoggedException("");
            }

            if (jiraKey == null) {
                throw new MissingParameterException("jira_key");
            }
        } catch (NotLoggedException | MissingParameterException e) {
            responseMap.put("status", "error");
            responseMap.put("message", e.getMessage());
        }

        try {
            if (useresponseId == null) {
                throw new MissingParameterException("useresponse_id");
            }
        } catch (MissingParameterException e) {
            Storage.log.error(e.getMessage());
        }

        try {
            if (objectType == null) {
                throw new MissingParameterException("object_type");
            }
        } catch (MissingParameterException e) {
            Storage.log.error(e.getMessage());
        }

        try {
            if (sync == null) {
                throw new MissingParameterException("sync");
            }
        } catch (MissingParameterException e) {
            Storage.log.error(e.getMessage());
        }

        try {
            if (statusName == null) {
                throw new MissingParameterException("status_name");
            }
        } catch (MissingParameterException e) {
            Storage.log.error(e.getMessage());
        }

        MutableIssue issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey(jiraKey);
        int parsedId = Integer.valueOf(useresponseId);

        if (sync) {
            issue = IssueService.setAssigneeByEmail(issue, responsibleEmail);
            issue = IssueService.setReporterByEmail(issue, creatorEmail);
            issue = IssueService.setStatusByStatusName(issue, statusName);
        }

        int issueId = issue.getId().intValue();
        useResponseObjectManager.findOrAdd(parsedId, issueId, objectType, sync);

//            ComponentAccessor.getIssueManager().updateIssue(ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser(), issue, EventDispatchOption.DO_NOT_DISPATCH, false);
        issue.store();
        responseMap.put("status", "success");

        String response = (new Gson()).toJson(responseMap);
        resp.getWriter().write(response);
    }

}