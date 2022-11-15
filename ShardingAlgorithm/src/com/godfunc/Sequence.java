/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.godfunc;


import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 */
public class Sequence {
    /**
     * 时间起始标记点，作为基准，一般取系统的最近时间（一旦确定不能变动）
     */
    private final long twepoch = 1288834974657L;

    /**
     * 分库分表占用位数
     * 建议 数据库：2   表：4
     */
    private final long dataIdBits = 6L;

    /**
     * 机器标识占用位数
     */
    private final long workerIdBits = 4L;
    /**
     * 机器标识的最大值
     */
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);
    /**
     * 毫秒内自增位
     */
    private final long sequenceBits = 12L;
    /**
     * 数据库标识左移动位
     */
    private final long dataIdShift = sequenceBits;
    /**
     * 机器标识左移位
     */
    private final long workerIdShift = sequenceBits + dataIdBits;
    /**
     * 时间戳左移动位
     */
    private final long timestampLeftShift = sequenceBits + dataIdBits + workerIdBits;
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    private final long workerId;
    /**
     * 数据库的最大值
     */
    private final long maxDatabaseId;
    /**
     * 表的最大值
     */
    private final long maxTableId;
    private final long tableIdBits;
    /**
     * 并发控制
     */
    private long sequence = 0L;
    /**
     * 上次生产 ID 时间戳
     */
    private long lastTimestamp = -1L;

    /**
     * 有参构造器
     *
     * @param workerId 工作机器 ID
     */
    public Sequence(long workerId, long databaseIdBits, long tableBits) {
        assert workerId <= maxWorkerId && workerId >= 0;
        assert databaseIdBits <= dataIdBits && databaseIdBits >= 0;
        assert tableBits <= dataIdBits && tableBits >= 0;
        assert (databaseIdBits + tableBits) <= dataIdBits;
        this.workerId = workerId;
        this.maxDatabaseId = -1L ^ (-1L << databaseIdBits);
        this.maxTableId = -1L ^ (-1L << tableBits);
        this.tableIdBits = tableBits;
    }

    /**
     * 获取当前id中表的id
     *
     * @param id
     * @return
     */
    public Long getDatabaseId(long id) {
        return id >> (sequenceBits + tableIdBits) & this.maxDatabaseId;
    }

    public Long getTableId(long id) {
        return id >> sequenceBits & this.maxTableId;
    }

    /**
     * 获取下一个 ID
     *
     * @return 下一个 ID
     */
    public synchronized long nextId(long databaseId, long tableId) {
        assert databaseId <= maxDatabaseId && databaseId >= 0;
        assert tableId <= maxTableId && tableId >= 0;
        long timestamp = timeGen();
        //闰秒
        if (timestamp < lastTimestamp) {
            long offset = lastTimestamp - timestamp;
            if (offset <= 5) {
                try {
                    wait(offset << 1);
                    timestamp = timeGen();
                    if (timestamp < lastTimestamp) {
                        throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", offset));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", offset));
            }
        }

        if (lastTimestamp == timestamp) {
            // 相同毫秒内，序列号自增
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                // 同一毫秒的序列数已经达到最大
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 不同毫秒内，序列号置为 1 - 3 随机数
            sequence = ThreadLocalRandom.current().nextLong(1, 3);
        }

        lastTimestamp = timestamp;

        // 时间戳部分 | 机器标识部分 | 分库分表标识 | 序列号部分
        return ((timestamp - twepoch) << timestampLeftShift) | (workerId << workerIdShift) | (((databaseId << 4) | tableId) << dataIdShift) | sequence;
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }


    protected long timeGen() {
        return SystemClock.now();
    }


}
