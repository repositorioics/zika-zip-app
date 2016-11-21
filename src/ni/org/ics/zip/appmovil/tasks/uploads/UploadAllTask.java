package ni.org.ics.zip.appmovil.tasks.uploads;

import java.util.ArrayList;
import java.util.List;

import ni.org.ics.zip.appmovil.database.ZipAdapter;
import ni.org.ics.zip.appmovil.domain.*;
import ni.org.ics.zip.appmovil.listeners.UploadListener;
import ni.org.ics.zip.appmovil.tasks.UploadTask;
import ni.org.ics.zip.appmovil.utils.Constants;
import ni.org.ics.zip.appmovil.utils.MainDBConstants;

import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.content.Context;
import android.util.Log;

public class UploadAllTask extends UploadTask {
	
	private final Context mContext;

	public UploadAllTask(Context context) {
		mContext = context;
	}

	protected static final String TAG = UploadAllTask.class.getSimpleName();
	private ZipAdapter zipA = null;
	private List<ZpPreScreening> mPreTamizajes = new ArrayList<ZpPreScreening>();
	private List<Zp00Screening> mTamizajes = new ArrayList<Zp00Screening>();
    private List<Zp01StudyEntrySectionAtoD> mIngresosAD = new ArrayList<Zp01StudyEntrySectionAtoD>();
    private List<Zp01StudyEntrySectionE> mIngresosE = new ArrayList<Zp01StudyEntrySectionE>();
    private List<Zp01StudyEntrySectionFtoK> mIngresosFK = new ArrayList<Zp01StudyEntrySectionFtoK>();
    private List<Zp02BiospecimenCollection> mCollections = new ArrayList<Zp02BiospecimenCollection>();
    private List<Zp03MonthlyVisit> mMonthlyVisits = new ArrayList<Zp03MonthlyVisit>();
    private List<Zp04TrimesterVisitSectionAtoD> mTrimesterVisitAD = new ArrayList<Zp04TrimesterVisitSectionAtoD>();
    private List<Zp04TrimesterVisitSectionE> mTrimesterVisitE = new ArrayList<Zp04TrimesterVisitSectionE>();
    private List<Zp04TrimesterVisitSectionFtoH> mTrimesterVisitFH = new ArrayList<Zp04TrimesterVisitSectionFtoH>();
    private List<Zp05UltrasoundExam> mUltrasounds = new ArrayList<Zp05UltrasoundExam>();
    private List<Zp06DeliveryAnd6weekVisit> mDeliverys = new ArrayList<Zp06DeliveryAnd6weekVisit>();
    private List<Zp08StudyExit> mExits = new ArrayList<Zp08StudyExit>();
    private List<ZpEstadoEmbarazada> mStatus = new ArrayList<ZpEstadoEmbarazada>();
	private String url = null;
	private String username = null;
	private String password = null;
	private String error = null;
	protected UploadListener mStateListener;

