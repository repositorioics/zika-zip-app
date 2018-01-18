package ni.org.ics.zip.appmovil.helpers;

import android.content.ContentValues;
import android.database.Cursor;

import ni.org.ics.zip.appmovil.domain.ZpAgendaEstudio;
import ni.org.ics.zip.appmovil.utils.MainDBConstants;
import ni.org.ics.zip.appmovil.utils.Zp01DBConstants;

import java.util.Date;

/**
 * Created by A.L. on 9/11/2017.
 */

public class ZpAgendaEstudioHelper {


    /**********************************************************/
    /********** PARA OBJETO ZpAgendaStudio *********/
    /**********************************************************/

    public static ContentValues crearZpAgendaStudioValues(ZpAgendaEstudio zpAgenda){
        ContentValues cv = new ContentValues();
        cv.put(MainDBConstants.record_Id, zpAgenda.getRecordId());
        cv.put(MainDBConstants.id, zpAgenda.getId());
        if (zpAgenda.getAppointmentDateTime() != null) cv.put(MainDBConstants.appointmentDateTime, zpAgenda.getAppointmentDateTime().getTime());
        cv.put(MainDBConstants.provider, zpAgenda.getProvider());
        cv.put(MainDBConstants.pasive, String.valueOf(zpAgenda.getPasive()));
        cv.put(MainDBConstants.healtUnit, zpAgenda.getHealtUnit());
        cv.put(MainDBConstants.subjectType, zpAgenda.getSubjectType());
        cv.put(MainDBConstants.appointmentType, zpAgenda.getAppointmentType());
        cv.put(MainDBConstants.specialityType, zpAgenda.getSpecialityType());
        cv.put(MainDBConstants.cellNumAuth, zpAgenda.getCellNumAuth());
        cv.put(MainDBConstants.smsNumber, zpAgenda.getSMSNumber());
        cv.put(MainDBConstants.pacienteAsistio, zpAgenda.getAsistio());
        cv.put(MainDBConstants.obs, zpAgenda.getObs());
        cv.put(MainDBConstants.turno, zpAgenda.getTurno());
        if (zpAgenda.getAppointmentEndTime() != null) cv.put(MainDBConstants.appointmentEndTime, zpAgenda.getAppointmentEndTime().getTime());
        cv.put(MainDBConstants.recordUser, zpAgenda.getRecordUser());
        if (zpAgenda.getRecordDate() != null) cv.put(MainDBConstants.recordDate, zpAgenda.getRecordDate().getTime());
        cv.put(MainDBConstants.ID_INSTANCIA, zpAgenda.getIdInstancia());
        cv.put(MainDBConstants.FILE_PATH, zpAgenda.getInstancePath());
        cv.put(MainDBConstants.STATUS, zpAgenda.getEstado());
        cv.put(MainDBConstants.START, zpAgenda.getStart());
        cv.put(MainDBConstants.END, zpAgenda.getEnd());
        cv.put(MainDBConstants.DEVICE_ID, zpAgenda.getDeviceid());
        cv.put(MainDBConstants.SIM_SERIAL, zpAgenda.getSimserial());
        cv.put(MainDBConstants.PHONE_NUMBER, zpAgenda.getPhonenumber());
        if (zpAgenda.getToday() != null) cv.put(MainDBConstants.TODAY, zpAgenda.getToday().getTime());
        return cv;
    }

