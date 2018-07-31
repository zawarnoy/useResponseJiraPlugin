package useresponse.atlassian.plugins.jira.servlet;

import com.atlassian.jira.issue.AttachmentManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.RendererManager;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.security.xsrf.RequiresXsrfCheck;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import useresponse.atlassian.plugins.jira.exception.InvalidResponseException;
import useresponse.atlassian.plugins.jira.exception.UndefinedUrl;
import useresponse.atlassian.plugins.jira.manager.impl.*;
import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import useresponse.atlassian.plugins.jira.request.Request;
import useresponse.atlassian.plugins.jira.service.SettingsService;
import useresponse.atlassian.plugins.jira.service.request.RequestBuilder;
import useresponse.atlassian.plugins.jira.service.request.parameters.builder.CommentRequestBuilder;
import useresponse.atlassian.plugins.jira.service.request.parameters.builder.CommentRequestParametersBuilder;
import useresponse.atlassian.plugins.jira.service.request.parameters.builder.IssueRequestBuilder;
import useresponse.atlassian.plugins.jira.service.request.parameters.builder.IssueRequestParametersBuilder;
import useresponse.atlassian.plugins.jira.settings.PluginSettings;
import useresponse.atlassian.plugins.jira.settings.PluginSettingsImpl;
import useresponse.atlassian.plugins.jira.storage.ConstStorage;

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

        if (!SettingsService.testURConnection(pluginSettingsFactory)) {
            resp.getWriter().write("Conn");
        }

//        HashMap<String, String> responseMap = new HashMap<>();
//        String jiraId = (req.getParameter("issue_id"));
//        UseResponseObject useResponseObject = useResponseObjectManager.findByJiraId(Integer.valueOf(jiraId));
//        Issue issue = issueManager.getIssueObject(Long.valueOf(jiraId));
//
//        if(!SettingsService.testURConnection(pluginSettingsFactory)) {
//            responseMap.put("status", "error");
//            responseMap.put("message", "Can't connect to UseResponse");
//            responseMap.put("slug", "Check your Domain/ApiKey settings");
//        } else {
//            LinkedSet<Future<String>> futureList = executeMoving(useResponseObject, issue);
//            Handler<LinkedSet<Future<String>>, IssueBinderResponseData> handler = new IssueBinderServletHandler();
//            IssueBinderResponseData responseData = handler.handle(futureList);
//            responseMap.put("message", responseData.message);
//            responseMap.put("data", responseData.data);
//            responseMap.put("status", "success");
//        }
//        resp.getWriter().write((new Gson()).toJson(responseMap));

        CommentRequestParametersBuilder commentRequestParametersBuilder = new CommentRequestParametersBuilder(
                commentLinkManager,
                useResponseObjectManager);

        IssueRequestParametersBuilder issueRequestParametersBuilder = new IssueRequestParametersBuilder(
                rendererManager,
                priorityLinkManager,
                useResponseObjectManager,
                attachmentManager,
                issueFileLinkManager,
                pluginSettingsFactory,
                statusesLinkManager
        );

        CommentRequestBuilder commentRequestBuilder = new CommentRequestBuilder(commentRequestParametersBuilder, commentLinkManager);
        IssueRequestBuilder issueRequestBuilder = new IssueRequestBuilder(issueRequestParametersBuilder, useResponseObjectManager);

        RequestBuilder requestBuilder = new RequestBuilder(issueRequestBuilder, commentRequestBuilder, commentManager);


        Issue issue = issueManager.getIssueObject(Long.valueOf(req.getParameter("issue_id")));

        Request request = requestBuilder.build(issue);
        String response = null;
        PluginSettings pluginSettings =  new PluginSettingsImpl(pluginSettingsFactory);

        request.setUrl(pluginSettings.getUseResponseDomain() + ConstStorage.API_STRING + ConstStorage.JIRA_DATA_HANDLER_ROUTE + "?apiKey=" + pluginSettings.getUseResponseApiKey());

        try {
            response = request.sendRequest();
        } catch (InvalidResponseException | NoSuchAlgorithmException | KeyManagementException | UndefinedUrl e) {
            e.printStackTrace();
        }

        if (response != null) {
            resp.getWriter().write(response);
        }

    }

//    private LinkedSet<Future<String>> executeMoving(UseResponseObject useResponseObject, Issue issue) {
//        Action action;
//
//        ListenerActionFactory issueActionFactory = new IssueActionFactory(
//                issue,
//                useResponseObjectManager,
//                rendererManager,
//                priorityLinkManager,
//                pluginSettingsFactory,
//                issueFileLinkManager,
//                statusesLinkManager,
//                new IssueRequestBuilder(
//                        new IssueRequestParametersBuilder(rendererManager, priorityLinkManager, useResponseObjectManager, attachmentManager, issueFileLinkManager, pluginSettingsFactory, statusesLinkManager),
//                        useResponseObjectManager
//                )
//
//        );
//
//        ListenerActionFactory commentActionFactory = new CommentActionFactory(
//                null,
//                useResponseObjectManager,
//                pluginSettingsFactory,
//                commentLinkManager,
//                new CommentRequestBuilder(
//                        new CommentRequestParametersBuilder(commentLinkManager, useResponseObjectManager),
//                        commentLinkManager
//                )
//        );
//
//        ExecutorService executor = Executors.newFixedThreadPool(9);
//        LinkedSet<Future<String>> futureList = new LinkedSet<>();
//
//        if (useResponseObject == null) {
//            action = issueActionFactory.createAction(CreateIssueAction.class);
//        } else {
//            action = issueActionFactory.createAction(UpdateIssueAction.class);
//        }
//
//        if (action != null) {
//            futureList.add(executor.submit(action));
//        }
//
//        for (Comment comment : commentManager.getComments(issue)) {
//            CommentLink commentLink = commentLinkManager.findByJiraId(comment.getId().intValue());
//            commentActionFactory.setEntity(comment);
//            if (commentLink == null) {
//                action = commentActionFactory.createAction(CreateCommentAction.class);
//            } else {
//                action = commentActionFactory.createAction(UpdateCommentAction.class);
//            }
//
//            if (action != null) {
//                futureList.add(executor.submit(action));
//            }
//        }
//
//        return futureList;
//    }


}