package useresponse.atlassian.plugins.jira.manager;

import com.atlassian.activeobjects.tx.Transactional;
import useresponse.atlassian.plugins.jira.model.PriorityLink;

import java.util.List;


@Transactional
public interface PriorityLinkManager {
    PriorityLink findByJiraPriorityName(String jiraPriorityName);
    PriorityLink findOrAdd(String jiraPriorityName, String useResponsePriorityName);
    PriorityLink editUseResponsePriority(String jiraPriorityName, String useResponsePriorityName);
    List<PriorityLink> all();
}
