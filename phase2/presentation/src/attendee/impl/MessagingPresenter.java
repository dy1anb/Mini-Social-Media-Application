package attendee.impl;

import adapter.MessageThreadAdapter;
import adapter.UserAdapter;
import attendee.IMessagingPresenter;
import attendee.IMessagingView;
import controllers.AttendeeController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import model.MessageThread;
import model.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import util.TextResultUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessagingPresenter implements IMessagingPresenter {
    private IMessagingView view;
    private AttendeeController ac;
    private ObservableList<User> users;
    private MessageThread selectedPrimaryMessageThread;
    private MessageThread selectedArchivedMessageThread;
    private MessageThread selectedTrashMessageThread;
    private CheckBox selectAll;
    private TextField senderField;
    private TextField recipientsField;
    private TextField subjectField;
    private TextArea contentArea;

    public MessagingPresenter(IMessagingView view) {
        this.view = view;
        this.ac = new AttendeeController(this.view.getSessionUsername());
        init();
    }

    @Override
    public void replyButtonAction(ActionEvent actionEvent) {
        clearResultText();

        //JSONObject responseJson = ac.reply(this.selectedPrimaryMessage.getMessageId(), this.view.getContent());
        JSONObject responseJson = new JSONObject();
        setResultText(String.valueOf(responseJson.get("result")), String.valueOf(responseJson.get("status")));
        if (responseJson.get("status").equals("success")) refreshAllInboxes();
    }

    @Override
    public void newMessageButtonAction(ActionEvent actionEvent) {
        SplitPane splitPane = new SplitPane();

        TableView<User> userTable = new TableView<>();
        this.selectAll = new CheckBox();
        this.selectAll.setOnAction(this::selectAllAction);
        TableColumn<User, Boolean> checkedColumn = new TableColumn<>();
        checkedColumn.setGraphic(selectAll);
        TableColumn<User, String> firstNameColumn = new TableColumn<>("First Name");
        TableColumn<User, String> lastNameColumn = new TableColumn<>("Last Name");
        TableColumn<User, String> usernameColumn = new TableColumn<>("Username");
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        userTable.getColumns().add(firstNameColumn);
        userTable.getColumns().add(lastNameColumn);
        userTable.getColumns().add(usernameColumn);

        GridPane gridPane = new GridPane();
        Label senderLabel = new Label("From:");
        this.senderField = new TextField(this.view.getSessionUsername());
        this.senderField.setEditable(false);
        Label recipientsLabel = new Label("To:");
        this.recipientsField = new TextField();
        this.recipientsField.setEditable(false);
        Label subjectLabel = new Label("Subject:");
        this.subjectField = new TextField();
        Label contentLabel = new Label("Message:");
        this.contentArea = new TextArea();
        Button sendButton = new Button("Send Message");
        sendButton.setOnAction(this::sendButtonAction);

        //JSONObject responseJson = ac.getFriendsList();
        JSONObject responseJson = new JSONObject();
        List<User> users = UserAdapter.getInstance().adaptData((JSONArray) responseJson.get("data"));
        this.users = FXCollections.observableArrayList(users);
        userTable.setItems(this.users);
        userTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> updateRecipientList(this.recipientsField));

        gridPane.setAlignment(Pos.CENTER);
        gridPane.addRow(0, senderLabel, this.senderField);
        gridPane.addRow(1, recipientsLabel, this.recipientsField);
        gridPane.addRow(2, subjectLabel, this.subjectField);
        gridPane.addRow(3, contentLabel, this.contentArea);
        gridPane.addRow(5, sendButton);

        splitPane.getItems().add(userTable);
        splitPane.getItems().add(gridPane);
        Tab newMessageTab = new Tab("New Message Thread", splitPane);
        this.view.getTabPane().getTabs().add(newMessageTab);
        this.view.getTabPane().getSelectionModel().selectLast();
    }

    @Override
    public void moveToTrashButtonAction(ActionEvent actionEvent) {
        //ac.moveToTrash(this.selectedPrimaryMessageThread.getMessageId());
        refreshAllInboxes();
    }

    @Override
    public void moveToArchivedButtonAction(ActionEvent actionEvent) {
        //ac.moveToArchive(this.selectedPrimaryMessageThread.getMessageId());
        refreshAllInboxes();
    }

    @Override
    public void moveToPrimaryFirstButtonAction(ActionEvent actionEvent) {
        //ac.moveToPrimary(this.selectedArchivedMessageThread.getMessageId());
        refreshAllInboxes();
    }

    @Override
    public void moveToPrimarySecondButtonAction(ActionEvent actionEvent) {
        //ac.moveToPrimary(this.selectedTrashMessageThread.getMessageId());
        refreshAllInboxes();
    }

    @Override
    public void unreadPrimaryButtonAction(ActionEvent actionEvent) {
        //ac.markUnread(this.selectedPrimaryMessageThread.getMessageId());
        refreshAllInboxes();
    }

    @Override
    public void unreadArchivedButtonAction(ActionEvent actionEvent) {
        //ac.markUnread(this.selectedArchivedMessageThread.getMessageId());
        refreshAllInboxes();
    }

    @Override
    public void unreadTrashButtonAction(ActionEvent actionEvent) {
        //ac.markUnread(this.selectedTrashMessageThread.getMessageId());
        refreshAllInboxes();
    }

    @Override
    public void setResultText(String resultText, String status) {
        this.view.setResultText(resultText);
        TextResultUtil.getInstance().addPseudoClass(status, this.view.getResultTextControl());
    }

    @Override
    public List<MessageThread> getInbox(String type) {
        JSONObject responseJson = new JSONObject();
        switch (type) {
            case "primary":
                //responseJson = ac.getPrimaryMessages();
                break;
            case "archived":
                //responseJson = ac.getArchivedMessages();
                break;
            case "trash":
                //responseJson = ac.getTrashMessages();
                break;
        }
        return MessageThreadAdapter.getInstance().adaptData((JSONArray) responseJson.get("data"));
    }

    @Override
    public void displayInbox(List<MessageThread> messageThreads, String type) {
        ObservableList<MessageThread> observableInbox = FXCollections.observableArrayList(messageThreads);

        switch (type) {
            case "primary":
                this.view.getPrimaryMembersColumn().setCellValueFactory(cdf ->
                        new SimpleStringProperty(getMessageMembers(cdf.getValue().getRecipientNames())));
                this.view.getPrimarySubjectColumn().setCellValueFactory(new PropertyValueFactory<>("subject"));
                this.view.getPrimaryUnreadColumn().setCellValueFactory(param -> param.getValue().readProperty());
                this.view.getPrimaryInbox().setItems(observableInbox);
                this.view.getPrimaryInbox().getSelectionModel().selectedItemProperty().addListener(
                        (observable, oldValue, newValue) -> displayMessageThread(newValue, type));
                break;
            case "archived":
                this.view.getArchivedMembersColumn().setCellValueFactory(cdf ->
                        new SimpleStringProperty(getMessageMembers(cdf.getValue().getRecipientNames())));
                this.view.getArchivedSubjectColumn().setCellValueFactory(new PropertyValueFactory<>("subject"));
                this.view.getArchivedUnreadColumn().setCellValueFactory(param -> param.getValue().readProperty());
                this.view.getArchivedInbox().setItems(observableInbox);
                this.view.getArchivedInbox().getSelectionModel().selectedItemProperty().addListener(
                        (observable, oldValue, newValue) -> displayMessageThread(newValue, type));
                break;
            case "trash":
                this.view.getTrashMembersColumn().setCellValueFactory(cdf ->
                        new SimpleStringProperty(getMessageMembers(cdf.getValue().getRecipientNames())));
                this.view.getTrashSubjectColumn().setCellValueFactory(new PropertyValueFactory<>("subject"));
                this.view.getTrashUnreadColumn().setCellValueFactory(param -> param.getValue().readProperty());
                this.view.getTrashInbox().setItems(observableInbox);
                this.view.getTrashInbox().getSelectionModel().selectedItemProperty().addListener(
                        (observable, oldValue, newValue) -> displayMessageThread(newValue, type));
                break;
        }
    }

    @Override
    public void displayMessageThread(MessageThread messageThread, String type) {
        //ac.setMessageRead(message.getMessageId());
        switch (type) {
            case "primary":
                this.selectedPrimaryMessageThread = messageThread;
                this.view.setPrimarySender(messageThread.getSenderName());
                this.view.setPrimaryRecipientNames(messageThread.getRecipientNames());
                this.view.setPrimarySubject(messageThread.getSubject());
                //this.view.getPrimaryThreadContainer().getChildren().
                break;
            case "archived":
                //responseJson = ac.getArchivedMessages();
                break;
            case "trash":
                //responseJson = ac.getTrashMessages();
                break;
        }
    }

    @Override
    public void init() {
        this.view.setMoveToArchivedButtonAction(this::moveToArchivedButtonAction);
        this.view.setMoveToTrashButtonAction(this::moveToTrashButtonAction);
        this.view.setMoveToPrimaryFirstButtonAction(this::moveToPrimaryFirstButtonAction);
        this.view.setMoveToPrimarySecondButtonAction(this::moveToPrimarySecondButtonAction);
        this.view.setUnreadPrimaryButtonAction(this::unreadPrimaryButtonAction);
        this.view.setUnreadArchivedButtonAction(this::unreadArchivedButtonAction);
        this.view.setUnreadTrashButtonAction(this::unreadTrashButtonAction);
        this.view.setReplyButtonAction(this::replyButtonAction);
        this.view.setNewMessageButtonAction(this::newMessageButtonAction);
        refreshAllInboxes();
    }

    private void refreshAllInboxes() {
        List<MessageThread> primaryInbox = getInbox("primary");
        displayInbox(primaryInbox, "primary");
        List<MessageThread> archivedInbox = getInbox("archived");
        displayInbox(archivedInbox, "archived");
        List<MessageThread> trashInbox = getInbox("trash");
        displayInbox(trashInbox, "trash");
    }

    private String getMessageMembers(List<String> recipients) {
        StringBuilder sb = new StringBuilder();
        for (String name : recipients) {
            sb.append(name);
            sb.append(", ");
        }
        sb.append("me");
        return sb.toString();
    }

    private List<HBox> alignMessageHistory() {
        List<HBox> messageHistory = new ArrayList<>();

        return messageHistory;
    }

    private void selectAllAction(ActionEvent actionEvent) {
        boolean checked = this.selectAll.isSelected();
        for (User u : this.users)
            u.setChecked(checked);
    }

    private void updateRecipientList(TextField recipientField) {
        StringBuilder sb = new StringBuilder();
        String prefix = "";
        for (User u : this.users) {
            if (u.getChecked()) {
                sb.append(prefix);
                prefix = ", ";
                sb.append(u.getUsername());
            }
        }
        recipientField.setText(sb.toString());
    }

    public void sendButtonAction(ActionEvent actionEvent) {
        JSONObject queryJson = constructMessageJson();
        //JSONObject responseJson = oc.sendNewMessage(queryJson);
        JSONObject responseJson = new JSONObject();
        setResultText(String.valueOf(responseJson.get("result")), String.valueOf(responseJson.get("status")));
    }

    @SuppressWarnings("unchecked")
    private JSONObject constructMessageJson() {
        JSONObject queryJson = new JSONObject();
        JSONArray recipients = new JSONArray();
        recipients.addAll(Arrays.asList(this.recipientsField.getText().split(", ")));
        queryJson.put("sender", this.senderField.getText());
        queryJson.put("recipients", recipients);
        queryJson.put("subject", this.subjectField.getText());
        queryJson.put("content", this.contentArea.getText());
        return queryJson;
    }

    private void clearResultText() {
        this.view.setResultText("");
        TextResultUtil.getInstance().removeAllPseudoClasses(this.view.getResultTextControl());
    }
}
