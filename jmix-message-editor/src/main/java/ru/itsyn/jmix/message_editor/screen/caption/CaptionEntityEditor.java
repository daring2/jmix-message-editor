package ru.itsyn.jmix.message_editor.screen.caption;

import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.Route;
import io.jmix.core.*;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.model.DataContext;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;
import ru.itsyn.jmix.message_editor.entity.CaptionEntity;
import ru.itsyn.jmix.message_editor.entity.MessageEntity;
import ru.itsyn.jmix.message_editor.message.MessageHelper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Route(value = "CaptionEntity/:id", layout = DefaultMainViewParent.class)
@ViewController("msg_CaptionEntity.detail")
@ViewDescriptor("caption-entity-editor.xml")
@EditedEntityContainer("editDc")
@DialogMode(width = "1024px", height = "768px", resizable = true)
public class CaptionEntityEditor extends StandardDetailView<CaptionEntity> {

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

    @ViewComponent
    protected CollectionContainer<MessageEntity> messagesDc;
    @ViewComponent
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

    @Supply(to = "messagesTable.locale", subject = "renderer")
    protected Renderer<MessageEntity> localeColumnRenderer() {
        return new TextRenderer<>(entity -> locales.get(entity.getLocale()));
    }

    @Supply(to = "messagesTable.locale", subject = "renderer")
    protected Renderer<MessageEntity> localeDefaultTextRenderer() {
        return new TextRenderer<>(messageHelper::getDefaultText);
    }

    @Subscribe(target = Target.DATA_CONTEXT)
    protected void onPreSave(DataContext.PreSaveEvent event) {
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
    protected void onAfterSave(final AfterSaveEvent event) {
        messagesDl.load();
        updateCaptionEntityAfterCommit();
    }

    protected void updateCaptionEntityAfterCommit() {
        var currentLocale = messageHelper.getCurrentLocale();
        for (var item : messagesDc.getItems()) {
            var isCurrent = isTrue(item.getActive()) &&
                    currentLocale.getLanguage().equals(item.getLocale());
            if (isCurrent) {
                getEditedEntity().setText(item.getText());
                break;
            }
        }
    }

//    @Override
//    protected void preventUnsavedChanges(BeforeCloseEvent event) {
//        if (event.getCloseAction() == WINDOW_COMMIT_AND_CLOSE_ACTION)
//            return;
//        super.preventUnsavedChanges(event);
//    }

}
