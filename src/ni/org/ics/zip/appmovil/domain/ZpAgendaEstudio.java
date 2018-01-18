package ni.org.ics.zip.appmovil.domain;

import java.util.Date;
/**
 * Created by AL on 7/11/2017.
 */

public class ZpAgendaEstudio extends BaseMetaData {
    private static final long serialVersionUID = 1L;
    private int id;
    private Date appointmentDateTime;
    private String provider;
    private String healtUnit;
    private String recordId;

    private String subjectType; // Tipo de agenda - Mujer o nino
    private String appointmentType; // Tipo de cita  - General o Especialidad o Ultra Sonido
    private String specialityType; // si es especialidad , definir que tipo
    private String cellNumAuth; // El paciente autoriza el uso de su telefono para envio de SMS

    private String smsNumber;
    private String asistio;
    private String obs;

    private String turno;
    private Date appointmentEndTime;



    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public Date getAppointmentDateTime() {
        return appointmentDateTime;
    }
    public void setAppointmentDateTime(Date appointmentDateTime) {
        this.appointmentDateTime = appointmentDateTime;
    }

    public String getProvider() {
        return provider;
    }
    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getHealtUnit() {
        return healtUnit;
    }

    public void setHealtUnit(String healtUnit) {
        this.healtUnit = healtUnit;
    }

    public String getRecordId() {
        return recordId;
    }
    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getSubjectType() {
        return subjectType;
    }
    public void setSubjectType(String subjectType) {
        this.subjectType = subjectType;
    }


    public String getAppointmentType() {
        return appointmentType;
    }
    public void setAppointmentType(String appointmentType) {
        this.appointmentType = appointmentType;
    }

    public String getSpecialityType() {
        return specialityType;
    }
    public void setSpecialityType(String specialityType) {
        this.specialityType = specialityType;
    }

    public String getSMSNumber() {
        return this.smsNumber;
    }
    public void setSMSNumber(String number) {
        this.smsNumber= number;
    }


    public String getCellNumAuth() {
        return cellNumAuth;
    }
    public void setCellNumAuth(String cellNumAuth) {
        this.cellNumAuth = cellNumAuth;
    }

    public String getAsistio() {
        return asistio;
    }
    public void setAsistio(String st) {
        this.asistio = st;
    }


    public String getObs() {
        return obs;
    }
    public void setObs(String obs) {
        this.obs = obs;
    }

    public Date getAppointmentEndTime() {
        return appointmentEndTime;
    }
    public void setAppointmentEndTime(Date appointmentEndTime) {
        this.appointmentEndTime = appointmentEndTime;
    }
    public String getTurno() {
        return turno;
    }
    public void setTurno(String turno) {
        this.turno = turno;
    }

    public ZpAgendaEstudio() {
        super();
    }

    @Override
    public boolean equals(Object other) {

        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof ZpAgendaEstudio))
            return false;

        ZpAgendaEstudio castOther = (ZpAgendaEstudio) other;

        return (this.id == castOther.id);
    }

}
