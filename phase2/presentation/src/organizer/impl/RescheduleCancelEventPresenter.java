package organizer.impl;

import adapter.ScheduleAdapter;
import common.UserAccountHolder;
import controllers.OrganizerController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.cell.PropertyValueFactory;
import model.ScheduleEntry;
import model.UserAccount;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import organizer.IRescheduleCancelEventPresenter;
import organizer.IRescheduleCancelEventView;
import util.DateTimePicker;
import util.DateTimeUtil;
import util.TextResultUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class RescheduleCancelEventPresenter implements IRescheduleCancelEventPresenter {
    private final IRescheduleCancelEventView view;
    private final OrganizerController oc;
    private ScheduleEntry selectedEvent;

    public RescheduleCancelEventPresenter(IRescheduleCancelEventView view) {
        this.view = view;
        getUserData();
        this.oc = new OrganizerController(this.view.getSessionUsername());
        init();
    }

    @Override
    public void cancelButtonAction(ActionEvent actionEvent) {
        clearResultText();

        JSONObject responseJson = oc.cancelEvent(this.selectedEvent.getEventId());
        setResultText(String.valueOf(responseJson.get("result")), String.valueOf(responseJson.get("status")));
        if (String.valueOf(responseJson.get("status")).equals("success")) {
            List<ScheduleEntry> allEvents = getEvents();
            displayEvents(allEvents);
        }
    }

    @Override
    public void rescheduleButtonAction(ActionEvent actionEvent) {
        clearResultText();

        JSONObject queryJson = constructEventJson();
        JSONObject responseJson = oc.rescheduleEvent(queryJson);
        setResultText(String.valueOf(responseJson.get("result")), String.valueOf(responseJson.get("status")));
        if (String.valueOf(responseJson.get("status")).equals("success")) {
            List<ScheduleEntry> allEvents = getEvents();
            displayEvents(allEvents);
        }
    }

    @Override
    public void toggleSwitchListener() {
        this.view.getToggleSwitch().setOnMouseClicked(this::toggleSwitchHandler);
        this.view.getToggleSwitch().getToggleButton().setOnMouseClicked(this::toggleSwitchHandler);
    }

    @Override
    public EventHandler<Event> toggleSwitchHandler(Event event) {
        this.view.getToggleSwitch().setToggleState(!this.view.getToggleSwitch().getToggleState());
        if (this.view.getToggleSwitch().getToggleState()) { // toggled on edit mode
            this.view.getSummaryStart().setDisable(false);
            this.view.getSummaryEnd().setDisable(false);
            this.view.getSummaryCapacityField().setDisable(false);
            this.view.getSummaryVipChoiceBox().setDisable(false);
            this.view.getSummaryRoomsChoiceBox().setDisable(false);
        } else { // toggled off edit mode
            this.view.getSummaryStart().setDisable(true);
            this.view.getSummaryEnd().setDisable(true);
            this.view.getSummaryCapacityField().setDisable(true);
            this.view.getSummaryVipChoiceBox().setDisable(true);
            this.view.getSummaryRoomsChoiceBox().setDisable(true);
        }
        return this.view.getToggleSwitch().toggleOnClick(event);
    }

    @Override
    public void setResultText(String resultText, String status) {
        this.view.setResultText(resultText);
        TextResultUtil.getInstance().addPseudoClass(status, this.view.getResultTextControl());
    }

    @Override
    public List<ScheduleEntry> getEvents() {
        JSONObject responseJson = oc.getAllEvents();
        return ScheduleAdapter.getInstance().adaptData((JSONArray) responseJson.get("data"));
    }

    @Override
    public void displayEvents(List<ScheduleEntry> schedule) {
        DateTimeUtil.getInstance().setScheduleDateTimeCellFactory(this.view.getEventStartColumn());
        DateTimeUtil.getInstance().setScheduleDateTimeCellFactory(this.view.getEventEndColumn());
        this.view.getEventNameColumn().setCellValueFactory(new PropertyValueFactory<>("eventName"));
        this.view.getRoomNameColumn().setCellValueFactory(new PropertyValueFactory<>("roomName"));
        this.view.getEventStartColumn().setCellValueFactory(new PropertyValueFactory<>("start"));
        this.view.getEventEndColumn().setCellValueFactory(new PropertyValueFactory<>("end"));
        this.view.getRemainingSpotsColumn().setCellValueFactory(new PropertyValueFactory<>("remainingSpots"));

        ObservableList<ScheduleEntry> observableSchedule = FXCollections.observableArrayList(schedule);
        this.view.getEventsTable().setItems(observableSchedule);
        this.view.getEventsTable().getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> displayEventDetails(newValue));
    }

    @Override
    public void displayEventDetails(ScheduleEntry event) {
        this.selectedEvent = event;
        this.view.setSummaryEventName(event.getEventName());
        this.view.setSummaryRoomName(event.getRoomName());
        this.view.setSummaryAttendees(event.getAttendees());
        this.view.setSummaryAmenities(event.getAmenities());
        this.view.setSummaryDuration(event.getDuration());
        this.view.setSummaryStart(event.getStart());
        this.view.setSummaryEnd(event.getEnd());
        this.view.setSummarySpeakers(event.getSpeakers());
        this.view.setSummaryCapacity(event.getCapacity());
        this.view.setSummaryRemainingSpots(event.getRemainingSpots());
        this.view.setSummaryVip(event.isVip() ? "Yes" : "No");
    }

    @Override
    public void init() {
        this.view.setCancelButtonAction(this::cancelButtonAction);
        this.view.setRescheduleButtonAction(this::rescheduleButtonAction);
        updateDuration(this.view.getSummaryStart());
        updateDuration(this.view.getSummaryEnd());
        //toggleSwitchListener();
        List<ScheduleEntry> allEvents = getEvents();
        displayEvents(allEvents);
    }

    @Override
    public void getUserData() {
        UserAccountHolder holder = UserAccountHolder.getInstance();
        UserAccount account = holder.getUserAccount();
        this.view.setSessionUsername(account.getUsername());
        this.view.setSessionUserType(account.getUserType());
    }

    private void updateDuration(DateTimePicker picker) {
        picker.valueProperty().addListener(((observableValue, oldValue, newValue) -> {
            LocalDateTime start = this.view.getSummaryStart().getDateTimeValue();
            LocalDateTime end = this.view.getSummaryEnd().getDateTimeValue();
            this.view.setSummaryDuration(Duration.between(start, end));
        }));
    }

    private void clearResultText() {
        this.view.setResultText("");
        TextResultUtil.getInstance().removeAllPseudoClasses(this.view.getResultTextControl());
    }

    @SuppressWarnings("unchecked")
    private JSONObject constructEventJson() {
        JSONObject queryJson = new JSONObject();
        queryJson.put("eventId", this.selectedEvent.getEventId());
        queryJson.put("capacity", this.view.getSummaryCapacityField().getNumber());
        queryJson.put("vip", this.view.getSummaryVipChoiceBox().getValue().equals("Yes"));
        queryJson.put("roomName", this.view.getSummaryRoomsChoiceBox().getValue());
        queryJson.put("start", this.view.getSummaryStart().getDateTimeValue());
        queryJson.put("end", this.view.getSummaryEnd().getDateTimeValue());
        return queryJson;
    }
}
