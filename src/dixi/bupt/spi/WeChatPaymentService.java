package dixi.bupt.spi;

/**
 * @author chendixi
 * Created on 2024-12-26
 */
public class WeChatPaymentService implements PaymentService {
    @Override
    public void pay(int amount) {
        System.out.println("Using Wechat Pay to pay " + amount + " dollars.");
    }
}