	@Override
	protected String doInBackground(String... values) {
		url = values[0];
		username = values[1];
		password = values[2];

		try {
			publishProgress("Obteniendo registros de la base de datos", "1", "2");
			zipA = new ZipAdapter(mContext, password, false);
			zipA.open();
			String filtro = MainDBConstants.STATUS + "='" + Constants.STATUS_NOT_SUBMITTED + "'";
			mPreTamizajes = zipA.getZpPreScreenings(filtro, MainDBConstants.recId);
			mTamizajes = zipA.getZp00Screenings(filtro, MainDBConstants.recordId);
			mIngresosAD = zipA.getZp01StudyEntrySectionAtoDs(filtro, MainDBConstants.recordId);
			mIngresosE = zipA.getZp01StudyEntrySectionEs(filtro, MainDBConstants.recordId);
			mIngresosFK = zipA.getZp01StudyEntrySectionFtoKs(filtro, MainDBConstants.recordId);
			mCollections = zipA.getZp02BiospecimenCollections(filtro, MainDBConstants.recordId);
			mMonthlyVisits = zipA.getZp03MonthlyVisits(filtro, MainDBConstants.recordId);
			mTrimesterVisitAD = zipA.getZp04TrimesterVisitSectionAtoDs(filtro, MainDBConstants.recordId);
			mTrimesterVisitE = zipA.getZp04TrimesterVisitSectionEs(filtro, MainDBConstants.recordId);
			mTrimesterVisitFH = zipA.getZp04TrimesterVisitSectionFtoHs(filtro, MainDBConstants.recordId);
			mUltrasounds = zipA.getZp05UltrasoundExams(filtro, MainDBConstants.recordId);
			mDeliverys = zipA.getZp06DeliveryAnd6weekVisits(filtro, MainDBConstants.recordId);
			mExits = zipA.getZp08StudyExits(filtro, MainDBConstants.recordId);
			mStatus = zipA.getZpEstadoEmbarazadas(filtro, MainDBConstants.recordId);
			zipA.close();
			publishProgress("Datos completos!", "2", "2");
			error = cargarPreTamizajes(url, username, password);
			if (!error.matches("Datos recibidos!")){
				return error;
			}
			error = cargarTamizajes(url, username, password);
			if (!error.matches("Datos recibidos!")){
				return error;
			}
            error = uploadEntrysAD(url, username, password);
            if (!error.matches("Datos recibidos!")){
                return error;
            }
            error = uploadEntrysZp01E(url, username, password);
            if (!error.matches("Datos recibidos!")){
                return error;
            }
            error = uploadEntrysFK(url, username, password);
            if (!error.matches("Datos recibidos!")){
                return error;
            }
            error = upLoadBioCollections(url, username, password);
            if (!error.matches("Datos recibidos!")){
                return error;
            }
            error = uploadMonthlyVisits(url, username, password);
            if (!error.matches("Datos recibidos!")){
                return error;
            }
            error = uploadTrimesterVisitAD(url, username, password);
            if (!error.matches("Datos recibidos!")){
                return error;
            }
            error = uploadTrimesterVisitE(url, username, password);
            if (!error.matches("Datos recibidos!")){
                return error;
            }
            error = uploadTrimesterVisitFH(url, username, password);
            if (!error.matches("Datos recibidos!")){
                return error;
            }
            error = uploadUltrasoundExams(url, username, password);
            if (!error.matches("Datos recibidos!")){
                return error;
            }
            error = uploadDeliverys(url, username, password);
            if (!error.matches("Datos recibidos!")){
                return error;
            }
            error = uploadExits(url, username, password);
            if (!error.matches("Datos recibidos!")){
                return error;
            }
            error = uploadStatusPreg(url, username, password);
            if (!error.matches("Datos recibidos!")){
                return error;
            }
            actualizarBaseDatos(Constants.STATUS_SUBMITTED);

		} catch (Exception e1) {
			e1.printStackTrace();
			return e1.getLocalizedMessage();
		}
		return error;
	}
	
