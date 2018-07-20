package useresponse.atlassian.plugins.jira.action.listener;

import com.atlassian.jira.entity.WithId;
import useresponse.atlassian.plugins.jira.action.Action;
import useresponse.atlassian.plugins.jira.action.ActionFactory;

public interface ListenerActionFactory extends ActionFactory {
    void setEntity(WithId entity);
}
