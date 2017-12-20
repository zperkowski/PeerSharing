package com.zperkowski.peersharing.views;

import static com.gluonhq.charm.glisten.afterburner.DefaultDrawerManager.DRAWER_LAYER;
import com.gluonhq.charm.glisten.afterburner.GluonPresenter;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.zperkowski.peersharing.PeerSharing;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DevicesListPresenter extends GluonPresenter<PeerSharing> {

    @FXML
    private View deviceslist;

    @FXML
    private Label label;

    @FXML
    private ResourceBundle resources;

    public void initialize() {
        deviceslist.showingProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                AppBar appBar = getApp().getAppBar();
                appBar.setNavIcon(MaterialDesignIcon.MENU.button(e -> 
                        getApp().showLayer(DRAWER_LAYER)));
                appBar.setTitleText(resources.getString("app_name"));
                appBar.getActionItems().add(MaterialDesignIcon.SEARCH.button(e -> 
                        System.out.println(resources.getString("search"))));
            }
        });
    }
    
    @FXML
    void buttonClick() {
        label.setText(resources.getString("label.text.2"));
    }

}
