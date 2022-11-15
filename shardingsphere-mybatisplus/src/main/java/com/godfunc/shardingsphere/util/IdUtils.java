package com.godfunc.shardingsphere.util;

import java.util.concurrent.ThreadLocalRandom;

public class IdUtils {

    private static final Sequence SEQUENCE = new Sequence(1, 2, 4);

    public static Long getId() {
        return SEQUENCE.nextId(ThreadLocalRandom.current().nextInt(2), ThreadLocalRandom.current().nextInt(3));
    }

    public static Long getId(Long databaseId, Long tableId) {
        return SEQUENCE.nextId(databaseId, tableId);
    }

    public static Long getDatabaseId(Long id) {
        return SEQUENCE.getDatabaseId(id);
    }

    public static Long getTableId(Long id) {
        return SEQUENCE.getTableId(id);
    }
}
