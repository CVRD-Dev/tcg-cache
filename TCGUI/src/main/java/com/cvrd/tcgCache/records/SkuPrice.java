package com.cvrd.tcgCache.records;

import java.math.BigDecimal;

public record SkuPrice(int skuId, BigDecimal lowPrice, BigDecimal marketPrice) {
}
