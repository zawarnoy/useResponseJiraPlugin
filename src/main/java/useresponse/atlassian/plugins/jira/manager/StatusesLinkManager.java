package useresponse.atlassian.plugins.jira.manager;

import com.atlassian.activeobjects.tx.Transactional;
import useresponse.atlassian.plugins.jira.model.StatusesLink;

import java.util.List;

@Transactional
public interface StatusesLinkManager {

    StatusesLink findByJiraStatusName(String jiraStatusName);
    StatusesLink findOrAdd(String jiraStatusName, String useResponseStatusSlug);
    StatusesLink editUseResponseSlug(String jiraStatusName, String useResponseStatusSlug);
    List<StatusesLink> all();
    StatusesLink editOrAdd(String jiraStatusName, String useResponseStatusSlug);
}