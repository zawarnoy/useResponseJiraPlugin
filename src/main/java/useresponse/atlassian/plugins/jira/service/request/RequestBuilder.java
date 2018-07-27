package useresponse.atlassian.plugins.jira.service.request;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.comments.CommentManager;
import com.google.gson.Gson;
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
 * Build request for sending in UseResponse system
 *
 */

public class RequestBuilder {

    private CommentManager commentManager;
    private IssueRequestBuilder issueRequestBuilder;
    private CommentRequestBuilder commentRequestBuilder;

    public RequestBuilder(IssueRequestBuilder issueRequestBuilder, CommentRequestBuilder commentRequestBuilder, CommentManager commentManager) {
        this.issueRequestBuilder = issueRequestBuilder;
        this.commentRequestBuilder = commentRequestBuilder;
        this.commentManager = commentManager;
    }

    public Request build(Issue issue) throws IOException {
        Request request = new PostRequest();
        Map<Object, Object> requestMap = new HashMap<>();
        requestMap.put("issue", issueRequestBuilder.build(issue));

        List<Map<Object, Object>> commentsList = new ArrayList<>();
        for (Comment comment : commentManager.getComments(issue)) {
            commentsList.add(commentRequestBuilder.build(comment));
        }
        requestMap.put("comments", commentsList);

        request.addParameter(requestMap);
        return request;
    }
}
