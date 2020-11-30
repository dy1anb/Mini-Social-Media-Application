package controllers;

import presenters.UserPresenter;
import usecases.*;

/**
 * A Controller class representing an AttendeeController which inherits from UserController.
 *
 * @author Echo Li
 * @version 1.0
 *
 */
public class AttendeeController extends UserController {

    /**
     * Constructor for AttendeeController object. Uses constructor from UserController.
     *
     * @param em  current session's EventManager class.
     * @param um  current session's UserManager class.
     * @param rm  current session's RoomManager class.
     * @param mm  current session's MessageManager class.
     * @param username current logged in user's username.
     */
    public AttendeeController(EventManager em, UserManager um, RoomManager rm, MessageManager mm, String username) {
        super(em, um, rm, mm, username);
        UserPresenter up = new UserPresenter();
        boolean inSession = true;
        // Enters a while loop that allows the user to continuously use Attendee functions
        while(inSession) {
            up.displayMenu("Attendee", username);
            String option = input.nextLine();
            switch(option) {
                case "0":
                    logout();
                    inSession = false;
                    break;
                case "1":
                    signUpMenu();
                    break;
                case "2":
                    cancelMenu();
                    break;
                case "3":
                    messageMenu();
                    break;
                case "4":
                    viewEventsMenu();
                    break;
                default:
                    up.invalidOptionError();
                    break;
            }
        }
    }
}
