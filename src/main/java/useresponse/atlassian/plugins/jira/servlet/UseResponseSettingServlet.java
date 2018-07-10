package useresponse.atlassian.plugins.jira.servlet;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import com.atlassian.webresource.api.assembler.WebResource;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import com.atlassian.jira.config.DefaultStatusManager;
import com.atlassian.jira.config.DefaultPriorityManager;
import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.plugin.webresource.WebResourceManagerImpl;


@Scanned
public class UseResponseSettingServlet extends HttpServlet {

    private static String SETTINGS_TEMPLATE = "/templates/ur_settings_template.vm";


    private final UserManager userManager;
    private final LoginUriProvider loginUriProvider;
    private final TemplateRenderer templateRenderer;
    private final PluginSettingsFactory pluginSettingsFactory;
    private final ActiveObjects ao;
//    private final WebResourceManager webResourceManager;

    @Autowired
    private PriorityLinkManagerImpl priorityLinkManager;
    @Autowired
    private URPriorityManagerImpl urPriorityManager;
    @Autowired
    private StatusesLinkManagerImpl linkManager;

    @Inject
    public UseResponseSettingServlet(@ComponentImport UserManager userManager,
                                     @ComponentImport LoginUriProvider loginUriProvider,
                                     @ComponentImport TemplateRenderer templateRenderer,
                                     @ComponentImport PluginSettingsFactory pluginSettignsFactory,
                                     @ComponentImport ActiveObjects ao
//                                     @ComponentImport WebResourceManager webResourceManager
                                     ) {
        this.userManager = userManager;
        this.loginUriProvider = loginUriProvider;
        this.templateRenderer = templateRenderer;
        this.pluginSettingsFactory = pluginSettignsFactory;
        this.ao = ao;
//        this.webResourceManager = webResourceManager;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SettingsService settingsService = new SettingsService(userManager, loginUriProvider, pluginSettingsFactory);
        if (!settingsService.checkIsAdmin(userManager.getRemoteUserKey())) {
            settingsService.redirectToLogin(request, response);
            return;
        }

        urPriorityManager.findOrAdd("low", "Low");
        urPriorityManager.findOrAdd("normal", "Normal");
        urPriorityManager.findOrAdd("high", "High");
        urPriorityManager.findOrAdd("urgent", "Urgent");

        ao.migrate(StatusesLink.class);
        ao.migrate(CommentLink.class);
        ao.migrate(UseResponseObject.class);
        ao.migrate(URPriority.class);
        ao.migrate(PriorityLink.class);
        ao.migrate(IssueFileLink.class);

        StatusesService statusesService = new StatusesService(ComponentAccessor.getComponent(DefaultStatusManager.class), linkManager);
        PrioritiesService prioritiesService = new PrioritiesService(ComponentAccessor.getComponent(DefaultPriorityManager.class), priorityLinkManager, urPriorityManager);


        PluginSettings pluginSettings = new PluginSettingsImpl(pluginSettingsFactory);
        Map<String, Object> context = new HashMap<String, Object>();


        context.put("domain", pluginSettings.getUseResponseDomain() == null ? "" : pluginSettings.getUseResponseDomain());
        context.put("apiKey", pluginSettings.getUseResponseApiKey() == null ? "" : pluginSettings.getUseResponseApiKey());

        context.put("statusSlugLinks", statusesService.getStatusSlugLinks());
        context.put("prioritySlugLinks", prioritiesService.getPrioritySlugLinks());
        context.put("useResponsePriorities", prioritiesService.getUseResponsePriorities());

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
            if(!SettingsService.testURConnection(domain, apiKey))
                return;
            settingsService.setURParameters(domain, apiKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        PrintWriter writer = response.getWriter();

        StatusesService statusesService = new StatusesService(ComponentAccessor.getComponent(DefaultStatusManager.class), linkManager);
        for(String statusName : statusesService.getStatusesNames()) {
            StatusesLink link = linkManager.editOrAdd(statusName, request.getParameter(statusName + "Status"));
        }

        PrioritiesService prioritiesService = new PrioritiesService(ComponentAccessor.getComponent(DefaultPriorityManager.class), priorityLinkManager, urPriorityManager);
        for(String priorityName : prioritiesService.getPrioritiesNames()) {
            URPriority priority = urPriorityManager.findBySlug(request.getParameter(priorityName + "Priority"));
            String param = request.getParameter(priorityName + "Priority");
            if(priority != null)
                priorityLinkManager.editUseResponsePriority(priorityName, priority);
        }

        response.sendRedirect("ursettings");
    }
}