# ShardingAlgorithm
精准分片id生成器

基于[雪花算法](https://gitee.com/yu120/sequence)的精准分片。

`1bit不可用 + 41bit时间戳 + 4bit工作机器id + 6bit分库分表标识 + 12bit序列号`