package useresponse.atlassian.plugins.jira.manager;
import com.atlassian.activeobjects.tx.Transactional;
import org.springframework.stereotype.Component;
import useresponse.atlassian.plugins.jira.model.UseResponseObject;

import java.util.List;

@Transactional
public interface UseResponseObjectManager {

    UseResponseObject add(int useResponseId, int jiraId);
    UseResponseObject findByUseResponseId(int useResponseId);
    UseResponseObject findByJiraId(int jiraId);
    List<UseResponseObject> all();
}
