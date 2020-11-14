package Presenters;

import java.util.List;

/**
 * Presenter outputs for client type User
 */

public class UserPresenter {
    //type of menu options:
    // go back, sign up for an event with a number
    // cancel an event with a known number
    // message a user, view list of messages
    public void displayMenu() {
        System.out.println("Select Option " +
                "\n 0. Logout" +
                "\n 1. Sign Up for Events" +
                "\n 2. Cancel Attendance from Events" +
                "\n 3. Message Users / View Messages");
    }
    //Numbered options during signup
    public void signUpEventPrompt() {
        System.out.println("Enter 0 to go back or enter a number to select an event to sign up for:");
    }
    //prompts user option for cancelling event
    public void cancelEventPrompt() {
        System.out.println("Enter a number to select an event to cancel:" +
                "\n Press 0 to go back.");
    }
    //prompt for messaging options
    public void messageMenuPrompt() {
        System.out.println("Press :" +
                "\n 0. Go back" +
                "\n 1. Message a user" +
                "\n 2. View your messages");
    }
    //prompt for receiver option
    public void enterReceiverPrompt() {
        System.out.println("Enter the user you wish to message:");
    }
    //prompt for writing message
    public void enterMessagePrompt() {
        System.out.println("Enter the message you wish to send:");
    }
    //list of events
    public void signUpEventListLabel() {
        System.out.println("Here are a list of events you can sign up for:");
    }
    //prompt for cancel event based on list
    public void cancelEventListLabel() {
        System.out.println("Here are a list of events you can cancel attendance from:");
    }
    //list of users that can be messaged list
    public void messageUserListLabel() {
        System.out.println("Here is a list of all users you can message:");
    }
    public void noMessagesLabel() {
        System.out.println("You have no messages.");
    }

    //list of events iterated and printed
    public void listEvents(List<String> events) {
        int count = 1;
        for (String x: events) {
            System.out.println("("+count + "): " + x);
            count++;
        }
    }
    //list of users iterated and printed
    public void listUsers(List<String> users) {
        for (String x : users) {
            System.out.println(x);
        }
    }
    //confirm signup result
    public void signUpResult(String event) {
        System.out.println("You have signed up for " + event);
    }
    //cancel attendance for passed in event
    public void cancelResult(String event) {
        System.out.println("You have cancelled your attendance for " + event);
    }
    //prints user that message is sent to
    public void messageResult(String recipient) {
        System.out.println("Message sent to " + recipient);
    }
    //prints error for invalid user
    public void invalidUserError() {
        System.out.println("The user you entered was invalid.");
    }
    //invalid messaging to organizer
    public void cannotMessageOrganizerError() {
        System.out.println("You cannot message an Organizer!");
    }
    // invalid message format
    public void invalidMessageError() {
        System.out.println("Message was formatted incorrectly!");
    }
    //invalid option
    public void invalidOptionError() {
        System.out.println("Please enter a valid option!");
    }
    //signed-up already error
    public void alreadySignedUpError() {
        System.out.println("You are already signed up for an event at this time!");
    }
    //full capacity error print statement
    public void eventFullCapacityError() {
        System.out.println("The event is already at full capacity!");
    }
    //not attending or not signed up for event error (passes in Event event to determine
    public void notAttendingEventError(String event) {
        System.out.println("You are not signed up for " + event);
    }
    //print statement at logout
    public void logoutMessage() {
        System.out.println("Logging out...");
    }
}
