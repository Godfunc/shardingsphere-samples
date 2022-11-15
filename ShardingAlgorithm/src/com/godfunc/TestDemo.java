package com.godfunc;

import java.util.Random;

public class TestDemo {

    public static void main(String[] args) {
        Sequence s = new Sequence(1L, 2, 4);
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            int database = random.nextInt(4);
            int table = random.nextInt(16);
            long l = s.nextId(database, table);
            Long databaseIdResult = s.getDatabaseId(l);
            Long tableIdResult = s.getTableId(l);
            System.out.println(databaseIdResult);
            System.out.println(tableIdResult);
            if (database != databaseIdResult || table != tableIdResult) {
                throw new RuntimeException();
            }
        }
    }
}
