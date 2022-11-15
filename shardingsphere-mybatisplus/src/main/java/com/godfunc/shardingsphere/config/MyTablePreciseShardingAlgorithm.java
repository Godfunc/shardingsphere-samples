package com.godfunc.shardingsphere.config;

import com.godfunc.shardingsphere.util.IdUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;

import java.util.Collection;
import java.util.Properties;

/**
 * 分表算法
 */
@Slf4j
public class MyTablePreciseShardingAlgorithm implements StandardShardingAlgorithm<Long> {
    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Long> shardingValue) {
        Long tableId = IdUtils.getTableId(shardingValue.getValue());
        for (String availableTargetName : availableTargetNames) {
            if (availableTargetName.endsWith(String.valueOf(tableId))) {
                return availableTargetName;
            }
        }
        throw new RuntimeException("分表异常");
    }

    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, RangeShardingValue<Long> shardingValue) {
        log.info("shardingValue={}", shardingValue);
        throw new RuntimeException("范围分表异常");
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
