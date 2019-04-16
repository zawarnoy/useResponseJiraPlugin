package useresponse.atlassian.plugins.jira.manager;

import com.atlassian.activeobjects.tx.Transactional;
import useresponse.atlassian.plugins.jira.model.PriorityLink;
import useresponse.atlassian.plugins.jira.model.URPriority;
import java.util.List;

@Transactional
public interface PriorityLinkManager {
    PriorityLink findByJiraPriorityName(String jiraPriorityName);
    PriorityLink findOrAdd(String jiraPriorityName, URPriority useResponsePriority);
    PriorityLink editUseResponsePriority(String jiraPriorityName, URPriority useResponsePriority);
    List<PriorityLink> all();
}