    public static ZpAgendaEstudio crearZpAgendaEstudio(Cursor cursorAgenda){
        ZpAgendaEstudio agendaStudio = new ZpAgendaEstudio();
        agendaStudio.setRecordId(cursorAgenda.getString(cursorAgenda.getColumnIndex(MainDBConstants.record_Id)));
        agendaStudio.setId(cursorAgenda.getInt(cursorAgenda.getColumnIndex(MainDBConstants.id)));
        if(cursorAgenda.getLong(cursorAgenda.getColumnIndex(MainDBConstants.appointmentDateTime))>0) agendaStudio.setAppointmentDateTime(new Date(cursorAgenda.getLong(cursorAgenda.getColumnIndex(MainDBConstants.appointmentDateTime))));
        agendaStudio.setProvider(cursorAgenda.getString(cursorAgenda.getColumnIndex(MainDBConstants.provider)));
        agendaStudio.setHealtUnit(cursorAgenda.getString(cursorAgenda.getColumnIndex(MainDBConstants.healtUnit)));
        //agendaStudio.setRecordId(cursorAgenda.getString(cursorAgenda.getColumnIndex(MainDBConstants.record_Id)));
        agendaStudio.setSubjectType(cursorAgenda.getString(cursorAgenda.getColumnIndex(MainDBConstants.subjectType))); //permite 0
        agendaStudio.setAppointmentType(cursorAgenda.getString(cursorAgenda.getColumnIndex(MainDBConstants.appointmentType)));
        agendaStudio.setSpecialityType(cursorAgenda.getString(cursorAgenda.getColumnIndex(MainDBConstants.specialityType)));
        agendaStudio.setCellNumAuth(cursorAgenda.getString(cursorAgenda.getColumnIndex(MainDBConstants.cellNumAuth)));
        agendaStudio.setSMSNumber(cursorAgenda.getString(cursorAgenda.getColumnIndex(MainDBConstants.smsNumber)));
        agendaStudio.setAsistio(cursorAgenda.getString(cursorAgenda.getColumnIndex(MainDBConstants.pacienteAsistio)));
        agendaStudio.setObs(cursorAgenda.getString(cursorAgenda.getColumnIndex(MainDBConstants.obs)));
        agendaStudio.setTurno(cursorAgenda.getString(cursorAgenda.getColumnIndex(MainDBConstants.turno)));
        if(cursorAgenda.getLong(cursorAgenda.getColumnIndex(MainDBConstants.appointmentEndTime))>0) agendaStudio.setAppointmentEndTime(new Date(cursorAgenda.getLong(cursorAgenda.getColumnIndex(MainDBConstants.appointmentEndTime))));
        if(cursorAgenda.getLong(cursorAgenda.getColumnIndex(MainDBConstants.recordDate))>0) agendaStudio.setRecordDate(new Date(cursorAgenda.getLong(cursorAgenda.getColumnIndex(MainDBConstants.recordDate))));
        agendaStudio.setRecordUser(cursorAgenda.getString(cursorAgenda.getColumnIndex(MainDBConstants.recordUser)));
       // agendaStudio.setPasive(cursorAgenda.getString(cursorAgenda.getColumnIndex(MainDBConstants.pasive)).charAt(0));
        agendaStudio.setIdInstancia(cursorAgenda.getInt(cursorAgenda.getColumnIndex(MainDBConstants.ID_INSTANCIA)));
        agendaStudio.setInstancePath(cursorAgenda.getString(cursorAgenda.getColumnIndex(MainDBConstants.FILE_PATH)));
        agendaStudio.setEstado(cursorAgenda.getString(cursorAgenda.getColumnIndex(MainDBConstants.STATUS)));
        agendaStudio.setStart(cursorAgenda.getString(cursorAgenda.getColumnIndex(MainDBConstants.START)));
        agendaStudio.setEnd(cursorAgenda.getString(cursorAgenda.getColumnIndex(MainDBConstants.END)));
        agendaStudio.setSimserial(cursorAgenda.getString(cursorAgenda.getColumnIndex(MainDBConstants.SIM_SERIAL)));
        agendaStudio.setPhonenumber(cursorAgenda.getString(cursorAgenda.getColumnIndex(MainDBConstants.PHONE_NUMBER)));
        agendaStudio.setDeviceid(cursorAgenda.getString(cursorAgenda.getColumnIndex(MainDBConstants.DEVICE_ID)));
        if(cursorAgenda.getLong(cursorAgenda.getColumnIndex(MainDBConstants.TODAY))>0) agendaStudio.setToday(new Date(cursorAgenda.getLong(cursorAgenda.getColumnIndex(MainDBConstants.TODAY))));
        return agendaStudio;
    }

}
