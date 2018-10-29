package useresponse.atlassian.plugins.jira.servlet;

import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserManager;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import useresponse.atlassian.plugins.jira.manager.impl.UseResponseObjectManagerImpl;
import useresponse.atlassian.plugins.jira.service.SettingsService;
import useresponse.atlassian.plugins.jira.service.request.ServletService;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class IssueUnlinkServlet extends HttpServlet{
    private static final Logger log = LoggerFactory.getLogger(IssueUnlinkServlet.class);

    @Autowired
    private UseResponseObjectManagerImpl objectManager;

    private final UserManager userManager;
    private final LoginUriProvider loginUriProvider;
    private final PluginSettingsFactory pluginSettingsFactory;

    @Inject
    public IssueUnlinkServlet(UserManager userManager, LoginUriProvider loginUriProvider, PluginSettingsFactory pluginSettingsFactory) {
        this.userManager = userManager;
        this.loginUriProvider = loginUriProvider;
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        SettingsService settingsService = new SettingsService(userManager, loginUriProvider, pluginSettingsFactory);
        if (!settingsService.checkIsAdmin(userManager.getRemoteUserKey())) {
            settingsService.redirectToLogin(req, resp);
            return;
        }

        String json = ServletService.getJsonFromRequest(req);

        Map<Object, Object> requestParams = (new Gson()).fromJson(json, Map.class);
        Map<String, String> responseMap = new HashMap<>();

        try {
            int useresponse_object_id = ((Double) requestParams.get("useresponse_object_id")).intValue();
            objectManager.delete(objectManager.findByUseResponseId(useresponse_object_id));
            responseMap.put("status", "success");
        } catch (Exception e) {
            responseMap.put("status", "error");
            responseMap.put("message", e.getMessage());
        }

        resp.getWriter().write((new Gson()).toJson(responseMap));
    }

}