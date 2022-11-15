package com.godfunc.shardingsphere.config;

import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.godfunc.shardingsphere.util.Sequence;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 为分片key和普通id生成id的生成器
 */
public class ShardingIdentifierGeneratorWrapper implements IdentifierGenerator {
    private final Sequence sequence;
    private DefaultIdentifierGenerator defaultIdentifierGenerator;

    public ShardingIdentifierGeneratorWrapper(Long workerId, Long dataCenterId, Long databaseIdBits, Long tableBits) {
        sequence = new Sequence(Optional.ofNullable(workerId).orElse(1L),
                Optional.ofNullable(databaseIdBits).orElse(2L),
                Optional.ofNullable(tableBits).orElse(4L));
        defaultIdentifierGenerator = new DefaultIdentifierGenerator(Optional.ofNullable(workerId).orElse(1L), dataCenterId);


    }

    @Override
    public Long nextId(Object entity) {
        try {
            entity.getClass().getDeclaredMethod("getUserId");
            // should be taken from the weights
            // 可以通过表统计来决定数据进入哪个库表合适
            return sequence.nextId(ThreadLocalRandom.current().nextLong(2), ThreadLocalRandom.current().nextInt(4));
        } catch (NoSuchMethodException e) {
        }
        return defaultIdentifierGenerator.nextId(entity);
    }
}
