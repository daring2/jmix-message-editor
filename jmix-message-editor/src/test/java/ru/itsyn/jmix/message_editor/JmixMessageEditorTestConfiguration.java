package ru.itsyn.jmix.message_editor;

import io.jmix.core.annotation.JmixModule;
import io.jmix.core.security.InMemoryUserRepository;
import io.jmix.core.security.UserRepository;
import io.jmix.security.SecurityConfiguration;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

@SpringBootConfiguration
@EnableAutoConfiguration
@Import(JmixMessageEditorConfiguration.class)
@PropertySource("classpath:/ru/itsyn/jmix/message_editor/test-app.properties")
@JmixModule(id = "ru.itsyn.jmix.message_editor.test", dependsOn = {
        JmixMessageEditorConfiguration.class,
        SecurityConfiguration.class
})
public class JmixMessageEditorTestConfiguration {

    @Bean
    @Primary
    DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .generateUniqueName(true)
                .setType(EmbeddedDatabaseType.HSQL)
                .build();
    }

    @Bean
    UserRepository userRepository() {
        return new InMemoryUserRepository();
    }

}
