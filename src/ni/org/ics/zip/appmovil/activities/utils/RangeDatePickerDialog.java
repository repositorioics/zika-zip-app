package ni.org.ics.zip.appmovil.activities.utils;


import android.app.DatePickerDialog;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import ni.org.ics.zip.appmovil.R;

/**
 * Created by A.L. on 27/11/2017.
 */

public class RangeDatePickerDialog extends DatePickerDialog {
    private Calendar calendar = Calendar.getInstance();
    private DateFormat dateFormat;
    protected DatePicker mDatePickerInput;
    protected static final String TAG = RangeDatePickerDialog.class.getSimpleName();
    public RangeDatePickerDialog(Context context, OnDateSetListener callBack, int year, int month, int day) {
        super(context, callBack, year, month, day);

        dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT);

        try {
            calendar.setTime(new Date());
            Class<?> superclass = getClass().getSuperclass();
            Field mDatePickerField = superclass.getDeclaredField("mDatePicker");
            mDatePickerField.setAccessible(true);
          //  DatePicker mDatePicker = (DatePicker) mDatePickerField.get(this);

            View v = LayoutInflater.from(context).inflate(R.layout.fragment_page_date, null);
            mDatePickerInput = (DatePicker) v.findViewById(R.id.datePickerInput);
          //  mDatePickerInput.setMinDate(calendar.getTimeInMillis());
            mDatePickerField.set(this,mDatePickerInput);

            this.setView(v);
        } catch (NoSuchFieldException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }




}