	private void actualizarBaseDatos(String estado) {
		int c;
		ZipAdapter zipA = new ZipAdapter(mContext, password, false);
		zipA.open();
		c = mPreTamizajes.size();
		if(c>0){
			for (ZpPreScreening pretamizaje : mPreTamizajes) {
				pretamizaje.setEstado(estado);
				zipA.editarZpPreScreening(pretamizaje);
				publishProgress("Actualizando pre-tamizajes base de datos local", Integer.valueOf(mPreTamizajes.indexOf(pretamizaje)).toString(), Integer
						.valueOf(c).toString());
			}
		}
		c = mTamizajes.size();
		if(c>0){
			for (Zp00Screening tamizaje : mTamizajes) {
				tamizaje.setEstado(estado);
				zipA.editarZp00Screening(tamizaje);
				publishProgress("Actualizando tamizajes base de datos local", Integer.valueOf(mTamizajes.indexOf(tamizaje)).toString(), Integer
						.valueOf(c).toString());
			}
		}
		c = mIngresosAD.size();
		if(c>0){
	        for (Zp01StudyEntrySectionAtoD ingreso : mIngresosAD) {
	            ingreso.setEstado(estado);
	            zipA.editarZp01StudyEntrySectionAtoD(ingreso);
	            publishProgress("Actualizando datos de ingreso (A-D) base de datos local", Integer.valueOf(mIngresosAD.indexOf(ingreso)).toString(), Integer
	                    .valueOf(c).toString());
	        }
		}
		c = mIngresosE.size();
		if(c>0){
	        for (Zp01StudyEntrySectionE ingreso : mIngresosE) {
	            ingreso.setEstado(estado);
	            zipA.editarZp01StudyEntrySectionE(ingreso);
	            publishProgress("Actualizando datos de ingreso (E) base de datos local", Integer.valueOf(mIngresosE.indexOf(ingreso)).toString(), Integer
	                    .valueOf(c).toString());
	        }
		}
		c = mIngresosFK.size();
		if(c>0){
	        for (Zp01StudyEntrySectionFtoK ingreso : mIngresosFK) {
	            ingreso.setEstado(estado);
	            zipA.editarZp01StudyEntrySectionFtoK(ingreso);
	            publishProgress("Actualizando datos de ingreso (F-K) base de datos local", Integer.valueOf(mIngresosFK.indexOf(ingreso)).toString(), Integer
	                    .valueOf(c).toString());
	        }
		}
		c = mCollections.size();
		if(c>0){
	        for (Zp02BiospecimenCollection collection : mCollections) {
	            collection.setEstado(estado);
	            zipA.editarZp02BiospecimenCollection(collection);
	            publishProgress("Actualizando recoleccion de muestras base de datos local", Integer.valueOf(mCollections.indexOf(collection)).toString(), Integer
	                    .valueOf(c).toString());
	        }
		}
		c = mMonthlyVisits.size();
		if(c>0){
	        for (Zp03MonthlyVisit monthlyVisit : mMonthlyVisits) {
	            monthlyVisit.setEstado(estado);
	            zipA.editarZp03MonthlyVisit(monthlyVisit);
	            publishProgress("Actualizando visita mensual base de datos local", Integer.valueOf(mMonthlyVisits.indexOf(monthlyVisit)).toString(), Integer
	                    .valueOf(c).toString());
	        }
		}
		c = mTrimesterVisitAD.size();
		if(c>0){
	        for (Zp04TrimesterVisitSectionAtoD trimesterVisitAD : mTrimesterVisitAD) {
	            trimesterVisitAD.setEstado(estado);
	            zipA.editarZp04TrimesterVisitSectionAtoD(trimesterVisitAD);
	            publishProgress("Actualizando Visita Trimestral (A-D) base de datos local", Integer.valueOf(mTrimesterVisitAD.indexOf(trimesterVisitAD)).toString(), Integer
	                    .valueOf(c).toString());
	        }
		}
		c = mTrimesterVisitE.size();
		if(c>0){
		    for (Zp04TrimesterVisitSectionE trimesterVisitE : mTrimesterVisitE) {
		        trimesterVisitE.setEstado(estado);
		        zipA.editarZp04TrimesterVisitSectionE(trimesterVisitE);
		        publishProgress("Actualizando Visita Trimestral (E) base de datos local", Integer.valueOf(mTrimesterVisitE.indexOf(trimesterVisitE)).toString(), Integer
		                .valueOf(c).toString());
		    }
		}
		c = mTrimesterVisitFH.size();
		if(c>0){
	        for (Zp04TrimesterVisitSectionFtoH trimesterVisitFH : mTrimesterVisitFH) {
	            trimesterVisitFH.setEstado(estado);
	            zipA.editarZp04TrimesterVisitSectionFtoH(trimesterVisitFH);
	            publishProgress("Actualizando Visita Trimestral (F-H) base de datos local", Integer.valueOf(mTrimesterVisitFH.indexOf(trimesterVisitFH)).toString(), Integer
	                    .valueOf(c).toString());
	        }
		}
		c = mUltrasounds.size();
		if(c>0){
	        for (Zp05UltrasoundExam ultrasoundExam : mUltrasounds) {
	            ultrasoundExam.setEstado(estado);
	            zipA.editarZp05UltrasoundExam(ultrasoundExam);
	            publishProgress("Actualizando ultrasonidos base de datos local", Integer.valueOf(mUltrasounds.indexOf(ultrasoundExam)).toString(), Integer
	                    .valueOf(c).toString());
	        }
		}
        c = mDeliverys.size();
        if(c>0){
	        for (Zp06DeliveryAnd6weekVisit deliveryAnd6weekVisit : mDeliverys) {
	            deliveryAnd6weekVisit.setEstado(estado);
	            zipA.editarZp06DeliveryAnd6weekVisit(deliveryAnd6weekVisit);
	            publishProgress("Actualizando partos base de datos local", Integer.valueOf(mDeliverys.indexOf(deliveryAnd6weekVisit)).toString(), Integer
	                    .valueOf(c).toString());
	        }
        }
        c = mExits.size();
        if(c>0){
	        for (Zp08StudyExit studyExit : mExits) {
	            studyExit.setEstado(estado);
	            zipA.editarZp08StudyExit(studyExit);
	            publishProgress("Actualizando salidas del estudio base de datos local", Integer.valueOf(mExits.indexOf(studyExit)).toString(), Integer
	                    .valueOf(c).toString());
	        }
        }
        c = mStatus.size();
        if(c>0){
	        for (ZpEstadoEmbarazada estadoEmbarazada : mStatus) {
	            estadoEmbarazada.setEstado(estado);
	            zipA.editarZpEstadoEmbarazada(estadoEmbarazada);
	            publishProgress("Actualizando estado de embarazadas base de datos local", Integer.valueOf(mStatus.indexOf(estadoEmbarazada)).toString(), Integer
	                    .valueOf(c).toString());
	        }
        }
		zipA.close();
	}

	
	/***************************************************/
	/********************* ZpPretamizajes ************************/
	/***************************************************/
    // url, username, password
    protected String cargarPreTamizajes(String url, String username, 
    		String password) throws Exception {
    	try {
    		if(mPreTamizajes.size()>0){
    			// La URL de la solicitud POST
    			publishProgress("Enviando pre-tamizajes!", "1", "14");
    			final String urlRequest = url + "/movil/zpPreScreening";
    			ZpPreScreening[] envio = mPreTamizajes.toArray(new ZpPreScreening[mPreTamizajes.size()]);
    			HttpHeaders requestHeaders = new HttpHeaders();
    			HttpAuthentication authHeader = new HttpBasicAuthentication(username, password);
    			requestHeaders.setContentType(MediaType.APPLICATION_JSON);
    			requestHeaders.setAuthorization(authHeader);
    			HttpEntity<ZpPreScreening[]> requestEntity = 
    					new HttpEntity<ZpPreScreening[]>(envio, requestHeaders);
    					RestTemplate restTemplate = new RestTemplate();
    					restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
    					restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
    					// Hace la solicitud a la red, pone la vivienda y espera un mensaje de respuesta del servidor
    					ResponseEntity<String> response = restTemplate.exchange(urlRequest, HttpMethod.POST, requestEntity,
    							String.class);
    					return response.getBody();
    		}
    		else{
    			return "Datos recibidos!";
    		}
    	} catch (Exception e) {
    		Log.e(TAG, e.getMessage(), e);
    		return e.getMessage();
    	}
    }

