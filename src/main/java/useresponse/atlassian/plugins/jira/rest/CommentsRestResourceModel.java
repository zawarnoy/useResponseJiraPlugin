package useresponse.atlassian.plugins.jira.rest;

import javax.xml.bind.annotation.*;
@XmlRootElement(name = "message")
@XmlAccessorType(XmlAccessType.FIELD)
public class CommentsRestResourceModel {

    @XmlElement(name = "value")
    private String message;

    public CommentsRestResourceModel() {
    }

    public CommentsRestResourceModel(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}