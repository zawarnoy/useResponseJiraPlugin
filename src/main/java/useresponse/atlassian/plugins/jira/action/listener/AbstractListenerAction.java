package useresponse.atlassian.plugins.jira.action.listener;

import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import useresponse.atlassian.plugins.jira.exception.ConnectionException;
import useresponse.atlassian.plugins.jira.exception.InvalidResponseException;
import useresponse.atlassian.plugins.jira.request.Request;
import useresponse.atlassian.plugins.jira.service.SettingsService;
import useresponse.atlassian.plugins.jira.settings.PluginSettings;
import useresponse.atlassian.plugins.jira.settings.PluginSettingsImpl;
import useresponse.atlassian.plugins.jira.storage.ConstStorage;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 * Parent of all actions which preform data transfer to UseResponse
 * _
 * (+_+)
 * -|-
 * /\
 * <p>
 * Contains methods which can help with transfer
 */
public abstract class AbstractListenerAction implements Action {

    protected Request request;
    protected PluginSettingsFactory pluginSettingsFactory;
    protected int actionType;

    /**
     * Returns error which could appear during the execution on successful completion returns action type.
     *
     * @return String;
     */
    @Override
    public String call() {
        try {
            execute();
            return String.valueOf(actionType);
        } catch (IOException | NoSuchAlgorithmException | InvalidResponseException | ParseException | KeyManagementException | ConnectionException e) {
            return e.getMessage();
        }
    }

    private void execute() throws ConnectionException, NoSuchAlgorithmException, KeyManagementException, InvalidResponseException, IOException, ParseException {
        if (!SettingsService.testURConnection(pluginSettingsFactory)) {
            throw new ConnectionException("Can't connect to UseResponse services");
        }
        request = addParameters(request);
        String url = createUrl();
        String response = request.sendRequest(url);
        handleResponse(response);
    }

    protected abstract Request addParameters(Request request) throws IOException;

    protected abstract String createUrl();

    protected abstract void handleResponse(String response) throws ParseException;

    protected Request prepareRequest(Request request, int objectId) {
        request.addParameter("jira_id", objectId);
        request.addParameter("treat_as_html", 1);
        return request;
    }

    protected String collectUrl(String requestString) {
        PluginSettings pluginSettings = new PluginSettingsImpl(pluginSettingsFactory);
        return pluginSettings.getUseResponseDomain() + ConstStorage.API_STRING + requestString + "?apiKey=" + pluginSettings.getUseResponseApiKey();
    }

    protected int getIdFromResponse(String response) throws ParseException {
        JSONObject object = (JSONObject) new JSONParser().parse(response);
        return ((Long) ((JSONObject) object.get("success")).get("id")).intValue();
    }
}