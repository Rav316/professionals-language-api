package ru.alex.professionalslanguageapi.util;

import lombok.experimental.UtilityClass;
import org.springframework.core.NestedExceptionUtils;

@UtilityClass
public class ExceptionUtils {
    public static String getSqlExceptionMessage(Exception exception) {
        String errorMessage = NestedExceptionUtils.getMostSpecificCause(exception).getMessage();
        String prefix = "Detail:";
        int startIndex = errorMessage.indexOf(prefix);
        if(startIndex != -1) {
            String detailedMessage = errorMessage.substring(startIndex + prefix.length()).trim();
            return detailedMessage.replace("\"", "'");
        }
        return null;
    }
}
