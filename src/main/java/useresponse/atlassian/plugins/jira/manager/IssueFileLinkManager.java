package useresponse.atlassian.plugins.jira.manager;

import com.atlassian.activeobjects.tx.Transactional;
import useresponse.atlassian.plugins.jira.model.IssueFileLink;

import java.util.List;


@Transactional
public interface IssueFileLinkManager {
    List<IssueFileLink> findByJiraIssueId(int issueId);
    IssueFileLink add(int issueId, String filename);
    List<IssueFileLink> all();
    IssueFileLink find(int issueId, String filename);
}
