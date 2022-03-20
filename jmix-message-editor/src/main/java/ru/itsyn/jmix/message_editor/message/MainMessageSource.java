package ru.itsyn.jmix.message_editor.message;

import io.jmix.core.impl.JmixMessageSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Locale;

@Primary
@Component("msg_MainMessageSource")
public class MainMessageSource implements MessageSource {

    @Autowired
    protected JmixMessageSource jmixMessageSource;

    protected volatile MessageSource parentMessageSource;

    @PostConstruct
    public void init() {
        parentMessageSource = jmixMessageSource;
    }

    public MessageSource getParentMessageSource() {
        return parentMessageSource;
    }

    public void setParentMessageSource(MessageSource parentMessageSource) {
        this.parentMessageSource = parentMessageSource;
    }

    @Override
    public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        return parentMessageSource.getMessage(code, args, defaultMessage, locale);
    }

    @Override
    public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
        return parentMessageSource.getMessage(code, args, locale);
    }

    @Override
    public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
        return parentMessageSource.getMessage(resolvable, locale);
    }

}
