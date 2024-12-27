package dixi.bupt.loadbalance;

import java.util.List;

/**
 * 集合哈希 Rendezvous Hashing,
 * 最高随机权重哈希 HRW
 * <a href="https://blog.csdn.net/m0_53157173/article/details/132218055">一致性哈希的前世今身</a>
 * Created on 2024-11-05
 */
@SuppressWarnings("checkstyle:MagicNumber")
public class HRWHash {

    private ProviderNode doSelect(RpcRequest request, List<ProviderNode> providerNodes) throws RuntimeException {


        if (null == request.getHashKey()) {
            throw new RuntimeException("没有指定Hash Key");
        }

        long hashKey = request.getHashKey();

        double maxWeight = 0.0;
        ProviderNode chosen = null;

        for (ProviderNode providerNode: providerNodes) {
            //
            double w = calculateWeight(hashKey, providerNode, providerNode.getWeight());
            if (maxWeight < w) {
                maxWeight = w;
                chosen = providerNode;
            }
        }
        return chosen;
    }

    private double calculateWeight(long key, ProviderNode server, int weight) {
        double score = 1.1;
        return score * weight;
    }
}