	/***************************************************/
	/********************* Zp00Tamizajes ************************/
	/***************************************************/
    // url, username, password
    protected String cargarTamizajes(String url, String username, 
    		String password) throws Exception {
    	try {
    		if(mTamizajes.size()>0){
    			publishProgress("Enviando tamizajes!", "2", "14");
    			// La URL de la solicitud POST
    			final String urlRequest = url + "/movil/zp00Screenings";
    			Zp00Screening[] envio = mTamizajes.toArray(new Zp00Screening[mTamizajes.size()]);
    			HttpHeaders requestHeaders = new HttpHeaders();
    			HttpAuthentication authHeader = new HttpBasicAuthentication(username, password);
    			requestHeaders.setContentType(MediaType.APPLICATION_JSON);
    			requestHeaders.setAuthorization(authHeader);
    			HttpEntity<Zp00Screening[]> requestEntity = 
    					new HttpEntity<Zp00Screening[]>(envio, requestHeaders);
    					RestTemplate restTemplate = new RestTemplate();
    					restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
    					restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
    					// Hace la solicitud a la red, pone la vivienda y espera un mensaje de respuesta del servidor
    					ResponseEntity<String> response = restTemplate.exchange(urlRequest, HttpMethod.POST, requestEntity,
    							String.class);
    					return response.getBody();
    		}
    		else{
    			return "Datos recibidos!";
    		}
    	} catch (Exception e) {
    		Log.e(TAG, e.getMessage(), e);
    		return e.getMessage();
    	}
    }

