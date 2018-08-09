package useresponse.atlassian.plugins.jira.rest.issue;

import javax.xml.bind.annotation.*;
@XmlAccessorType(XmlAccessType.FIELD)
public class IssueRestResourceModel {

    @XmlElement
    int id;

    @XmlElement
    String content;

    @XmlElement
    String title;

    @XmlElement
    String dueOn;

    @XmlElement
    String authorEmail;

    @XmlElement
    String priority;

    @XmlElement
    String status;

    public IssueRestResourceModel() {
        content = "here";
        id =3;
        title = "asd";
        dueOn = "asd";
        authorEmail = "asdasd";
        priority = "asdad";
        status = " ssad";
    }

 }