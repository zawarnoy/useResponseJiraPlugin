package useresponse.atlassian.plugins.jira.request;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;

public class PostRequest extends RequestImpl {

    public PostRequest() {
        this.requestType = "POST";
    }
}
