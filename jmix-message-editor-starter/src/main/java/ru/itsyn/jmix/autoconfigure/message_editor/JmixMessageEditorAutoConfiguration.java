package ru.itsyn.jmix.autoconfigure.message_editor;

import ru.itsyn.jmix.message_editor.JmixMessageEditorConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({JmixMessageEditorConfiguration.class})
public class JmixMessageEditorAutoConfiguration {
}

