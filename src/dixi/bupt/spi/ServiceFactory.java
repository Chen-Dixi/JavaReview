package dixi.bupt.spi;

import java.util.ServiceLoader;

/**
 * @author chendixi
 * Created on 2024-12-26
 */
public class ServiceFactory {
    public static PaymentService getFirstAvailableService() {
        ServiceLoader<PaymentService> loader = ServiceLoader.load(PaymentService.class);
        for (PaymentService service : loader) {
            return service;
        }
        throw new IllegalStateException("No PaymentService found");
    }
}
