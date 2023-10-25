package ru.itsyn.jmix.message_editor.screen.caption;

import io.jmix.core.LoadContext;
import io.jmix.core.MessageTools;
import io.jmix.core.Metadata;
import io.jmix.core.MetadataTools;
import io.jmix.core.metamodel.model.MetaProperty;
import io.jmix.ui.Notifications;
import io.jmix.ui.WindowConfig;
import io.jmix.ui.WindowInfo;
import io.jmix.ui.action.Action.ActionPerformedEvent;
import io.jmix.ui.component.ComboBox;
import io.jmix.ui.component.HasValue.ValueChangeEvent;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.navigation.Route;
import io.jmix.ui.screen.*;
import io.jmix.ui.sys.ScreensHelper;
import org.springframework.beans.factory.annotation.Autowired;
import ru.itsyn.jmix.message_editor.entity.CaptionEntity;
import ru.itsyn.jmix.message_editor.message.MessageHelper;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.TreeMap;

@Route("CaptionEntity")
@UiController("msg_CaptionEntity.browse")
@UiDescriptor("caption-entity-browser.xml")
@LookupComponent("table")
public class CaptionEntityBrowser extends StandardLookup<CaptionEntity> {

    @Autowired
    protected Metadata metadata;
    @Autowired
    protected MetadataTools metadataTools;
    @Autowired
    protected MessageTools messageTools;
    @Autowired
    protected WindowConfig windowConfig;
    @Autowired
    protected ScreensHelper screensHelper;
    @Autowired
    protected Notifications notifications;
    @Autowired
    protected MessageBundle messageBundle;
    @Autowired
    protected CaptionEntityHelper captionEntityHelper;
    @Autowired
    protected MessageHelper messageHelper;
    @Autowired
    protected CollectionLoader<CaptionEntity> tableDl;
    @Autowired
    protected ComboBox<Object> entityFilterField;

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        initEntityFilterField();
    }

    protected void initEntityFilterField() {
        var options = new TreeMap<String, Object>();
        metadata.getClasses().forEach(mc ->
                options.put(messageTools.getDetailedEntityCaption(mc), mc)
        );
        metadata.getClasses().stream()
                .flatMap(mc -> mc.getOwnProperties().stream())
                .filter(mp -> mp.getRange().isEnum())
                .forEach(mp -> {
                    var enumType = mp.getRange().asEnumeration();
                    options.put(getEnumCaption(mp), enumType);
                });
        windowConfig.getWindows().forEach(wi ->
                options.put(getScreenCaption(wi), wi)
        );
        entityFilterField.setOptionsMap(options);
    }

    @Install(to = "tableDl", target = Target.DATA_LOADER)
    protected List<CaptionEntity> tableDlLoadDelegate(LoadContext<CaptionEntity> loadContext) {
        var entity = entityFilterField.getValue();
        return captionEntityHelper.buildCaptions(entity);
    }

    @Subscribe("entityFilterField")
    public void onEntityFilterChange(ValueChangeEvent event) {
        if (!event.isUserOriginated())
            return;
        tableDl.load();
    }

    @Subscribe("table.apply")
    public void onTableApply(ActionPerformedEvent event) {
        messageHelper.reloadMessages();
        tableDl.load();
        notifications.create()
                .withCaption(messageBundle.getMessage("changesApplied"))
                .show();
    }

    protected String getEnumCaption(MetaProperty property) {
        var enumType = property.getRange().asEnumeration();
        return messageTools.getPropertyCaption(property) +
                " (" + enumType.getJavaClass().getSimpleName() + ")";
    }

    protected String getScreenCaption(WindowInfo windowInfo) {
        try {
            return screensHelper.getDetailedScreenCaption(windowInfo);
        } catch (FileNotFoundException e) {
            return windowInfo.getId();
        }
    }

}
