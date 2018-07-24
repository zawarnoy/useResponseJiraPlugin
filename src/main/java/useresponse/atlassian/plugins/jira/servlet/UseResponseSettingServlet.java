package useresponse.atlassian.plugins.jira.servlet;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.util.BuildUtils;
import com.atlassian.jira.util.JiraVelocityHelper;
import com.atlassian.jira.util.JiraVelocityUtils;
import com.atlassian.jira.util.velocity.VelocityRequestContext;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import useresponse.atlassian.plugins.jira.exception.ConnectionException;
import useresponse.atlassian.plugins.jira.manager.impl.PriorityLinkManagerImpl;
import useresponse.atlassian.plugins.jira.manager.impl.StatusesLinkManagerImpl;
import useresponse.atlassian.plugins.jira.manager.impl.URPriorityManagerImpl;
import useresponse.atlassian.plugins.jira.model.*;
import useresponse.atlassian.plugins.jira.service.PrioritiesService;
import useresponse.atlassian.plugins.jira.service.SettingsService;
import useresponse.atlassian.plugins.jira.service.StatusesService;
import useresponse.atlassian.plugins.jira.settings.PluginSettings;
import useresponse.atlassian.plugins.jira.settings.PluginSettingsImpl;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import com.atlassian.jira.config.DefaultStatusManager;
import com.atlassian.jira.config.DefaultPriorityManager;
import com.atlassian.activeobjects.external.ActiveObjects;
import useresponse.atlassian.plugins.jira.storage.ConstStorage;


@Scanned
public class UseResponseSettingServlet extends HttpServlet {

    private static String SETTINGS_TEMPLATE = "/templates/ur_connection_settings_template.vm";
    private static String LINK_TEMPLATE = "/templates/ur_link_settings_template.vm";

    private final UserManager userManager;
    private final LoginUriProvider loginUriProvider;
    private final TemplateRenderer templateRenderer;
    private final PluginSettingsFactory pluginSettingsFactory;
    private final ActiveObjects ao;

    @Autowired
    private PriorityLinkManagerImpl priorityLinkManager;
    @Autowired
    private URPriorityManagerImpl urPriorityManager;
    @Autowired
    private StatusesLinkManagerImpl linkManager;
    @Autowired
    private ApplicationProperties applicationProperties;

