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

public class UploadTamizajesTask extends UploadTask {
	
	private final Context mContext;

	public UploadTamizajesTask(Context context) {
		mContext = context;
	}

	protected static final String TAG = UploadTamizajesTask.class.getSimpleName();
	private List<Zp00Screening> mTamizajes = new ArrayList<Zp00Screening>();
    private List<Zp01StudyEntrySectionAtoD> mIngresosAD = new ArrayList<Zp01StudyEntrySectionAtoD>();
    private List<Zp01StudyEntrySectionFtoK> mIngresosFK = new ArrayList<Zp01StudyEntrySectionFtoK>();
    private List<Zp02BiospecimenCollection> mCollections = new ArrayList<Zp02BiospecimenCollection>();
    private List<Zp03MonthlyVisit> mMonthlyVisits = new ArrayList<Zp03MonthlyVisit>();
    private List<Zp05UltrasoundExam> mUltrasounds = new ArrayList<Zp05UltrasoundExam>();
    private List<Zp06DeliveryAnd6weekVisit> mDeliverys = new ArrayList<Zp06DeliveryAnd6weekVisit>();
    private List<Zp08StudyExit> mExits = new ArrayList<Zp08StudyExit>();
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
			error = cargarParticipantes(url, username, password);
			if (!error.matches("Datos recibidos!")){
				return error;
			}
            error = uploadEntrysAD(url, username, password);
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

		} catch (Exception e1) {
			e1.printStackTrace();
			return e1.getLocalizedMessage();
		}
		return error;
	}

	// url, username, password
	protected String cargarParticipantes(String url, String username, 
			String password) throws Exception {
		try {
			getTamizajes();
			if(mTamizajes.size()>0){
				saveTamizajes(Constants.STATUS_SUBMITTED);
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
						// Regresa la respuesta a mostrar al usuario
						if (!response.getBody().matches("Datos recibidos!")) {
							saveTamizajes(Constants.STATUS_NOT_SUBMITTED);
						}
						return response.getBody();
			}
			else{
				return "Datos recibidos!";
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
			saveTamizajes(Constants.STATUS_NOT_SUBMITTED);
			return e.getMessage();
		}

	}

	private void saveTamizajes(String estado) {
		ZipAdapter zipA = new ZipAdapter(mContext, password, false);
		zipA.open();
		int c = mTamizajes.size();
		for (Zp00Screening tamizaje : mTamizajes) {
			tamizaje.setEstado(estado);
			zipA.editarZp00Screening(tamizaje);
			publishProgress("Actualizando tamizajes", Integer.valueOf(mTamizajes.indexOf(tamizaje)).toString(), Integer
					.valueOf(c).toString());
		}
		zipA.close();
	}

	private void getTamizajes() {
		ZipAdapter zipA = new ZipAdapter(mContext, password, false);
		zipA.open();
		mTamizajes = zipA.getZp00Screenings("", MainDBConstants.recordId);
		zipA.close();
	}

    /***************************************************/
    /*************** Zp01 AtoD ************************/
    /***************************************************/
    // url, username, password
    protected String uploadEntrysAD(String url, String username,
                                    String password) throws Exception {
        try {
            getEntryAD();
            if(mIngresosAD.size()>0){
                saveEntryAD(Constants.STATUS_SUBMITTED);
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
                // Regresa la respuesta a mostrar al usuario
                if (!response.getBody().matches("Datos recibidos!")) {
                    saveEntryAD(Constants.STATUS_NOT_SUBMITTED);
                }
                return response.getBody();
            }
            else{
                return "Datos recibidos!";
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            saveEntryAD(Constants.STATUS_NOT_SUBMITTED);
            return e.getMessage();
        }

    }

    private void saveEntryAD(String estado) {
        ZipAdapter zipA = new ZipAdapter(mContext, password, false);
        zipA.open();
        int c = mIngresosAD.size();
        for (Zp01StudyEntrySectionAtoD ingreso : mIngresosAD) {
            ingreso.setEstado(estado);
            zipA.editarZp01StudyEntrySectionAtoD(ingreso);
            publishProgress("Actualizando datos de ingreso (A-D)", Integer.valueOf(mIngresosAD.indexOf(ingreso)).toString(), Integer
                    .valueOf(c).toString());
        }
        zipA.close();
    }

    private void getEntryAD() {
        ZipAdapter zipA = new ZipAdapter(mContext, password, false);
        zipA.open();
        mIngresosAD = zipA.getZp01StudyEntrySectionAtoDs("", MainDBConstants.recordId);
        zipA.close();
    }

    /***************************************************/
    /*************** Zp01 FtoK ************************/
    /***************************************************/
    // url, username, password
    protected String uploadEntrysFK(String url, String username,
                                   String password) throws Exception {
        try {
            getEntryFK();
            if(mIngresosFK.size()>0){
                saveEntryFK(Constants.STATUS_SUBMITTED);
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
                // Regresa la respuesta a mostrar al usuario
                if (!response.getBody().matches("Datos recibidos!")) {
                    saveEntryFK(Constants.STATUS_NOT_SUBMITTED);
                }
                return response.getBody();
            }
            else{
                return "Datos recibidos!";
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            saveEntryFK(Constants.STATUS_NOT_SUBMITTED);
            return e.getMessage();
        }

    }

    private void saveEntryFK(String estado) {
        ZipAdapter zipA = new ZipAdapter(mContext, password, false);
        zipA.open();
        int c = mIngresosFK.size();
        for (Zp01StudyEntrySectionFtoK ingreso : mIngresosFK) {
            ingreso.setEstado(estado);
            zipA.editarZp01StudyEntrySectionFtoK(ingreso);
            publishProgress("Actualizando datos de ingreso (F-K)", Integer.valueOf(mIngresosFK.indexOf(ingreso)).toString(), Integer
                    .valueOf(c).toString());
        }
        zipA.close();
    }

    private void getEntryFK() {
        ZipAdapter zipA = new ZipAdapter(mContext, password, false);
        zipA.open();
        mIngresosFK = zipA.getZp01StudyEntrySectionFtoKs("", MainDBConstants.recordId);
        zipA.close();
    }

    /***************************************************/
    /********************* Zp02 ************************/
    /***************************************************/
    // url, username, password
    protected String upLoadBioCollections(String url, String username,
                                      String password) throws Exception {
        try {
            getBioCollections();
            if(mCollections.size()>0){
                saveBioCollection(Constants.STATUS_SUBMITTED);
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
                // Regresa la respuesta a mostrar al usuario
                if (!response.getBody().matches("Datos recibidos!")) {
                    saveBioCollection(Constants.STATUS_NOT_SUBMITTED);
                }
                return response.getBody();
            }
            else{
                return "Datos recibidos!";
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            saveBioCollection(Constants.STATUS_NOT_SUBMITTED);
            return e.getMessage();
        }

    }

    private void saveBioCollection(String estado) {
        ZipAdapter zipA = new ZipAdapter(mContext, password, false);
        zipA.open();
        int c = mCollections.size();
        for (Zp02BiospecimenCollection collection : mCollections) {
            collection.setEstado(estado);
            zipA.editarZp02BiospecimenCollection(collection);
            publishProgress("Actualizando recolección de muestras", Integer.valueOf(mCollections.indexOf(collection)).toString(), Integer
                    .valueOf(c).toString());
        }
        zipA.close();
    }

    private void getBioCollections() {
        ZipAdapter zipA = new ZipAdapter(mContext, password, false);
        zipA.open();
        mCollections = zipA.getZp02BiospecimenCollections("", MainDBConstants.recordId);
        zipA.close();
    }

    /***************************************************/
    /********************* Zp03 ************************/
    /***************************************************/
    // url, username, password
    protected String uploadMonthlyVisits(String url, String username,
                                         String password) throws Exception {
        try {
            getMonthlyVisit();
            if(mMonthlyVisits.size()>0){
                saveMonthlyVisit(Constants.STATUS_SUBMITTED);
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
                // Regresa la respuesta a mostrar al usuario
                if (!response.getBody().matches("Datos recibidos!")) {
                    saveMonthlyVisit(Constants.STATUS_NOT_SUBMITTED);
                }
                return response.getBody();
            }
            else{
                return "Datos recibidos!";
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            saveMonthlyVisit(Constants.STATUS_NOT_SUBMITTED);
            return e.getMessage();
        }

    }

    private void saveMonthlyVisit(String estado) {
        ZipAdapter zipA = new ZipAdapter(mContext, password, false);
        zipA.open();
        int c = mMonthlyVisits.size();
        for (Zp03MonthlyVisit monthlyVisit : mMonthlyVisits) {
            monthlyVisit.setEstado(estado);
            zipA.editarZp03MonthlyVisit(monthlyVisit);
            publishProgress("Actualizando visita mensual", Integer.valueOf(mMonthlyVisits.indexOf(monthlyVisit)).toString(), Integer
                    .valueOf(c).toString());
        }
        zipA.close();
    }

    private void getMonthlyVisit() {
        ZipAdapter zipA = new ZipAdapter(mContext, password, false);
        zipA.open();
        mMonthlyVisits = zipA.getZp03MonthlyVisits("", MainDBConstants.recordId);
        zipA.close();
    }

    /***************************************************/
    /********************* Zp05 ************************/
    /***************************************************/
    // url, username, password
    protected String uploadUltrasoundExams(String url, String username,
                                        String password) throws Exception {
        try {
            getUltrasoundExam();
            if(mUltrasounds.size()>0){
                saveUltrasoundExams(Constants.STATUS_SUBMITTED);
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
                // Regresa la respuesta a mostrar al usuario
                if (!response.getBody().matches("Datos recibidos!")) {
                    saveUltrasoundExams(Constants.STATUS_NOT_SUBMITTED);
                }
                return response.getBody();
            }
            else{
                return "Datos recibidos!";
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            saveUltrasoundExams(Constants.STATUS_NOT_SUBMITTED);
            return e.getMessage();
        }

    }

    private void saveUltrasoundExams(String estado) {
        ZipAdapter zipA = new ZipAdapter(mContext, password, false);
        zipA.open();
        int c = mUltrasounds.size();
        for (Zp05UltrasoundExam ultrasoundExam : mUltrasounds) {
            ultrasoundExam.setEstado(estado);
            zipA.editarZp05UltrasoundExam(ultrasoundExam);
            publishProgress("Actualizando exámenes de ultrasonido", Integer.valueOf(mUltrasounds.indexOf(ultrasoundExam)).toString(), Integer
                    .valueOf(c).toString());
        }
        zipA.close();
    }

    private void getUltrasoundExam() {
        ZipAdapter zipA = new ZipAdapter(mContext, password, false);
        zipA.open();
        mUltrasounds = zipA.getZp05UltrasoundExams("", MainDBConstants.recordId);
        zipA.close();
    }

    /***************************************************/
    /********************* Zp06 ************************/
    /***************************************************/
    // url, username, password
    protected String uploadDeliverys(String url, String username,
                                          String password) throws Exception {
        try {
            getDelivery();
            if(mDeliverys.size()>0){
                saveDelivery(Constants.STATUS_SUBMITTED);
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
                // Regresa la respuesta a mostrar al usuario
                if (!response.getBody().matches("Datos recibidos!")) {
                    saveDelivery(Constants.STATUS_NOT_SUBMITTED);
                }
                return response.getBody();
            }
            else{
                return "Datos recibidos!";
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            saveDelivery(Constants.STATUS_NOT_SUBMITTED);
            return e.getMessage();
        }

    }

    private void saveDelivery(String estado) {
        ZipAdapter zipA = new ZipAdapter(mContext, password, false);
        zipA.open();
        int c = mDeliverys.size();
        for (Zp06DeliveryAnd6weekVisit deliveryAnd6weekVisit : mDeliverys) {
            deliveryAnd6weekVisit.setEstado(estado);
            zipA.editarZp06DeliveryAnd6weekVisit(deliveryAnd6weekVisit);
            publishProgress("Actualizando partos", Integer.valueOf(mDeliverys.indexOf(deliveryAnd6weekVisit)).toString(), Integer
                    .valueOf(c).toString());
        }
        zipA.close();
    }

    private void getDelivery() {
        ZipAdapter zipA = new ZipAdapter(mContext, password, false);
        zipA.open();
        mDeliverys = zipA.getZp06DeliveryAnd6weekVisits("", MainDBConstants.recordId);
        zipA.close();
    }

    /***************************************************/
    /********************* Zp08 ************************/
    /***************************************************/
    // url, username, password
    protected String uploadExits(String url, String username,
                                    String password) throws Exception {
        try {
            getExits();
            if(mExits.size()>0){
                saveExits(Constants.STATUS_SUBMITTED);
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
                // Regresa la respuesta a mostrar al usuario
                if (!response.getBody().matches("Datos recibidos!")) {
                    saveExits(Constants.STATUS_NOT_SUBMITTED);
                }
                return response.getBody();
            }
            else{
                return "Datos recibidos!";
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            saveExits(Constants.STATUS_NOT_SUBMITTED);
            return e.getMessage();
        }

    }

    private void saveExits(String estado) {
        ZipAdapter zipA = new ZipAdapter(mContext, password, false);
        zipA.open();
        int c = mExits.size();
        for (Zp08StudyExit studyExit : mExits) {
            studyExit.setEstado(estado);
            zipA.editarZp08StudyExit(studyExit);
            publishProgress("Actualizando salidas del estudio", Integer.valueOf(mExits.indexOf(studyExit)).toString(), Integer
                    .valueOf(c).toString());
        }
        zipA.close();
    }

    private void getExits() {
        ZipAdapter zipA = new ZipAdapter(mContext, password, false);
        zipA.open();
        mExits = zipA.getZp08StudyExits("", MainDBConstants.recordId);
        zipA.close();
    }
}