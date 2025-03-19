package com.example.guiex1.controller;

import com.example.guiex1.domain.Message;
import com.example.guiex1.domain.Tuple;
import com.example.guiex1.domain.Utilizator;
import com.example.guiex1.services.UtilizatorService;
import com.example.guiex1.utils.events.Event;
import com.example.guiex1.utils.events.MessageEntityChangeEvent;
import com.example.guiex1.utils.observer.Observer;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldListCell;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MessageController implements Observer<Event> {
    UtilizatorService service;
    Utilizator utilizator;
    Utilizator friend;
    ObservableList<Message> model = FXCollections.observableArrayList();
    @FXML
    private Label labelFriend;
    @FXML
    private TextField txtMessage;
    @FXML
    private TableView<Message> tableView;
    @FXML
    private TableColumn<Message, LocalDateTime> columnDate;
    @FXML
    private TableColumn<Message, String> columnMessage;

    public void setService(UtilizatorService service, Utilizator utilizator, Utilizator friend) {
        this.service = service;
        this.utilizator = utilizator;
        this.friend = friend;
        service.addObserver(this);
        labelFriend.setText("Chating with "+friend.getFirstName()+" "+friend.getLastName());
        initModel();
    }

    private void initModel() {
//        List<Message> list = service.findAll(utilizator.getId(), friend.getId());
//        model.clear();
//        list.sort(Comparator.comparing(Message::getDate).reversed());
//        model.addAll(list);
        Message last = service.getChat(utilizator.getId(),friend.getId());
        model.clear();
        if(last!=null) {
            while (last.getReply() != null) {
                model.add(last);
                last = last.getReply();
            }
            model.add(last);
        }
    }

    @FXML
    public void initialize() {
        columnMessage.setCellValueFactory(new PropertyValueFactory<Message, String>("message"));
        columnDate.setCellValueFactory(new PropertyValueFactory<Message, LocalDateTime>("date"));
        columnDate.setCellFactory(col -> new TableCell<Message, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null); // Golește celula dacă este goală
                } else {
                    // Formatează și afișează data
                    setText(item.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                }
            }
        });

        // Set row factory to color rows based on condition
        tableView.setRowFactory(tv -> new TableRow<Message>() {
            @Override
            protected void updateItem(Message item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setStyle(""); // Reset style for empty or null rows
                } else {
                    // Check condition and set row color
                    if (item.getFrom().getId().equals(utilizator.getId())) {
                        setStyle("-fx-background-color: green;"); // Color row yellow if from the same user
                    } else {
                        setStyle("-fx-background-color: yellow;"); // Default style
                    }
                }
            }
        });
        tableView.setItems(model); // Set items for the TableView
    }


    @Override
    public void update(Event event) {
        if(event.getClass()== MessageEntityChangeEvent.class){
            Message msg = (Message) ((MessageEntityChangeEvent) event).getData();
            initModel();
        }
    }

    public void handleSendMessage(){
        String text = txtMessage.getText();
        Message msg = new Message(utilizator,List.of(friend),text, LocalDateTime.now(),model.isEmpty() ? null : model.get(0));
        service.addMessage(msg);
        txtMessage.clear();
    }
}