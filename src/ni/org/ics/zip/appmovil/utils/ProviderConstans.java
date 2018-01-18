package ni.org.ics.zip.appmovil.utils;

/**
 * Created by ics on 23/11/2017.
 */

public class ProviderConstans {

    public static final String PROVIDER_TABLE = "provider";
    public static final String ID_COL = "id";
    public static final String NAME_COL = "name";
    public static final String CENTER_COL = "us";
    public static final String SPECIALITY_COL = "speciality";
 // "drop table if exists " + PROVIDER_TABLE +";" +
    public static final String CREATE_PROVIDER_TABLE = "create table if not exists "
            + PROVIDER_TABLE + " ("
            + ID_COL + " Integer PRIMARY KEY not null"
            + "," + NAME_COL + " text   not null "
            + "," + CENTER_COL + " text   not null "
            + "," + SPECIALITY_COL + " text   not null "
            + "," + MainDBConstants.recordUser + " text  "
            + "," + MainDBConstants.DEVICE_ID + " text  "
            + "," + MainDBConstants.ID_INSTANCIA + " text   "
            + "," + MainDBConstants.END + " text  "
            + "," + MainDBConstants.STATUS + " text   "
            + "," + MainDBConstants.pasive + " text   "
            + "," + MainDBConstants.SIM_SERIAL + " text    "
            + "," + MainDBConstants.FILE_PATH + " text   "
            + "," + MainDBConstants.PHONE_NUMBER + " text    "
            + "," + MainDBConstants.REGISTER_USER + " text    "
            + "," + MainDBConstants.START + " text   "
            + "," + MainDBConstants.TODAY + " date    "
            +");";
}
