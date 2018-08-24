package useresponse.atlassian.plugins.jira.servlet;

import com.atlassian.jira.component.ComponentAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import useresponse.atlassian.plugins.jira.manager.impl.UseResponseObjectManagerImpl;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class IssueLinkServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(IssueLinkServlet.class);

    @Autowired
    private UseResponseObjectManagerImpl useResponseObjectManager;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String useresponseId = req.getParameter("useresponse_id");
        String jiraKey = req.getParameter("jira_key");
        String objectType = req.getParameter("object_type");

        if (useresponseId == null && jiraKey == null && objectType == null) {
            return;
        }

        int parsedId = Integer.valueOf(useresponseId);
        int issueId = ComponentAccessor.getIssueManager().getIssueByCurrentKey(jiraKey).getId().intValue();

        useResponseObjectManager.findOrAdd(parsedId, issueId, objectType);
        resp.getWriter().write("{ \"status\" : \"success\" }");
    }

}