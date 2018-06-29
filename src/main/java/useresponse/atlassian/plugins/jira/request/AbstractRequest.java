package useresponse.atlassian.plugins.jira.request;

import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import useresponse.atlassian.plugins.jira.settings.PluginSettings;
import useresponse.atlassian.plugins.jira.settings.PluginSettingsImpl;

import javax.inject.Inject;
import java.net.*;
import java.util.*;

public abstract class AbstractRequest implements Request {

    private CloseableHttpClient client = HttpClients.createDefault();
    protected HttpEntityEnclosingRequestBase request;

    @Inject
    private PluginSettings pluginSettings;

    private final String domain = pluginSettings.getUseResponseDomain();
    private final String apiKey = pluginSettings.getUseResponseApiKey();
    private final String apiPath = "api/4.0/";

    protected String requestPath;
    protected List<NameValuePair> parameters = new ArrayList<>();


    protected URI createURI() throws URISyntaxException {
        return new URI(domain + apiPath + requestPath + "?" + "apiKey=" + apiKey);
    }

    protected abstract void setRequestParameters() throws Exception;

    public void addParameter(String name, String value) {
        this.parameters.add(new BasicNameValuePair(name, value));
    }

    public void sendRequest() throws Exception {
        this.request.setURI(this.createURI());
        this.setRequestParameters();
        CloseableHttpResponse response = this.client.execute(this.request);
        System.out.println(response.toString());
    }

}
