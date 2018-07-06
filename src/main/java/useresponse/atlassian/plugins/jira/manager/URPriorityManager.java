package useresponse.atlassian.plugins.jira.manager;

import com.atlassian.activeobjects.tx.Transactional;
import useresponse.atlassian.plugins.jira.model.UseResponsePriority;

import java.util.List;

@Transactional
public interface UseResponsePriorityManager {

    List<UseResponsePriority> all();

}
