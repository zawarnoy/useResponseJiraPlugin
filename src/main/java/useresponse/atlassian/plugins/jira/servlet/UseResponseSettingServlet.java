package useresponse.atlassian.plugins.jira.servlet;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import useresponse.atlassian.plugins.jira.service.SettingsService;
import useresponse.atlassian.plugins.jira.service.request.ServletService;
import useresponse.atlassian.plugins.jira.storage.Storage;

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

@Scanned
public class UseResponseSettingServlet extends HttpServlet {

    private static final String SETTINGS_TEMPLATE = "/templates/ur_connection_settings_template.vm";
    private static final String LINK_TEMPLATE = "/templates/ur_link_settings_template.vm";

    private static final String SUCCESSFULL_CONNECTION_STRING = "Your connection is successful!";
    private static final String SETTINGS_ARE_CHANCHED_STRING = "Settings are changed!";

    private final UserManager userManager;
    private final TemplateRenderer templateRenderer;

    @Autowired
    private SettingsService settingsService;

    @Inject
    public UseResponseSettingServlet(@ComponentImport UserManager userManager,
                                     @ComponentImport TemplateRenderer templateRenderer
    ) {
        this.userManager = userManager;
        this.templateRenderer = templateRenderer;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if (!settingsService.checkIsAdmin(userManager.getRemoteUserKey())) {
            settingsService.redirectToLogin(request, response);
            return;
        }

        response.setContentType("text/html");
        templateRenderer.render(SETTINGS_TEMPLATE, settingsService.formTemplateParameters(), response.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (!settingsService.checkIsAdmin(userManager.getRemoteUserKey())) {
            settingsService.redirectToLogin(request, response);
            return;
        }

        if (request.getParameterMap().size() == 0) {
            settingsService.setFromUR((new Gson()).fromJson(ServletService.getJsonFromRequest(request), Map.class));
            return;
        }

        HashMap<String, Object> map = new HashMap<>();

        try {
            Map<Object, Object> result = settingsService.setParameters(request);

//            settingsService.sendSettings(result);

            Writer writer = new StringWriter();
//            templateRenderer.render(LINK_TEMPLATE, settingsService.formTemplateParameters(), writer);

            map.put("linkTemplate", writer.toString());
            map.put("status", "success");
            map.put("message", request.getParameterMap().size() > 5 ? SETTINGS_ARE_CHANCHED_STRING : SUCCESSFULL_CONNECTION_STRING);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("status", "error");
            map.put("message", e.getMessage());
        }

        String responseBody = (new Gson()).toJson(map);
        response.getWriter().write(responseBody);
    }
}