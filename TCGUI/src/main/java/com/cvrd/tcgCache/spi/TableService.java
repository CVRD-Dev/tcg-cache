package com.cvrd.tcgCache.spi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.List;

public interface TableService<T> {

    boolean tableExist();
    void createTable();

    List<T> getAll();
    T get(int id);

    void addItems(List<T> items, RecordComponent[] recordComponents) throws InvocationTargetException, IllegalAccessException;

    void updateItem(T item);

    List<T> conditionalGet(String conditionalSql);
}
