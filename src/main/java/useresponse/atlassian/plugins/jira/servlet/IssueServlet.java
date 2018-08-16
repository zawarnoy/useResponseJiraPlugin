package useresponse.atlassian.plugins.jira.servlet;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.event.type.EventDispatchOption;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueImpl;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.user.ApplicationUser;


import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class IssueServlet extends HttpServlet {


    public IssueServlet() {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


        IssueManager issueManager = ComponentAccessor.getIssueManager();

        super.doPost(req, resp);
    }

    /**
     *
     * For update reporter 
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String reporter = req.getParameter("reporter");
        String issueKey = req.getParameter("issueKey");

        IssueManager manager = ComponentAccessor.getIssueManager();

        MutableIssue issue = manager.getIssueByCurrentKey(issueKey);

        ApplicationUser user = ComponentAccessor.getUserManager().getUserByName(reporter);

        if(user != null) {
            issue.setReporter(user);
        }

        manager.updateIssue(user, issue, EventDispatchOption.DO_NOT_DISPATCH, false);

    }
}