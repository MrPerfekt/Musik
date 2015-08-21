package at.musik.web.rest.util;

import org.springframework.http.HttpHeaders;

/**
 * Utility class for http header creation.
 *
 */
public class HeaderUtil {
 
    public static HttpHeaders createAlert(String message, String param) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-musikApp-alert", message);
        headers.add("X-musikApp-params", param);
        return headers;
    }

    public static HttpHeaders createEntityCreationAlert(String entityName, String param) {
        return createAlert("musikApp." + entityName + ".created", param);
    }

    public static HttpHeaders createEntityUpdateAlert(String entityName, String param) {
        return createAlert("musikApp." + entityName + ".updated", param);
    }

    public static HttpHeaders createEntityDeletionAlert(String entityName, String param) {
        return createAlert("musikApp." + entityName + ".deleted", param);
    }

}