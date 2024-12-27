package dixi.bupt.loadbalance;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 权重随机
 * @author chendixi
 * Created on 2024-11-05
 */
public class WeightRandom {

    private ProviderNode doSelect(RpcRequest request, List<ProviderNode> providerNodes) throws RuntimeException {
        int totalWeight = providerNodes.stream()
                .mapToInt(ProviderNode::getWeight)
                .sum();

        int size = providerNodes.size();

        int random = ThreadLocalRandom.current().nextInt(totalWeight);
        for (int i = 0; i < size; i++) {
            ProviderNode node = providerNodes.get(i);
            int weight = node.getWeight();
            random -= weight;
            if (random < 0) {
                return node;
            }
        }
        // 理论上不会走到这里
        return null;
    }
}
