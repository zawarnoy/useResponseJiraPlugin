package useresponse.atlassian.plugins.jira.service;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.priority.Priority;
import useresponse.atlassian.plugins.jira.manager.PriorityLinkManager;
import useresponse.atlassian.plugins.jira.manager.URPriorityManager;
import useresponse.atlassian.plugins.jira.model.PriorityLink;
import useresponse.atlassian.plugins.jira.model.URPriority;
import com.atlassian.jira.config.DefaultPriorityManager;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class PrioritiesService {

    private DefaultPriorityManager priorityManager;

    @Inject
    @Named("priorityLinkManager")
    private PriorityLinkManager priorityLinkManger;

    @Inject
    @Named("priorityManager")
    private URPriorityManager urPriorityManager;

    public PrioritiesService() {
        priorityManager = ComponentAccessor.getComponent(DefaultPriorityManager.class);
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

                String value =
                        priorityLink.getUseResponsePriority() == null ?
                        null :
                        priorityLink.getUseResponsePriority().getUseResponsePrioritySlug();

                entry.setValue(value);
            }
            prioritySlugLinks.put(entry.getKey(), entry.getValue());
        }

        return prioritySlugLinks;
    }

}