    /***************************************************/
    /*************** Zp01 AtoD ************************/
    /***************************************************/
    // url, username, password
    protected String uploadEntrysAD(String url, String username,
    		String password) throws Exception {
    	try {
    		if(mIngresosAD.size()>0){
    			publishProgress("Enviando ingresos (1)!", "3", "14");
    			// La URL de la solicitud POST
    			final String urlRequest = url + "/movil/zp01StudyEntrySectionAtoDs";
    			Zp01StudyEntrySectionAtoD[] envio = mIngresosAD.toArray(new Zp01StudyEntrySectionAtoD[mIngresosAD.size()]);
    			HttpHeaders requestHeaders = new HttpHeaders();
    			HttpAuthentication authHeader = new HttpBasicAuthentication(username, password);
    			requestHeaders.setContentType(MediaType.APPLICATION_JSON);
    			requestHeaders.setAuthorization(authHeader);
    			HttpEntity<Zp01StudyEntrySectionAtoD[]> requestEntity =
    					new HttpEntity<Zp01StudyEntrySectionAtoD[]>(envio, requestHeaders);
    					RestTemplate restTemplate = new RestTemplate();
    					restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
    					restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
    					// Hace la solicitud a la red, pone la vivienda y espera un mensaje de respuesta del servidor
    					ResponseEntity<String> response = restTemplate.exchange(urlRequest, HttpMethod.POST, requestEntity,
    							String.class);
    					return response.getBody();
    		}
    		else{
    			return "Datos recibidos!";
    		}
    	} catch (Exception e) {
    		Log.e(TAG, e.getMessage(), e);
    		return e.getMessage();
    	}
    }   

    /***************************************************/
    /*************** Zp01 E ************************/
    /***************************************************/
    // url, username, password
    protected String uploadEntrysZp01E(String url, String username,
    		String password) throws Exception {
    	try {
    		if(mIngresosE.size()>0){
    			publishProgress("Enviando ingresos (2)!", "4", "14");
    			// La URL de la solicitud POST
    			final String urlRequest = url + "/movil/zp01StudyEntrySectionEs";
    			Zp01StudyEntrySectionE[] envio = mIngresosE.toArray(new Zp01StudyEntrySectionE[mIngresosE.size()]);
    			HttpHeaders requestHeaders = new HttpHeaders();
    			HttpAuthentication authHeader = new HttpBasicAuthentication(username, password);
    			requestHeaders.setContentType(MediaType.APPLICATION_JSON);
    			requestHeaders.setAuthorization(authHeader);
    			HttpEntity<Zp01StudyEntrySectionE[]> requestEntity =
    					new HttpEntity<Zp01StudyEntrySectionE[]>(envio, requestHeaders);
    					RestTemplate restTemplate = new RestTemplate();
    					restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
    					restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
    					// Hace la solicitud a la red, pone la vivienda y espera un mensaje de respuesta del servidor
    					ResponseEntity<String> response = restTemplate.exchange(urlRequest, HttpMethod.POST, requestEntity,
    							String.class);
    					return response.getBody();
    		}
    		else{
    			return "Datos recibidos!";
    		}
    	} catch (Exception e) {
    		Log.e(TAG, e.getMessage(), e);
    		return e.getMessage();
    	}
    }
    
