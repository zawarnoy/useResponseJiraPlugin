package useresponse.atlassian.plugins.jira.condition;

import com.atlassian.jira.plugin.webfragment.conditions.AbstractWebCondition;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.user.ApplicationUser;
import org.springframework.beans.factory.annotation.Autowired;
import useresponse.atlassian.plugins.jira.manager.UseResponseObjectManager;
import useresponse.atlassian.plugins.jira.manager.impl.UseResponseObjectManagerImpl;

public class UseresponseLinkButtonCondition extends AbstractWebCondition {

    @Autowired
    private UseResponseObjectManagerImpl objectManager;


    @Override
    public boolean shouldDisplay(ApplicationUser applicationUser, JiraHelper jiraHelper) {
        return true;
    }
}
