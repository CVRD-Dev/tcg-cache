package com.cvrd.tcgCache.spi;

import com.cvrd.tcgCache.TCGUI.views.DownloadingDataView;
import com.vaadin.flow.component.UI;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.List;

public interface TableService<T> {

    boolean tableExist();
    void createTable();

    List<T> getAll();
    T get(int id);

    void addItems(List<T> items, RecordComponent[] recordComponents) throws InvocationTargetException, IllegalAccessException;

    void updateItem(T item, String idColumn,RecordComponent[] recordComponents) throws InvocationTargetException, IllegalAccessException;

    List<T> conditionalGet(String conditionalSql);

    void setUIComp(UI ui, DownloadingDataView view);

    boolean isTableEmpty();
}
