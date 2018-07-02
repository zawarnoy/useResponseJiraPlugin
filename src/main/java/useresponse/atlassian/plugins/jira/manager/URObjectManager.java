package useresponse.atlassian.plugins.jira.manager;
import com.atlassian.activeobjects.tx.Transactional;
import useresponse.atlassian.plugins.jira.model.URObject;

import java.util.List;

@Transactional
public interface URObjectManager {

    URObject add(int useResponseId, int jiraId);
    URObject findByUseResponseId(int useResponseId);
    URObject findByJiraId(int jiraId);
    List<URObject> all();
}
