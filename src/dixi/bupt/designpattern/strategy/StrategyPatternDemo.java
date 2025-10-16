package dixi.bupt.designpattern.strategy;

/**
 * @author chendixi
 * Created on 2025-09-02
 */
public class StrategyPatternDemo {
    public static void main(String[] args) {
        Calculator calculator = new Calculator();

        // 使用加法策略
        calculator.setStrategy(new AddStrategy());
        System.out.println("10 + 5 = " + calculator.executeStrategy(10, 5));

        // 使用减法策略
        calculator.setStrategy(new SubtractStrategy());
        System.out.println("10 - 5 = " + calculator.executeStrategy(10, 5));

        // 使用乘法策略
        calculator.setStrategy(new MultiplyStrategy());
        System.out.println("10 * 5 = " + calculator.executeStrategy(10, 5));
    }
}
