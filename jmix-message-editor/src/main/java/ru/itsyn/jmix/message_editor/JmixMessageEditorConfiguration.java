package ru.itsyn.jmix.message_editor;

import io.jmix.core.annotation.JmixModule;
import io.jmix.core.impl.scanning.AnnotationScanMetadataReaderFactory;
import io.jmix.eclipselink.EclipselinkConfiguration;
import io.jmix.ui.UiConfiguration;
import io.jmix.ui.sys.UiControllersConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Collections;

@Configuration
@ComponentScan
@ConfigurationPropertiesScan
@JmixModule(dependsOn = {EclipselinkConfiguration.class, UiConfiguration.class})
@PropertySource(name = "ru.itsyn.jmix.message_editor", value = "classpath:/ru/itsyn/jmix/message_editor/module.properties")
public class JmixMessageEditorConfiguration {

    @Bean("msg_JmixMessageEditorUiControllers")
    public UiControllersConfiguration screens(
            ApplicationContext applicationContext,
            AnnotationScanMetadataReaderFactory metadataReaderFactory
    ) {
        var uiControllers = new UiControllersConfiguration(applicationContext, metadataReaderFactory);
        uiControllers.setBasePackages(Collections.singletonList("ru.itsyn.jmix.message_editor"));
        return uiControllers;
    }
}
