package useresponse.atlassian.plugins.jira.service;
import com.atlassian.jira.config.DefaultStatusManager;
import com.atlassian.jira.issue.status.Status;
import useresponse.atlassian.plugins.jira.model.StatusesLink;

import java.util.*;


public class StatusesService {

    private final DefaultStatusManager statusManager;


    public StatusesService(DefaultStatusManager statusManager) {
        this.statusManager = statusManager;
    }


    public List<String> getStatusesNames() {
        Collection<Status> statuses = statusManager.getStatuses();
        List<String> statusList = null;

        for (Status status : statuses) {
            statusList.add(status.getSimpleStatus().getName());
        }

        return statusList;
    }

    public Map<String, String> getStatusSlugLinks(List<StatusesLink> statusesLinks) {

        Map<String,String> statusSlugLinks = new HashMap<>();



        return statusSlugLinks;
    }




}
