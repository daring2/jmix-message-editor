package org.springframework.context.support;

import java.util.Locale;
import java.util.Properties;

public class MessageSourceUtils {

    public static Properties getMergedProperties(ReloadableResourceBundleMessageSource messageSource, Locale locale) {
        return messageSource.getMergedProperties(locale).getProperties();
    }

    private MessageSourceUtils() {
    }

}
