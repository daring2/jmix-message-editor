package ru.itsyn.jmix.message_editor.screen.message;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Route;
import io.jmix.core.MessageTools;
import io.jmix.core.Messages;
import io.jmix.flowui.model.InstanceContainer.ItemPropertyChangeEvent;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;
import ru.itsyn.jmix.message_editor.entity.MessageEntity;
import ru.itsyn.jmix.message_editor.message.MessageHelper;

import java.util.LinkedHashMap;

@Route(value = "MessageEntity/:id", layout = DefaultMainViewParent.class)
@ViewController("msg_MessageEntity.detail")
@ViewDescriptor("message-entity-editor.xml")
@EditedEntityContainer("editDc")
@DialogMode(width = "1024px", height = "768px", resizable = true)
public class MessageEntityEditor extends StandardDetailView<MessageEntity> {

    @Autowired
    protected Messages messages;
    @Autowired
    protected MessageTools messageTools;
    @Autowired
    protected MessageHelper messageHelper;
    @ViewComponent
    protected ComboBox<String> localeField;
    @ViewComponent
    protected TextArea defaultTextField;

    @Subscribe
    public void onInit(InitEvent event) {
        initLocaleField();
    }

    protected void initLocaleField() {
        var options = new LinkedHashMap<String, String>();
        for (var entry : messageTools.getAvailableLocalesMap().entrySet()) {
            options.put(entry.getValue().getLanguage(), entry.getKey());
        }
        localeField.setItems(options.keySet());
        localeField.setItemLabelGenerator(options::get);
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
