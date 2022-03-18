package ru.itsyn.jmix.message_editor.message;

import io.jmix.core.DataManager;
import io.jmix.core.impl.JmixMessageSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.stereotype.Component;
import ru.itsyn.jmix.message_editor.entity.MessageEntity;

import java.util.Locale;

@Component("msg_MessageHelper")
public class MessageHelper {

    @Autowired
    protected JmixMessageSource jmixMessageSource;
    @Autowired
    protected MainMessageSource mainMessageSource;
    @Autowired
    protected DataManager dataManager;

    @EventListener(ContextStartedEvent.class)
    public void init() {
        reloadMessages();
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

}
