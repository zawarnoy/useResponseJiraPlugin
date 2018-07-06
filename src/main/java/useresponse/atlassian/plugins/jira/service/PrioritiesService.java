package useresponse.atlassian.plugins.jira.service;

import com.atlassian.jira.issue.priority.Priority;
import useresponse.atlassian.plugins.jira.manager.PriorityLinkManager;
import useresponse.atlassian.plugins.jira.manager.URPriorityManager;
import useresponse.atlassian.plugins.jira.model.PriorityLink;
import useresponse.atlassian.plugins.jira.model.StatusesLink;
import useresponse.atlassian.plugins.jira.model.URPriority;
import com.atlassian.jira.config.DefaultPriorityManager;

import java.util.*;

public class PrioritiesService {

    private DefaultPriorityManager priorityManager;
    private PriorityLinkManager priorityLinkManger;
    private URPriorityManager urPriorityManager;

    public PrioritiesService(DefaultPriorityManager priorityManager, PriorityLinkManager priorityLinkManger, URPriorityManager urPriorityManager) {
        this.priorityManager = priorityManager;
        this.priorityLinkManger = priorityLinkManger;
        this.urPriorityManager = urPriorityManager;
    }


    public List<String> getPrioritiesNames() {
        Collection<Priority> priorities = priorityManager.getPriorities();
        List<String> priorityList = new ArrayList<>();

        for (Priority priority : priorities) {
            if (priority != null) {
                priorityList.add(priority.getName());
            }
        }
        return priorityList;
    }

    public Map<String, String> getUseResponsePriorities() {
        Map<String, String> result = new HashMap<>();
        for (URPriority urPriority : urPriorityManager.all()) {
            result.put(urPriority.getUseResponsePrioritySlug(), urPriority.getUseResponsePriorityValue());
        }
        return result;
    }

    public Map<String, String> getPrioritySlugLinks() {
        Map<String, String> prioritySlugLinks = new HashMap<>();
        List<String> prioritiesNames = getPrioritiesNames();

        for (String priorityName : prioritiesNames) {
            Map.Entry<String, String> entry = new AbstractMap.SimpleEntry<>(priorityName, "");
            PriorityLink priorityLink = priorityLinkManger.findByJiraPriorityName(priorityName);
            if (priorityLink != null) {
                entry.setValue(priorityLink.getUseResponsePriority().getUseResponsePrioritySlug());
            }
            prioritySlugLinks.put(entry.getKey(), entry.getValue());
        }
        return prioritySlugLinks;
    }

}
