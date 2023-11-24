package ru.itsyn.jmix.message_editor.screen.caption;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.ItemDoubleClickEvent;
import com.vaadin.flow.router.Route;
import io.jmix.core.LoadContext;
import io.jmix.core.MessageTools;
import io.jmix.core.Metadata;
import io.jmix.core.MetadataTools;
import io.jmix.core.metamodel.model.MetaProperty;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.action.list.EditAction;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.sys.ViewSupport;
import io.jmix.flowui.view.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import ru.itsyn.jmix.message_editor.entity.CaptionEntity;
import ru.itsyn.jmix.message_editor.message.MessageHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Route(value = "CaptionEntity", layout = DefaultMainViewParent.class)
@ViewController("msg_CaptionEntity.list")
@ViewDescriptor("caption-entity-browser.xml")
@LookupComponent("table")
@DialogMode(width = "1024px", height = "768px", resizable = true)
public class CaptionEntityBrowser extends StandardListView<CaptionEntity> {

    @Autowired
    protected Metadata metadata;
    @Autowired
    protected MetadataTools metadataTools;
    @Autowired
    protected MessageTools messageTools;
    @Autowired
    protected ViewRegistry viewRegistry;
    @Autowired
    protected ViewSupport viewSupport;
    @Autowired
    protected Notifications notifications;
    @Autowired
    protected MessageBundle messageBundle;
    @Autowired
    protected CaptionEntityHelper captionEntityHelper;
    @Autowired
    protected MessageHelper messageHelper;

    @ViewComponent
    protected CollectionLoader<CaptionEntity> tableDl;
    @ViewComponent
    protected ComboBox<Object> entityFilterField;
    @ViewComponent
    private DataGrid<CaptionEntity> table;
    @ViewComponent("table.edit")
    protected EditAction<CaptionEntity> tableEditAction;

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        initEntityFilterField();
    }

    protected void initEntityFilterField() {
        var options = new HashMap<Object, String>();
        metadata.getClasses().forEach(mc -> {
            options.put(mc, messageTools.getDetailedEntityCaption(mc));
        });
        metadata.getClasses().stream()
                .flatMap(mc -> mc.getOwnProperties().stream())
                .filter(mp -> mp.getRange().isEnum())
                .forEach(mp -> {
                    var enumType = mp.getRange().asEnumeration();
                    options.put(enumType, getEnumCaption(mp));
                });
        viewRegistry.getViewInfos().forEach(viewInfo ->
                options.put(viewInfo, getScreenCaption(viewInfo))
        );
        var items = options.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        entityFilterField.setItems(items);
        entityFilterField.setItemLabelGenerator(options::get);
    }

    @Install(to = "tableDl", target = Target.DATA_LOADER)
    protected List<CaptionEntity> loadData(LoadContext<CaptionEntity> loadContext) {
        var entity = entityFilterField.getValue();
        return captionEntityHelper.buildCaptions(entity);
    }

    @Subscribe("entityFilterField")
    public void onEntityFilterChange(ComponentValueChangeEvent<?, ?> event) {
        if (!event.isFromClient())
            return;
        tableDl.load();
    }

    @Subscribe("table")
    protected void onTableItemDoubleClick(ItemDoubleClickEvent<CaptionEntity> event) {
        table.select(event.getItem());
        tableEditAction.actionPerform(table);
    }

    @Subscribe("table.apply")
    public void onTableApply(ActionPerformedEvent event) {
        messageHelper.reloadMessages();
        tableDl.load();
        notifications.show(messageBundle.getMessage("changesApplied"));
    }

    protected String getEnumCaption(MetaProperty property) {
        var enumType = property.getRange().asEnumeration();
        return messageTools.getPropertyCaption(property) +
                " (" + enumType.getJavaClass().getSimpleName() + ")";
    }

    protected String getScreenCaption(ViewInfo viewInfo) {
        var caption = viewSupport.getLocalizedTitle(viewInfo);
        if (StringUtils.isBlank(caption))
            return viewInfo.getId();
        return caption + " (" + viewInfo.getId() + ")";
    }

}
