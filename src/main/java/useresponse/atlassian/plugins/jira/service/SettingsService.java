package useresponse.atlassian.plugins.jira.service;

import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import useresponse.atlassian.plugins.jira.request.GetRequest;
import useresponse.atlassian.plugins.jira.request.Request;
import useresponse.atlassian.plugins.jira.settings.PluginSettings;
import useresponse.atlassian.plugins.jira.settings.PluginSettingsImpl;
import useresponse.atlassian.plugins.jira.storage.ConstStorage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;

public class SettingsService {

    private final PluginSettingsFactory pluginSettingsFactory;
    private final UserManager userManager;
    private final LoginUriProvider loginUriProvider;

    public SettingsService(UserManager userManager, LoginUriProvider loginUriProvider, PluginSettingsFactory pluginSettignsFactory) {
        this.pluginSettingsFactory = pluginSettignsFactory;
        this.loginUriProvider = loginUriProvider;
        this.userManager = userManager;
    }


    public HashMap<String, String> getUseResponseStatuses(PluginSettings useResponseSettings) throws Exception {
        String requestUrl = createUseResponseStatusesLinkFromSettings(useResponseSettings);
        Request statusesRequest = new GetRequest();
        return getStatusesFromJson(statusesRequest.sendRequest(requestUrl));
    }

    private String createUseResponseStatusesLinkFromSettings(PluginSettings settings) {
        String domain = settings.getUseResponseDomain();
        String apiKey = settings.getUseResponseApiKey();
        return domain + ConstStorage.API_STRING +"statuses.json?object_type=ticket&apiKey=" + apiKey;
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

    public boolean checkIsAdmin(UserKey userKey) {
        return userKey != null && userManager.isSystemAdmin(userKey);
    }

    public void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
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

    public void setURParameters(String domain, String apiKey) {
        PluginSettings pluginSettings = new PluginSettingsImpl(pluginSettingsFactory);
        pluginSettings.setUseResponseDomain(domain);
        pluginSettings.setUseResponseApiKey(apiKey);
    }

    public boolean testURConnection(String urDomain, String urApiKey) throws Exception {
        Request request = new GetRequest();
        String response = request.sendRequest(urDomain + ConstStorage.API_STRING +"me.json?apiKey=" + urApiKey);

        JSONParser parser = new JSONParser();
        JSONObject data = (JSONObject) parser.parse(response);
        return data.get("error") == null;
    }

}
