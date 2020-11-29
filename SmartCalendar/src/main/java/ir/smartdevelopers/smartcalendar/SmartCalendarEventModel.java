package ir.smartdevelopers.smartcalendar;

import java.io.Serializable;

/**All dates must be as format as yyyy/MM/dd */
public class SmartCalendarEventModel implements Serializable {
    private static final long serialVersionUID=875L;
    protected String startDate;
    private String startTime;
    private String endDate;
    private String endTime;
    private String reminderDate;
    private String reminderTime;


    public String getStartDate() {
        return startDate;
    }
    /**start date must be as format as yyyy/MM/dd */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(String reminderDate) {
        this.reminderDate = reminderDate;
    }

    public String getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(String reminderTime) {
        this.reminderTime = reminderTime;
    }
}
