package presenters;

import org.json.simple.JSONObject;

import java.util.ArrayList;

/**
 * A Presenter class representing an AttendeePresenter which inherits from UserPresenter
 *
 * @author Edmund Lo
 *
 */
public class AttendeePresenter extends UserPresenter{

    public AttendeePresenter() {
        super();
    }

    /**
     * Lists all vip events
     *
     * @param allVipEvents an arraylist of all vip events
     * @return all vip events
     */
    public JSONObject listVipEvents(ArrayList<String> allVipEvents) {
        return pu.createJSON("success", "VIP Events have been listed", "List of VIP Events", allVipEvents);
    }

    /**
     * Prints message that attendee is not a vip
     */
    public JSONObject notVipError() {
        return pu.createJSON("error", "Attendee is not a VIP");
    }

    /**
     * Confirm sign up to vip event
     *
     * @param eventName the event name
     */
    public JSONObject signUpVipResult(String eventName) {
        return pu.createJSON("success", "You have signed up for the VIP event: " + eventName);
    }

    /**
     * Confirms cancellation of attendance in vip event
     *
     * @param eventName the event name
     */
    public JSONObject cancelVipResult(String eventName) {
        return pu.createJSON("success", "You have cancelled your attendance in the VIP event: " + eventName);
    }
}