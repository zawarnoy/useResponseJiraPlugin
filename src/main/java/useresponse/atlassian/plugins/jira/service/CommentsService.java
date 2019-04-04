package useresponse.atlassian.plugins.jira.service;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;
import useresponse.atlassian.plugins.jira.manager.CommentLinkManager;
import useresponse.atlassian.plugins.jira.model.CommentLink;
import useresponse.atlassian.plugins.jira.set.linked.LinkedSet;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

public class CommentsService {

    @Inject
    @Named("commentLinkManager")
    private CommentLinkManager commentLinkManager;

    public Integer getDeletedCommentId(Issue issue) {
        List<Integer> arrayResult = getDeletedCommentsId(issue);
        return arrayResult.size() > 0 ? arrayResult.get(0) : null;
    }

    public List<Integer> getDeletedCommentsId(Issue issue) {
        List<CommentLink> comments = commentLinkManager.findByIssueId(issue.getId().intValue());
        List<Comment> remainingComments = ComponentAccessor.getCommentManager().getComments(issue);

        List<Integer> result = new ArrayList<>();

        LinkedSet<CommentLink> commentLinksSet = new LinkedSet<>(comments);

        for (CommentLink link : commentLinksSet) {
            if (isInCommentsList(remainingComments, link.getJiraCommentId())) {
                commentLinksSet.remove(link);
            }
        }

        for (CommentLink link : commentLinksSet) {
            result.add(link.getUseResponseCommentId());
        }
        return result;
    }

    private boolean isInCommentsList(List<Comment> comments, int wantedCommentId) {
        for (Comment comment : comments) {
            if (comment.getId().intValue() == wantedCommentId) {
                return true;
            }
        }
        return false;
    }
}
