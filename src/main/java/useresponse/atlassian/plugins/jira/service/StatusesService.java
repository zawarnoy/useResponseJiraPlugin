package useresponse.atlassian.plugins.jira.service;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.DefaultStatusManager;
import com.atlassian.jira.issue.status.Status;
import org.springframework.beans.factory.annotation.Autowired;
import useresponse.atlassian.plugins.jira.manager.impl.StatusesLinkManagerImpl;
import useresponse.atlassian.plugins.jira.model.StatusesLink;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;


public class StatusesService {

    private final DefaultStatusManager statusManager;

    @Inject
    @Named("statusesLinkManager")
    private StatusesLinkManagerImpl linkManager;

    public StatusesService() {
        this.statusManager = ComponentAccessor.getComponent(DefaultStatusManager.class);
    }

    public List<String> getStatusesNames() {
        Collection<Status> statuses = statusManager.getStatuses();
        List<String> statusList = new ArrayList<String>();

        for (Status status : statuses) {
            if (status != null)
                statusList.add(status.getName());
        }

        return statusList;
    }

    public Map<String, String> getStatusSlugLinks() {
        Map<String, String> statusSlugLinks = new HashMap<>();
        List<String> statusesNames = getStatusesNames();

        for (String statusName : statusesNames) {
            Map.Entry<String, String> entry = new AbstractMap.SimpleEntry<>(statusName, "");
            StatusesLink link = linkManager.findByJiraStatusName(statusName);
            if (link != null) {
                entry.setValue(link.getUseResponseStatusSlug());
            }
            statusSlugLinks.put(entry.getKey(), entry.getValue());
        }

        return statusSlugLinks;
    }
}
