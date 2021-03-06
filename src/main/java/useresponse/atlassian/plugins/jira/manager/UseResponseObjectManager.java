package useresponse.atlassian.plugins.jira.manager;

import com.atlassian.activeobjects.tx.Transactional;
import useresponse.atlassian.plugins.jira.model.UseResponseObject;
import java.util.List;

@Transactional
public interface UseResponseObjectManager {

    UseResponseObject add(int useResponseId, int jiraId, String type, boolean sync);

    UseResponseObject findOrAdd(int useResponseId, int jiraId, String type, boolean sync);

    UseResponseObject findByUseResponseId(int useResponseId);

    UseResponseObject findByJiraId(int jiraId);

    List<UseResponseObject> all();

    UseResponseObject changeAutosendingFlag(int jiraId, boolean autosendingFlag);

    void delete(UseResponseObject object);

    void delete(int jiraId, int useResponseId);
}
