package com.example.guiex1.controller;

import com.example.guiex1.domain.Utilizator;
import com.example.guiex1.domain.ValidationException;
import com.example.guiex1.services.UtilizatorService;
import com.example.guiex1.utils.Hashing;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;

import static com.example.guiex1.utils.Hashing.hashPassword;

public class LoginController {
    public UtilizatorService service;
    public TextField firstNameField;
    public TextField lastNameField;
    Stage loginStage;

    @FXML
    private TextField passwordField;

    public void setService(UtilizatorService service, Stage loginStage) {
        this.service = service;
        this.loginStage = loginStage;
    }
    public void handleLogin(ActionEvent actionEvent) throws Exception {
        String password = passwordField.getText();
        Utilizator user = service.searchName(firstNameField.getText(), lastNameField.getText());
        if(firstNameField.getText().equals("admin") && lastNameField.getText().equals("admin") && password.equals("admin")) {
            try {
                // Încarcă view-ul pentru prieteni
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("../views/utilizator-view.fxml"));

                AnchorPane root = loader.load();

                // Creează un dialog Stage.
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Lista de utilizatori");
                dialogStage.initModality(Modality.WINDOW_MODAL);

                Scene scene = new Scene(root);
                dialogStage.setScene(scene);

                // Setează serviciul și utilizatorul în controller
                UtilizatorController friendController = loader.getController();
                friendController.setUtilizatorService(service);

                dialogStage.show();

            } catch (IOException e) {
                MessageAlert.showErrorMessage(null, "Eroare la încărcarea ferestrei Friends!");
                e.printStackTrace();
            }
        }
        if (user == null) {
            MessageAlert.showErrorMessage(null, "Utilizatorul nu este înregistrat!");
        } else {
            if (service.getHashedPasswordFromDB(user.getId()).equals(hashPassword(password))) {
                try {
                    // Încarcă view-ul pentru prieteni
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("../views/friends-view.fxml"));

                    AnchorPane root = loader.load();

                    // Creează un dialog Stage.
                    Stage dialogStage = new Stage();
                    dialogStage.setTitle("Friends");
                    dialogStage.initModality(Modality.WINDOW_MODAL);

                    Scene scene = new Scene(root);
                    dialogStage.setScene(scene);

                    // Setează serviciul și utilizatorul în controller
                    FriendController friendController = loader.getController();
                    friendController.setService(service, dialogStage, user);

                    dialogStage.show();

                } catch (IOException e) {
                    MessageAlert.showErrorMessage(null, "Eroare la încărcarea ferestrei Friends!");
                    e.printStackTrace();
                }
            } else {
                MessageAlert.showErrorMessage(null, "Parola incorecta!");
            }
        }
    }

    public void handleRegister(ActionEvent actionEvent) {
        Utilizator user = service.searchName(firstNameField.getText(), lastNameField.getText());
        if(user != null) {
            MessageAlert.showErrorMessage(null, "Utilizatorul este deja inregistrat!");
        }
        else {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("../views/register-view.fxml"));

                AnchorPane root = (AnchorPane) loader.load();

                // Create the dialog Stage.
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Register");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                //dialogStage.initOwner(primaryStage);
                Scene scene = new Scene(root);
                dialogStage.setScene(scene);

                RegisterController registerController = loader.getController();
                registerController.setService(service, dialogStage);

                dialogStage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
