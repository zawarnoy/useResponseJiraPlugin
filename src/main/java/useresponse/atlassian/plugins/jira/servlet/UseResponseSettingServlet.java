package useresponse.atlassian.plugins.jira.servlet;


import java.net.URI;

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
import useresponse.atlassian.plugins.jira.settings.PluginSettings;
import useresponse.atlassian.plugins.jira.settings.PluginSettingsImpl;
import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.plaf.basic.BasicScrollPaneUI;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
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
        UserKey userKey = userManager.getRemoteUserKey();
        if (!checkIsAdmin(userKey)) {
            redirectToLogin(request, response);
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
            statuses = getUseResponseStatuses(pluginSettings);
        } catch (Exception e) {
            e.printStackTrace();
        }

        context.put("useResponseStatuses", statuses);

        response.setContentType("text/html");
        templateRenderer.render(SETTINGS_TEMPLATE, context, response.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserKey userKey = userManager.getRemoteUserKey();
        if (!checkIsAdmin(userKey)) {
            redirectToLogin(request, response);
            return;
        }

        setURParameters(
                request.getParameter("domain"),
                request.getParameter("apiKey")
        );

        setURStatuses(
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

    private boolean checkIsAdmin(UserKey userKey) {
        return userKey != null && userManager.isSystemAdmin(userKey);
    }

    private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(loginUriProvider.getLoginUri(getUri(request)).toASCIIString());
    }

    private URI getUri(HttpServletRequest request) {
        StringBuffer builder = request.getRequestURL();
        if (request.getQueryString() != null) {
            builder.append("?");
            builder.append(request.getQueryString());
        }
        return URI.create(builder.toString());
    }

    private void setURParameters(String domain, String apiKey) {
        useresponse.atlassian.plugins.jira.settings.PluginSettings pluginSettings = new PluginSettingsImpl(pluginSettingsFactory);
        pluginSettings.setUseResponseDomain(domain);
        pluginSettings.setUseResponseApiKey(apiKey);
    }

    private void setURStatuses(String openStatus, String inProgressStatus, String reopenedStatus, String resolvedStatus, String closedStatus, String todoStatus, String doneStatus) {
        PluginSettings pluginSettings = new PluginSettingsImpl(pluginSettingsFactory);
        pluginSettings.setUseResponseClosedStatus(closedStatus);
        pluginSettings.setUseResponseInProgressStatus(inProgressStatus);
        pluginSettings.setUseResponseOpenStatus(openStatus);
        pluginSettings.setUseResponseDoneStatus(doneStatus);
        pluginSettings.setUseResponseReopenedStatus(reopenedStatus);
        pluginSettings.setUseResponseResolvedStatus(resolvedStatus);
        pluginSettings.setUseResponseToDoStatus(todoStatus);
    }

    private HashMap<String, String> getUseResponseStatuses(PluginSettings useResponseSettings) throws Exception {
        String requestUrl = createUseResponseStatusesLinkFromSettings(useResponseSettings);
        Request statusesRequest = new GetRequest();
        return getStatusesFromJson(statusesRequest.sendRequest(requestUrl));
    }

    private String createUseResponseStatusesLinkFromSettings(PluginSettings settings) {
        String domain = settings.getUseResponseDomain();
        String apiKey = settings.getUseResponseApiKey();
        return domain + "api/4.0/statuses.json?object_type=ticket&apiKey=" + apiKey;
    }

    private HashMap<String, String> getStatusesFromJson(String json) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(json);
        JSONArray statuses = (JSONArray) object.get("success");

        Iterator<JSONObject> iterator = statuses.iterator();

        HashMap<String, String> encodedStatuses = new HashMap<String, String>();

        while (iterator.hasNext()) {
            JSONObject status = iterator.next();
            encodedStatuses.put((String) status.get("title"), (String) status.get("slug"));
        }

        return encodedStatuses;
    }

}