package ni.org.ics.zip.appmovil.utils;

/**
 * Created by A.L. on 23/11/2017.
 */

public class ParametroConstans {

    public static final String PARAMETRO_TABLE = "parametro";
    public static final String ID_COL = "id";
    public static final String NAME_COL = "name";
    public static final String VALUE_COL = "value";

    public static final String CREATE_PARAMETROS_TABLE = "create table if not exists "
            + PARAMETRO_TABLE + " ("
            + ID_COL + " Integer PRIMARY KEY not null"
            + "," + NAME_COL + " text   not null "
            + "," + VALUE_COL + " text   not null "
            +");";
}
