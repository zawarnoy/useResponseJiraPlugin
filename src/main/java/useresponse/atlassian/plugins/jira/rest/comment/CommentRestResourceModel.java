package useresponse.atlassian.plugins.jira.rest.comment;

import javax.xml.bind.annotation.*;
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CommentRestResourceModel {

    @XmlElement
    private int id;

    @XmlElement
    private int issueId;

    @XmlElement
    private String content;

    @XmlElement
    private String authorEmaill;

    @XmlElement
    private String createdAt;

    public CommentRestResourceModel() {
    }

    public CommentRestResourceModel(int id, int issueId, String content, String authorEmaill, String createdAt) {
        this.id = id;
        this.issueId = issueId;
        this.content = content;
        this.authorEmaill = authorEmaill;
        this.createdAt = createdAt;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIssueId() {
        return issueId;
    }

    public void setIssueId(int issueId) {
        this.issueId = issueId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthorEmaill() {
        return authorEmaill;
    }

    public void setAuthorEmaill(String authorEmaill) {
        this.authorEmaill = authorEmaill;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}