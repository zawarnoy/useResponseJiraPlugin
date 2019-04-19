package useresponse.atlassian.plugins.jira.service;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.issue.status.Status;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.google.gson.internal.StringMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import useresponse.atlassian.plugins.jira.action.Action;
import useresponse.atlassian.plugins.jira.action.servlet.SettingsSendAction;
import useresponse.atlassian.plugins.jira.exception.ConnectionException;
import useresponse.atlassian.plugins.jira.manager.impl.PriorityLinkManagerImpl;
import useresponse.atlassian.plugins.jira.manager.impl.StatusesLinkManagerImpl;
import useresponse.atlassian.plugins.jira.manager.impl.URPriorityManagerImpl;
import useresponse.atlassian.plugins.jira.model.*;
import useresponse.atlassian.plugins.jira.request.GetRequest;
import useresponse.atlassian.plugins.jira.request.Request;
import useresponse.atlassian.plugins.jira.settings.PluginSettings;
import useresponse.atlassian.plugins.jira.storage.Storage;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SettingsService {

    @Autowired
    private PluginSettings pluginSettings;

    @Inject
    private UserManager userManager;

    @Inject
    LoginUriProvider loginUriProvider;

    @Autowired
    private PriorityLinkManagerImpl priorityLinkManager;

    @Autowired
    private URPriorityManagerImpl urPriorityManager;

    @Autowired
    private StatusesLinkManagerImpl linkManager;

    @Autowired
    private PrioritiesService prioritiesService;

    @Autowired
    private ApplicationProperties applicationProperties;

    @Inject
    ActiveObjects ao;

    @Autowired
    StatusesService statusesService;

    Logger logger = LoggerFactory.getLogger(SettingsService.class);

    public SettingsService() {

    }

    public HashMap<String, String> getUseResponseStatuses() {
        if (pluginSettings.getUseResponseDomain() == null || pluginSettings.getUseResponseApiKey() == null) {
            return null;
        }

        String requestUrl = createUseResponseStatusesLinkFromSettings();
        Request statusesRequest = new GetRequest();

        try {
            return getStatusesFromJson(statusesRequest.sendRequest(requestUrl));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String createUseResponseStatusesLinkFromSettings() {
        String domain = pluginSettings.getUseResponseDomain();
        String apiKey = pluginSettings.getUseResponseApiKey();
        return domain + Storage.API_STRING + "statuses.json?object_type=ticket&apiKey=" + apiKey;
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

    private void setURParameters(String domain, String apiKey) {
        pluginSettings.setUseResponseDomain(domain);
        pluginSettings.setUseResponseApiKey(apiKey);
    }

    private boolean testURConnection(String urDomain, String urApiKey) {
        Request request = new GetRequest();
        String response = null;
        try {
            response = request.sendRequest(urDomain + Storage.API_STRING + "me.json?apiKey=" + urApiKey);
        } catch (Exception e) {
            return false;
        }

        JSONParser parser = new JSONParser();
        JSONObject data = null;
        try {
            data = (JSONObject) parser.parse(response);
        } catch (Exception e) {
            return false;
        }

        return data.get("error") == null;
    }

    public boolean testURConnection() {
        return testURConnection(pluginSettings.getUseResponseDomain(), pluginSettings.getUseResponseApiKey());
    }

    public void prepareDB() {
        migrate();
        addURPriorities();
    }

    public void migrate() {
        ao.migrate(StatusesLink.class);
        ao.migrate(CommentLink.class);
        ao.migrate(UseResponseObject.class);
        ao.migrate(URPriority.class);
        ao.migrate(PriorityLink.class);
        ao.migrate(IssueFileLink.class);
        pluginSettings.setNeedExecute(true);
    }

    private void addURPriorities() {
        for (Map.Entry<String, String> entry : Storage.UR_PRIORITIES.entrySet()) {
            urPriorityManager.findOrAdd(entry.getKey(), entry.getValue());
        }
    }

    public void setFromUR(Map<Object, Object> params)
    {
        Object param;

        if ((param = params.get("jira_sync_basic_fields")) != null) {
            pluginSettings.setSyncBasicFields(param.equals("1"));
        }

        if ((param = params.get("jira_sync_statuses")) != null) {
            pluginSettings.setSyncStatuses(param.equals("1"));
        }

        if ((param = params.get("jira_sync_comments")) != null) {
            pluginSettings.setSyncComments(param.equals("1"));
        }

        if ((param = params.get("jira_sync_tickets_data")) != null) {
            pluginSettings.setSyncTicketsData(param.equals("1"));
        }

        for (Status status : statusesService.getApiStatuses()) {
            Object value = params.get(status.getNameTranslation() + "Status");

            linkManager.editOrAdd(status.getName(), (String) value);
        }

        if ((param = params.get("jira_projects")) != null) {
            ArrayList<Long> projects = new ArrayList<>();
            for (Object project : ((StringMap) param).entrySet()) {
                String key = (String) ((StringMap.Entry) project).getKey();
                projects.add((Long.valueOf(key)));
            }

            pluginSettings.setAvailableProjectsIds(projects);
        }

        for (String jiraPriority : prioritiesService.getPrioritiesNames()) {
            Object value = params.get(jiraPriority + "Priority");

            if (value == null) {
                priorityLinkManager.editUseResponsePriority(jiraPriority, null);
                continue;
            }

            URPriority priority = urPriorityManager.findBySlug((String) value);

            if (priority == null) {
                continue;
            }

            priorityLinkManager.editUseResponsePriority(jiraPriority, priority);
        }

    }

    public Map<Object, Object> setParameters(HttpServletRequest request) throws ConnectionException {
        Map<Object, Object> result = new HashMap<>();

        setConnectionParameters(request);
        Map<String, String> statuses = setStatuses(request);
        Map<String, String> priorities = setPriorities(request);
        Map<String, Object> syncSettings = setSyncSettings(request);

        result.put("statuses", statuses);
        result.put("priorities", priorities);
        result.put("syncSettings", syncSettings);

        return result;
    }

    private boolean parseOnOff(String val) {
        return val != null && (val.equals("on") || val.equals("1"));
    }

    private Map<String, Object> setSyncSettings(HttpServletRequest request) {

        Map<String, Object> result = new HashMap<>();

        pluginSettings.setSyncStatuses(parseOnOff(request.getParameter("syncStatuses")));
        result.put("syncStatuses", request.getParameter("syncStatuses"));

        pluginSettings.setSyncComments(parseOnOff(request.getParameter("syncComments")));
        result.put("syncComments", request.getParameter("syncComments"));

        pluginSettings.setSyncBasicFields(parseOnOff(request.getParameter("syncBasicFields")));
        result.put("syncBasicFields", request.getParameter("syncBasicFields"));

        return result;
    }

    private Map<String, String> setStatuses(HttpServletRequest request) {

        Map<String, String> result = new HashMap<>();

        for (String statusName : statusesService.getStatusesNames()) {
            String statusValue = request.getParameter(statusName + "Status");

            if (statusValue == null) {
                continue;
            }

            linkManager.editOrAdd(statusName, statusValue);
            result.put(statusName, statusValue);
        }
        return result;
    }

    private Map<String, String> setPriorities(HttpServletRequest request) {

        Map<String, String> result = new HashMap<>();

        try {
            for (String priorityName : prioritiesService.getPrioritiesNames()) {

                String value = request.getParameter(priorityName);

//                logger.error(priorityName + " " + value);

                if (value == null || value.equals("")) {
                    result.put(priorityName, null);
                    continue;
                }

                URPriority priority = urPriorityManager.findBySlug(value);
                priorityLinkManager.editUseResponsePriority(priorityName, priority);

                result.put(priorityName, priority.getUseResponsePrioritySlug());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private void setConnectionParameters(HttpServletRequest request) throws ConnectionException {
        String domain = request.getParameter("domain");
        String apiKey = request.getParameter("apiKey");

        if (domain == null || apiKey == null) {
            return;
        }

        if (!this.testURConnection(domain, apiKey))
            throw (new ConnectionException("Wrong domain/apiKey"));
        this.setURParameters(domain, apiKey);
    }

    public boolean isConfiguredConnection() {
        return !(pluginSettings.getUseResponseApiKey().isEmpty() || pluginSettings.getUseResponseDomain().isEmpty());
    }

    public void sendSettings(Map settings) {
        try {
            Action action = new SettingsSendAction(settings, pluginSettings);
            ExecutorService executor = Executors.newCachedThreadPool();
            Future<String> result = executor.submit(action);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, Object> formTemplateParameters() {

        Map<String, Object> context = new HashMap<>();

        context.put("domain", pluginSettings.getUseResponseDomain() == null ? "" : pluginSettings.getUseResponseDomain());
        context.put("apiKey", pluginSettings.getUseResponseApiKey() == null ? "" : pluginSettings.getUseResponseApiKey());
        context.put("syncComments", pluginSettings.getSyncComments() == null ? false : pluginSettings.getSyncComments());
        context.put("syncStatuses", pluginSettings.getSyncStatuses() == null ? false : pluginSettings.getSyncStatuses());
        context.put("syncBasicFields", pluginSettings.getSyncBasicFields() == null ? false : pluginSettings.getSyncBasicFields());
        context.put("statusSlugLinks", statusesService.getStatusSlugLinks());
        context.put("prioritySlugLinks", prioritiesService.getPrioritySlugLinks());
        context.put("useResponsePriorities", prioritiesService.getUseResponsePriorities());
        context.put("baseUrl", applicationProperties.getBaseUrl(UrlMode.ABSOLUTE));
        context.put("useResponseStatuses", this.getUseResponseStatuses());

        return context;
    }
}
