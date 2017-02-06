package ni.org.ics.zip.appmovil.domain;

import java.util.Date;

/**
 * Created by FIRSTICT on 10/6/2016.
 * V1.0
 */
public class ZpControlReporteUSRecepcion extends BaseMetaData{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String lugarLlegada;
	private String codigo;
	private String evento;
	private Date fechaDato;
	private Date fechaHoraLLegada;	
	private String persona;
	
	public String getLugarLlegada() {
		return lugarLlegada;
	}
	public void setLugarLlegada(String lugarLlegada) {
		this.lugarLlegada = lugarLlegada;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public Date getFechaHoraLLegada() {
		return fechaHoraLLegada;
	}
	public void setFechaHoraLLegada(Date fechaHoraLLegada) {
		this.fechaHoraLLegada = fechaHoraLLegada;
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
