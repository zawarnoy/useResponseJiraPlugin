package useresponse.atlassian.plugins.jira.condition;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.plugin.webfragment.conditions.AbstractWebCondition;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.user.ApplicationUser;

public class UserIsAdminCondition extends AbstractWebCondition {
    @Override
    public boolean shouldDisplay(ApplicationUser applicationUser, JiraHelper jiraHelper) {
        return applicationUser != null && ComponentAccessor.getUserUtil().getJiraAdministrators().contains(applicationUser);
    }
}
