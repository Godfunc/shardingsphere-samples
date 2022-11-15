package com.godfunc.shardingsphere.config;

import com.godfunc.shardingsphere.util.IdUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;

import java.util.Collection;
import java.util.Properties;

/**
 * 分库算法
 */
@Slf4j
public class MyDatabasePreciseShardingAlgorithm implements StandardShardingAlgorithm<Long> {

    public static ThreadLocal<Long> databaseLocal = new ThreadLocal<>();

    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Long> shardingValue) {
        Long databaseId = IdUtils.getDatabaseId(shardingValue.getValue());
        for (String av : availableTargetNames) {
            if (av.endsWith(String.valueOf(databaseId))) {
                return av;
            }
        }
        throw new RuntimeException("分库异常");
    }

    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, RangeShardingValue<Long> shardingValue) {
        throw new RuntimeException("范围分库异常");
    }



    @Override
    public String getType() {
        return null;
    }

    @Override
    public Properties getProps() {
        return null;
    }


    @Override
    public void init(Properties properties) {

    }
}
