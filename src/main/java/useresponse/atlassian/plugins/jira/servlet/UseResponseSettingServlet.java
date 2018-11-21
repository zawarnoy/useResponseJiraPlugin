package useresponse.atlassian.plugins.jira.servlet;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import useresponse.atlassian.plugins.jira.action.Action;
import useresponse.atlassian.plugins.jira.action.servlet.SettingsSendAction;
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
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.atlassian.jira.config.DefaultStatusManager;
import com.atlassian.jira.config.DefaultPriorityManager;
import com.atlassian.activeobjects.external.ActiveObjects;
import useresponse.atlassian.plugins.jira.storage.Storage;


@Scanned
public class UseResponseSettingServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(UseResponseSettingServlet.class);


    private static final String SETTINGS_TEMPLATE = "/templates/ur_connection_settings_template.vm";
    private static final String LINK_TEMPLATE = "/templates/ur_link_settings_template.vm";

    private static final String SUCCESSFULL_CONNECTION_STRING = "Your connection is successful!";
    private static final String SETTINGS_ARE_CHANCHED_STRING = "Settings are changed!";

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

        prepareDB();

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
            Map<Object, Object> result = setParameters(request, settingsService);

            this.sendSettings(result);

            context.put("statusSlugLinks", statusesService.getStatusSlugLinks());
            context.put("prioritySlugLinks", prioritiesService.getPrioritySlugLinks());
            context.put("useResponsePriorities", prioritiesService.getUseResponsePriorities());
            context.put("baseUrl", applicationProperties.getBaseUrl(UrlMode.ABSOLUTE));
            context.put("useResponseStatuses", settingsService.getUseResponseStatuses(pluginSettings));

            Writer writer = new StringWriter();
            templateRenderer.render(LINK_TEMPLATE, context, writer);


            map.put("linkTemplate", writer.toString());
            map.put("status", "success");
            map.put("message", request.getParameterMap().size() > 5 ? SETTINGS_ARE_CHANCHED_STRING : SUCCESSFULL_CONNECTION_STRING);
        } catch (
                Exception e)

        {
            map.put("status", "error");
            map.put("message", e.getMessage());
        }

        String responseBody = (new Gson()).toJson(map);

        response.getWriter().

                write(responseBody);

    }

    private void prepareDB() {
        migrate();
        addURPriorities();
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
        for (Map.Entry<String, String> entry : Storage.UR_PRIORITIES.entrySet()) {
            urPriorityManager.findOrAdd(entry.getKey(), entry.getValue());
        }
    }

    private Map<Object, Object> setParameters(HttpServletRequest request, SettingsService settingsService) throws ConnectionException {
        Map<Object, Object> result = new HashMap<>();

        setConnectionParameters(request, settingsService);
        Map<String, String> statuses = setStatuses(request);
        Map<String, String> priorities = setPriorities(request);
        String autosending = request.getParameter("autosending");
        if (autosending != null) {
            (new PluginSettingsImpl(pluginSettingsFactory)).setAutosendingFlag(autosending);
            result.put("autosending", Boolean.parseBoolean(autosending));
        }

        result.put("statuses", statuses);
        result.put("priorities", priorities);

        return result;
    }

    private Map<String, String> setStatuses(HttpServletRequest request) {
        StatusesService statusesService = new StatusesService(ComponentAccessor.getComponent(DefaultStatusManager.class), linkManager);

        Map<String, String> result = new HashMap<>();

        for (String statusName : statusesService.getStatusesNames()) {
            String statusValue = request.getParameter(statusName + "Status");
            linkManager.editOrAdd(statusName, statusValue);
            result.put(statusName, statusValue);
        }
        return result;
    }

    private Map<String, String> setPriorities(HttpServletRequest request) {
        PrioritiesService prioritiesService = new PrioritiesService(ComponentAccessor.getComponent(DefaultPriorityManager.class), priorityLinkManager, urPriorityManager);

        Map<String, String> result = new HashMap<>();

        for (String priorityName : prioritiesService.getPrioritiesNames()) {
            URPriority priority = urPriorityManager.findBySlug(request.getParameter(priorityName + "Priority"));
            if (priority != null){
                priorityLinkManager.editUseResponsePriority(priorityName, priority);
                result.put(priorityName, priority.getUseResponsePrioritySlug());
            }
        }
        return result;
    }

    private void setConnectionParameters(HttpServletRequest request, SettingsService settingsService) throws ConnectionException {
        String domain = request.getParameter("domain");
        String apiKey = request.getParameter("apiKey");

        if (!SettingsService.testURConnection(domain, apiKey))
            throw (new ConnectionException("Wrong domain/apiKey"));
        settingsService.setURParameters(domain, apiKey);
    }

    private void sendSettings(Map settings) {
        try {
            Action action = new SettingsSendAction(settings, new PluginSettingsImpl(pluginSettingsFactory));
            ExecutorService executor = Executors.newCachedThreadPool();
            Future<String> result = executor.submit(action);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}