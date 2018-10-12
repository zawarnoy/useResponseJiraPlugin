package useresponse.atlassian.plugins.jira.servlet;

import com.atlassian.jira.component.ComponentAccessor;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import useresponse.atlassian.plugins.jira.manager.impl.UseResponseObjectManagerImpl;
import useresponse.atlassian.plugins.jira.service.request.ServletService;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

        if (useresponseId == null && jiraKey == null && objectType == null) {
            return;
        }

        int parsedId = Integer.valueOf(useresponseId);
        int issueId = ComponentAccessor.getIssueManager().getIssueByCurrentKey(jiraKey).getId().intValue();

        useResponseObjectManager.findOrAdd(parsedId, issueId, objectType);
        resp.getWriter().write("{ \"status\" : \"success\" }");
    }

}