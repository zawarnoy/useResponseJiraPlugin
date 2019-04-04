package useresponse.atlassian.plugins.jira.servlet;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.gson.Gson;
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
import com.atlassian.activeobjects.external.ActiveObjects;
import useresponse.atlassian.plugins.jira.storage.Storage;


@Scanned
public class UseResponseSettingServlet extends HttpServlet {

    private static final String SETTINGS_TEMPLATE = "/templates/ur_connection_settings_template.vm";
    private static final String LINK_TEMPLATE = "/templates/ur_link_settings_template.vm";

    private static final String SUCCESSFULL_CONNECTION_STRING = "Your connection is successful!";
    private static final String SETTINGS_ARE_CHANCHED_STRING = "Settings are changed!";

    private final UserManager userManager;
    private final TemplateRenderer templateRenderer;
    private final ActiveObjects ao;

    @Autowired
    private PriorityLinkManagerImpl priorityLinkManager;
    @Autowired
    private URPriorityManagerImpl urPriorityManager;
    @Autowired
    private StatusesLinkManagerImpl linkManager;
    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private PluginSettingsImpl pluginSettings;

    @Autowired
    private PrioritiesService prioritiesService;

    @Autowired
    private StatusesService statusesService;

    @Inject
    public UseResponseSettingServlet(@ComponentImport UserManager userManager,
                                     @ComponentImport TemplateRenderer templateRenderer,
                                     @ComponentImport ActiveObjects ao
    ) {
        this.userManager = userManager;
        this.templateRenderer = templateRenderer;
        this.ao = ao;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if (!settingsService.checkIsAdmin(userManager.getRemoteUserKey())) {
            settingsService.redirectToLogin(request, response);
            return;
        }

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

        if (!settingsService.checkIsAdmin(userManager.getRemoteUserKey())) {
            settingsService.redirectToLogin(request, response);
            return;
        }

        prepareDB();

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
            pluginSettings.setAutosendingFlag(autosending);
            result.put("autosending", Boolean.parseBoolean(autosending));
        }

        result.put("statuses", statuses);
        result.put("priorities", priorities);

        return result;
    }

    private Map<String, String> setStatuses(HttpServletRequest request) {

        Map<String, String> result = new HashMap<>();

        for (String statusName : statusesService.getStatusesNames()) {
            String statusValue = request.getParameter(statusName + "Status");
            linkManager.editOrAdd(statusName, statusValue);
            result.put(statusName, statusValue);
        }
        return result;
    }

    private Map<String, String> setPriorities(HttpServletRequest request) {

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

        if (!settingsService.testURConnection(domain, apiKey))
            throw (new ConnectionException("Wrong domain/apiKey"));
        settingsService.setURParameters(domain, apiKey);
    }

    private void sendSettings(Map settings) {
        try {
            Action action = new SettingsSendAction(settings, pluginSettings);
            ExecutorService executor = Executors.newCachedThreadPool();
            Future<String> result = executor.submit(action);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}