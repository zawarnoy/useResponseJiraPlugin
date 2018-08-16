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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

public class IssueLinkServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(IssueLinkServlet.class);

    @Autowired
    protected UseResponseObjectManagerImpl useResponseObjectManager;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = req.getReader();

        String useresponseId = req.getParameter("useresponse_id");
        String jiraKey = req.getParameter("jira_key");

        if (useresponseId == null && jiraKey == null) {
            return;
        }

        int parsedId = Integer.valueOf(useresponseId);

        int issueId = ComponentAccessor.getIssueManager().getIssueByCurrentKey(jiraKey).getId().intValue();

        useResponseObjectManager.findOrAdd(parsedId, issueId);
        resp.getWriter().write("{ \"status\" : \"success\" }");
    }

}