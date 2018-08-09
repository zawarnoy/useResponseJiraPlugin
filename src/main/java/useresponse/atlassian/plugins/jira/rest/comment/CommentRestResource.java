package useresponse.atlassian.plugins.jira.rest.comment;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.comments.MutableComment;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.UserUtils;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.issue.comments.CommentManager;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * A resource of message.
 */
@Path("/")
public class CommentRestResource {

    protected CommentManager commentManager;

    protected IssueManager issueManager;

    protected UserManager userManager;


    public CommentRestResource() {

    }

    @GET
    public Response message() {
        return Response.ok("here!").build();
    }

    @POST
    @Path("/")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createComment(
            @FormParam("author_email") String authorEmail,
            @FormParam("content") String content,
            @FormParam("issue_id") String issueId
    ) {

        issueManager = ComponentAccessor.getIssueManager();
        commentManager = ComponentAccessor.getCommentManager();
        userManager = ComponentAccessor.getUserManager();

        ApplicationUser user = UserUtils.getUserByEmail(authorEmail);
        try {
            throw new Exception("email: " + authorEmail);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (user == null) {
            // TODO GET FROM REQUEST ADDING/EDIT COMMENTS AND ISSUES FROM REST API RESPONSE
        }

        Issue issue = issueManager.getIssueObject(Long.valueOf(issueId));
        if (issue == null) {
//            return error
        }

        Comment comment = commentManager.create(issue, user, content, false);

        return Response.ok(comment).build();
    }

    @PUT
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateComment(@PathParam("id") String id) {

        commentManager = ComponentAccessor.getCommentManager();

        MutableComment comment = commentManager.getMutableComment(Long.valueOf(id));

        comment.setBody("");

        commentManager.update(comment, false);  // second param disables event


        return Response.ok("ok").build();
    }
}