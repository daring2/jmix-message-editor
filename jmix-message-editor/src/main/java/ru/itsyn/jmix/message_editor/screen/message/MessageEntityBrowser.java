package ru.itsyn.jmix.message_editor.screen.message;

import io.jmix.ui.action.Action.ActionPerformedEvent;
import io.jmix.ui.navigation.Route;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;
import ru.itsyn.jmix.message_editor.entity.MessageEntity;
import ru.itsyn.jmix.message_editor.message.MessageHelper;

@Route("MessageEntity")
@UiController("msg_MessageEntity.browse")
@UiDescriptor("message-entity-browser.xml")
@LookupComponent("table")
public class MessageEntityBrowser extends StandardLookup<MessageEntity> {

    @Autowired
    protected MessageHelper messageHelper;

    @Subscribe("table.apply")
    public void onTableApply(ActionPerformedEvent event) {
        messageHelper.reloadMessages();
    }

}
