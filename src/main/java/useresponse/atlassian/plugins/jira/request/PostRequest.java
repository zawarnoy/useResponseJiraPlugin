package useresponse.atlassian.plugins.jira.request;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;

public class PostRequest extends AbstractRequest {

    public PostRequest() {
        this.requestPath = "objects.json";
        this.request = new HttpPost();
    }

    @Override
    protected void setRequestParameters() throws Exception {
        this.request.setEntity(new UrlEncodedFormEntity(this.parameters));
    }
}
