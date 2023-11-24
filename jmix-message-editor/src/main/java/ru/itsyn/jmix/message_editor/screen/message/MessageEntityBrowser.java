package ru.itsyn.jmix.message_editor.screen.message;

import com.vaadin.flow.component.grid.ItemDoubleClickEvent;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.action.list.EditAction;
import io.jmix.flowui.component.grid.DataGrid;
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

    @ViewComponent
    private DataGrid<MessageEntity> table;
    @ViewComponent("table.edit")
    protected EditAction<MessageEntity> tableEditAction;

    @Subscribe("table")
    protected void onTableItemDoubleClick(ItemDoubleClickEvent<MessageEntity> event) {
        table.select(event.getItem());
        tableEditAction.actionPerform(table);
    }

    @Subscribe("table.apply")
    public void onTableApply(ActionPerformedEvent event) {
        messageHelper.reloadMessages();
    }

}
