package useresponse.atlassian.plugins.jira.service;


import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.AttachmentManager;

import java.util.ArrayList;
import java.util.List;

public class AttachmentService {

    public static List<Integer> findDeleted() {
        List<Integer> result = new ArrayList<>();
        AttachmentManager attachmentManager = ComponentAccessor.getAttachmentManager();
        return result;
    }
}
