package com.example.guiex1.controller;

import com.example.guiex1.domain.Prietenie;
import com.example.guiex1.domain.Profile;
import com.example.guiex1.domain.Utilizator;
import com.example.guiex1.domain.ValidationException;
import com.example.guiex1.services.UtilizatorService;
import com.example.guiex1.utils.events.Event;
import com.example.guiex1.utils.observer.Observer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.List;

public class ProfileController implements Observer<Event> {
    @FXML
    private ImageView profileImageView;

    @FXML
    private Label userNameLabel;

    @FXML
    private Label friendsCountLabel;

    @FXML
    private Label descriptionLabel;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField imageurlField;


    private UtilizatorService service;
    Profile profilePage;
    Utilizator utilizator;
    @FXML
    private void initialize() {
    }

    public void setService(UtilizatorService service,  Profile profile, Utilizator u) {
        this.service = service;
        this.profilePage = profile;
        this.utilizator =u;
        profilePage = service.getProfile(this.utilizator.getId());
        System.out.println(profilePage);
        descriptionLabel.setText(profilePage.getDescription());
        profileImageView.setImage(new Image(profilePage.getImageURL()));

        userNameLabel.setText(utilizator.getFirstName()+" "+utilizator.getLastName());


        friendsCountLabel.setText(this.service.getFriend(utilizator)+" friends");

        service.addObserver(this);
    }

    public void handleChangeProfileAction(ActionEvent actionEvent) {
        String imageUrl = imageurlField.getText();
        String description = descriptionField.getText();
        if(imageUrl != null && description != null) {
            long id = utilizator.getId();
            Profile p = new Profile(imageUrl, description, id);
            long id1 = p.getUid();
            service.updateProfile(p);
        }
    }

    @Override
    public void update(Event profileEntityChangedEvent) {
        //System.out.println("Am dat update");
        this.profilePage=service.getProfile(this.utilizator.getId());
        profileImageView.setImage(new Image(profilePage.getImageURL()));
        descriptionLabel.setText(profilePage.getDescription());
    }
}
