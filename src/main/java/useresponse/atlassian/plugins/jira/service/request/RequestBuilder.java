package useresponse.atlassian.plugins.jira.service.request;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.comments.CommentManager;
import useresponse.atlassian.plugins.jira.request.PostRequest;
import useresponse.atlassian.plugins.jira.request.Request;
import useresponse.atlassian.plugins.jira.service.request.parameters.builder.CommentRequestBuilder;
import useresponse.atlassian.plugins.jira.service.request.parameters.builder.IssueRequestBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for building request for sending in UseResponse system
 */

public class RequestBuilder {

    private CommentManager commentManager;
    private IssueRequestBuilder issueRequestBuilder;
    private CommentRequestBuilder commentRequestBuilder;

    public RequestBuilder(IssueRequestBuilder issueRequestBuilder, CommentRequestBuilder commentRequestBuilder) {
        this.issueRequestBuilder = issueRequestBuilder;
        this.commentRequestBuilder = commentRequestBuilder;
        this.commentManager = ComponentAccessor.getCommentManager();
    }

    /**
     * @param issue
     * @return Request
     * @throws IOException Returns prepared request for sending in to UseResponse system
     */
    public Request build(Issue issue) throws IOException {
        return build(issue, 0);
    }

    public Request build(Issue issue, int syncStatus) throws IOException {
        Request request = new PostRequest();
        Map<Object, Object> requestMap = new HashMap<>();

        Map<Object, Object> issueMap = issueRequestBuilder.build(issue);
        issueMap.put("sync", syncStatus);

        requestMap.put("issue", issueMap);

        List<Map<Object, Object>> commentsList = new ArrayList<>();
        for (Comment comment : commentManager.getComments(issue)) {
            commentsList.add(commentRequestBuilder.build(comment, false));
        }

        commentsList.addAll(commentRequestBuilder.getDeletedComments(issue));

        requestMap.put("comments", commentsList);

        request.addParameter(requestMap);
        return request;
    }
}
