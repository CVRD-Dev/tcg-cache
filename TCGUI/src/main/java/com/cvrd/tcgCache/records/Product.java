package com.cvrd.tcgCache.records;

public record Product(int productId, String name, String cleanName, String imageUrl, int categoryId, int groupId, String url) {
}
