package ir.smartdevelopers.smartcalendar;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ir.smartdevelopers.smartcalendar.persiandatepicker.util.PersianCalendar;


public class SmartCalendarFragment extends Fragment {

    private SmartCalendarView mSmartCalendarView;
    private RecyclerView mCalendarRecyclerView;
    private ArrayList<SmartCalendarCellModel> mCellModels = new ArrayList<>();

    private int mAddMount = 0;
    private SmartCalendarCellAdapter<? extends RecyclerView.ViewHolder> adapter;
    private SmartCalendarCellAdapterFactory mAdapterFactory;
    private List<? extends SmartCalendarEventModel> mCurrentMountEventModels;
    private static List<? extends SmartCalendarEventModel> mEventModels;

    private int mActiveYear, mActiveMount, mActiveDay;

    private int mPosition, mOldPosition;
    private String mActiveMountName;
    private int mAddWeek = 0;
    private int mCalendarViewId;
    private boolean isCreatedByGetInstance = false;
    private int mLastSelectedPos = -1;


    public static SmartCalendarFragment getInstance(int position,
                                                    ArrayList<? extends SmartCalendarEventModel> eventModels,
                                                    SmartCalendarCellAdapterFactory adapterFactory,
                                                    int calendarViewId) {
        SmartCalendarFragment smartCalendarFragment = new SmartCalendarFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("mPosition", position);
        bundle.putInt("mCalendarViewId", calendarViewId);
        bundle.putSerializable("mAdapterFactory", adapterFactory);
//        bundle.putSerializable("mEventModels", eventModels);
        mEventModels=eventModels;
        smartCalendarFragment.setArguments(bundle);

        smartCalendarFragment.isCreatedByGetInstance = true;
        return smartCalendarFragment;
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && !isCreatedByGetInstance) {
            mAddMount = savedInstanceState.getInt("mAddMount");
            mAddWeek = savedInstanceState.getInt("mAddWeek");
            mActiveMount = savedInstanceState.getInt("mActiveMount");
            mActiveYear = savedInstanceState.getInt("mActiveYear");
            mActiveMountName = savedInstanceState.getString("mActiveMountName");
            mOldPosition = savedInstanceState.getInt("mOldPosition");
            mLastSelectedPos = savedInstanceState.getInt("mLastSelectedPos");
        }
        Bundle bundle = getArguments();
        if (bundle != null) {
//            mEventModels = (List<? extends SmartCalendarEventModel>) bundle.getSerializable("mEventModels");
            mAdapterFactory = (SmartCalendarCellAdapterFactory) bundle.getSerializable("mAdapterFactory");
            mCalendarViewId = bundle.getInt("mCalendarViewId");
            mPosition = bundle.getInt("mPosition");

        }
    }

    private SmartCalendarView findCalendarView() {
        SmartCalendarView smartCalendarView = null;
        Fragment parentFragment = getParentFragment();
        if (parentFragment != null) {
            View view = parentFragment.getView();
            if (view != null) {
                smartCalendarView = view.findViewById(mCalendarViewId);
            }
        } else {
            Activity activity = getActivity();
            if (activity != null) {
                smartCalendarView = activity.findViewById(mCalendarViewId);
            }
        }
        if (smartCalendarView == null) {
            throw new RuntimeException("SmartCalendarView not Founded in your layouts");
        }
        return smartCalendarView;
    }

    void calculateAddMount() {

        mOldPosition = mSmartCalendarView.getActivePosition();
        if (mOldPosition == 0) {
            mOldPosition = Integer.MAX_VALUE / 2;
        }
        /*calculate add mount*/
        PersianCalendar persianCalendar = new PersianCalendar();
        int initialMount = mSmartCalendarView.getInitialMount();
        int initialYear = mSmartCalendarView.getInitialYear();
        int initialDay = mSmartCalendarView.getInitialDay();
        persianCalendar.setPersianDate(initialYear, initialMount, initialDay);
        int activeMount = 0;
        int activeYear = 0;

        activeMount = mSmartCalendarView.getActiveMount();
        activeYear = mSmartCalendarView.getActiveYear();

        if (mSmartCalendarView.isExpanded()) {
            int mountAddFactor = mPosition - mOldPosition;
            // add mount
            mAddWeek = 0;
            if (activeMount != 0) {
                /*if slide to bigger position increase mount else decrease mount*/
                mAddMount = SmartCalendarView.calculateDifferenceMount(activeYear, activeMount, persianCalendar.getPersianYear(), persianCalendar.getPersianMonth());
                mAddMount += mountAddFactor;

            } else {
                mAddMount = 0;
            }

        } else {
            // add week

            mAddMount = SmartCalendarView.calculateDifferenceMount(activeYear, activeMount, persianCalendar.getPersianYear(), persianCalendar.getPersianMonth());
            if (activeMount == 0) {
                mAddWeek = 0;
            } else {
                mAddWeek = mPosition - mOldPosition;
            }

        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("mAddMount", mAddMount);
        outState.putInt("mAddWeek", mAddWeek);
        outState.putInt("mActiveMount", mActiveMount);
        outState.putInt("mActiveDay", mActiveDay);
        outState.putInt("mActiveYear", mActiveYear);
        outState.putString("mActiveMountName", mActiveMountName);
        outState.putInt("mOldPosition", mOldPosition);
        if (adapter != null) {
            outState.putInt("mLastSelectedPos", adapter.getSelectedPosition());
        }
        super.onSaveInstanceState(outState);

    }


    public int getAddMount() {
        return mAddMount;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.smart_calendar_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSmartCalendarView = findCalendarView();
        if (isCreatedByGetInstance) {
            calculateAddMount();
        }
        findViews(view);
        initViews();


        GridLayoutManager gridLayoutManager = new CalendarViewLayoutManager(getContext(), 7, this);
        mCalendarRecyclerView.setLayoutManager(gridLayoutManager);
        mSmartCalendarView.addTag(mPosition, getTag());
        int space = getResources().getDimensionPixelSize(R.dimen.space_1dp);
        mCalendarRecyclerView.addItemDecoration(new GridSpacingItemDecoration(7, space, false));
        mCellModels = getCellModels(mAddMount);

        adapter = mAdapterFactory.getInstance(getContext().getApplicationContext());
        adapter.setOnDateSelectListener(mSmartCalendarView.mOnDateSelectListener);
        mCalendarRecyclerView.setAdapter(adapter);

        adapter.setCellModels(mCellModels);

        updateMountName();
    }

    private void updateMountName() {
        PersianCalendar currentCalendar = new PersianCalendar();

        currentCalendar.setPersianDate(mActiveYear, mActiveMount, getActiveDay());
        mActiveMountName = String.format(new Locale("fa"), "%s %2d",
                currentCalendar.getPersianMonthName(), currentCalendar.getPersianYear());

    }

    private void initViews() {

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void setAdapter(SmartCalendarCellAdapter<? extends RecyclerView.ViewHolder> adapter) {
        this.adapter = adapter;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSmartCalendarView.setActivePosition(mPosition);
        mSmartCalendarView.setActiveYear(mActiveYear);
        mSmartCalendarView.setActiveMount(mActiveMount);
//        mSmartCalendarView.setActiveDay(mActiveDay);
        mSmartCalendarView.setMountName(mActiveMountName);
        if (mSmartCalendarView.mOnDateChangeListener != null) {
            mSmartCalendarView.mOnDateChangeListener.onDateChanged(mActiveYear, mActiveMount, mActiveDay);
        }
        if (mSmartCalendarView.mOnDateSelectListener != null) {
            mSmartCalendarView.mOnDateSelectListener.onDateSelected(getSelectedDate());
        }


    }


    public void notifyDataSetChanged() {

        mLastSelectedPos = -1;
        calculateAddMount();
        adapter.setCellModels(getCellModels(mAddMount));
        updateMountName();
        updateTranslationY();
    }

    void updateTranslationY() {
        if (mSmartCalendarView.isExpanded()) {
            mCalendarRecyclerView.setTranslationY(0);
            return;
        }
        View view = getSelectedView();
        if (view != null) {
            int top = view.getTop();
            setTranslation(-top);
        }
    }

    public void notifyHasNoEvent(SmartCalendarEventModel eventModel) {
        String[] dates = eventModel.getStartDate().split("/");
        int day = Integer.parseInt(dates[2]);
        int mount = Integer.parseInt(dates[1]);
        int year = Integer.parseInt(dates[0]);
        int pos = -1;
        for (int i = 0; i < mCellModels.size(); i++) {
            SmartCalendarCellModel c = mCellModels.get(i);
            if (c.getPersianDay() == day && c.getCurrentMount() == mount && c.getCurrentYear() == year) {
                pos = i;
            }
        }
        if (pos != -1)
            adapter.notifyCellHasNoEvent(pos);

    }

    public void notifyHasEvent(SmartCalendarEventModel eventModel) {
        String[] dates = eventModel.getStartDate().split("/");
        int day = Integer.parseInt(dates[2]);
        int mount = Integer.parseInt(dates[1]);
        int year = Integer.parseInt(dates[0]);
        int pos = -1;
        for (int i = 0; i < mCellModels.size(); i++) {
            SmartCalendarCellModel c = mCellModels.get(i);
            if (c.getPersianDay() == day && c.getCurrentMount() == mount && c.getCurrentYear() == year) {
                pos = i;
            }
        }
        if (pos != -1)
            adapter.notifyCellHasEvent(pos);

//        adapter.notifyItemChanged(pos);
    }

    private void findViews(View view) {
        mCalendarRecyclerView = view.findViewById(R.id.smart_calendar_recyclerView);

    }

    private void setEventModels(List<? extends SmartCalendarEventModel> eventModels, int persianMonth) {
        List<SmartCalendarEventModel> currentEventModels = new ArrayList<>();
        if (eventModels != null) {
            for (SmartCalendarEventModel model : eventModels) {
                String mount = model.getStartDate().split("/")[1];
                if (persianMonth == Integer.parseInt(mount)) {
                    currentEventModels.add(model);
                }
            }
        }
        mCurrentMountEventModels = currentEventModels;
    }

    private SmartCalendarFragment findFragmentByPosition(int position) {
        return (SmartCalendarFragment) getParentFragmentManager().findFragmentByTag(mSmartCalendarView.mTags.get(position));
    }

    public List<? extends SmartCalendarEventModel> getCurrentMountEventModels() {
        return mCurrentMountEventModels;
    }

    private ArrayList<SmartCalendarCellModel> getCellModels(int addMount) {
        ArrayList<SmartCalendarCellModel> cellModels = new ArrayList<>();
        PersianCalendar persianCalendar = new PersianCalendar();
        int currentDay = persianCalendar.getPersianDay();
        int currentMount = persianCalendar.getPersianMonth();
        int currentYear = persianCalendar.getPersianYear();
        SmartCalendarFragment oldFragment = findFragmentByPosition(mOldPosition);
        int initialYear = mSmartCalendarView.getInitialYear();
        int initialMount = mSmartCalendarView.getInitialMount();
        int initialDay = mSmartCalendarView.getInitialDay();
        persianCalendar.setPersianDate(initialYear, initialMount, initialDay);
        persianCalendar.addPersianDate(Calendar.MONTH, addMount);
        if (oldFragment != null && oldFragment.adapter != null) {
            SmartCalendarCellModel cellModel = oldFragment.adapter.getSelectedDate();
            if (cellModel != null) {
//                mLastSelectedPos=oldFragment.adapter.getSelectedPosition();
                int selectedDay = cellModel.getPersianDay();
                if (mAddWeek != 0) {
                    persianCalendar.setPersianDate(persianCalendar.getPersianYear(), persianCalendar.getPersianMonth(),
                            selectedDay);
                }
            }
        }

        persianCalendar.addPersianDate(Calendar.WEEK_OF_MONTH, mAddWeek);
        int selectedMount = persianCalendar.getPersianMonth();
        int selectedDay = persianCalendar.getPersianDay();
        int selectedYear = persianCalendar.getPersianYear();

        setEventModels(mEventModels, persianCalendar.getPersianMonth());
        /*get day of week of first day of current mount*/
        PersianCalendar firstDay = new PersianCalendar();
        firstDay.setPersianDate(persianCalendar.getPersianYear(), persianCalendar.getPersianMonth(), 1);
        int dayOfWeekOfFirstDay = firstDay.get_Day_Of_Week();

        /*last day of last mount*/
        PersianCalendar lastMount = new PersianCalendar();
        lastMount.setPersianDate(persianCalendar.getPersianYear(), persianCalendar.getPersianMonth(), 15);
        lastMount.addPersianDate(Calendar.MONTH, -1);
        int lastDayOfLAstMount = getMaximumDAyOfMount(lastMount.getPersianYear(), lastMount.getPersianMonth());

        int startDayOfCalendar = lastDayOfLAstMount - (dayOfWeekOfFirstDay - 1);
        /*create persian calendar with startDayOfCalendar and persianCalendar.getPersianMonth()-1*/
        PersianCalendar startCalendar = new PersianCalendar();
        startCalendar.setPersianDate(lastMount.getPersianYear(), lastMount.getPersianMonth(), startDayOfCalendar);

        PersianCalendar temp = new PersianCalendar();
        temp.setPersianDate(startCalendar.getPersianYear(), startCalendar.getPersianMonth(), startCalendar.getPersianDay());


        for (int i = 0; i < 42; i++) {

            SmartCalendarCellModel model = new SmartCalendarCellModel();
            model.setPersianDay(temp.getPersianDay());
            model.setGregorianDay(temp.get(Calendar.DAY_OF_MONTH));
            model.setDayOfWeek(temp.get_Day_Of_Week());
            model.setCurrentMount(temp.getPersianMonth());
            model.setCurrentGregorianMount(temp.get(Calendar.DAY_OF_MONTH));
            model.setCurrentYear(temp.getPersianYear());
            model.setCurrentGregorianYear(temp.get(Calendar.YEAR));
            model.setCurrentMountName(temp.getPersianMonthName());
            model.setDayOfWeekName(temp.getPersianWeekDayName());
            if (temp.getPersianMonth() != persianCalendar.getPersianMonth()) {
                model.setInactivate(true);
            } else {
                model.setInactivate(false);
            }
            if (temp.getPersianDay() == currentDay &&
                    temp.getPersianMonth() == currentMount &&
                    temp.getPersianYear() == currentYear) {
                model.setCurrentDay(true);
                if (temp.getPersianMonth() == selectedMount && mAddWeek == 0
                        && mLastSelectedPos == -1 && model.getPersianDay() == selectedDay) {
                    model.setSelected(true);
                }
            }
            if (mSmartCalendarView.isExpanded()) {
                /*if it is not current mount*/
//                if (mAddMount != 0 && mLastSelectedPos == -1 && initialDay==0) {


                if (isInitialDateSet() && mLastSelectedPos == -1
                        && temp.getPersianMonth() == initialMount
                        && temp.getPersianYear() == initialYear
                ) {/*if initial date is set*/
                    if (temp.getPersianDay() == initialDay
                            && !model.isInactivate()) {
                        model.setSelected(true);
                    }
                } else {/*if initial date is NOT set*/
                    if (mAddMount != 0 && mLastSelectedPos == -1) {
                        if (selectedMount == temp.getPersianMonth() &&
                                temp.getPersianDay() == 1) {
                            model.setSelected(true);
                        }
                    } else if (mLastSelectedPos != -1 && i == mLastSelectedPos) {
                        model.setSelected(true);
                    }
                }

            } else {
                /*if it is no current week*/
                if (isInitialDateSet() && mLastSelectedPos == -1
                        && temp.getPersianMonth() == initialMount
                        && temp.getPersianYear() == initialYear
                        && mAddWeek == 0
                ) {/*if initial date is set*/
                    if (temp.getPersianDay() == initialDay
                            && !model.isInactivate()) {
                        model.setSelected(true);
                    }
                } else {/*if initial date is NOT set*/

                    if (mAddWeek != 0 && !model.isInactivate() &&
                            mLastSelectedPos == -1) {
                        if (model.getPersianDay() == selectedDay && model.getCurrentMount() == selectedMount
                                && model.getCurrentYear() == selectedYear) {
                            model.setSelected(true);
                        }
                    } else if (mLastSelectedPos != -1 && i == mLastSelectedPos) {
                        model.setSelected(true);
                    }
                }
            }

            if (model.isSelected()) {
                mActiveDay = model.getPersianDay();
            }

            ArrayList<SmartCalendarEventModel> eventModelList = new ArrayList<>();
            for (SmartCalendarEventModel eventModel : mCurrentMountEventModels) {
                String[] dates = eventModel.getStartDate().split("/");
                int eventDay = Integer.parseInt(dates[2]);
                int eventMount = Integer.parseInt(dates[1]);
                int eventYear = Integer.parseInt(dates[0]);

                if (model.getPersianDay() == eventDay && eventMount == model.getCurrentMount() && eventYear == model.getCurrentYear()) {
                    model.setHasEvent(true);
                    eventModelList.add(eventModel);
                }
            }
            model.setEventModels(eventModelList);
            cellModels.add(model);
            temp.addPersianDate(Calendar.DAY_OF_MONTH, 1);
        }

        mActiveMount = cellModels.get(cellModels.size() / 2).getCurrentMount();
        mActiveYear = cellModels.get(cellModels.size() / 2).getCurrentYear();

        return cellModels;
    }

    private boolean isInitialDateSet() {
        return mSmartCalendarView.getInitialDay() != 0;
    }

    private int getMaximumDAyOfMount(int year, int mount) {
        PersianCalendar persianCalendar = new PersianCalendar();
        persianCalendar.setPersianDate(year, mount, 1);
        if (mount <= 6) {
            return 31;
        }
        if (mount < 12) {
            return 30;
        }

        if (persianCalendar.isPersianLeapYear()) {
            return 30;
        } else {
            return 29;
        }

    }

    public SmartCalendarCellModel getSelectedDate() {
        return adapter.getSelectedDate();
    }

    public void setSelectedDate(SmartCalendarCellModel selectedDate) {
        adapter.setSelectedDate(selectedDate);
    }

    public int getActiveYear() {
        return mActiveYear;
    }

    public int getActiveMount() {
        return mActiveMount;
    }

    public int getActiveDay() {
        SmartCalendarCellModel model = getSelectedDate();
        if (model != null) {
            return model.getPersianDay();
        }
        return mActiveDay;
    }

    public int getDifferenceMount() {
        return mAddMount;
    }


    public String getActiveMountName() {
        return mActiveMountName;
    }

    public void setTranslation(int top) {
        if (mSmartCalendarView.isExpanded()) {
            mCalendarRecyclerView.setTranslationY(0);
            return;
        }

        mCalendarRecyclerView.setTranslationY(top);
    }

    public SmartCalendarCellAdapter<? extends RecyclerView.ViewHolder> getAdapter() {
        return adapter;
    }

    View getSelectedView() {
        View view = null;
        GridLayoutManager gridLayoutManager = (GridLayoutManager) mCalendarRecyclerView.getLayoutManager();
        if (gridLayoutManager != null) {
            view = gridLayoutManager.findViewByPosition(adapter.getSelectedPosition());
        }
        return view;
    }

    RecyclerView getCalendarRecyclerView() {
        return mCalendarRecyclerView;
    }
}
