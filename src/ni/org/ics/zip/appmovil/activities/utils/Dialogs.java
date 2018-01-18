package ni.org.ics.zip.appmovil.activities.utils;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.util.Log;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ni.org.ics.zip.appmovil.MyZipApplication;
import ni.org.ics.zip.appmovil.database.ZipAdapter;
import ni.org.ics.zip.appmovil.domain.Parametro;

/**
 * Created by A.L. on 28/11/2017.
 */

public class Dialogs {

    private static SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    private static SimpleDateFormat stf = new SimpleDateFormat("hh:mma");
    private static  EditText mEditText;
    protected static final String TAG = Dialogs.class.getSimpleName();
    private static Calendar mCalendar;

    public static  void ShowDatePickerDialog(Context context,EditText mText){
        mEditText = mText;
        Date mfechaval = new Date();
        Calendar calendar = Calendar.getInstance();
        try {
            mfechaval = sdf.parse(mText.getText().toString());
        } catch (Exception e) {

        }
        calendar.setTime(mfechaval);
        ni.org.ics.zip.appmovil.activities.utils.RangeDatePickerDialog datePickerDialog =
                new ni.org.ics.zip.appmovil.activities.utils.RangeDatePickerDialog(context
                        , new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        mEditText.setText(sdf.format(newDate.getTime()));
                    }

                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public  static void  ShowTimePickerDialog(Context context,EditText mText){
        mEditText = mText ;
        Calendar calendar = Calendar.getInstance();
        mCalendar = Calendar.getInstance();
        ni.org.ics.zip.appmovil.activities.utils.RangeTimePickerDialog  timePickerDialog = new ni.org.ics.zip.appmovil.activities.utils.RangeTimePickerDialog( context, new TimePickerDialog.OnTimeSetListener(){

            public void onTimeSet(android.widget.TimePicker view, int hour, int minute) {

                Calendar newDate = Calendar.getInstance();
                newDate.set(newDate.get(Calendar.YEAR), newDate.get(Calendar.MONTH), newDate.get(Calendar.DAY_OF_MONTH)
                        , hour,minute);
                mEditText.setText(stf.format(newDate.getTime()));
            }

        }, mCalendar.get(Calendar.HOUR), 0 , false);

        String mPass = ((MyZipApplication) ((Activity)context).getApplication()).getPassApp();
        ZipAdapter zipA = new ZipAdapter(context,mPass,false,false);
        Parametro parametro = null;
        try{
            zipA.open();
            parametro = zipA.getParametro("name  = 'agenda_HoraInicioAtencion'","name");
        }
        finally {
            zipA.close();
        }
        Date fecha = new Date();

        if(parametro != null){

            try {
                fecha = stf.parse(parametro.getValue());
                calendar.setTime(fecha);
                timePickerDialog.setMin(calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE));
            }
            catch (Exception ex){
                Log.e(TAG, ex.getLocalizedMessage(), ex);

            }
        }
        else{
            timePickerDialog.setMin(8,0);
        }

        try{
            zipA.open();
            parametro = zipA.getParametro("name  = 'agenda_HoraFinAtencion'","name");
        }
        finally {
            zipA.close();
        }

        if(parametro != null){

            try {
                fecha = stf.parse(parametro.getValue());
                calendar.setTime(fecha);
                timePickerDialog.setMax(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            }
            catch (Exception ex){

            }
        }
        else{
            timePickerDialog.setMax(16,30);
        }


        timePickerDialog.show();
    }
}
