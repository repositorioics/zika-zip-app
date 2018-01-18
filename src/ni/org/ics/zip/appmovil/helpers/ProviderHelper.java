package ni.org.ics.zip.appmovil.helpers;

import android.content.ContentValues;
import android.database.Cursor;

import ni.org.ics.zip.appmovil.domain.Provider;
import ni.org.ics.zip.appmovil.utils.MainDBConstants;
import ni.org.ics.zip.appmovil.utils.ProviderConstans;

/**
 * Created by ics on 23/11/2017.
 */

public class ProviderHelper {


    public static ContentValues crearProviderValues(Provider zpDatos){
        ContentValues cv = new ContentValues();
        cv.put(ProviderConstans.ID_COL, zpDatos.getId());
        cv.put(ProviderConstans.NAME_COL, zpDatos.getName());
        cv.put(ProviderConstans.SPECIALITY_COL, zpDatos.getSpeciality());
        cv.put(ProviderConstans.CENTER_COL, zpDatos.getUs());
        cv.put(MainDBConstants.recordUser, zpDatos.getRecordUser());
     //   if (zpDatos.getRecordDate() != null) cv.put(MainDBConstants.recordDate, zpDatos.getRecordDate().getTime());
        cv.put(MainDBConstants.ID_INSTANCIA, zpDatos.getIdInstancia());
        cv.put(MainDBConstants.FILE_PATH, zpDatos.getInstancePath());
        cv.put(MainDBConstants.STATUS, zpDatos.getEstado());
        cv.put(MainDBConstants.START, zpDatos.getStart());
        cv.put(MainDBConstants.END, zpDatos.getEnd());
        cv.put(MainDBConstants.DEVICE_ID, zpDatos.getDeviceid());
        cv.put(MainDBConstants.SIM_SERIAL, zpDatos.getSimserial());
        cv.put(MainDBConstants.PHONE_NUMBER, zpDatos.getPhonenumber());
        if (zpDatos.getToday() != null) cv.put(MainDBConstants.TODAY, zpDatos.getToday().getTime());

        return cv;
    }

    public static Provider crearProvider(Cursor cursor){
        Provider param = new Provider();
        param.setId(cursor.getInt(cursor.getColumnIndex(ProviderConstans.ID_COL)));
        param.setName(cursor.getString(cursor.getColumnIndex(ProviderConstans.NAME_COL)));
        param.setSpeciality(cursor.getString(cursor.getColumnIndex(ProviderConstans.SPECIALITY_COL)));
        param.setUs(cursor.getString(cursor.getColumnIndex(ProviderConstans.CENTER_COL)));

        return param;
    }
}
