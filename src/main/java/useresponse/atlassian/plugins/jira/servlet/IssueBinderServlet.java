package useresponse.atlassian.plugins.jira.servlet;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.issue.*;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserManager;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import useresponse.atlassian.plugins.jira.exception.IssueNotExistException;
import useresponse.atlassian.plugins.jira.manager.impl.*;
import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import useresponse.atlassian.plugins.jira.request.Request;
import useresponse.atlassian.plugins.jira.service.SettingsService;
import useresponse.atlassian.plugins.jira.service.handler.Handler;
import useresponse.atlassian.plugins.jira.service.handler.servlet.binder.IssueBinderServletRequestHandler;
import useresponse.atlassian.plugins.jira.service.request.RequestBuilder;
import useresponse.atlassian.plugins.jira.service.request.parameters.builder.CommentRequestBuilder;
import useresponse.atlassian.plugins.jira.service.request.parameters.builder.CommentRequestParametersBuilder;
import useresponse.atlassian.plugins.jira.service.request.parameters.builder.IssueRequestBuilder;
import useresponse.atlassian.plugins.jira.service.request.parameters.builder.IssueRequestParametersBuilder;
import useresponse.atlassian.plugins.jira.settings.PluginSettings;
import useresponse.atlassian.plugins.jira.settings.PluginSettingsImpl;
import useresponse.atlassian.plugins.jira.storage.Storage;

@Scanned
public class IssueBinderServlet extends HttpServlet {

    private final UserManager userManager;
    private final LoginUriProvider loginUriProvider;
    private final PluginSettingsFactory pluginSettingsFactory;
    private final AttachmentManager attachmentManager;
    private final IssueManager issueManager;

    @Autowired
    private CommentLinkManagerImpl commentLinkManager;
    @Autowired
    private UseResponseObjectManagerImpl useResponseObjectManager;

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
        this.gson = new Gson();
    }

    @Autowired
    CommentRequestParametersBuilder commentRequestParametersBuilder;

    @Autowired
    IssueRequestParametersBuilder issueRequestParametersBuilder;

    @Autowired
    CommentRequestBuilder commentRequestBuilder;

    @Autowired
    IssueRequestBuilder issueRequestBuilder;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        SettingsService settingsService = new SettingsService(userManager, loginUriProvider, pluginSettingsFactory);

        if (userManager.getRemoteUser() == null) {
            settingsService.redirectToLogin(req, resp);
        }

        if (!SettingsService.testURConnection(pluginSettingsFactory)) {
            resp.getWriter().write("Conn");
        }

        Storage.isFromBinder = true;

        RequestBuilder requestBuilder = new RequestBuilder(issueRequestBuilder, commentRequestBuilder);

        String responseForUser;

        Issue issue = null;

        try {
            int issueId = Integer.parseInt(req.getParameter("issue_id"));
            issue = issueManager.getIssueObject(Long.valueOf(issueId));
        } catch (NumberFormatException e) {
            String issueKey = (String) req.getParameter("issue_id");
            issue = issueManager.getIssueByCurrentKey(issueKey);
        }

        int syncStatus = Integer.parseInt(req.getParameter("sync"));

        try {
            if (issue == null) {
                throw new IssueNotExistException("Can't find issue");
            }

            Request request = requestBuilder.build(issue, syncStatus);
            PluginSettings pluginSettings = new PluginSettingsImpl(pluginSettingsFactory);

            request.setUrl(pluginSettings.getUseResponseDomain() + Storage.API_STRING + Storage.JIRA_DATA_HANDLER_ROUTE + "?apiKey=" + pluginSettings.getUseResponseApiKey());
            String response = request.sendRequest();

            Handler<String, String> handler = new IssueBinderServletRequestHandler(useResponseObjectManager, commentLinkManager);
            responseForUser = handler.handle(response);

        } catch (Exception e) {
            e.printStackTrace();
            responseForUser = handleException(e);
        }

        if (req.getHeader("x-requested-with") == null) {
            resp.sendRedirect(ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL) + "/browse/" + issue.getKey());
        }

        if (responseForUser != null) {
            resp.getWriter().write(responseForUser);
        }
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
