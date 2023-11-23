package ru.itsyn.jmix.message_editor.screen.message;

import com.vaadin.flow.router.Route;
import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;
import ru.itsyn.jmix.message_editor.entity.MessageEntity;
import ru.itsyn.jmix.message_editor.message.MessageHelper;

@Route(value = "MessageEntity", layout = DefaultMainViewParent.class)
@ViewController("msg_MessageEntity.list")
@ViewDescriptor("message-entity-browser.xml")
@LookupComponent("table")
@DialogMode(width = "1024px", height = "768px", resizable = true)
public class MessageEntityBrowser extends StandardListView<MessageEntity> {

    @Autowired
    protected MessageHelper messageHelper;

    @Subscribe("table.apply")
    public void onTableApply(ActionPerformedEvent event) {
        messageHelper.reloadMessages();
    }

}
