package ir.smartdevelopers.smartcalendar;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;


class SmartCalendarViewAdapter extends FragmentStateAdapter {

//    private SmartCalendarCellAdapter.OnDateSelectListener mOnDateSelectListener;


   private ArrayList< ? extends  SmartCalendarEventModel> mEventModels;
   private FragmentManager mFragmentManager;
   private  SmartCalendarCellAdapterFactory mSmartCalendarCellAdapterFactory;
   private  SmartCalendarView mSmartCalendarView;
   private int oldPosition=0;
   private int addMount=0;
   private boolean firstInit=true;


   public SmartCalendarViewAdapter(@NonNull FragmentActivity fragmentActivity,  SmartCalendarCellAdapterFactory smartCalendarCellAdapterFactory,  SmartCalendarView smartCalendarView) {
       super(fragmentActivity);
       mFragmentManager=fragmentActivity.getSupportFragmentManager();
       mSmartCalendarCellAdapterFactory=smartCalendarCellAdapterFactory;

       mSmartCalendarView = smartCalendarView;

   }

   public SmartCalendarViewAdapter(@NonNull Fragment fragment,  SmartCalendarCellAdapterFactory smartCalendarCellAdapterFactory,  SmartCalendarView smartCalendarView) {
       super(fragment);
       mFragmentManager=fragment.getChildFragmentManager();
       mSmartCalendarCellAdapterFactory=smartCalendarCellAdapterFactory;
       mSmartCalendarView = smartCalendarView;
   }


   @NonNull
   @Override
   public Fragment createFragment(int position) {


        SmartCalendarFragment calendarFragment=  SmartCalendarFragment.getInstance(position,
               mEventModels,mSmartCalendarCellAdapterFactory,mSmartCalendarView.getId());
       oldPosition=position;
       return calendarFragment;


   }

    void notifyHasNoEvent(int fragmentPos,  SmartCalendarEventModel eventModel){
        SmartCalendarFragment smartCalendarFragment=
               ( SmartCalendarFragment) mFragmentManager.findFragmentByTag(mSmartCalendarView.mTags.get(fragmentPos));
       if (smartCalendarFragment!=null) {
           smartCalendarFragment.notifyHasNoEvent(eventModel);
       }
   }
    void notifyHasEvent(int fragmentPos,  SmartCalendarEventModel eventModel){
        SmartCalendarFragment smartCalendarFragment=
               ( SmartCalendarFragment) mFragmentManager.findFragmentByTag(mSmartCalendarView.mTags.get(fragmentPos));
       if (smartCalendarFragment!=null) {
           smartCalendarFragment.notifyHasEvent(eventModel);
       }
   }

   @Override
   public int getItemCount() {
       return Integer.MAX_VALUE;
   }


     SmartCalendarCellModel getSelectedDate(int fragmentPosition){
        SmartCalendarFragment smartCalendarFragment=
               ( SmartCalendarFragment) mFragmentManager.findFragmentByTag(mSmartCalendarView.mTags.get(fragmentPosition));
          if (smartCalendarFragment!=null) {
              return smartCalendarFragment.getSelectedDate();
          }else {
              return null;
          }
   }
    void setSelectedDate( SmartCalendarCellModel selectedDate, int position){
        SmartCalendarFragment smartCalendarFragment=
               ( SmartCalendarFragment) mFragmentManager.findFragmentByTag(mSmartCalendarView.mTags.get(position));
       if (smartCalendarFragment!=null) {
          smartCalendarFragment.setSelectedDate(selectedDate);
       }
   }

    List<? extends  SmartCalendarEventModel> getCurrentEventModel(int position){
        SmartCalendarFragment smartCalendarFragment=
               ( SmartCalendarFragment) mFragmentManager.findFragmentByTag(mSmartCalendarView.mTags.get(position));
       if (smartCalendarFragment!=null) {
           return smartCalendarFragment.getCurrentMountEventModels();
       }
       return null;
   }


    void setEvents(ArrayList<? extends  SmartCalendarEventModel> eventModels) {
    mEventModels=eventModels;
   }

    int getActiveYear(int fragmentPosition) {
        SmartCalendarFragment smartCalendarFragment=
               ( SmartCalendarFragment) mFragmentManager.findFragmentByTag(mSmartCalendarView.mTags.get(fragmentPosition));
       if (smartCalendarFragment!=null){
           return smartCalendarFragment.getActiveYear();
       }
       return 0;
   }

    int getActiveMount(int fragmentPosition) {
        SmartCalendarFragment smartCalendarFragment=
               ( SmartCalendarFragment) mFragmentManager.findFragmentByTag(mSmartCalendarView.mTags.get(fragmentPosition));
       if (smartCalendarFragment!=null){
           return smartCalendarFragment.getActiveMount();
       }
       return 0;
   }

    int getActiveDay(int fragmentPosition) {
        SmartCalendarFragment smartCalendarFragment=
               ( SmartCalendarFragment) mFragmentManager.findFragmentByTag(mSmartCalendarView.mTags.get(fragmentPosition));
       if (smartCalendarFragment!=null){
           return smartCalendarFragment.getActiveDay();
       }
       return 1;
   }
    int getDifferenceMount(int fragmentPosition){
        SmartCalendarFragment smartCalendarFragment=
               ( SmartCalendarFragment) mFragmentManager.findFragmentByTag(mSmartCalendarView.mTags.get(fragmentPosition));
       if (smartCalendarFragment!=null){
           return smartCalendarFragment.getDifferenceMount();
       }
       return 0;
   }
    View getSelectedView(int fragmentPosition){
       View selectedView=null;
        SmartCalendarFragment smartCalendarFragment=
               ( SmartCalendarFragment) mFragmentManager.findFragmentByTag(mSmartCalendarView.mTags.get(fragmentPosition));
       if (smartCalendarFragment!=null){
           selectedView=smartCalendarFragment.getSelectedView();
       }
       return selectedView;
   }
   RecyclerView getCalendarRecyclerView(int fragmentPosition){
       RecyclerView recyclerView=null;
        SmartCalendarFragment smartCalendarFragment=
               ( SmartCalendarFragment) mFragmentManager.findFragmentByTag(mSmartCalendarView.mTags.get(fragmentPosition));
       if (smartCalendarFragment!=null){
           recyclerView=smartCalendarFragment.getCalendarRecyclerView();
       }
       return recyclerView;
   }



     SmartCalendarFragment findFragmentByPos(int position){
       return ( SmartCalendarFragment) mFragmentManager.findFragmentByTag(mSmartCalendarView.mTags.get(position));
   }
}
