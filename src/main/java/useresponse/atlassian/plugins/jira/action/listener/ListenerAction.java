package useresponse.atlassian.plugins.jira.action.listener;

import com.atlassian.jira.entity.WithId;
import useresponse.atlassian.plugins.jira.action.Action;

public interface ListenerAction extends Action {
    void setEntity(WithId entity);
}
