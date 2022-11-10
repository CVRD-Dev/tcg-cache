package com.cvrd.tcgCache.spi;

import java.util.List;

public interface TableService<T> {

    boolean tableExist();
    void createTable();

    List<T> getAll();
    T get(int id);

    void addItems(List<T> items);
}
