package useresponse.atlassian.plugins.jira.servlet;

import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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

    private static String STATUSES_LINK_TEMPLATE = "/templates/ur_statuses_link_template.vm";

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
        PluginSettings settings = new PluginSettingsImpl(pluginSettingsFactory);
        Request linksRequest = new GetRequest();
        PrintWriter writer = resp.getWriter();

        String response = null;
        try {
            response = linksRequest.sendRequest(createUseResponseStatusesLinkFromSettings(settings));

            for(HashMap.Entry<String, String> status: getStatusesFromJson(response).entrySet()) {
                writer.write("key :" + status.getKey() + " value: " + status.getValue() + "<br>");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {

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
//            JSONObject status = (JSONObject) parser.parse(iterator.next());
            JSONObject status = iterator.next();
            encodedStatuses.put((String) status.get("title"), (String) status.get("slug"));
        }

        return encodedStatuses;
    }



}