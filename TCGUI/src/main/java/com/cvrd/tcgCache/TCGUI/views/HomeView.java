package com.cvrd.tcgCache.TCGUI.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.router.Route;

@Route(value = "home")
public class HomeView extends AppLayout {


    public HomeView() {
        Div content = new Div(new H1("Collection Cache"));
        Div collectionDiv = new Div(new Icon("book"), new H2("Collection"));
        collectionDiv.addClickListener(e -> {
            collectionDiv.getUI().ifPresent(ui -> {
                ui.navigate("categories");
            });
        });
        content.add(collectionDiv);
        setContent(content);

    }
}
