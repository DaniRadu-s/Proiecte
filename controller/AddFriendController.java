package com.example.guiex1.controller;

import com.example.guiex1.domain.Prietenie;
import com.example.guiex1.domain.Utilizator;
import com.example.guiex1.domain.ValidationException;
import com.example.guiex1.services.UtilizatorService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddFriendController {
    @FXML
    private TextField first_name;
    @FXML
    private TextField last_name;


    private UtilizatorService service;
    Stage dialogStage;
    Utilizator utilizator;
    @FXML
    private void initialize() {
    }

    public void setService(UtilizatorService service,  Stage stage, Utilizator u) {
        this.service = service;
        this.dialogStage=stage;
        this.utilizator =u;
    }

    @FXML
    public void handleAdd() {
        String firstName = first_name.getText();
        String lastName = last_name.getText();
        Utilizator friend = service.searchName(firstName, lastName);
        try {
            if (friend == null) {
                MessageAlert.showErrorMessage(null, "Nu exista asa cineva");
            } else {
                Long id1 = utilizator.getId();
                Long id2 = friend.getId();
                service.addFriendship(new Prietenie(id1, id2));
            }
        }catch (ValidationException e) {
            MessageAlert.showErrorMessage(null, e.getMessage());
        }

    }
}
