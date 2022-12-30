package com.cvrd.tcgCache.records;

public record Category(int categoryId, String name, String displayName, int tracking) {

    public Category setCollecting(int collecting) {
        return new Category(categoryId(), name(), displayName(), collecting);
    }
}
