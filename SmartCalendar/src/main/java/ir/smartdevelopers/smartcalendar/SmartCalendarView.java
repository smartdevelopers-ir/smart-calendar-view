package ir.smartdevelopers.smartcalendar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.RestrictTo;
import androidx.annotation.Size;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

import ir.smartdevelopers.smartcalendar.persiandatepicker.util.PersianCalendar;


public class SmartCalendarView extends RelativeLayout implements RecyclerView.OnItemTouchListener {
    private ViewPager2 mViewPager2;
    private SmartCalendarViewAdapter mSmartCalendarViewAdapter;
    private ViewPager2.OnPageChangeCallback mOnPageChangeCallback;
    private ArrayList<? extends SmartCalendarEventModel> mEventModels;
    private SmartCalendarCellAdapterFactory mAdapterFactory;
    private MotionHelper mMotionHelper;
    private TextView txtMountName;
    private boolean mIsExpanded =true;
    SparseArray<String> mTags=new SparseArray<>();
    int mActivePosition =0;
    private  int mSelectedPagePos=-1;
    private boolean mIsExpandable;
    private int mActiveYear,mActiveMount,mActiveDay;
    private ImageView imgArrow;
    private FrameLayout mArrowPanel;
    /** Expandable helper arrow tint*/
    private int mArrowTintColor= Color.BLACK;
    private boolean mShowArrowPanel;
    OnDateChangeListener mOnDateChangeListener;
    OnDateSelectListener mOnDateSelectListener;
    /**the bottom of smart_calendar_view_bottomLineHelperView that specify the real siz of parent for expanding*/
    private int mRealParentBottom;
    /**Top of viewPager to prevent decreasing size when collapsing calendar*/
    private int mRealParentTop;
    private View mBottomLineHelperView;

    /** ViewGroup of days of week*/
    private LinearLayout mDaysOfWeekViewGroup;

    private int mInitialDay=0;
    private int mInitialMount=0;
    private int mInitialYear=0;
    public SmartCalendarView(@NonNull Context context) {
        super(context);
        init(context, null);


    }

    public SmartCalendarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SmartCalendarView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SmartCalendarView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        setSaveEnabled(true);
        setLayoutDirection(LAYOUT_DIRECTION_RTL);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.smart_calendar_view_layout, this, true);
        mViewPager2 = view.findViewById(R.id.smart_calendar_view_viewPager);
        imgArrow = view.findViewById(R.id.smart_calendar_view_imgArrow);
        mArrowPanel = view.findViewById(R.id.smart_calendar_view_arrowPanel);
        mBottomLineHelperView=view.findViewById(R.id.smart_calendar_view_bottomLineHelperView);
        mDaysOfWeekViewGroup=view.findViewById(R.id.smart_calendar_week_name_holder);

        if (attrs!=null){
            TypedArray typedArray=context.obtainStyledAttributes(attrs,R.styleable.SmartCalendarView);
            mIsExpanded=typedArray.getBoolean(R.styleable.SmartCalendarView_scv_expanded,true);
            mIsExpandable =typedArray.getBoolean(R.styleable.SmartCalendarView_scv_expandable,false);
            mArrowTintColor=typedArray.getColor(R.styleable.SmartCalendarView_scv_arrowTint,mArrowTintColor);
            mShowArrowPanel=typedArray.getBoolean(R.styleable.SmartCalendarView_scv_showArrowPanel,true);
            mShowArrowPanel=typedArray.getBoolean(R.styleable.SmartCalendarView_scv_showArrowPanel,true);
            int arrowPanelColor=typedArray.getColor(R.styleable.SmartCalendarView_scv_arrowPanelColor,Color.WHITE);
            mArrowPanel.setVisibility(mShowArrowPanel?VISIBLE:GONE);
            mArrowPanel.setBackgroundColor(arrowPanelColor);
            typedArray.recycle();
        }



        mViewPager2.setLayoutDirection(LAYOUT_DIRECTION_RTL);
