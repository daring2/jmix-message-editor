package ru.itsyn.jmix.message_editor.screen.caption;

import io.jmix.core.MessageTools;
import io.jmix.core.Messages;
import io.jmix.core.Metadata;
import io.jmix.core.metamodel.datatype.Enumeration;
import io.jmix.core.metamodel.model.MetaClass;
import io.jmix.core.metamodel.model.MetaProperty;
import io.jmix.ui.WindowInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.itsyn.jmix.message_editor.entity.CaptionEntity;
import ru.itsyn.jmix.message_editor.message.MessageHelper;

import java.util.ArrayList;
import java.util.List;

@Component("msg_CaptionEntityHelper")
public class CaptionEntityHelper {

    @Autowired
    protected Metadata metadata;
    @Autowired
    protected Messages messages;
    @Autowired
    protected MessageTools messageTools;
    @Autowired
    protected MessageHelper messageHelper;

    public List<CaptionEntity> buildCaptions(Object object) {
        var entities = new ArrayList<CaptionEntity>();
        if (object instanceof MetaClass) {
            var metaClass = (MetaClass) object;
            entities.add(createCaptionEntity(metaClass));
            for (var property : metaClass.getProperties()) {
                entities.add(createCaptionEntity(property));
            }
        } else if (object instanceof Enumeration<?>) {
            var enumType = (Enumeration<?>) object;
            for (var enumValue : enumType.getValues()) {
                entities.add(createCaptionEntity(enumValue));
            }
        } else if (object instanceof WindowInfo) {
            entities.addAll(buildWindowCaptions((WindowInfo) object));
        }
        return entities;
    }

    public List<CaptionEntity> buildWindowCaptions(WindowInfo windowInfo) {
        var entities = new ArrayList<CaptionEntity>();
        var menuKey = "menu-config." + windowInfo.getId();
        var menuCaption = messages.getMessage(menuKey);
        if (!menuCaption.equals(menuKey))
            entities.add(createCaptionEntity(menuKey, menuCaption));
        var messageGroup = windowInfo.getControllerClass().getPackage().getName();
        var keys = messageHelper.getMessageKeys(messageGroup + "/");
        for (var key : keys) {
            var text = messages.getMessage(key);
            entities.add(createCaptionEntity(key, text));
        }
        return entities;
    }

    public CaptionEntity createCaptionEntity(MetaClass metaClass) {
        return createCaptionEntity(
                getClassKey(metaClass.getJavaClass()),
                messageTools.getEntityCaption(metaClass)
        );
    }

    public CaptionEntity createCaptionEntity(MetaProperty property) {
        return createCaptionEntity(
                getClassKey(property.getDeclaringClass()) + "." + property.getName(),
                messageTools.getPropertyCaption(property)
        );
    }

    public CaptionEntity createCaptionEntity(Enum<?> enumValue) {
        return createCaptionEntity(
                getClassKey(enumValue.getDeclaringClass()) + "." + enumValue.name(),
                messages.getMessage(enumValue)
        );
    }

    public CaptionEntity createCaptionEntity(String key, String text) {
        var entity = metadata.create(CaptionEntity.class);
        entity.setKey(key);
        entity.setText(text);
        return entity;
    }

    public String getClassKey(Class<?> javaClass) {
        return javaClass.getPackageName() + "/" + javaClass.getSimpleName();
    }

}
