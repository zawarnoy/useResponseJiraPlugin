package useresponse.atlassian.plugins.jira.servlet;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.event.type.EventDispatchOption;
import com.atlassian.jira.issue.MutableIssue;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import useresponse.atlassian.plugins.jira.exception.MissingParameterException;
import useresponse.atlassian.plugins.jira.manager.impl.UseResponseObjectManagerImpl;
import useresponse.atlassian.plugins.jira.service.IssueService;
import useresponse.atlassian.plugins.jira.service.request.ServletService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class IssueLinkServlet extends HttpServlet {

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
        String responsibleEmail = (String) data.get("responsibleEmail");

        Map<String, String> responseMap = new HashMap<>();

//        ComponentAccessor.getUserManager().getUserByName();

        try {
            if (useresponseId == null) {
                throw new MissingParameterException("useresponse_id");
            }
            if (jiraKey == null) {
                throw new MissingParameterException("jira_key");
            }
            if (objectType == null) {
                throw new MissingParameterException("object_type");
            }
            if (sync == null) {
                throw new MissingParameterException("sync");
            }

            MutableIssue issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey(jiraKey);

            int parsedId = Integer.valueOf(useresponseId);

            if (responsibleEmail != null) {
                issue = IssueService.setAssigneeByEmail(issue, responsibleEmail);
            }

            int issueId = issue.getId().intValue();
            useResponseObjectManager.findOrAdd(parsedId, issueId, objectType, sync);

            ComponentAccessor.getIssueManager().updateIssue(ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser(), issue, EventDispatchOption.DO_NOT_DISPATCH, false);
            responseMap.put("status", "success");
        } catch (MissingParameterException e) {
            responseMap.put("status", "error");
            responseMap.put("message", e.getMessage());
        }

        String response = (new Gson()).toJson(responseMap);
        resp.getWriter().write(response);
    }

}