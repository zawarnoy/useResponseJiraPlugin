package useresponse.atlassian.plugins.jira.servlet;

import java.net.URI;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.status.Status;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import useresponse.atlassian.plugins.jira.request.GetRequest;
import useresponse.atlassian.plugins.jira.request.Request;
import useresponse.atlassian.plugins.jira.service.SettingsService;
import useresponse.atlassian.plugins.jira.service.StatusesService;
import useresponse.atlassian.plugins.jira.settings.PluginSettings;
import useresponse.atlassian.plugins.jira.settings.PluginSettingsImpl;
import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.plaf.basic.BasicScrollPaneUI;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@Scanned
public class UseResponseSettingServlet extends HttpServlet {

    private static String SETTINGS_TEMPLATE = "/templates/ur_settings_template.vm";

    @ComponentImport
    private final UserManager userManager;
    @ComponentImport
    private final LoginUriProvider loginUriProvider;
    @ComponentImport
    private final TemplateRenderer templateRenderer;
    @ComponentImport
    private final PluginSettingsFactory pluginSettingsFactory;

    @Inject
    public UseResponseSettingServlet(UserManager userManager, LoginUriProvider loginUriProvider, TemplateRenderer templateRenderer, PluginSettingsFactory pluginSettignsFactory) {
        this.userManager = userManager;
        this.loginUriProvider = loginUriProvider;
        this.templateRenderer = templateRenderer;
        this.pluginSettingsFactory = pluginSettignsFactory;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SettingsService settingsService = new SettingsService(userManager, loginUriProvider, pluginSettingsFactory);
        StatusesService statusesService = new StatusesService(ComponentAccessor.getComponent(DefaultStatusManager.class));


        if (!settingsService.checkIsAdmin(userManager.getRemoteUserKey())) {
            settingsService.redirectToLogin(request, response);
            return;
        }
        PluginSettings pluginSettings = new PluginSettingsImpl(pluginSettingsFactory);
        Map<String, Object> context = new HashMap<String, Object>();


        context.put("domain", pluginSettings.getUseResponseDomain() == null ? "" : pluginSettings.getUseResponseDomain());
        context.put("apiKey", pluginSettings.getUseResponseApiKey() == null ? "" : pluginSettings.getUseResponseApiKey());

        context.put("openStatus", pluginSettings.getUseResponseOpenStatus() == null ? "" : pluginSettings.getUseResponseOpenStatus());
        context.put("closedStatus", pluginSettings.getUseResponseClosedStatus() == null ? "" : pluginSettings.getUseResponseClosedStatus());
        context.put("doneStatus", pluginSettings.getUseResponseDoneStatus() == null ? "" : pluginSettings.getUseResponseDoneStatus());
        context.put("todoStatus", pluginSettings.getUseResponseToDoStatus() == null ? "" : pluginSettings.getUseResponseToDoStatus());
        context.put("inProgressStatus", pluginSettings.getUseResponseInProgressStatus() == null ? "" : pluginSettings.getUseResponseInProgressStatus());
        context.put("reopenedStatus", pluginSettings.getUseResponseReopenedStatus() == null ? "" : pluginSettings.getUseResponseReopenedStatus());
        context.put("resolvedStatus", pluginSettings.getUseResponseResolvedStatus() == null ? "" : pluginSettings.getUseResponseResolvedStatus());

        HashMap<String, String> statuses = null;
        try {
            statuses = settingsService.getUseResponseStatuses(pluginSettings);
        } catch (Exception e) {

        }

        context.put("useResponseStatuses", statuses);

        response.setContentType("text/html");
        templateRenderer.render(SETTINGS_TEMPLATE, context, response.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SettingsService settingsService = new SettingsService(userManager, loginUriProvider, pluginSettingsFactory);

        if (!settingsService.checkIsAdmin(userManager.getRemoteUserKey())) {
            settingsService.redirectToLogin(request, response);
            return;
        }

        String domain = request.getParameter("domain");
        String apiKey = request.getParameter("apiKey");

        try {
            if(!settingsService.testURConnection(domain, apiKey))
                return;
            settingsService.setURParameters(domain, apiKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        settingsService.setURStatuses(
                request.getParameter("openSelect"),
                request.getParameter("inProgressSelect"),
                request.getParameter("reopenedSelect"),
                request.getParameter("resolvedSelect"),
                request.getParameter("closedSelect"),
                request.getParameter("todoSelect"),
                request.getParameter("doneSelect")
        );
        response.sendRedirect("ursettings");
    }
}