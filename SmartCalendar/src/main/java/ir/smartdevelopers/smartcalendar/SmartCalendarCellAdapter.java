package ir.smartdevelopers.smartcalendar;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public abstract class SmartCalendarCellAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {


    private  List<? extends SmartCalendarCellModel> mCellModels=new ArrayList<>();
    private SmartCalendarCellModel mSelectedDate;
    private int mSelectedPosition=0;
    private SmartCalendarView.OnDateSelectListener mOnDateSelectListener;



    public SmartCalendarCellAdapter() {

    }


    @Override
    public final int getItemCount() {
        return mCellModels.size();
    }


    public void notifyCellHasNoEvent(int pos) {
        mCellModels.get(pos).setHasEvent(false);
        notifyItemChanged(pos);
    }

    public void notifyCellHasEvent(int pos) {
        mCellModels.get(pos).setHasEvent(true);
        notifyItemChanged(pos);
    }




    public final SmartCalendarCellModel getSelectedDate(){
        return mSelectedDate;
    }

    public final void setSelectedDate(SmartCalendarCellModel selectedDate) {
        setSelectedDate(selectedDate,true);
    }
    public final void setSelectedDate(int position) {
        SmartCalendarCellModel selectedDate=mCellModels.get(position);
        setSelectedDate(selectedDate,true);
    }
    private  void setSelectedDate(SmartCalendarCellModel selectedDate,boolean notify) {
        if (mSelectedDate!=null){
            mSelectedDate.setSelected(false);
        }
        mSelectedDate = selectedDate;
        int pos=mCellModels.indexOf(mSelectedDate);
        mSelectedDate.setSelected(true);
        mSelectedPosition=pos;
        if (notify) {
            onDateSelected(mSelectedDate, pos);
        }
    }

     final void setCellModels(List<SmartCalendarCellModel> cellModels) {
        mCellModels = cellModels;
        for (SmartCalendarCellModel model:cellModels){
            if (model.isSelected()){
                setSelectedDate(model,false);
                break;
            }
        }
//        notifyItemRangeRemoved(0,cellModels.size()-1);
//        notifyItemRangeInserted(0,cellModels.size()-1);
        notifyDataSetChanged();
    }

    public List<? extends SmartCalendarCellModel> getCellModels() {
        return mCellModels;
    }

    protected  void onDateSelected(SmartCalendarCellModel selectedDate,int position){
        if (mOnDateSelectListener!=null){
            mOnDateSelectListener.onDateSelected(selectedDate);
        }

    }
    public final int getSelectedPosition(){
       return mSelectedPosition;
    }

    final void setOnDateSelectListener(SmartCalendarView.OnDateSelectListener onDateSelectListener) {
        mOnDateSelectListener = onDateSelectListener;
    }
}
