package com.zperkowski.peersharing.views;

import static com.gluonhq.charm.glisten.afterburner.DefaultDrawerManager.DRAWER_LAYER;
import com.gluonhq.charm.glisten.afterburner.GluonPresenter;
import com.gluonhq.charm.glisten.animation.BounceInRightTransition;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.layout.layer.FloatingActionButton;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.zperkowski.peersharing.PeerSharing;
import javafx.fxml.FXML;

import java.util.ResourceBundle;

public class ConnectedDevicePresenter extends GluonPresenter<PeerSharing> {

    @FXML
    private View connecteddevice;

    @FXML
    private ResourceBundle resources;

    public void initialize() {
        connecteddevice.setShowTransitionFactory(BounceInRightTransition::new);
        
        connecteddevice.getLayers().add(new FloatingActionButton(MaterialDesignIcon.INFO.text,
            e -> System.out.println("Info")).getLayer());
        
        connecteddevice.showingProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                AppBar appBar = getApp().getAppBar();
                appBar.setNavIcon(MaterialDesignIcon.MENU.button(e -> 
                        getApp().showLayer(DRAWER_LAYER)));
                appBar.setTitleText(resources.getString("title"));
                appBar.getActionItems().add(MaterialDesignIcon.FAVORITE.button(e -> 
                        System.out.println("Favorite")));
            }
        });
    }
}
