package ru.itsyn.jmix.message_editor.screen.message;

import io.jmix.ui.navigation.Route;
import io.jmix.ui.screen.LookupComponent;
import io.jmix.ui.screen.StandardLookup;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;
import ru.itsyn.jmix.message_editor.entity.MessageEntity;

@Route("MessageEntity")
@UiController("msg_MessageEntity.browse")
@UiDescriptor("message-entity-browser.xml")
@LookupComponent("table")
public class MessageEntityBrowser extends StandardLookup<MessageEntity> {
}