	/***************************************************/
	/*************** Zp01 FtoK ************************/
	/***************************************************/
	// url, username, password
	protected String uploadEntrysFK(String url, String username,
			String password) throws Exception {
		try {
			if(mIngresosFK.size()>0){
				publishProgress("Enviando ingresos (3)!", "5", "14");
				// La URL de la solicitud POST
				final String urlRequest = url + "/movil/zp01StudyEntrySectionFtoKs";
				Zp01StudyEntrySectionFtoK[] envio = mIngresosFK.toArray(new Zp01StudyEntrySectionFtoK[mIngresosFK.size()]);
				HttpHeaders requestHeaders = new HttpHeaders();
				HttpAuthentication authHeader = new HttpBasicAuthentication(username, password);
				requestHeaders.setContentType(MediaType.APPLICATION_JSON);
				requestHeaders.setAuthorization(authHeader);
				HttpEntity<Zp01StudyEntrySectionFtoK[]> requestEntity =
						new HttpEntity<Zp01StudyEntrySectionFtoK[]>(envio, requestHeaders);
						RestTemplate restTemplate = new RestTemplate();
						restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
						restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
						// Hace la solicitud a la red, pone la vivienda y espera un mensaje de respuesta del servidor
						ResponseEntity<String> response = restTemplate.exchange(urlRequest, HttpMethod.POST, requestEntity,
								String.class);
						return response.getBody();
			}
			else{
				return "Datos recibidos!";
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
			return e.getMessage();
		}
	}

	/***************************************************/
	/********************* Zp02 ************************/
	/***************************************************/
	// url, username, password
	protected String upLoadBioCollections(String url, String username,
			String password) throws Exception {
		try {
			if(mCollections.size()>0){
				publishProgress("Enviando muestras!", "6", "14");
				// La URL de la solicitud POST
				final String urlRequest = url + "/movil/zp02BiospecimenCollections";
				Zp02BiospecimenCollection[] envio = mCollections.toArray(new Zp02BiospecimenCollection[mCollections.size()]);
				HttpHeaders requestHeaders = new HttpHeaders();
				HttpAuthentication authHeader = new HttpBasicAuthentication(username, password);
				requestHeaders.setContentType(MediaType.APPLICATION_JSON);
				requestHeaders.setAuthorization(authHeader);
				HttpEntity<Zp02BiospecimenCollection[]> requestEntity =
						new HttpEntity<Zp02BiospecimenCollection[]>(envio, requestHeaders);
						RestTemplate restTemplate = new RestTemplate();
						restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
						restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
						// Hace la solicitud a la red, pone la vivienda y espera un mensaje de respuesta del servidor
						ResponseEntity<String> response = restTemplate.exchange(urlRequest, HttpMethod.POST, requestEntity,
								String.class);
						return response.getBody();
			}
			else{
				return "Datos recibidos!";
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
			return e.getMessage();
		}
	}
	
	/***************************************************/
	/********************* Zp03 ************************/
	/***************************************************/
	// url, username, password
	protected String uploadMonthlyVisits(String url, String username,
			String password) throws Exception {
		try {
			if(mMonthlyVisits.size()>0){
				publishProgress("Enviando visitas mensuales!", "7", "14");
				// La URL de la solicitud POST
				final String urlRequest = url + "/movil/zp03MonthlyVisits";
				Zp03MonthlyVisit[] envio = mMonthlyVisits.toArray(new Zp03MonthlyVisit[mMonthlyVisits.size()]);
				HttpHeaders requestHeaders = new HttpHeaders();
				HttpAuthentication authHeader = new HttpBasicAuthentication(username, password);
				requestHeaders.setContentType(MediaType.APPLICATION_JSON);
				requestHeaders.setAuthorization(authHeader);
				HttpEntity<Zp03MonthlyVisit[]> requestEntity =
						new HttpEntity<Zp03MonthlyVisit[]>(envio, requestHeaders);
						RestTemplate restTemplate = new RestTemplate();
						restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
						restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
						// Hace la solicitud a la red, pone la vivienda y espera un mensaje de respuesta del servidor
						ResponseEntity<String> response = restTemplate.exchange(urlRequest, HttpMethod.POST, requestEntity,
								String.class);
						return response.getBody();
			}
			else{
				return "Datos recibidos!";
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
			return e.getMessage();
		}
	}

	/***************************************************/
	/********************* Zp04 AtoD ************************/
	/***************************************************/
	// url, username, password
	protected String uploadTrimesterVisitAD(String url, String username,
			String password) throws Exception {
		try {
			if(mTrimesterVisitAD.size()>0){
				publishProgress("Enviando visitas trimestrales (1)!", "8", "14");
				// La URL de la solicitud POST
				final String urlRequest = url + "/movil/zp04TrimesterVisitSectionAtoDs";
				Zp04TrimesterVisitSectionAtoD[] envio = mTrimesterVisitAD.toArray(new Zp04TrimesterVisitSectionAtoD[mTrimesterVisitAD.size()]);
				HttpHeaders requestHeaders = new HttpHeaders();
				HttpAuthentication authHeader = new HttpBasicAuthentication(username, password);
				requestHeaders.setContentType(MediaType.APPLICATION_JSON);
				requestHeaders.setAuthorization(authHeader);
				HttpEntity<Zp04TrimesterVisitSectionAtoD[]> requestEntity =
						new HttpEntity<Zp04TrimesterVisitSectionAtoD[]>(envio, requestHeaders);
						RestTemplate restTemplate = new RestTemplate();
						restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
						restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
						// Hace la solicitud a la red, pone la vivienda y espera un mensaje de respuesta del servidor
						ResponseEntity<String> response = restTemplate.exchange(urlRequest, HttpMethod.POST, requestEntity,
								String.class);
						return response.getBody();
			}
			else{
				return "Datos recibidos!";
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
			return e.getMessage();
		}
	}	
    
	/***************************************************/
	/********************* Zp04 E ************************/
	/***************************************************/
	// url, username, password
	protected String uploadTrimesterVisitE(String url, String username,
			String password) throws Exception {
		try {
			if(mTrimesterVisitE.size()>0){
				publishProgress("Enviando visitas trimestrales (2)!", "9", "14");
				// La URL de la solicitud POST
				final String urlRequest = url + "/movil/zp04TrimesterVisitSectionEs";
				Zp04TrimesterVisitSectionE[] envio = mTrimesterVisitE.toArray(new Zp04TrimesterVisitSectionE[mTrimesterVisitE.size()]);
				HttpHeaders requestHeaders = new HttpHeaders();
				HttpAuthentication authHeader = new HttpBasicAuthentication(username, password);
				requestHeaders.setContentType(MediaType.APPLICATION_JSON);
				requestHeaders.setAuthorization(authHeader);
				HttpEntity<Zp04TrimesterVisitSectionE[]> requestEntity =
						new HttpEntity<Zp04TrimesterVisitSectionE[]>(envio, requestHeaders);
						RestTemplate restTemplate = new RestTemplate();
						restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
						restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
						// Hace la solicitud a la red, pone la vivienda y espera un mensaje de respuesta del servidor
						ResponseEntity<String> response = restTemplate.exchange(urlRequest, HttpMethod.POST, requestEntity,
								String.class);
						return response.getBody();
			}
			else{
				return "Datos recibidos!";
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
			return e.getMessage();
		}
	}

	/***************************************************/
	/********************* Zp04 FtoH ************************/
	/***************************************************/
	// url, username, password
	protected String uploadTrimesterVisitFH(String url, String username,
			String password) throws Exception {
		try {
			if(mTrimesterVisitFH.size()>0){
				publishProgress("Enviando visitas trimestrales (3)!", "10", "14");
				// La URL de la solicitud POST
				final String urlRequest = url + "/movil/zp04TrimesterVisitSectionFtoHs";
				Zp04TrimesterVisitSectionFtoH[] envio = mTrimesterVisitFH.toArray(new Zp04TrimesterVisitSectionFtoH[mTrimesterVisitFH.size()]);
				HttpHeaders requestHeaders = new HttpHeaders();
				HttpAuthentication authHeader = new HttpBasicAuthentication(username, password);
				requestHeaders.setContentType(MediaType.APPLICATION_JSON);
				requestHeaders.setAuthorization(authHeader);
				HttpEntity<Zp04TrimesterVisitSectionFtoH[]> requestEntity =
						new HttpEntity<Zp04TrimesterVisitSectionFtoH[]>(envio, requestHeaders);
						RestTemplate restTemplate = new RestTemplate();
						restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
						restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
						// Hace la solicitud a la red, pone la vivienda y espera un mensaje de respuesta del servidor
						ResponseEntity<String> response = restTemplate.exchange(urlRequest, HttpMethod.POST, requestEntity,
								String.class);
						return response.getBody();
			}
			else{
				return "Datos recibidos!";
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
			return e.getMessage();
		}
	}	

	/***************************************************/
	/********************* Zp05 ************************/
	/***************************************************/
	// url, username, password
	protected String uploadUltrasoundExams(String url, String username,
			String password) throws Exception {
		try {
			if(mUltrasounds.size()>0){
				publishProgress("Enviando ultrasonidos!", "11", "14");
				// La URL de la solicitud POST
				final String urlRequest = url + "/movil/zp05UltrasoundExams";
				Zp05UltrasoundExam[] envio = mUltrasounds.toArray(new Zp05UltrasoundExam[mUltrasounds.size()]);
				HttpHeaders requestHeaders = new HttpHeaders();
				HttpAuthentication authHeader = new HttpBasicAuthentication(username, password);
				requestHeaders.setContentType(MediaType.APPLICATION_JSON);
				requestHeaders.setAuthorization(authHeader);
				HttpEntity<Zp05UltrasoundExam[]> requestEntity =
						new HttpEntity<Zp05UltrasoundExam[]>(envio, requestHeaders);
						RestTemplate restTemplate = new RestTemplate();
						restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
						restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
						// Hace la solicitud a la red, pone la vivienda y espera un mensaje de respuesta del servidor
						ResponseEntity<String> response = restTemplate.exchange(urlRequest, HttpMethod.POST, requestEntity,
								String.class);
						return response.getBody();
			}
			else{
				return "Datos recibidos!";
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
			return e.getMessage();
		}
	}

	/***************************************************/
	/********************* Zp06 ************************/
	/***************************************************/
	// url, username, password
	protected String uploadDeliverys(String url, String username,
			String password) throws Exception {
		try {
			if(mDeliverys.size()>0){
				publishProgress("Enviando partos!", "12", "14");
				// La URL de la solicitud POST
				final String urlRequest = url + "/movil/zp06DeliveryAnd6weekVisits";
				Zp06DeliveryAnd6weekVisit[] envio = mDeliverys.toArray(new Zp06DeliveryAnd6weekVisit[mDeliverys.size()]);
				HttpHeaders requestHeaders = new HttpHeaders();
				HttpAuthentication authHeader = new HttpBasicAuthentication(username, password);
				requestHeaders.setContentType(MediaType.APPLICATION_JSON);
				requestHeaders.setAuthorization(authHeader);
				HttpEntity<Zp06DeliveryAnd6weekVisit[]> requestEntity =
						new HttpEntity<Zp06DeliveryAnd6weekVisit[]>(envio, requestHeaders);
						RestTemplate restTemplate = new RestTemplate();
						restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
						restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
						// Hace la solicitud a la red, pone la vivienda y espera un mensaje de respuesta del servidor
						ResponseEntity<String> response = restTemplate.exchange(urlRequest, HttpMethod.POST, requestEntity,
								String.class);
						return response.getBody();
			}
			else{
				return "Datos recibidos!";
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
			return e.getMessage();
		}
	}
	
	/***************************************************/
	/********************* Zp08 ************************/
	/***************************************************/
	// url, username, password
	protected String uploadExits(String url, String username,
			String password) throws Exception {
		try {
			if(mExits.size()>0){
				publishProgress("Enviando salidas!", "13", "14");
				// La URL de la solicitud POST
				final String urlRequest = url + "/movil/zp08StudyExits";
				Zp08StudyExit[] envio = mExits.toArray(new Zp08StudyExit[mExits.size()]);
				HttpHeaders requestHeaders = new HttpHeaders();
				HttpAuthentication authHeader = new HttpBasicAuthentication(username, password);
				requestHeaders.setContentType(MediaType.APPLICATION_JSON);
				requestHeaders.setAuthorization(authHeader);
				HttpEntity<Zp08StudyExit[]> requestEntity =
						new HttpEntity<Zp08StudyExit[]>(envio, requestHeaders);
						RestTemplate restTemplate = new RestTemplate();
						restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
						restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
						// Hace la solicitud a la red, pone la vivienda y espera un mensaje de respuesta del servidor
						ResponseEntity<String> response = restTemplate.exchange(urlRequest, HttpMethod.POST, requestEntity,
								String.class);
						return response.getBody();
			}
			else{
				return "Datos recibidos!";
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
			return e.getMessage();
		}
	}	
	
	/***************************************************/
	/********************* ZpEstadosEmbarazadas ************************/
	/***************************************************/
	// url, username, password
	protected String uploadStatusPreg(String url, String username,
			String password) throws Exception {
		try {
			if(mStatus.size()>0){
				publishProgress("Enviando estado embarazadas!", "14", "14");
				// La URL de la solicitud POST
				final String urlRequest = url + "/movil/zpEstadoEmb";
				ZpEstadoEmbarazada[] envio = mStatus.toArray(new ZpEstadoEmbarazada[mStatus.size()]);
				HttpHeaders requestHeaders = new HttpHeaders();
				HttpAuthentication authHeader = new HttpBasicAuthentication(username, password);
				requestHeaders.setContentType(MediaType.APPLICATION_JSON);
				requestHeaders.setAuthorization(authHeader);
				HttpEntity<ZpEstadoEmbarazada[]> requestEntity =
						new HttpEntity<ZpEstadoEmbarazada[]>(envio, requestHeaders);
						RestTemplate restTemplate = new RestTemplate();
						restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
						restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
						// Hace la solicitud a la red, pone la vivienda y espera un mensaje de respuesta del servidor
						ResponseEntity<String> response = restTemplate.exchange(urlRequest, HttpMethod.POST, requestEntity,
								String.class);
						return response.getBody();
			}
			else{
				return "Datos recibidos!";
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
			return e.getMessage();
		}
	}
}