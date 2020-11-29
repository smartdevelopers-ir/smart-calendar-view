package ir.smartdevelopers.smartcalendar;

import java.io.Serializable;
import java.util.ArrayList;

public class SmartCalendarCellModel implements Serializable {
    private int mPersianDay;
    private int mGregorianDay;
    private boolean hasEvent;
    private int mCurrentMount;
    private int mCurrentGregorianMount;
    private int mLastMount;
    private int mNextMount;
    private int mCurrentYear;
    private int mCurrentGregorianYear;
    private int mLastYear;
    private int mNextYear;
    private int dayOfWeek;
    private boolean inactivate =false;
    private boolean isHoliday=false;
    private boolean mSelected;
    /**Day of today*/
    private boolean mCurrentDay;
    private String mDayOfWeekName;
    private String mCurrentMountName;
    private ArrayList<SmartCalendarEventModel> mEventModels;





    public SmartCalendarCellModel() {
    }

    public SmartCalendarCellModel(int persianDay, int gregorianDay) {
        mPersianDay = persianDay;
        mGregorianDay = gregorianDay;
    }
    public ArrayList<SmartCalendarEventModel> getEventModels() {
        return mEventModels;
    }

    public void setEventModels(ArrayList<SmartCalendarEventModel> eventModels) {
        mEventModels = eventModels;
    }

    public String getDayOfWeekName() {
        return mDayOfWeekName;
    }

    public void setDayOfWeekName(String dayOfWeekName) {
        mDayOfWeekName = dayOfWeekName;
    }

    public boolean isCurrentDay() {
        return mCurrentDay;
    }

    public void setCurrentDay(boolean currentDay) {
        mCurrentDay = currentDay;
    }

    public boolean isHoliday() {
        return isHoliday;
    }

    public void setHoliday(boolean holiday) {
        isHoliday = holiday;
    }

    public boolean isSelected() {
        return mSelected;
    }

    public void setSelected(boolean selected) {
        mSelected = selected;
    }

    public int getPersianDay() {
        return mPersianDay;
    }

    public void setPersianDay(int persianDay) {
        mPersianDay = persianDay;
    }

    public int getGregorianDay() {
        return mGregorianDay;
    }

    public void setGregorianDay(int gregorianDay) {
        mGregorianDay = gregorianDay;
    }

    public boolean hasEvent() {
        return hasEvent;
    }

    public void setHasEvent(boolean hasEvent) {
        this.hasEvent = hasEvent;
    }

    public boolean isHasEvent() {
        return hasEvent;
    }

    public int getCurrentMount() {
        return mCurrentMount;
    }

    public void setCurrentMount(int currentMount) {
        mCurrentMount = currentMount;
    }

    public int getLastMount() {
        return mLastMount;
    }

    public void setLastMount(int lastMount) {
        mLastMount = lastMount;
    }

    public int getNextMount() {
        return mNextMount;
    }

    public void setNextMount(int nextMount) {
        mNextMount = nextMount;
    }

    public int getCurrentYear() {
        return mCurrentYear;
    }

    public void setCurrentYear(int currentYear) {
        mCurrentYear = currentYear;
    }

    public int getLastYear() {
        return mLastYear;
    }

    public void setLastYear(int lastYear) {
        mLastYear = lastYear;
    }

    public int getNextYear() {
        return mNextYear;
    }

    public void setNextYear(int nextYear) {
        mNextYear = nextYear;
    }

    public int getDayOfWeek() {

        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        if (dayOfWeek==6){// friday
            isHoliday=true;
        }
        this.dayOfWeek = dayOfWeek;
    }

    public boolean isInactivate() {
        return inactivate;
    }

    public void setInactivate(boolean inactivate) {
        this.inactivate = inactivate;
    }

    public String getCurrentMountName() {
        return mCurrentMountName;
    }

    public void setCurrentMountName(String currentMountName) {
        mCurrentMountName = currentMountName;
    }

    public int getCurrentGregorianMount() {
        return mCurrentGregorianMount;
    }

    public void setCurrentGregorianMount(int currentGregorianMount) {
        mCurrentGregorianMount = currentGregorianMount;
    }

    public int getCurrentGregorianYear() {
        return mCurrentGregorianYear;
    }

    public void setCurrentGregorianYear(int currentGregorianYear) {
        mCurrentGregorianYear = currentGregorianYear;
    }
}
