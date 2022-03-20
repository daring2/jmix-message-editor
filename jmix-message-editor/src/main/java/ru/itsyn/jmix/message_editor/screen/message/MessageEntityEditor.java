package ru.itsyn.jmix.message_editor.screen.message;

import io.jmix.core.MessageTools;
import io.jmix.core.Messages;
import io.jmix.ui.component.ComboBox;
import io.jmix.ui.component.TextArea;
import io.jmix.ui.model.InstanceContainer.ItemPropertyChangeEvent;
import io.jmix.ui.navigation.Route;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;
import ru.itsyn.jmix.message_editor.entity.MessageEntity;
import ru.itsyn.jmix.message_editor.message.MessageHelper;

import java.util.LinkedHashMap;

@Route(path = "MessageEntity/edit", parentPrefix = "MessageEntity")
@UiController("msg_MessageEntity.edit")
@UiDescriptor("message-entity-editor.xml")
@EditedEntityContainer("editDc")
public class MessageEntityEditor extends StandardEditor<MessageEntity> {

    @Autowired
    protected Messages messages;
    @Autowired
    protected MessageTools messageTools;
    @Autowired
    protected MessageHelper messageHelper;
    @Autowired
    protected ComboBox<String> localeField;
    @Autowired
    protected TextArea<String> defaultTextField;

    @Subscribe
    public void onInit(InitEvent event) {
        initLocaleField();
    }

    protected void initLocaleField() {
        var options = new LinkedHashMap<String, String>();
        for (var entry : messageTools.getAvailableLocalesMap().entrySet()) {
            options.put(entry.getKey(), entry.getValue().getLanguage());
        }
        localeField.setOptionsMap(options);
    }

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        updateDefaultText();
    }

    @Subscribe(id = "editDc", target = Target.DATA_CONTAINER)
    public void onEditDcItemPropertyChange(ItemPropertyChangeEvent<MessageEntity> event) {
        updateDefaultText();
    }

    protected void updateDefaultText() {
        var entity = getEditedEntity();
        var text = messageHelper.getDefaultText(entity);
        defaultTextField.setValue(text);
    }

}
