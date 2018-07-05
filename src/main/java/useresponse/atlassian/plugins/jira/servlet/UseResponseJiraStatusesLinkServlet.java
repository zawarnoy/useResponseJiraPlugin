package useresponse.atlassian.plugins.jira.servlet;

import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import useresponse.atlassian.plugins.jira.service.IssueActionService;
import useresponse.atlassian.plugins.jira.settings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import useresponse.atlassian.plugins.jira.request.GetRequest;
import useresponse.atlassian.plugins.jira.request.Request;
import useresponse.atlassian.plugins.jira.settings.PluginSettingsImpl;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.font.ShapeGraphicAttribute;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class UseResponseJiraStatusesLinkServlet extends HttpServlet {

    @ComponentImport
    private final UserManager userManager;
    @ComponentImport
    private final LoginUriProvider loginUriProvider;
    @ComponentImport
    private final TemplateRenderer templateRenderer;
    @ComponentImport
    private final PluginSettingsFactory pluginSettingsFactory;

    @Inject
    public UseResponseJiraStatusesLinkServlet(UserManager userManager, TemplateRenderer templateRenderer, LoginUriProvider loginUriProvider, PluginSettingsFactory pluginSettingsFactory) {
        this.userManager = userManager;
        this.loginUriProvider = loginUriProvider;
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.templateRenderer = templateRenderer;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {

    }





}