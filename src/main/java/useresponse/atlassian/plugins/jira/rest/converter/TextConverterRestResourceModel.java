package useresponse.atlassian.plugins.jira.rest.converter;

import javax.xml.bind.annotation.*;
@XmlRootElement(name = "message")
@XmlAccessorType(XmlAccessType.FIELD)
public class TextConverterRestResourceModel {

    @XmlElement(name = "value")
    private String message;

    public TextConverterRestResourceModel() {
    }

    public TextConverterRestResourceModel(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}