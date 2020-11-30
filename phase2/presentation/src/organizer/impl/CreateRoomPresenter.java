package organizer.impl;

//import Controllers.IOrganizerController
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.scene.control.CheckBox;
import organizer.ICreateRoomPresenter;
import organizer.ICreateRoomView;

public class CreateRoomPresenter implements ICreateRoomPresenter {
    private ICreateRoomView view;
    private final ObservableSet<CheckBox> selectedAmenities = FXCollections.observableSet();
    private final PseudoClass errorClass = PseudoClass.getPseudoClass("error");

    public CreateRoomPresenter(ICreateRoomView view) {
        this.view = view;
        init();
    }

    @Override
    public void createRoomButtonAction(ActionEvent actionEvent) {
        clearError();

        if (this.view.getRoomName().equals(""))
            setError("Room Name cannot be empty!", 0);
        else if (this.view.getCapacity() <= 0)
            setError("Capacity must be greater than 0", 1);
        else {
            //call oc.createRoom method
        }
    }

    @Override
    public void setError(String error, int errorId) {
        if (errorId == 0)
            this.view.getRoomNameField().pseudoClassStateChanged(errorClass, true);
        else if (errorId == 1)
            this.view.getCapacityField().pseudoClassStateChanged(errorClass, true);
        this.view.setResultMsg(error);
    }

    @Override
    public void observeAmenities() {
        configureCheckBox(this.view.getAmenityBox(1));
        configureCheckBox(this.view.getAmenityBox(2));
        configureCheckBox(this.view.getAmenityBox(3));
    }

    @Override
    public void init() {
        observeAmenities();
        this.view.setCreateRoomButtonAction(this::createRoomButtonAction);
    }

    private void configureCheckBox(CheckBox checkBox) {
        if (checkBox.isSelected())
            selectedAmenities.add(checkBox);

        checkBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (isNowSelected)
                selectedAmenities.add(checkBox);
            else
                selectedAmenities.remove(checkBox);
        });
    }

    private void clearError() {
        this.view.getRoomNameField().pseudoClassStateChanged(errorClass, false);
        this.view.getCapacityField().pseudoClassStateChanged(errorClass, false);
        this.view.setResultMsg("");
    }
}