    @Inject
    public UseResponseSettingServlet(@ComponentImport UserManager userManager,
                                     @ComponentImport LoginUriProvider loginUriProvider,
                                     @ComponentImport TemplateRenderer templateRenderer,
                                     @ComponentImport PluginSettingsFactory pluginSettignsFactory,
                                     @ComponentImport ActiveObjects ao
    ) {
        this.userManager = userManager;
        this.loginUriProvider = loginUriProvider;
        this.templateRenderer = templateRenderer;
        this.pluginSettingsFactory = pluginSettignsFactory;
        this.ao = ao;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SettingsService settingsService = new SettingsService(userManager, loginUriProvider, pluginSettingsFactory);
        if (!settingsService.checkIsAdmin(userManager.getRemoteUserKey())) {
            settingsService.redirectToLogin(request, response);
            return;
        }

        PrioritiesService prioritiesService = new PrioritiesService(ComponentAccessor.getComponent(DefaultPriorityManager.class), priorityLinkManager, urPriorityManager);
        StatusesService statusesService = new StatusesService(ComponentAccessor.getComponent(DefaultStatusManager.class), linkManager);
        PluginSettings pluginSettings = new PluginSettingsImpl(pluginSettingsFactory);

        migrate();
        addURPriorities();

        Map<String, Object> context = new HashMap<String, Object>();
        HashMap<String, String> statuses = null;
        try {
            statuses = settingsService.getUseResponseStatuses(pluginSettings);
        } catch (Exception e) {
            e.printStackTrace();
        }

        context.put("domain", pluginSettings.getUseResponseDomain() == null ? "" : pluginSettings.getUseResponseDomain());
        context.put("apiKey", pluginSettings.getUseResponseApiKey() == null ? "" : pluginSettings.getUseResponseApiKey());
        context.put("statusSlugLinks", statusesService.getStatusSlugLinks());
        context.put("prioritySlugLinks", prioritiesService.getPrioritySlugLinks());
        context.put("useResponsePriorities", prioritiesService.getUseResponsePriorities());
        context.put("baseUrl", applicationProperties.getBaseUrl(UrlMode.ABSOLUTE));
        context.put("useResponseStatuses", statuses);
        context.put("autosending", pluginSettings.getAutosendingFlag() != null && Boolean.parseBoolean(pluginSettings.getAutosendingFlag()));

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

        PrioritiesService prioritiesService = new PrioritiesService(ComponentAccessor.getComponent(DefaultPriorityManager.class), priorityLinkManager, urPriorityManager);
        StatusesService statusesService = new StatusesService(ComponentAccessor.getComponent(DefaultStatusManager.class), linkManager);
        PluginSettings pluginSettings = new PluginSettingsImpl(pluginSettingsFactory);

        HashMap<String, Object> map = new HashMap<>();
        HashMap<String, Object> context = new HashMap<>();

        try {
            setConnectionParameters(request, settingsService);
            setStatuses(request);
            setPriorities(request);


            context.put("statusSlugLinks", statusesService.getStatusSlugLinks());
            context.put("prioritySlugLinks", prioritiesService.getPrioritySlugLinks());
            context.put("useResponsePriorities", prioritiesService.getUseResponsePriorities());
            context.put("baseUrl", applicationProperties.getBaseUrl(UrlMode.ABSOLUTE));
            context.put("useResponseStatuses", settingsService.getUseResponseStatuses(pluginSettings));

            Writer writer = new StringWriter();

            templateRenderer.render(LINK_TEMPLATE, context, writer);



            map.put("status", "success");
            map.put("linkTemplate", writer.toString());  //RENDERED TEMPLATE
        } catch (Exception e) {
            map.put("status", "error");
            map.put("message", e.getMessage());
        }

        String responseBody = (new Gson()).toJson(map);

        response.getWriter().write(responseBody);
//        response.sendRedirect("ursettings");
    }

    private void migrate() {
        ao.migrate(StatusesLink.class);
        ao.migrate(CommentLink.class);
        ao.migrate(UseResponseObject.class);
        ao.migrate(URPriority.class);
        ao.migrate(PriorityLink.class);
        ao.migrate(IssueFileLink.class);
    }

    private void addURPriorities() {
        for (Map.Entry<String, String> entry : ConstStorage.UR_PRIORITIES.entrySet()) {
            urPriorityManager.findOrAdd(entry.getKey(), entry.getValue());
        }
    }

    private void setStatuses(HttpServletRequest request) {
        StatusesService statusesService = new StatusesService(ComponentAccessor.getComponent(DefaultStatusManager.class), linkManager);

        for (String statusName : statusesService.getStatusesNames()) {
            linkManager.editOrAdd(statusName, request.getParameter(statusName + "Status"));
        }
    }

    private void setPriorities(HttpServletRequest request) {
        PrioritiesService prioritiesService = new PrioritiesService(ComponentAccessor.getComponent(DefaultPriorityManager.class), priorityLinkManager, urPriorityManager);

        for (String priorityName : prioritiesService.getPrioritiesNames()) {
            URPriority priority = urPriorityManager.findBySlug(request.getParameter(priorityName + "Priority"));
            if (priority != null)
                priorityLinkManager.editUseResponsePriority(priorityName, priority);
        }
    }

    private void setConnectionParameters(HttpServletRequest request, SettingsService settingsService) throws ConnectionException {
        String domain = request.getParameter("domain");
        String apiKey = request.getParameter("apiKey");
        String autosending = request.getParameter("autosending");

        if (!SettingsService.testURConnection(domain, apiKey))
            throw (new ConnectionException("Wrong domain/apiKey"));
        settingsService.setURParameters(domain, apiKey, autosending);
    }

}