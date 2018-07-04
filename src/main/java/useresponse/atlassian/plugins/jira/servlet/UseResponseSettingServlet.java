package useresponse.atlassian.plugins.jira.servlet;


import java.net.URI;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import useresponse.atlassian.plugins.jira.settings.PluginSettings;
import useresponse.atlassian.plugins.jira.settings.PluginSettingsImpl;
import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
        UserKey userKey = userManager.getRemoteUserKey();
        if (!checkIsAdmin(userKey)) {
            redirectToLogin(request, response);
            return;
        }
        PluginSettings pluginSettings = new PluginSettingsImpl(pluginSettingsFactory);
        Map<String, Object> context = new HashMap<String, Object>();

        context.put("domain", pluginSettings.getUseResponseDomain());
        context.put("apiKey", pluginSettings.getUseResponseApiKey());

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
        setURParameters(request.getParameter("domain"), request.getParameter("apiKey"));
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

}