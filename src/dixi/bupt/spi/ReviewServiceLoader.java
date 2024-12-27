package dixi.bupt.spi;

/**
 * SPI机制
 * @author chendixi
 * Created on 2024-12-26
 */
public class ReviewServiceLoader {

    public static void main(String[] args) {
        PaymentService service = ServiceFactory.getFirstAvailableService();
        service.pay(10);
    }
}
