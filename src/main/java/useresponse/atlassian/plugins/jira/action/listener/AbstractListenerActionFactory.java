package useresponse.atlassian.plugins.jira.action.listener;

import com.atlassian.jira.entity.WithId;

public abstract class AbstractListenerActionFactory implements ListenerActionFactory {

    protected WithId entity;

    @Override
    public void setEntity(WithId entity) {
        this.entity = entity;
    }

}
