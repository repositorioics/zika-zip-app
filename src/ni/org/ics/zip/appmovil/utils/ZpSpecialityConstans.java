package ni.org.ics.zip.appmovil.utils;

/**
 * Created by A.L. on 16/11/2017.
 */

public class ZpSpecialityConstans {

    public static final String SPECIALTY_TABLE = "specialities";

    public static final String especialidad = "especialidad";

    public static final String CREATE_SPECIALTY_TABLE = "create table if not exists "
            + SPECIALTY_TABLE + " ("
            + especialidad + " text PRIMARY KEY not null );";
}
