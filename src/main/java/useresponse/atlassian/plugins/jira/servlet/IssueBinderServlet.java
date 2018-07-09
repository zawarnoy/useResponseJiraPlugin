package useresponse.atlassian.plugins.jira.servlet;

import com.atlassian.jira.issue.AttachmentManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.RendererManager;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import useresponse.atlassian.plugins.jira.manager.impl.*;
import useresponse.atlassian.plugins.jira.model.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import com.atlassian.activeobjects.external.ActiveObjects;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.atlassian.jira.config.DefaultStatusManager;
import com.atlassian.jira.config.DefaultPriorityManager;
import useresponse.atlassian.plugins.jira.service.IssueActionService;
import useresponse.atlassian.plugins.jira.service.SettingsService;


@Scanned
public class IssueBinderServlet extends HttpServlet {

    @ComponentImport
    private final UserManager userManager;
    @ComponentImport
    private final LoginUriProvider loginUriProvider;
    @ComponentImport
    private final PluginSettingsFactory pluginSettingsFactory;
    @ComponentImport
    private AttachmentManager attachmentManager;
    @ComponentImport
    private final IssueManager issueManager;
    @ComponentImport
    private final CommentManager commentManager;
    @ComponentImport
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
    public IssueBinderServlet(UserManager userManager,
                              LoginUriProvider loginUriProvider,
                              PluginSettingsFactory pluginSettignsFactory,
                              AttachmentManager attachmentManager,
                              IssueManager issueManager,
                              CommentManager commentManager,
                              RendererManager rendererManager) {
        this.userManager = userManager;
        this.loginUriProvider = loginUriProvider;
        this.pluginSettingsFactory = pluginSettignsFactory;
        this.attachmentManager = attachmentManager;
        this.issueManager = issueManager;
        this.commentManager = commentManager;
        this.rendererManager = rendererManager;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        SettingsService settingsService = new SettingsService(userManager, loginUriProvider, pluginSettingsFactory);

        if (userManager.getRemoteUser() == null) {
            settingsService.redirectToLogin(req, resp);
        }

        IssueActionService issueActionService = new IssueActionService(
                pluginSettingsFactory,
                commentLinkManager,
                useResponseObjectManager,
                statusesLinkManager,
                priorityLinkManager,
                attachmentManager,
                rendererManager,
                issueFileLinkManager);


        String jira_id = (req.getParameter("issue_id"));
        UseResponseObject useResponseObject;
        useResponseObject = useResponseObjectManager.findByJiraId(Integer.valueOf(jira_id));

        Issue issue = issueManager.getIssueObject(Long.valueOf(jira_id));

        try {
            if (useResponseObject == null) {
                issueActionService.createAction(issue);

            } else {
                issueActionService.updateAction(issue);
            }
        } catch (Exception ignored) {

        }

        for(Comment comment : commentManager.getComments(issue)) {
            try {
                issueActionService.createCommentAction(comment);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        resp.getWriter().write(issue.getKey());
        resp.sendRedirect( "projects/" + issue.getProjectObject().getOriginalKey() + "/issues/" + issue.getKey());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

}