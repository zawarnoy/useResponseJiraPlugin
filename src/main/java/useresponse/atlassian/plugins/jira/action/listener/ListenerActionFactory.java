package useresponse.atlassian.plugins.jira.action.listener;

import com.atlassian.jira.entity.WithId;

public interface ListenerActionFactory extends ActionFactory {
    void setEntity(WithId entity);
}
