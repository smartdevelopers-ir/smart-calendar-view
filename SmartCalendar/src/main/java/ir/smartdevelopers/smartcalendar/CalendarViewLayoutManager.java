package ir.smartdevelopers.smartcalendar;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CalendarViewLayoutManager extends GridLayoutManager {
    private SmartCalendarFragment mSmartCalendarFragment;
    public CalendarViewLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public CalendarViewLayoutManager(Context context, int spanCount,SmartCalendarFragment calendarFragment) {
        super(context, spanCount);
        mSmartCalendarFragment=calendarFragment;
    }

    public CalendarViewLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout,
                                     SmartCalendarFragment calendarFragment) {
        super(context, spanCount, orientation, reverseLayout);
        mSmartCalendarFragment=calendarFragment;
    }

    @Override
    public void onLayoutCompleted(RecyclerView.State state) {
        super.onLayoutCompleted(state);

        mSmartCalendarFragment.updateTranslationY();
    }

}
