package ir.smartdevelopers.smartcalendar;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;

public interface SmartCalendarCellAdapterFactory extends Serializable {
     static final long serialVersionUID=85L;
     SmartCalendarCellAdapter<? extends RecyclerView.ViewHolder> getInstance(Context context);
}
