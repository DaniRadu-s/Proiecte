package com.example.guiex1.controller;

import com.example.guiex1.domain.Profile;
import com.example.guiex1.domain.Tuple;
import com.example.guiex1.domain.Utilizator;
import com.example.guiex1.domain.ValidationException;
import com.example.guiex1.services.UtilizatorService;
import com.example.guiex1.utils.Hashing;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class RegisterController {
    @FXML
    public TextField lastNameField;
    @FXML
    public TextField firstNameField;
    private UtilizatorService utilizatorService;
    Stage stage;

    @FXML
    private TextField setPasswordField;
    @FXML
    private TextField confirmPasswordField;

    public void setService(UtilizatorService utilizatorService, Stage stage) {
        this.utilizatorService = utilizatorService;
        this.stage = stage;
    }

    public void handleRegister(ActionEvent actionEvent) {
        if(setPasswordField.getText().equals(confirmPasswordField.getText())) {
            try {
                Utilizator utilizator = new Utilizator(firstNameField.getText(),lastNameField.getText());
                saveMessage(utilizator, setPasswordField.getText());
            }
            catch (Exception e) {
                throw new ValidationException("Eroare la hash-uirea parolei!");
            }
        }
        else{
            MessageAlert.showErrorMessage(null,"Parola trebuie sa fie aceeasi!");
        }
    }

    private void updateMessage(Utilizator m, String s)
    {
        try {
            Utilizator r= utilizatorService.updatePasswordUtilizator(m,s);
            if (r==null)
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Modificare user","Userul a fost modificat");
        } catch (ValidationException e) {
            MessageAlert.showErrorMessage(null,e.getMessage());
        }
        stage.close();
    }


    private void saveMessage(Utilizator m, String s)
    {
        try {
            Utilizator r= this.utilizatorService.addUtilizator(m,s);
            Long id = this.utilizatorService.searchName(r.getFirstName(),r.getLastName()).getId();
            Profile profile = new Profile("https://cdn.pixabay.com/photo/2023/02/18/11/00/icon-7797704_640.png","Sunt nou!", id);
            Profile p1 = this.utilizatorService.addProfile(profile);
            if (r==null || p1 == null)
                stage.close();
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Salvare user","Mesajul a fost salvat");
        } catch (ValidationException e) {
            MessageAlert.showErrorMessage(null,e.getMessage());
        }
        stage.close();

    }
}
