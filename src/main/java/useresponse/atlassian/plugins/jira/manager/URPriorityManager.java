package useresponse.atlassian.plugins.jira.manager;

import com.atlassian.activeobjects.tx.Transactional;
import useresponse.atlassian.plugins.jira.model.URPriority;

import java.util.List;

@Transactional
public interface URPriorityManager {

    URPriority add(String useResponsePrioritySlug, String useResponsePriorityValue);

    URPriority findBySlug(String useResponsePrioritySlug);

    List<URPriority> all();

}
