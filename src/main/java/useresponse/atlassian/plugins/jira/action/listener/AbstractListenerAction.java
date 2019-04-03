package useresponse.atlassian.plugins.jira.action.listener;

import com.atlassian.jira.entity.WithId;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import useresponse.atlassian.plugins.jira.exception.ConnectionException;
import useresponse.atlassian.plugins.jira.exception.InvalidResponseException;
import useresponse.atlassian.plugins.jira.manager.CommentLinkManager;
import useresponse.atlassian.plugins.jira.manager.UseResponseObjectManager;
import useresponse.atlassian.plugins.jira.request.Request;
import useresponse.atlassian.plugins.jira.service.SettingsService;
import useresponse.atlassian.plugins.jira.settings.PluginSettingsImpl;
import useresponse.atlassian.plugins.jira.storage.Storage;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 * Parent of all actions which preform data transfer to UseResponse
 * Contains methods which can help with transfer
 */
public abstract class AbstractListenerAction implements ListenerAction {

    protected Request request;
    protected int actionType;
    protected WithId entity;

    @Autowired
    SettingsService settingsService;

    @Autowired
    PluginSettingsImpl pluginSettings;

    protected PluginSettingsFactory pluginSettingsFactory;
    @Inject
    public void setPluginSettingsFactory(PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    protected CommentLinkManager commentLinkManager;
    @Inject
    @Named("commentLinkManager")
    public void setCommentLinkManager(CommentLinkManager commentLinkManager) {
        this.commentLinkManager = commentLinkManager;
    }

    protected UseResponseObjectManager useResponseObjectManager;
    @Inject
    @Named("useResponseObjectManager")
    public void setUseResponseObjectManager(UseResponseObjectManager useResponseObjectManager) {
        this.useResponseObjectManager = useResponseObjectManager;
    }

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
            e.printStackTrace();
            return e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    private void execute() throws ConnectionException, NoSuchAlgorithmException, KeyManagementException, InvalidResponseException, IOException, ParseException {
        if (!settingsService.testURConnection()) {
            throw new ConnectionException("Can't connect to UseResponse services");
        }

        if (!Storage.userWhoPerformedAction.equals("")) {
            request.addParameter("logged_user_email", Storage.userWhoPerformedAction);
        }

        request = addParameters(request);
        String url = createUrl();
        String response = request.sendRequest(url);
        handleResponse(response);
    }

    protected abstract Request addParameters(Request request) throws IOException;

    protected abstract String createUrl();

    protected abstract void handleResponse(String response) throws ParseException;

    protected String collectUrl(String requestString) {
        return pluginSettings.getUseResponseDomain() + Storage.API_STRING + requestString + "?apiKey=" + pluginSettings.getUseResponseApiKey();
    }

    /**
     * For creating via jira in Useresponse system
     *
     */
    protected String getSpecialApiPath() {
        return pluginSettings.getUseResponseDomain() + Storage.API_STRING + Storage.JIRA_DATA_HANDLER_ROUTE + "?apiKey=" + pluginSettings.getUseResponseApiKey();
    }

    protected int getIdFromResponse(String response) throws ParseException {
        JSONObject object = (JSONObject) new JSONParser().parse(response);
        return ((Long) ((JSONObject) object.get("success")).get("id")).intValue();
    }

    public abstract void setEntity(WithId entity);
}
