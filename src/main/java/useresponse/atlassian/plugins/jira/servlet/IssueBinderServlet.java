package useresponse.atlassian.plugins.jira.servlet;

import com.atlassian.jira.issue.AttachmentManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.RendererManager;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.security.xsrf.RequiresXsrfCheck;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import useresponse.atlassian.plugins.jira.action.Action;
import useresponse.atlassian.plugins.jira.action.listener.comment.CreateCommentAction;
import useresponse.atlassian.plugins.jira.action.listener.issue.CreateIssueAction;
import useresponse.atlassian.plugins.jira.action.listener.issue.UpdateIssueAction;
import useresponse.atlassian.plugins.jira.manager.impl.*;
import useresponse.atlassian.plugins.jira.model.*;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
//import useresponse.atlassian.plugins.jira.service.IssueActionService;
import useresponse.atlassian.plugins.jira.service.SettingsService;


@Scanned
public class IssueBinderServlet extends HttpServlet {

    private final UserManager userManager;
    private final LoginUriProvider loginUriProvider;
    private final PluginSettingsFactory pluginSettingsFactory;
    private final AttachmentManager attachmentManager;
    private final IssueManager issueManager;
    private final CommentManager commentManager;
    private final RendererManager rendererManager;

    @Autowired
    private PriorityLinkManagerImpl priorityLinkManager;
    @Autowired
    private CommentLinkManagerImpl commentLinkManager;
    @Autowired
    private UseResponseObjectManagerImpl useResponseObjectManager;
    @Autowired
    private StatusesLinkManagerImpl statusesLinkManager;
    @Autowired
    private IssueFileLinkManagerImpl issueFileLinkManager;


    @Inject
    public IssueBinderServlet(@ComponentImport UserManager userManager,
                              @ComponentImport LoginUriProvider loginUriProvider,
                              @ComponentImport PluginSettingsFactory pluginSettignsFactory,
                              @ComponentImport AttachmentManager attachmentManager,
                              @ComponentImport IssueManager issueManager,
                              @ComponentImport CommentManager commentManager,
                              @ComponentImport RendererManager rendererManager) {
        this.userManager = userManager;
        this.loginUriProvider = loginUriProvider;
        this.pluginSettingsFactory = pluginSettignsFactory;
        this.attachmentManager = attachmentManager;
        this.issueManager = issueManager;
        this.commentManager = commentManager;
        this.rendererManager = rendererManager;
    }

    @Override
    @RequiresXsrfCheck
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        SettingsService settingsService = new SettingsService(userManager, loginUriProvider, pluginSettingsFactory);

        if (userManager.getRemoteUser() == null) {
            settingsService.redirectToLogin(req, resp);
        }


        String jira_id = (req.getParameter("issue_id"));
        UseResponseObject useResponseObject = useResponseObjectManager.findByJiraId(Integer.valueOf(jira_id));

        Issue issue = issueManager.getIssueObject(Long.valueOf(jira_id));

        executeMoving(useResponseObject, issue);

        resp.sendRedirect("projects/" + issue.getProjectObject().getOriginalKey() + "/issues/" + issue.getKey());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    private void executeMoving(UseResponseObject useResponseObject, Issue issue) {
        Action action = null;

        try {
            if (useResponseObject == null) {
                action = new CreateIssueAction(issue, useResponseObjectManager, rendererManager, priorityLinkManager, pluginSettingsFactory, attachmentManager, issueFileLinkManager);
            } else {
                action = new UpdateIssueAction(issue, useResponseObjectManager, rendererManager, priorityLinkManager, pluginSettingsFactory, attachmentManager, issueFileLinkManager, statusesLinkManager);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (action != null) {
            (new Thread(action, "Issue binder thread")).start();
        }

        for (Comment comment : commentManager.getComments(issue)) {
            try {
                action = new CreateCommentAction(comment, commentLinkManager, useResponseObjectManager, pluginSettingsFactory);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (action != null) {
                (new Thread(action, "Issue binder thread")).start();
            }
        }
    }

}