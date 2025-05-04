package com.mylearn.agentgate.utils.shardings;

import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class UserIdShardingAlgorithm  implements StandardShardingAlgorithm<String> {
    /**
     * 偶数进入第一个库，奇数进入第二个库
     * @param collection
     * @param preciseShardingValue
     * @return
     */
    @Override
    public String doSharding(Collection<String> collection, PreciseShardingValue<String> preciseShardingValue) {
        String userId = preciseShardingValue.getValue();
        int i = userId.length() % 2;
        Iterator<String> iterator = collection.iterator();
        for (int i1 = 0; i1 < i; i1++) {
            iterator.next();
        }
        return iterator.next();
    }

    /**
     * 确定不会使用，空置了不写
     * @param collection
     * @param rangeShardingValue
     * @return
     */
    @Override
    public Collection<String> doSharding(Collection<String> collection, RangeShardingValue<String> rangeShardingValue) {
        return List.of();
    }


    @Override
    public String getType() {
        return "USERID_SPI";
    }
}
