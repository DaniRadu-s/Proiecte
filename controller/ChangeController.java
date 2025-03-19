package com.example.guiex1.controller;

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

import static com.example.guiex1.utils.Hashing.hashPassword;

public class ChangeController {
    private UtilizatorService utilizatorService;
    Stage stage;

    @FXML
    private TextField setPasswordField;
    @FXML
    private TextField confirmPasswordField;
    Utilizator utilizator;

    public void setService(UtilizatorService utilizatorService, Stage stage, Utilizator u) {
        this.utilizatorService = utilizatorService;
        this.stage = stage;
        this.utilizator = u;
    }

    public void handleRegister(ActionEvent actionEvent) {
        if(setPasswordField.getText().equals(confirmPasswordField.getText())) {
            try {
                String hashedpassword = hashPassword(setPasswordField.getText());
                updateMessage(utilizator, hashedpassword);
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
        // TODO
        try {
            Utilizator r= this.utilizatorService.addUtilizator(m,s);
            if (r==null)
                stage.close();
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Salvare user","Mesajul a fost salvat");
        } catch (ValidationException e) {
            MessageAlert.showErrorMessage(null,e.getMessage());
        }
        stage.close();

    }
}
