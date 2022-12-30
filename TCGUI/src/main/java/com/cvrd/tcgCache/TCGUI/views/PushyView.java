package com.cvrd.tcgCache.TCGUI.views;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.Route;

@Route("push")
public class PushyView extends VerticalLayout {
    private FeederThread thread;
    private ProgressBar progressBar;
    @Override
    protected void onAttach(AttachEvent attachEvent) {
        progressBar = new ProgressBar();
        add(new Span("Waiting for updates"), progressBar);

        // Start the data feed thread
        thread = new FeederThread(attachEvent.getUI(), this);
        thread.start();
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        // Cleanup
        thread.interrupt();
        thread = null;
    }

    private void updateProgressBar(double value) {
        progressBar.setValue(value);
    }

    private static class FeederThread extends Thread {
        private final UI ui;
        private final PushyView view;

        private double count = 0;

        public FeederThread(UI ui, PushyView view) {
            this.ui = ui;
            this.view = view;
        }

        @Override
        public void run() {
            try {
                // Update the data for a while
                while (count < 1.0) {
                    // Sleep to emulate background work
                    Thread.sleep(1000);
                    count+=.01;
                    String message = "This is update " + count;
                    ui.access(() -> {
                        view.updateProgressBar(count);
                        view.add(new Span(message));
                    });
                }

                // Inform that we're done
                ui.access(() -> {
                    view.add(new Span("Done updating"));
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}