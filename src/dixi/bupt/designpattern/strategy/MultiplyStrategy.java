package dixi.bupt.designpattern.strategy;

/**
 * @author chendixi
 * Created on 2025-09-02
 */
public class MultiplyStrategy implements CalculateStrategy {
    @Override
    public int doOperation(int num1, int num2) {
        return num1 * num2;
    }
}