//        ((RecyclerView) mViewPager2.getChildAt(0)).setItemViewCacheSize(2);
        mViewPager2.setOffscreenPageLimit(1);
        txtMountName = view.findViewById(R.id.smart_calendar_view_txtDate);
        mMotionHelper = new MotionHelper(context);
        if (getId()==0){
            setId((int) System.currentTimeMillis());
        }
    }




    public void setAdapter(SmartCalendarCellAdapterFactory adapterFactory, FragmentActivity fragmentActivity) {
        mAdapterFactory=adapterFactory;
        mSmartCalendarViewAdapter = new SmartCalendarViewAdapter(fragmentActivity, adapterFactory,this);
        updateAdapter();
    }

    public void setAdapter(SmartCalendarCellAdapterFactory adapterFactory, Fragment fragment) {
        mAdapterFactory=adapterFactory;
        mSmartCalendarViewAdapter = new SmartCalendarViewAdapter(fragment, adapterFactory,this);
        updateAdapter();
    }

    @SuppressLint("ClickableViewAccessibility")
    void updateAdapter() {
        if (mInitialDay==0){
            PersianCalendar persianCalendar=new PersianCalendar();
            setInitialDate(persianCalendar.getPersianYear(),persianCalendar.getPersianMonth(),
                    persianCalendar.getPersianDay());
        }
        mSmartCalendarViewAdapter.setEvents(mEventModels);
        if (mIsExpandable) {
            ((RecyclerView) mViewPager2.getChildAt(0)).addOnItemTouchListener(this);
            if (mShowArrowPanel){
                mArrowPanel.setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()){
                            case MotionEvent.ACTION_CANCEL:
                            case MotionEvent.ACTION_UP:
                                onTouchEvent((RecyclerView) mViewPager2.getChildAt(0),event);
                                return true;
                            case MotionEvent.ACTION_MOVE:
                                if (onInterceptTouchEvent((RecyclerView) mViewPager2.getChildAt(0),event)){
                                    onTouchEvent((RecyclerView) mViewPager2.getChildAt(0),event);
                                    return true;
                                }
                            case MotionEvent.ACTION_DOWN:
                                onInterceptTouchEvent((RecyclerView) mViewPager2.getChildAt(0),event);
                                return true;
                        }

                        return false;
                    }
                });
            }
        }

        if (mOnPageChangeCallback != null)
            mViewPager2.registerOnPageChangeCallback(mOnPageChangeCallback);


        mViewPager2.setAdapter(mSmartCalendarViewAdapter);
        mViewPager2.setCurrentItem(mSelectedPagePos==-1?Integer.MAX_VALUE / 2:mSelectedPagePos, false);
        post(() -> {

            mRealParentBottom=getRealTop(mBottomLineHelperView);
            mRealParentTop=getRealTop(mViewPager2);
            if (!mIsExpanded){
                toggle(false, ScrollDirection.GOING_UP,null);

            }
            if (mShowArrowPanel && mIsExpandable) {
                new Handler().postDelayed(this::startArrowAnimation, 1500);
            }
        });

    }

    public void setInitialDate(int year,int mount,int day){
        mInitialYear=year;
        mInitialMount=mount;
        mInitialDay=day;
    }
    private int getRealTop(View view){
        int[] location=new int[2];
        view.getLocationOnScreen(location);
        return location[1];
    }
    public void startArrowAnimation(){
        int[] animationCount={0};
        AnimatedVectorDrawableCompat animatedVectorDrawableCompat= AnimatedVectorDrawableCompat.create(getContext(),
                mIsExpanded ? R.drawable.avd_double_arrow_up : R.drawable.avd_double_arrow_down);
        animatedVectorDrawableCompat.setTint(mArrowTintColor);
        animatedVectorDrawableCompat.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
            @Override
            public void onAnimationEnd(Drawable drawable) {
                super.onAnimationEnd(drawable);
                if (animationCount[0]<1) {
                    animatedVectorDrawableCompat.start();
                    animationCount[0]++;
                }
            }
        });
        imgArrow.setImageDrawable(animatedVectorDrawableCompat);
        animatedVectorDrawableCompat.start();
    }
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState=(SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());

        mTags=savedState.tags;
        mIsExpanded=savedState.isExpanded;
        mIsExpandable =savedState.isExpandable;
        mSelectedPagePos=savedState.selectedPagePos;
        mActivePosition =mSelectedPagePos;
        txtMountName.setText(savedState.mountName);

    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parcelable=super.onSaveInstanceState();

        SavedState savedState=new SavedState(parcelable);
        savedState.tags=mTags;
        savedState.isExpanded=mIsExpanded;
        savedState.isExpandable= mIsExpandable;
        savedState.mountName=txtMountName.getText().toString();
        savedState.selectedPagePos=mViewPager2.getCurrentItem();
        return savedState;
    }

    public boolean isExpandable() {
        return mIsExpandable;
    }

    public void setExpandable(boolean expandable) {
        mIsExpandable = expandable;
        if (mIsExpandable){
            ((RecyclerView) mViewPager2.getChildAt(0)).addOnItemTouchListener(this);
        }else {
            ((RecyclerView) mViewPager2.getChildAt(0)).removeOnItemTouchListener(this);
        }
    }

    public void setOnDateChangeListener(OnDateChangeListener onDateChangeListener) {
        mOnDateChangeListener = onDateChangeListener;
    }

    public void setOnDateSelectListener(OnDateSelectListener onDateSelectListener) {
        mOnDateSelectListener = onDateSelectListener;
    }

    public SmartCalendarCellAdapterFactory getAdapterFactory() {
        return mAdapterFactory;
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
     int getInitialDay() {
        return mInitialDay;
    }

    public int getInitialMount() {
        return mInitialMount;
    }

    public int getInitialYear() {
        return mInitialYear;
    }


    private  static class SavedState extends BaseSavedState {
        SparseArray<String> tags;
        String mountName;
        int selectedPos;
        int selectedPagePos;
        boolean isExpanded;
        boolean isExpandable;
        public SavedState(Parcel source) {
            super(source);
            tags=source.readSparseArray(getClass().getClassLoader());
            mountName=source.readString();
            selectedPos=source.readInt();
            selectedPagePos=source.readInt();
            isExpanded=source.readInt()==0;
            isExpandable=source.readInt()==0;
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeSparseArray(tags);
            out.writeString(mountName);
            out.writeInt(selectedPos);
            out.writeInt(selectedPagePos);
            out.writeInt(isExpanded?1:0);
            out.writeInt(isExpandable?1:0);
        }
        public final static Creator<SavedState> CREATOR=new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    private void createOtherPages(int position) {
        if (mSmartCalendarViewAdapter.findFragmentByPos(position-1)==null){
            mSmartCalendarViewAdapter.notifyItemInserted(position-1);
        }
        if (mSmartCalendarViewAdapter.findFragmentByPos(position+1)==null){
            mSmartCalendarViewAdapter.notifyItemInserted(position+1);
        }
    }

    private void onExpanded(){
        notifyOthers();
    }
    private void onCollapsed(){
        notifyOthers();
    }
    private void notifyOthers(){
        int currentPos=mViewPager2.getCurrentItem();
        for (int i=currentPos-5;i<currentPos+5;i++){

            SmartCalendarFragment fragment=mSmartCalendarViewAdapter.findFragmentByPos(i);
            if (fragment!=null){
                if (i==currentPos){
                    fragment.calculateAddMount();
                    continue;
                }
                fragment.notifyDataSetChanged();
            }
        }
    }
    public int getActivePosition() {
        return mActivePosition;
    }

    void setMountName(String mountName){
            post(()->txtMountName.setText(mountName));
    }
    void addTag(int position, String tag){
        mTags.put(position,tag);
    }
    public void setOnPageChangeCallback(ViewPager2.OnPageChangeCallback onPageChangeCallback) {
        mOnPageChangeCallback = onPageChangeCallback;
    }

    public void setEventModels(ArrayList<? extends SmartCalendarEventModel> eventModels) {
        mEventModels = eventModels;
    }

//    public void scrollToDate(int year, int mount) {
//        int activeYear = getActiveYear();
//        int activeMount = getActiveMount();
//        int differenceMount = mSmartCalendarViewAdapter.getDifferenceMount(mViewPager2.getCurrentItem());
//        PersianCalendar activeCalendar = new PersianCalendar();
//        activeCalendar.setPersianDate(activeYear, activeMount, 1);
//        PersianCalendar targetCalendar = new PersianCalendar();
//        targetCalendar.setPersianDate(year, mount, 1);
//
//        long diffInMillis = targetCalendar.getTimeInMillis() - activeCalendar.getTimeInMillis();
//        int diffDays = (int) (diffInMillis / 1000 / 60 / 60 / 24);// diff mount
//        int diffMount = (int) Math.round(diffDays / 30.41);
//        int diffPos = differenceMount + diffMount;
//        mViewPager2.setCurrentItem(Integer.MAX_VALUE / 2 + diffPos, true);
//
//    }

    public void slideLeft(boolean animate) {
        int currentPos = mViewPager2.getCurrentItem();
        mViewPager2.setCurrentItem(currentPos - 1, animate);
    }

    public void slideRight(boolean animate) {
        int currentPos = mViewPager2.getCurrentItem();
        mViewPager2.setCurrentItem(currentPos + 1, animate);
    }

    public int getActiveYear() {
        return mActiveYear;

    }

     void setActiveYear(int activeYear) {
        mActiveYear = activeYear;
    }

    public int getActiveMount() {
        return mActiveMount;
    }

     void setActiveMount(int activeMount) {
        mActiveMount = activeMount;
    }



    public int getActiveDay() {
        return mSmartCalendarViewAdapter.getActiveDay(mViewPager2.getCurrentItem());
    }

     void setActiveDay(int activeDay) {
        mActiveDay = activeDay;
    }

     void setActivePosition(int activePosition) {
        mActivePosition = activePosition;
    }


    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent ev) {

        if (!mIsExpandable){
            return false;
        }
        mMotionHelper.initMotion(ev);

        if (mMotionHelper.isMovingVertically(ev)) {
            Logger.LogV("moving vertically");

            return true;
        }




        return false;
    }

    private int mHeightDiff = 0;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = getMeasuredHeight();
        int width = getMeasuredWidth();
        int newHeight = height - mHeightDiff;

        setMeasuredDimension(width, newHeight);
    }


    private int oldY = 0;
    private int mOldHeightDiff = 0;

    @Override
    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        if (!mIsExpandable){
            return ;
        }
        mMotionHelper.initMotion(e);
        if (mMotionHelper.selectedView == null) {
            return;
        }
        RecyclerView recyclerView=mSmartCalendarViewAdapter.getCalendarRecyclerView(mViewPager2.getCurrentItem());
        int recyclerViewHeight = rv.getMeasuredHeight();


        int selectedViewHeight = mMotionHelper.selectedView.getMeasuredHeight();
        int maxDifference = Math.abs(recyclerViewHeight - selectedViewHeight);
        int targetTranslation =  mMotionHelper.selectedView.getTop();
        if (mMotionHelper.isMovingVertically(e)) {

            int Y = (int) (e.getRawY());
            if (Y > mRealParentBottom) {
                Y = mRealParentBottom;
            } else if (Y < mRealParentTop) {
                Y = mRealParentTop;
            }

            int diff = oldY - Y;
            oldY = Y;


            mHeightDiff += diff;
            /*t= x * MAXt /MAXx*/
            float translation = 1f * mHeightDiff * targetTranslation / maxDifference;
            if (translation > targetTranslation) {
                translation = targetTranslation;
            } else if (translation < 0) {
                translation = 0;
            }
            if (mHeightDiff > maxDifference) {
                mHeightDiff = maxDifference;
            } else if (mHeightDiff < 0) {
                mHeightDiff = 0;
            }
            if (mOldHeightDiff != mHeightDiff) {
                recyclerView.setTranslationY(-translation);
                mArrowPanel.setTranslationY(-mHeightDiff);
                requestLayout();
            }

            mOldHeightDiff = mHeightDiff;
        }

        int action = e.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:


                if (mMotionHelper.scrollDirection==ScrollDirection.GOING_DOWN){

                    expand(true);
                }else if (mMotionHelper.scrollDirection==ScrollDirection.GOING_UP){

                    collapse(true);
                }
        }
    }

    class MotionHelper {
        private float mLastY = 0;
        private float mLastX = 0;
        private float mLastRawY = 0;
        private int mTouchSlop;
        private boolean mIsMoving = false;
        private View selectedView;
        private ScrollDirection scrollDirection;

        public MotionHelper(Context context) {
            ViewConfiguration configuration = ViewConfiguration.get(context);
            mTouchSlop = configuration.getScaledTouchSlop();

        }

        public void initMotion(MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mLastX = event.getX();
                    mLastY = event.getY();
                    mLastRawY = event.getRawY();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    mLastY = 0;
                    mLastX = 0;
                    mLastRawY = 0;
                    mIsMoving = false;
                    break;
            }
        }

        public boolean isMovingVertically(MotionEvent ev) {
            if (ev.getAction() == MotionEvent.ACTION_MOVE) {

                if (mLastRawY > ev.getRawY()) {
                    scrollDirection = ScrollDirection.GOING_UP;
                } else if (mLastRawY < ev.getRawY()) {
                    scrollDirection = ScrollDirection.GOING_DOWN;
                }
                mLastRawY=ev.getRawY();
                if (mIsMoving) {
                    return true;
                }

                float xdiff = Math.abs(mLastX - ev.getX());
                float ydiff = Math.abs(mLastY - ev.getY());

                if (xdiff > ydiff) {
                    selectedView = null;
                    return false;
                }
                if (ydiff > mTouchSlop) {
                    mIsMoving = true;
                    oldY=(int) ev.getRawY();
                    selectedView = mSmartCalendarViewAdapter.getSelectedView(mViewPager2.getCurrentItem());

                    return true;
                }
            }
            return false;
        }

    }
    public void toggle(){
        toggle(true);
    }
    public void toggle(boolean animate){
        if (mIsExpanded){
            // collapse it
            collapse(animate);
        }else {
            expand(animate);
        }
    }
    private void toggle(boolean animate,ScrollDirection direction,OnExpandEndListener expandEndListener){
        View selectedView=mSmartCalendarViewAdapter.getSelectedView(mViewPager2.getCurrentItem());
        View rv=getCalendarRecyclerView();
        int recyclerViewHeight = rv.getMeasuredHeight();
        int selectedViewHeight = selectedView.getMeasuredHeight();
        int maxDifference = Math.abs(recyclerViewHeight - selectedViewHeight);
        int targetTranslation = selectedView.getTop();
        switch (direction){
            case GOING_UP:
                if (animate){
                    animateCollapseOrExpand(maxDifference,targetTranslation,rv,maxDifference,expandEndListener);
                }else {
                    mHeightDiff=maxDifference;
                    float translation = 1f * mHeightDiff * targetTranslation / maxDifference;
                    rv.setTranslationY(-translation);
                    mArrowPanel.setTranslationY(-mHeightDiff);
                    requestLayout();
                    if (expandEndListener!=null){
                        expandEndListener.onEnd();
                    }
                }
                break;
            case GOING_DOWN:
                if (animate){
                    animateCollapseOrExpand(maxDifference,targetTranslation,rv,0,expandEndListener);
                }else {
                    rv.setTranslationY(0);
                    mHeightDiff=0;
                    mArrowPanel.setTranslationY(-mHeightDiff);
                    requestLayout();
                    if (expandEndListener!=null){
                        expandEndListener.onEnd();
                    }
                }
        }

    }
    public void collapse(boolean animate){
        mIsExpanded =false;

        toggle(animate, ScrollDirection.GOING_UP, new OnExpandEndListener() {
            @Override
            public void onEnd() {
                onCollapsed();
            }
        });
    }
    public void expand(boolean animate){
        mIsExpanded =true;

        toggle(animate, ScrollDirection.GOING_DOWN, new OnExpandEndListener() {
            @Override
            public void onEnd() {
                onExpanded();
            }
        });
    }
    private void animateCollapseOrExpand(int maxDiff,int targetTranslation,View view,int targetDiff,OnExpandEndListener expandEndListener) {
        ValueAnimator valueAnimator=ValueAnimator.ofInt(mHeightDiff,targetDiff);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mHeightDiff=(int) animation.getAnimatedValue();
                float translation = 1f * mHeightDiff * targetTranslation / maxDiff;
                view.setTranslationY(-translation);
                mArrowPanel.setTranslationY(-mHeightDiff);
                requestLayout();
            }

        });
        valueAnimator.setInterpolator(new LinearOutSlowInInterpolator());
        valueAnimator.setDuration(300);
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (expandEndListener!=null){
                    expandEndListener.onEnd();
                }
            }
        });
        valueAnimator.start();

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    public boolean isExpanded() {
        return mIsExpanded;
    }





    public void setDate(int year,int mount){
        setDate(year,mount,1);
    }
    public void goToToday(){
        PersianCalendar persianCalendar=new PersianCalendar();
        setDate(persianCalendar.getPersianYear(),persianCalendar.getPersianMonth(),persianCalendar.getPersianDay());
    }
    private void setDate(int year,int mount,int day){
        int currentPos=mViewPager2.getCurrentItem();
        if (mIsExpanded){
            int activeMount=mSmartCalendarViewAdapter.getActiveMount(currentPos);
            int activeYear=mSmartCalendarViewAdapter.getActiveYear(currentPos);
            int diff=calculateDifferenceMount(year,mount,activeYear,activeMount);
            int newPos=currentPos+diff;
            mViewPager2.setCurrentItem(newPos,false);

        }else {

            int activeMount=mSmartCalendarViewAdapter.getActiveMount(currentPos);
            int activeYear=mSmartCalendarViewAdapter.getActiveYear(currentPos);
            int activeDay=mSmartCalendarViewAdapter.getActiveDay(currentPos);
            PersianCalendar activeCalendar=new PersianCalendar();
            activeCalendar.setPersianDate(activeYear,activeMount,activeDay);
            PersianCalendar targetCalendar=new PersianCalendar();
            targetCalendar.setPersianDate(year,mount,day);
            long diffMillis=targetCalendar.getTimeInMillis()-activeCalendar.getTimeInMillis();
            long diffDays=diffMillis/1000/60/60/24;
            int diffPos=(int) diffDays/7;
            int newPos=currentPos+diffPos;
            mViewPager2.setCurrentItem(newPos,false);


        }
    }

    static int calculateDifferenceMount(int newYear, int newMount, int oldYear, int oldMount){

        if (newYear==0||newMount ==0){
            return 0;
        }
        int diffYear=newYear-oldYear;
        int diffMount=newMount-oldMount;
        return (diffYear*12)+diffMount;
    }

   public void setSelectedDate(SmartCalendarCellModel selectedDate){
        if (selectedDate.getCurrentMount()!=mActiveMount){
            throw new IllegalArgumentException("Can not set selectedDate on another mount");
        }
        mSmartCalendarViewAdapter.setSelectedDate(selectedDate,mActivePosition);
   }
   /**get active mount event models*/
    public List<? extends SmartCalendarEventModel> getCurrentEventModel(){
        return mSmartCalendarViewAdapter.getCurrentEventModel(mActivePosition);
    }
    /**if a cell or a date updates it's events just create an event model and set it by this to update cell property*/
    public void notifyHasEvent(SmartCalendarEventModel eventModel ){
        if (eventModel.startDate==null){
            throw new IllegalArgumentException("You must set startDay for event models");
        }
        for (int i=mActivePosition-5;i<mActivePosition+5;i++){
            mSmartCalendarViewAdapter.notifyHasEvent(i,eventModel);
        }
    }
    /**if a cell or a date lose it's events you can clear that event and then notify not update cell*/
    public void notifyHasNoEvent(SmartCalendarEventModel eventModel ){
        if (eventModel.startDate==null){
            throw new IllegalArgumentException("You must set startDay for event models");
        }
        for (int i=mActivePosition-5;i<mActivePosition+5;i++){
            mSmartCalendarViewAdapter.notifyHasNoEvent(i,eventModel);
        }
    }
   public RecyclerView getCalendarRecyclerView(){
        return mSmartCalendarViewAdapter.getCalendarRecyclerView(mActivePosition);
    }

    public TextView getMountNameTextView() {
        return txtMountName;
    }
    public void setDayOfWeekNames(@Size(value = 7) String[] names){
        int size=mDaysOfWeekViewGroup.getChildCount();
        for (int i=0;i<size;i++){
            ((TextView)mDaysOfWeekViewGroup.getChildAt(i)).setText(names[i]);
        }
    }

    public  SmartCalendarCellAdapter<?extends RecyclerView.ViewHolder> getCurrentCalendarRecyclerViewAdapter(){

        SmartCalendarFragment fragment=mSmartCalendarViewAdapter.findFragmentByPos(mActivePosition);
        if (fragment!=null){
            return fragment.getAdapter();
        }
        return null;
    }
    static class CalendarBinder extends Binder{
         SmartCalendarView mSmartCalendarView;

        public CalendarBinder( SmartCalendarView smartCalendarView) {
            mSmartCalendarView = smartCalendarView;
        }

        public  SmartCalendarView getSmartCalendarView() {
            return mSmartCalendarView;
        }
    }
    private interface OnExpandEndListener{
        void onEnd();
    }
    public interface OnDateChangeListener{
        void onDateChanged(int year,int mount,int day);
    }
    public interface OnDateSelectListener{
        void onDateSelected(SmartCalendarCellModel cellModel);
    }

}
