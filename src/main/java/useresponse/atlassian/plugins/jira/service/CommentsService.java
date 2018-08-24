package useresponse.atlassian.plugins.jira.service;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import useresponse.atlassian.plugins.jira.manager.CommentLinkManager;
import useresponse.atlassian.plugins.jira.model.CommentLink;
import useresponse.atlassian.plugins.jira.set.linked.LinkedSet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CommentsService {

    private static Logger log = LoggerFactory.getLogger(CommentsService.class);

    public static Integer getDeletedCommentId(Issue issue, CommentLinkManager commentLinkManager) {
        List<CommentLink> comments = commentLinkManager.findByIssueId(issue.getId().intValue());
        List<Comment> remainingComments = ComponentAccessor.getCommentManager().getComments(issue);

        LinkedSet<CommentLink> commentLinksSet = new LinkedSet<>(comments);

        for (CommentLink link : commentLinksSet) {
            if (isInCommentsList(remainingComments, link.getJiraCommentId())) {
                commentLinksSet.remove(link);
            }
        }

        Iterator<CommentLink> iterator = commentLinksSet.iterator();
        if (iterator.hasNext()) {
            return iterator.next().getUseResponseCommentId();
        }
        return null;
    }

    private static boolean isInCommentsList(List<Comment> comments, int wantedCommentId) {
        for (Comment comment : comments) {
            if (comment.getId().intValue() == wantedCommentId) {
                return true;
            }
        }
        return false;
    }

    private static List<Integer> getDeletedCommentsId() {
        ArrayList result = new ArrayList();
        return result;
    }
}
