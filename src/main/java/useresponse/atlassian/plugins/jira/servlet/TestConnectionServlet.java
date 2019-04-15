package useresponse.atlassian.plugins.jira.servlet;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.component.pico.ComponentManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.sal.api.user.UserManager;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import useresponse.atlassian.plugins.jira.service.SettingsService;
import useresponse.atlassian.plugins.jira.settings.PluginSettingsImpl;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class TestConnectionServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(TestConnectionServlet.class);

    private Gson gson = new Gson();

    @Autowired
    PluginSettingsImpl pluginSettings;

    @Inject
    private UserManager userManager;

    @Autowired
    SettingsService settingsService;

    public TestConnectionServlet() {

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        HashMap<Object, Object> responseMap = new HashMap<>();

        if (!settingsService.checkIsAdmin(userManager.getRemoteUserKey())) {
            responseMap.put("error", "Log in as administrator");
            resp.getWriter().write(gson.toJson(responseMap));
            return;
        }


        boolean isConfigured = true;

        if (pluginSettings.getUseResponseDomain().isEmpty() || pluginSettings.getUseResponseApiKey().isEmpty()) {
            isConfigured = false;
        }

        responseMap.put("isConfigured", isConfigured);

        resp.getWriter().write(gson.toJson(responseMap));
    }

}