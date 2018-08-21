//package useresponse.atlassian.plugins.jira.listener.plugin;
//
//import com.atlassian.event.api.EventListener;
//import com.atlassian.event.api.EventPublisher;
//import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.DisposableBean;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
////import com.atlassian.plugin.event.events.PluginEnabledEvent;
//
//@Component
//public class PluginEnabledListener implements InitializingBean, DisposableBean {
//
//    private static final Logger log = LoggerFactory.getLogger(PluginEnabledListener.class);
//
//    @JiraImport
//    private final EventPublisher eventPublisher;
//
//    @Autowired
//    public PluginEnabledListener(EventPublisher eventPublisher) {
//        this.eventPublisher = eventPublisher;
//    }
//
//    @Override
//    public void destroy() throws Exception {
//        eventPublisher.unregister(this);
//    }
//
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        eventPublisher.register(this);
//    }
//
//    @EventListener
//    public void onPluginEnable(PluginEnabledEvent pluginEnabledEvent) {
//        log.error(pluginEnabledEvent.getPlugin().getKey());
//    }
//}
