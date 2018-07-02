package useresponse.atlassian.plugins.jira.manager;

import com.atlassian.activeobjects.tx.Transactional;
import useresponse.atlassian.plugins.jira.model.CommentsLink;
import useresponse.atlassian.plugins.jira.model.UseResponseObject;

@Transactional
public interface CommentsLinkManager {
    CommentsLink add(int useResponseCommentId, int jiraCommentId);
    CommentsLink findByUseResponseId(int useResponseCommentId);
    CommentsLink findByJiraId(int jiraCommentId);
}
