package useresponse.atlassian.plugins.jira.manager;

import com.atlassian.activeobjects.tx.Transactional;
import useresponse.atlassian.plugins.jira.model.CommentLink;

import java.util.List;

@Transactional
public interface CommentLinkManager {
    CommentLink add(int useResponseCommentId, int jiraCommentId);
    CommentLink findByUseResponseId(int useResponseCommentId);
    CommentLink findByJiraId(int jiraCommentId);
    CommentLink findOrAdd(int useResponseCommentId, int jiraCommentId);
    List<CommentLink> all();
}
