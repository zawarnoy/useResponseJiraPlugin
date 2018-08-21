package useresponse.atlassian.plugins.jira.model;

import net.java.ao.Entity;

public interface URObjectType extends Entity {

    String getObjectName();

    void setObjectName(String name);
}
