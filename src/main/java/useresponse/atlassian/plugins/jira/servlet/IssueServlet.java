package useresponse.atlassian.plugins.jira.servlet;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.DefaultStatusManager;
import com.atlassian.jira.event.type.EventDispatchOption;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.status.Status;
import com.atlassian.jira.user.ApplicationUser;
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


        DefaultStatusManager statusManager = ComponentAccessor.getComponent(DefaultStatusManager.class);


        for (Status status : statusManager.getStatuses()) {
            if (status.getName().equals(statusName)) {
                MutableIssue issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey(issueKey);

                IssueService issueService = ComponentAccessor.getIssueService();

                IssueInputParameters parameters = issueService.newIssueInputParameters();
                parameters.setStatusId(status.getId());

                ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();

                IssueService.UpdateValidationResult result = issueService.validateUpdate(user, issue.getId(), parameters);

                if(result.isValid()) {
                    IssueService.IssueResult issueResult = issueService.update(user, result);
                }
                return;
            }
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPut(req, resp);
    }
}