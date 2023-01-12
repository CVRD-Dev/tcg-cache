package com.cvrd.tcgCache.records;

import java.math.BigDecimal;

public record Item(BigDecimal lowPrice, BigDecimal MarketPrice, String languageAbbr, String Print, String conditionAbbr, int count, String name, String imageUrl) {
}
