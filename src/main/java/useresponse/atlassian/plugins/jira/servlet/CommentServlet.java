package useresponse.atlassian.plugins.jira.servlet;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.issue.comments.MutableComment;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.gson.Gson;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommentServlet extends HttpServlet{

    private final UserManager userManager;

    public CommentServlet(@ComponentImport UserManager userManager) {
        this.userManager = userManager;
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ApplicationUser loggedUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
        if (loggedUser == null && userManager.isSystemAdmin(userManager.getRemoteUserKey())) {
            return;
        }

        Issue issue = null;
        String issueKey = req.getParameter("issue_key");

        if(issueKey != null) {
            issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey(issueKey);
        }

        ApplicationUser user = null;
        String username = req.getParameter("username");

        if(username != null) {
            user = ComponentAccessor.getUserManager().getUserByName(username);
        }

        if(user == null) {
            user = loggedUser;
        }

        String content = req.getParameter("content");

        ComponentAccessor.getCommentManager().create(issue, user, content, false);


    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ApplicationUser loggedUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
        if (loggedUser == null && userManager.isSystemAdmin(userManager.getRemoteUserKey())) {
            return;
        }


        InputStreamReader reader = new InputStreamReader(req.getInputStream());

        BufferedReader br = new BufferedReader(reader);

        String bufer;
        String data = "";

        while( (bufer = br.readLine()) != null ) {
            data = data + bufer;
        }



        resp.getWriter().write(data);

//        String commentId = req.getParameter("comment_id");
//        String commentBody = req.getParameter("comment_body");
//
//        if(commentId == null || commentBody == null) {
//            return;
//        }
//
//        CommentManager commentManager = ComponentAccessor.getCommentManager();
//
//        MutableComment comment = commentManager.getMutableComment(Long.valueOf(commentId));
//
//        comment.setBody(commentBody);
//
//        commentManager.update(comment, false);
//
//        resp.getWriter().write("{ \"status\" : \"success\" }");
    }
}