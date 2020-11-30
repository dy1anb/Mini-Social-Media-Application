package usecases;

import entities.Room;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Set;

/**
 * A Use Case class that stores the rooms of the conference and updates the appropriate attributes of the rooms
 * to reflect their current state.
 *
 * @author Keegan McGonigal
 * @version 1.0
 *
 */

public class RoomManager implements Serializable {
    private HashMap<String, Room> allRooms;

    /**
     * Constructs a new empty RoomManager object containing no rooms.
     */
    public RoomManager() {
        this.allRooms = new HashMap<>();
    }

    /**
     * Creates a new Room object with an empty schedule and adds it into this RoomManager.
     * @param name  the name of the new room.
     * @return      a boolean value of true if the room was successfully created, false otherwise.
     */
    public boolean createRoom(String name) {
        if (!this.allRooms.containsKey(name)){
            this.allRooms.put(name, new Room(name));
            return true;
        }
        return false;
    }
//      ***** Saving method for phase 2
//    /**
//     * Removes an existing Room object from this RoomManager.
//     * @param name  the name of the room to be removed.
//     * @return      a boolean value of true if the room was successfully removed, false otherwise.
//     */
//    public boolean removeRoom(String name) {
//        if (this.allRooms.containsKey(name)){
//            this.allRooms.remove(name);
//            return true;
//        }
//        return false;
//    }

    private Room getRoom(String roomName){
        return this.allRooms.get(roomName);
    }

    /**
     * Adds an event to the schedule of a given Room in this RoomManager.
     * @param startTime      the start time of the event to add
     * @param endTime        the end time of the event to add
     * @param roomName       the name of the room to add the event to.
     * @return               a boolean value of true if the event was successfully added to the room, false otherwise.
     */
    public boolean addToRoomSchedule(LocalDateTime startTime, LocalDateTime endTime, String roomName,
                                     String eventName) {
        Room room = getRoom(roomName);
        if (room.canBook(startTime, endTime)){
            room.addEvent(startTime, endTime, eventName);
            return true;
        }
        return false;
    }

//      ***** Saving method for phase 2
//    /**
//     * Removes an event to the schedule of a given Room in this RoomManager.
//     *
//     * @param startTime the start time of the event to be removed to the schedule of a room.
//     * @param endTime   the end time of the event to be removed from the schedule of a room.
//     * @param roomName  the name of the room to remove the event from.
//     * @param eventName the name of the event to be removed
//     * @return          a boolean value of true if the event was successfully removed from the room, false otherwise.
//     */
//    public boolean removeFromRoomSchedule(LocalDateTime startTime, LocalDateTime endTime, String roomName,
//                                          String eventName){
//        Room room = getRoom(roomName);
//        if (room.hasEvent(startTime, endTime, eventName)) {
//            room.removeEvent(startTime, endTime);
//            return true;
//        }
//        return false;
//    }

    /**
     * Gets a list of all the room names in the system.
     *
     * @return  a set containing all of the room names
     */
    public Set<String> getAllRooms(){
        return this.allRooms.keySet();
    }

//      ***** Saving method for phase 2
//    /**
//     * Gets the string representation for a Room in this RoomManager.
//     *
//     * @param   roomName the name of the Room to get the String representation of.
//     * @return  the String representation of a Room in this RoomManager
//     */
//    public String getRoomString(String roomName){
//        return getRoom(roomName).toString();
//    }

    /**
     * Gets the string representation for Room in this RoomManager.
     *
     * @param   roomName the name of the Room to get the String representation of the schedule.
     * @return  the String representation of a Room's schedule in this RoomManager.
     */
    public String getRoomSchedule(String roomName){
        return getRoom(roomName).roomScheduleToString();
    }

//      ***** Saving method for phase 2
//    /**
//     * Reschedules an event that is happening inside a Room in this RoomManager.
//     *
//     * @param eventName the name of the event to be rescheduled.
//     * @param currTime  the current start time of the event.
//     * @param newTime   the new start time of the event.
//     * @param roomName  the name of the room where the event is taking place.
//     * @return          true if the event was successfully rescheduled, false otherwise.
//     */
//    public boolean reschedule(LocalDateTime currTime, LocalDateTime newTime, String roomName, String eventName){
//        Room room = getRoom(roomName);
//        if (room.hasEvent(currTime, eventName) && room.canBook(newTime)){
//            room.removeEvent(currTime);
//            room.addEvent(newTime, eventName);
//            return true;
//        }
//        return false;
//    }
}
