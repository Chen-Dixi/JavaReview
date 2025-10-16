package dixi.bupt.designpattern.strategy;

/**
 * @author chendixi
 * Created on 2025-09-02
 */
public class Calculator {
    private CalculateStrategy strategy;

    // 设置策略
    public void setStrategy(CalculateStrategy strategy) {
        this.strategy = strategy;
    }

    // 执行策略
    public int executeStrategy(int num1, int num2) {
        return strategy.doOperation(num1, num2);
    }
}
