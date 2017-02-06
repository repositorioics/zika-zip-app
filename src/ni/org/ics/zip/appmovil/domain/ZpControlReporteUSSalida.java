package ni.org.ics.zip.appmovil.domain;

import java.util.Date;

/**
 * Created by FIRSTICT on 10/6/2016.
 * V1.0
 */
public class ZpControlReporteUSSalida extends BaseMetaData{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String lugarSalida;
	private String codigo;
	private String evento;
	private Date fechaDato;
	private Date fechaHoraSalida;	
	private String persona;
	
	public String getLugarSalida() {
		return lugarSalida;
	}
	public void setLugarSalida(String lugarSalida) {
		this.lugarSalida = lugarSalida;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public Date getFechaHoraSalida() {
		return fechaHoraSalida;
	}
	public void setFechaHoraSalida(Date fechaHoraSalida) {
		this.fechaHoraSalida = fechaHoraSalida;
	}
	public String getPersona() {
		return persona;
	}
	public void setPersona(String persona) {
		this.persona = persona;
	}
	public String getEvento() {
		return evento;
	}
	public void setEvento(String evento) {
		this.evento = evento;
	}
	public Date getFechaDato() {
		return fechaDato;
	}
	public void setFechaDato(Date fechaDato) {
		this.fechaDato = fechaDato;
	}
	
	
}
