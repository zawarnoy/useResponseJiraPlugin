package useresponse.atlassian.plugins.jira.servlet;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.issue.*;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.security.xsrf.RequiresXsrfCheck;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserManager;
import com.google.gson.Gson;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import useresponse.atlassian.plugins.jira.exception.ConnectionException;
import useresponse.atlassian.plugins.jira.exception.InvalidResponseException;
import useresponse.atlassian.plugins.jira.exception.IssueNotExistException;
import useresponse.atlassian.plugins.jira.exception.UndefinedUrlException;
import useresponse.atlassian.plugins.jira.manager.impl.*;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import useresponse.atlassian.plugins.jira.request.Request;
import useresponse.atlassian.plugins.jira.service.SettingsService;
import useresponse.atlassian.plugins.jira.service.handler.Handler;
import useresponse.atlassian.plugins.jira.service.handler.servlet.binder.RequestHandler;
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

    private final Gson gson;

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
        this.gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        SettingsService settingsService = new SettingsService(userManager, loginUriProvider, pluginSettingsFactory);

        if (userManager.getRemoteUser() == null) {
            settingsService.redirectToLogin(req, resp);
        }

        if (!SettingsService.testURConnection(pluginSettingsFactory)) {
            resp.getWriter().write("Conn");
        }

        CommentRequestParametersBuilder commentRequestParametersBuilder = new CommentRequestParametersBuilder(
                commentLinkManager,
                useResponseObjectManager
        );

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

        RequestBuilder requestBuilder = new RequestBuilder(issueRequestBuilder, commentRequestBuilder);

        String responseForUser;

        int issueId = Integer.parseInt(req.getParameter("issue_id"));
        Issue issue = issueManager.getIssueObject(Long.valueOf(issueId));

        String syncStatus;

        try {


            if (issue == null) {
                throw new IssueNotExistException("Can't find issue with id " + issueId);
            }

            Request request = requestBuilder.build(issue);
            PluginSettings pluginSettings = new PluginSettingsImpl(pluginSettingsFactory);

            request.setUrl(pluginSettings.getUseResponseDomain() + ConstStorage.API_STRING + ConstStorage.JIRA_DATA_HANDLER_ROUTE + "?apiKey=" + pluginSettings.getUseResponseApiKey());
            String response = request.sendRequest();

            Handler<String, String> handler = new RequestHandler(useResponseObjectManager, commentLinkManager);
            responseForUser = handler.handle(response);
            syncStatus = "1";

        } catch (
                InvalidResponseException |
                        NoSuchAlgorithmException |
                        KeyManagementException |
                        UndefinedUrlException |
                        IssueNotExistException |
                        ParseException |
                        UnknownHostException |
                        ConnectException e) {
            e.printStackTrace();
            responseForUser = handleException(e);
            syncStatus = "0";
        }

        if (req.getHeader("x-requested-with") == null) {
            resp.sendRedirect(ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL) + "/browse/" + issue.getKey() + "?sync_status=" + syncStatus);
        }

        if (responseForUser != null) {
            resp.getWriter().write(responseForUser);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String responseForUser = null;

        PluginSettings pluginSettings = new PluginSettingsImpl(pluginSettingsFactory);

        try {
            if (pluginSettings.getUseResponseDomain().equals(request.getParameter("apiKey"))) {
                throw new ConnectionException("Invalid apiKey!");
            }

            responseForUser = "kek";

            //Todo receive data from ur


        } catch (ConnectionException e) {
            responseForUser = handleException(e);
        }


        response.getWriter().write(responseForUser);

    }

    private String handleException(Exception e) {
        return gson.toJson(
                new HashMap<String, String>() {
                    {
                        put("status", "error");
                        put("message", e.getMessage());
                    }
                });
    }
}
