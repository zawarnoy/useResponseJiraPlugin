package useresponse.atlassian.plugins.jira.service;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.DefaultStatusManager;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.exception.PermissionException;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.status.Status;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.UserDetails;
import com.atlassian.jira.user.UserUtils;
import useresponse.atlassian.plugins.jira.service.converter.content.ContentConverter;

public class IssueService {

    public static MutableIssue setStatusByStatusName(MutableIssue issue, String statusName) {
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

    public static MutableIssue setDescription(MutableIssue issue, String content) {
        if (content != null) {
            content = ContentConverter.convertForJira(content, issue);
            issue.setDescription(content);
        }
        return issue;
    }

    public static MutableIssue setReporterByEmail(MutableIssue issue, String reporterEmail) {
        if (!reporterEmail.equals("")) {
            try {
                issue.setReporter(createUser(reporterEmail));
            } catch (PermissionException | CreateException e) {
                e.printStackTrace();
            }
        }
        return issue;
    }

    public static MutableIssue setAssigneeByEmail(MutableIssue issue, String assigneeEmail) {
        if (!assigneeEmail.equals("")) {
            try {
                issue.setAssignee(createUser(assigneeEmail));
            } catch (CreateException | PermissionException e) {
                e.printStackTrace();
            }
        }
        return issue;
    }

    private static ApplicationUser createUser(String email) throws PermissionException, CreateException {
        UserDetails userDetails = (new UserDetails(email, email).withEmail(email));
        return ComponentAccessor.getUserManager().createUser(userDetails);
    }
}