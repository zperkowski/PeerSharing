package com.zperkowski.peersharing;

import com.zperkowski.peersharing.views.AppViewManager;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.visual.Swatch;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class PeerSharing extends MobileApplication {

    @Override
    public void init() {
        AppViewManager.registerViewsAndDrawer(this);
    }

    @Override
    public void postInit(Scene scene) {
        Swatch.BLUE.assignTo(scene);

        scene.getStylesheets().add(PeerSharing.class.getResource("style.css").toExternalForm());
        ((Stage) scene.getWindow()).getIcons().add(new Image(PeerSharing.class.getResourceAsStream("/icon.png")));
    }
}
