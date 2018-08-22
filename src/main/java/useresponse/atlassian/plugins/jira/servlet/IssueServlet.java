package useresponse.atlassian.plugins.jira.servlet;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.DefaultStatusManager;
import com.atlassian.jira.event.type.EventDispatchOption;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.status.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class IssueServlet extends HttpServlet {

    Logger log = LoggerFactory.getLogger(IssueServlet.class);

    public IssueServlet() {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String statusName = req.getParameter("status_name");
        String issueKey = req.getParameter("issue_key");

        log.error(statusName);
        log.error(issueKey);

        DefaultStatusManager statusManager = ComponentAccessor.getComponent(DefaultStatusManager.class);


        for (Status status : statusManager.getStatuses()) {
            if (status.getName().equals(statusName)) {
                MutableIssue issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey(issueKey);
                issue.setStatus(status);
                ComponentAccessor.getIssueManager().updateIssue(
                        ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser(),
                        issue,
                        EventDispatchOption.DO_NOT_DISPATCH,
                        false
                );
                return;
            }
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPut(req, resp);
    }
}