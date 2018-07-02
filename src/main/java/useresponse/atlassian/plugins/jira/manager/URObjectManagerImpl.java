package useresponse.atlassian.plugins.jira.manager;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import useresponse.atlassian.plugins.jira.model.URObject;

import javax.inject.Named;
import java.util.List;

@Scanned
@Named
public class URObjectManagerImpl implements URObjectManager {



    @Override
    public URObject add(int useResponseId, int jiraId) {
        return null;
    }

    @Override
    public URObject findByUseResponseId(int useResponseId) {
        return null;
    }

    @Override
    public URObject findByJiraId(int jiraId) {
        return null;
    }

    @Override
    public List<URObject> all() {
        return null;
    }
}
