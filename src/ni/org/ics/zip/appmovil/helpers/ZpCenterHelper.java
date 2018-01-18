package ni.org.ics.zip.appmovil.helpers;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.Date;

import ni.org.ics.zip.appmovil.domain.ZpCenter;
import ni.org.ics.zip.appmovil.utils.ZpCenterConstans;

/**
 * Created by A.L. on 16/11/2017.
 */

public class ZpCenterHelper {


    /**********************************************************/
    /********** PARA OBJETO ZpCenter *********/
    /**********************************************************/

    public static ContentValues crearZpCenterValues(ZpCenter zpDatos){
        ContentValues cv = new ContentValues();
        cv.put(ZpCenterConstans.cs, zpDatos.getCs());

        return cv;
    }

    public static ZpCenter crearZpCenter(Cursor cursor){
        ZpCenter agendaStudio = new ZpCenter();
        agendaStudio.setCs(cursor.getString(cursor.getColumnIndex(ZpCenterConstans.cs)));
         return agendaStudio;
    }
}
