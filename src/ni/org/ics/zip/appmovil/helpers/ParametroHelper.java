package ni.org.ics.zip.appmovil.helpers;

import android.content.ContentValues;
import android.database.Cursor;

import ni.org.ics.zip.appmovil.domain.Parametro;
import ni.org.ics.zip.appmovil.utils.ParametroConstans;

/**
 * Created by A.L. on 23/11/2017.
 */

public class ParametroHelper {

    public static ContentValues crearParametroValues(Parametro zpDatos){
        ContentValues cv = new ContentValues();
        cv.put(ParametroConstans.ID_COL, zpDatos.getId());
        cv.put(ParametroConstans.NAME_COL, zpDatos.getName());
        cv.put(ParametroConstans.VALUE_COL, zpDatos.getValue());

        return cv;
    }

    public static Parametro crearParametro(Cursor cursor){
        Parametro param = new Parametro();
        param.setId(cursor.getInt(cursor.getColumnIndex(ParametroConstans.ID_COL)));
        param.setName(cursor.getString(cursor.getColumnIndex(ParametroConstans.NAME_COL)));
        param.setValue(cursor.getString(cursor.getColumnIndex(ParametroConstans.VALUE_COL)));

        return param;
    }
}
