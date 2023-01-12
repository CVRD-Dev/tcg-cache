package com.cvrd.tcgCache.threads;

import com.cvrd.tcgCache.TCGUI.views.DownloadingDataView;
import com.vaadin.flow.component.UI;

public class DownloadThread extends Thread{

    UI ui;
    DownloadingDataView view;
    public DownloadThread(UI ui, DownloadingDataView view) {
        this.ui = ui;
        this.view = view;
    }

    @Override
    public void run() {

    }
}


