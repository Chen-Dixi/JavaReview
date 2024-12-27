package dixi.bupt.spi;

/**
 * @author chendixi
 * Created on 2024-12-26
 */
public class AlipayPaymentService implements PaymentService {
    @Override
    public void pay(int amount) {
        System.out.println("Using Alipay to pay " + amount + " dollars.");
    }
}
