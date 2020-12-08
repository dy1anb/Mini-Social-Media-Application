package adapter;

import model.ScheduleEntry;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import util.DateTimeUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;

public class ScheduleAdapter {
    private static final ScheduleAdapter Instance = new ScheduleAdapter();

    public static ScheduleAdapter getInstance() { return Instance; }

    private ScheduleAdapter() {}

    public List<ScheduleEntry> adaptData(JSONArray data) {
        List<ScheduleEntry> schedule = new ArrayList<>();
        for (Object datum : data) {
            JSONObject jsonObject = (JSONObject) datum;
            schedule.add(mapScheduleEntry(jsonObject));
        }
        return schedule;
    }

    private ScheduleEntry mapScheduleEntry(JSONObject jsonObject) {
        LocalDateTime start = DateTimeUtil.getInstance().parse(String.valueOf(jsonObject.get("start")));
        LocalDateTime end = DateTimeUtil.getInstance().parse(String.valueOf(jsonObject.get("end")));
        Duration duration = Duration.ofMinutes(parseLong(String.valueOf(jsonObject.get("duration"))));
        String eventId = String.valueOf(jsonObject.get("eventId"));
        String eventName = String.valueOf(jsonObject.get("eventName"));
        String roomName = String.valueOf(jsonObject.get("roomName"));
        String amenities = String.valueOf(jsonObject.get("amenities"));
        String attendees = String.valueOf(jsonObject.get("attendees"));
        String speakers = String.valueOf(jsonObject.get("speakers"));
        int remainingSpots = parseInt(String.valueOf(jsonObject.get("remainingSpots")));
        int capacity = parseInt(String.valueOf(jsonObject.get("capacity")));
        boolean vip = jsonObject.get("vip").equals("true");

        return new ScheduleEntry(start, end, eventId, eventName, roomName, amenities, attendees, speakers, duration,
                remainingSpots, capacity, vip);
    }
}
