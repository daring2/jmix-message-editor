package ru.itsyn.jmix.message_editor.screen.caption;

import io.jmix.core.*;
import io.jmix.ui.component.Component;
import io.jmix.ui.component.Table;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.model.DataContext.PreCommitEvent;
import io.jmix.ui.navigation.Route;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;
import ru.itsyn.jmix.message_editor.entity.CaptionEntity;
import ru.itsyn.jmix.message_editor.entity.MessageEntity;
import ru.itsyn.jmix.message_editor.message.MessageHelper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Route(path = "CaptionEntity/edit", parentPrefix = "CaptionEntity")
@UiController("msg_CaptionEntity.edit")
@UiDescriptor("caption-entity-editor.xml")
@EditedEntityContainer("editDc")
public class CaptionEntityEditor extends StandardEditor<CaptionEntity> {

    @Autowired
    protected Metadata metadata;
    @Autowired
    protected DataManager dataManager;
    @Autowired
    protected EntityStates entityStates;
    @Autowired
    protected MessageTools messageTools;
    @Autowired
    protected MessageHelper messageHelper;
    @Autowired
    protected CollectionLoader<MessageEntity> messagesDl;

    protected Map<String, String> locales = new LinkedHashMap<>();

    @Subscribe
    public void onInit(InitEvent event) {
        initLocales();
    }

    protected void initLocales() {
        for (var entry : messageTools.getAvailableLocalesMap().entrySet()) {
            locales.put(entry.getValue().getLanguage(), entry.getKey());
        }
    }

    @Install(to = "messagesDl", target = Target.DATA_LOADER)
    protected List<MessageEntity> loadMessages(LoadContext<MessageEntity> loadContext) {
        var messages = new ArrayList<MessageEntity>();
        messages.addAll(loadDefaultMessages());
        messages.addAll(loadDatabaseMessages());
        var messageMap = new LinkedHashMap<String, MessageEntity>();
        messages.forEach(e -> messageMap.put(e.getLocale(), e));
        return new ArrayList<>(messageMap.values());
    }

    protected List<MessageEntity> loadDefaultMessages() {
        var entity = getEditedEntity();
        var messages = new ArrayList<MessageEntity>();
        for (String locale : locales.keySet()) {
            var message = metadata.create(MessageEntity.class);
            message.setKey(entity.getKey());
            message.setLocale(locale);
            message.setActive(true);
            messages.add(message);
        }
        return messages;
    }

    protected List<MessageEntity> loadDatabaseMessages() {
        var entity = getEditedEntity();
        var query = "select e from msg_MessageEntity e where e.key = :key";
        return dataManager.load(MessageEntity.class)
                .query(query)
                .parameter("key", entity.getKey())
                .list();
    }

    @Install(to = "messagesTable.locale", subject = "columnGenerator")
    protected Component localeColumnGenerator(MessageEntity entity) {
        var text = locales.get(entity.getLocale());
        return new Table.PlainTextCell(text);
    }

    @Install(to = "messagesTable.defaultText", subject = "columnGenerator")
    protected Component defaultTextColumnGenerator(MessageEntity entity) {
        var text = messageHelper.getDefaultText(entity);
        return new Table.PlainTextCell(text);
    }

    @Subscribe(target = Target.DATA_CONTEXT)
    protected void onPreCommit(PreCommitEvent event) {
        event.getModifiedInstances().remove(getEditedEntity());
        event.getModifiedInstances().forEach(obj -> {
            var entity = (MessageEntity) obj;
            if (isBlank(entity.getText()) && !entityStates.isNew(entity))
                event.getRemovedInstances().add(entity);
        });
        event.getModifiedInstances().removeIf(obj -> {
            var entity = (MessageEntity) obj;
            return isBlank(entity.getText());
        });
    }

    @Subscribe
    public void onAfterCommitChanges(AfterCommitChangesEvent event) {
        messagesDl.load();
    }

}
