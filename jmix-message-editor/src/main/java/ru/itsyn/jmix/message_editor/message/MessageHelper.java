package ru.itsyn.jmix.message_editor.message;

import io.jmix.core.DataManager;
import io.jmix.core.impl.JmixMessageSource;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.core.security.SystemAuthenticator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.stereotype.Component;
import ru.itsyn.jmix.message_editor.entity.MessageEntity;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.context.support.MessageSourceUtils.getMergedProperties;

@Component("msg_MessageHelper")
public class MessageHelper {

    @Autowired
    protected JmixMessageSource jmixMessageSource;
    @Autowired
    protected MainMessageSource mainMessageSource;
    @Autowired
    protected DataManager dataManager;
    @Autowired
    protected SystemAuthenticator systemAuthenticator;
    @Autowired
    protected CurrentAuthentication currentAuthentication;

    @EventListener(ContextRefreshedEvent.class)
    public void init() {
        systemAuthenticator.runWithSystem(this::reloadMessages);
    }

    public void reloadMessages() {
        var messageSource = new StaticMessageSource();
        loadDatabaseMessages(messageSource);
        messageSource.setParentMessageSource(jmixMessageSource);
        mainMessageSource.setParentMessageSource(messageSource);
        jmixMessageSource.clearCache();
    }

    protected void loadDatabaseMessages(StaticMessageSource messageSource) {
        var query = "select e from msg_MessageEntity e where e.active = true";
        var entities = dataManager.load(MessageEntity.class)
                .query(query)
                .list();
        for (var entity : entities) {
            var locale = new Locale(entity.getLocale());
            messageSource.addMessage(entity.getKey(), locale, entity.getText());
        }
    }

    public List<String> getMessageKeys(String keyPrefix) {
        var properties = getMergedProperties(jmixMessageSource, currentAuthentication.getLocale());
        return properties.keySet().stream()
                .filter(key -> (key instanceof String) && ((String) key).startsWith(keyPrefix))
                .map(key -> (String) key)
                .collect(Collectors.toList());
    }

    public String getDefaultText(MessageEntity entity) {
        if (isNotBlank(entity.getKey()) && isNotBlank(entity.getLocale())) {
            var locale = new Locale(entity.getLocale());
            try {
                return jmixMessageSource.getMessage(entity.getKey(), null, locale);
            } catch (NoSuchMessageException e) {
                // ignore
            }
        }
        return "";
    }

    public Locale getCurrentLocale() {
        return currentAuthentication.getLocale();
    }

}
