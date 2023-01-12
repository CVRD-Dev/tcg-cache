package com.cvrd.tcgCache.records;

public record Sku(int skuId, int productId, int languageId, int printingId, int conditionId, int count) {

    public Sku withCount(int count) {
        return new Sku(skuId(), productId(), languageId(), printingId(), conditionId(), count);
    }
}
