package com.zperkowski.peersharing.views;

import static com.gluonhq.charm.glisten.afterburner.DefaultDrawerManager.DRAWER_LAYER;
import com.gluonhq.charm.glisten.afterburner.GluonPresenter;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.CharmListView;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.zperkowski.peersharing.PeerSharing;
import com.zperkowski.peersharing.service.FilesService;

import java.util.Date;
import java.util.ResourceBundle;

import com.zperkowski.peersharing.model.File;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import javax.inject.Inject;

public class DevicesListPresenter extends GluonPresenter<PeerSharing> {

    @Inject private FilesService filesService;

    @FXML private View deviceslist;
    @FXML private Label label;
    @FXML private ResourceBundle resources;
    @FXML private CharmListView<File, Date> charList_files; // TODO: Init this list.

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
        System.out.println(filesService.getListOfFiles("."));
    }
    
    @FXML
    void buttonClick() {
        label.setText(resources.getString("label.text.2"));
    }

}
