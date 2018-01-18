package ni.org.ics.zip.appmovil.helpers;

import android.content.ContentValues;
import android.database.Cursor;

import ni.org.ics.zip.appmovil.domain.ZpSpecialities;
import ni.org.ics.zip.appmovil.utils.ZpSpecialityConstans;

/**
 * Created by A.L. on 16/11/2017.
 */

public class ZpSpecialityHelper {

    /**********************************************************/
    /********** PARA OBJETO ZpSpeciality *********/
    /**********************************************************/

    public static ContentValues crearZpSpecialityValues(ZpSpecialities zpDatos){
        ContentValues cv = new ContentValues();
        cv.put(ZpSpecialityConstans.especialidad, zpDatos.getSpeciality());

        return cv;
    }

    public static ZpSpecialities crearZpSpeciality(Cursor cursor){
        ZpSpecialities obj = new ZpSpecialities();
        obj.setSpeciality(cursor.getString(cursor.getColumnIndex(ZpSpecialityConstans.especialidad)));
        return obj;
    }
}
