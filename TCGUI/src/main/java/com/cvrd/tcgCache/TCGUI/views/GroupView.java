package com.cvrd.tcgCache.TCGUI.views;

import com.cvrd.tcgCache.records.Category;
import com.cvrd.tcgCache.records.Group;
import com.cvrd.tcgCache.spi.DatabaseService;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.NativeButtonRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterListener;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route(value = "groups/:categoryId")
public class GroupView extends VerticalLayout implements BeforeEnterObserver {

    private int categoryId = 0;

    @Autowired
    DatabaseService dbService;

    public GroupView(DatabaseService dbService) {
        this.dbService = dbService;


    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        this.categoryId = Integer.parseInt(event.getRouteParameters().get("categoryId").get());

        List<Group> groups = dbService.groupService().conditionalGet(String.format("WHERE categoryId=%s",categoryId));

        VirtualList<Group> groupVirtualList = new VirtualList<>();
        groupVirtualList.setItems(groups);
        groupVirtualList.setRenderer(new NativeButtonRenderer<Group>(
                item -> item.name(),
                clickedItem -> {
                    groupVirtualList.getUI().ifPresent(ui -> {
                        ui.navigate(String.format("products/%s", clickedItem.groupId()));
                    });
                }));
        add(groupVirtualList);
    }
}
