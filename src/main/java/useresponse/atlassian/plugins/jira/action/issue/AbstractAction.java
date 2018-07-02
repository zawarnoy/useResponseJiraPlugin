package useresponse.atlassian.plugins.jira.action.issue;

import org.springframework.beans.factory.annotation.Autowired;
import useresponse.atlassian.plugins.jira.manager.UseResponseObjectManagerImpl;

public abstract class AbstractAction implements Action{

    @Autowired
    protected UseResponseObjectManagerImpl useResponseObjectManager;

}
