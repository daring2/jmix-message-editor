package ru.itsyn.jmix.autoconfigure.message_editor;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import ru.itsyn.jmix.message_editor.JmixMessageEditorConfiguration;

@AutoConfiguration
@Import({JmixMessageEditorConfiguration.class})
public class JmixMessageEditorAutoConfiguration {
}

