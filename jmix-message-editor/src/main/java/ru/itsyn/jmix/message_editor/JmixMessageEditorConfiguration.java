package ru.itsyn.jmix.message_editor;

import io.jmix.core.JmixModules;
import io.jmix.core.Resources;
import io.jmix.core.annotation.JmixModule;
import io.jmix.core.impl.JmixMessageSource;
import io.jmix.core.impl.scanning.AnnotationScanMetadataReaderFactory;
import io.jmix.eclipselink.EclipselinkConfiguration;
import io.jmix.flowui.FlowuiConfiguration;
import io.jmix.flowui.sys.ActionsConfiguration;
import io.jmix.flowui.sys.ViewControllersConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import static java.util.Collections.singletonList;

@Configuration
@ComponentScan
@ConfigurationPropertiesScan
@JmixModule(dependsOn = {EclipselinkConfiguration.class, FlowuiConfiguration.class})
@PropertySource(name = "ru.itsyn.jmix.message_editor", value = "classpath:/ru/itsyn/jmix/message_editor/module.properties")
public class JmixMessageEditorConfiguration {

    @Bean
    public JmixMessageSource jmixMessageSource(JmixModules modules, Resources resources) {
        return new JmixMessageSource(modules, resources);
    }

    @Bean("msg_MsgViewControllers")
    public ViewControllersConfiguration screens(
            ApplicationContext applicationContext,
            AnnotationScanMetadataReaderFactory metadataReaderFactory
    ) {
        var viewControllers = new ViewControllersConfiguration(applicationContext, metadataReaderFactory);
        viewControllers.setBasePackages(singletonList("ru.itsyn.jmix.message_editor"));
        return viewControllers;
    }

    @Bean("msg_MsgActions")
    public ActionsConfiguration actions(
            ApplicationContext applicationContext,
            AnnotationScanMetadataReaderFactory metadataReaderFactory
    ) {
        var actions = new ActionsConfiguration(applicationContext, metadataReaderFactory);
        actions.setBasePackages(singletonList("ru.itsyn.jmix.message_editor"));
        return actions;
    }

}
