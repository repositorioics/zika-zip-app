package ni.org.ics.zip.appmovil.tasks.downloads;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import ni.org.ics.zip.appmovil.database.ZipAdapter;
import ni.org.ics.zip.appmovil.domain.Zp00Screening;
import ni.org.ics.zip.appmovil.domain.Zp01StudyEntrySectionAtoD;
import ni.org.ics.zip.appmovil.domain.Zp01StudyEntrySectionE;
import ni.org.ics.zip.appmovil.domain.Zp01StudyEntrySectionFtoK;
import ni.org.ics.zip.appmovil.domain.Zp02BiospecimenCollection;
import ni.org.ics.zip.appmovil.domain.Zp03MonthlyVisit;
import ni.org.ics.zip.appmovil.domain.ZpPreScreening;
import ni.org.ics.zip.appmovil.tasks.DownloadTask;
import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.content.Context;
import android.util.Log;



public class DownloadAllTask extends DownloadTask {
	
	private final Context mContext;
	
	public DownloadAllTask(Context context) {
		mContext = context;
	}
	
	protected static final String TAG = DownloadAllTask.class.getSimpleName();
	private ZipAdapter zipA = null;
	private List<ZpPreScreening> mPreTamizajes = null;
	private List<Zp00Screening> mTamizajes = null;
	private List<Zp01StudyEntrySectionAtoD> mIngresosAD = null;
	private List<Zp01StudyEntrySectionE> mIngresosE = null;
	private List<Zp01StudyEntrySectionFtoK> mIngresosFK = null;
	private List<Zp02BiospecimenCollection> mCollections = null;
	private List<Zp03MonthlyVisit> mMonthlyVisits = null;

	private String error = null;
	private String url = null;
	private String username = null;
	private String password = null;
	private int v =0;

	@Override
	protected String doInBackground(String... values) {
		url = values[0];
		username = values[1];
		password = values[2];
		
		try {
			error = descargarDatos();
			if (error!=null) return error;
		} catch (Exception e) {
			// Regresa error al descargar
			e.printStackTrace();
			return e.getLocalizedMessage();
		}
		publishProgress("Abriendo base de datos...","1","1");
		zipA = new ZipAdapter(mContext, password, false);
		zipA.open();
		//Borrar los datos de la base de datos
		zipA.borrarZpPreScreening();
		zipA.borrarZp00Screening();
		zipA.borrarZp01StudyEntrySectionAtoD();
		zipA.borrarZp01StudyEntrySectionE();
		zipA.borrarZp01StudyEntrySectionFtoK();
		zipA.borrarZp02BiospecimenCollection();
		zipA.borrarZp03MonthlyVisit();
		try {
			if (mPreTamizajes != null){
				v = mPreTamizajes.size();
				ListIterator<ZpPreScreening> iter = mPreTamizajes.listIterator();
				while (iter.hasNext()){
					zipA.crearZpPreScreening(iter.next());
					publishProgress("Insertando pre tamizajes en la base de datos...", Integer.valueOf(iter.nextIndex()).toString(), Integer
							.valueOf(v).toString());
				}
			}
			if (mTamizajes != null){
				v = mTamizajes.size();
				ListIterator<Zp00Screening> iter = mTamizajes.listIterator();
				while (iter.hasNext()){
					zipA.crearZp00Screening(iter.next());
					publishProgress("Insertando tamizajes en la base de datos...", Integer.valueOf(iter.nextIndex()).toString(), Integer
							.valueOf(v).toString());
				}
			}
			if (mIngresosAD != null){
				v = mIngresosAD.size();
				ListIterator<Zp01StudyEntrySectionAtoD> iter = mIngresosAD.listIterator();
				while (iter.hasNext()){
					zipA.crearZp01StudyEntrySectionAtoD(iter.next());
					publishProgress("Insertando ingresos(1) en la base de datos...", Integer.valueOf(iter.nextIndex()).toString(), Integer
							.valueOf(v).toString());
				}
			}
			if (mIngresosE != null){
				v = mIngresosE.size();
				ListIterator<Zp01StudyEntrySectionE> iter = mIngresosE.listIterator();
				while (iter.hasNext()){
					zipA.crearZp01StudyEntrySectionE(iter.next());
					publishProgress("Insertando ingresos(2) en la base de datos...", Integer.valueOf(iter.nextIndex()).toString(), Integer
							.valueOf(v).toString());
				}
			}
			if (mIngresosFK != null){
				v = mIngresosFK.size();
				ListIterator<Zp01StudyEntrySectionFtoK> iter = mIngresosFK.listIterator();
				while (iter.hasNext()){
					zipA.crearZp01StudyEntrySectionFtoK(iter.next());
					publishProgress("Insertando ingresos(3) en la base de datos...", Integer.valueOf(iter.nextIndex()).toString(), Integer
							.valueOf(v).toString());
				}
			}
			if (mCollections != null){
				v = mCollections.size();
				ListIterator<Zp02BiospecimenCollection> iter = mCollections.listIterator();
				while (iter.hasNext()){
					zipA.crearZp02BiospecimenCollection(iter.next());
					publishProgress("Insertando muestras en la base de datos...", Integer.valueOf(iter.nextIndex()).toString(), Integer
							.valueOf(v).toString());
				}
			}
			if (mMonthlyVisits != null){
				v = mMonthlyVisits.size();
				ListIterator<Zp03MonthlyVisit> iter = mMonthlyVisits.listIterator();
				while (iter.hasNext()){
					zipA.crearZp03MonthlyVisit(iter.next());
					publishProgress("Insertando visitas mensuales en la base de datos...", Integer.valueOf(iter.nextIndex()).toString(), Integer
							.valueOf(v).toString());
				}
			}
		} catch (Exception e) {
			// Regresa error al insertar
			e.printStackTrace();
			zipA.close();
			return e.getLocalizedMessage();
		}
		zipA.close();
		return error;
	}
	
	// url, username, password
	protected String descargarDatos() throws Exception {
		try {
			// The URL for making the GET request
			String urlRequest;
			// Set the Accept header for "application/json"
			HttpAuthentication authHeader = new HttpBasicAuthentication(username, password);
			HttpHeaders requestHeaders = new HttpHeaders();
			List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
			acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
			requestHeaders.setAccept(acceptableMediaTypes);
			requestHeaders.setAuthorization(authHeader);
			// Populate the headers in an HttpEntity object to use for the request
			HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);
			// Create a new RestTemplate instance
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
			//Descargar pretamizajes
			urlRequest = url + "/movil/zpPreScreening/{username}";
			publishProgress("Solicitando pretamizajes","1","14");
			// Perform the HTTP GET request
			ResponseEntity<ZpPreScreening[]> responseEntityZpPreScreening = restTemplate.exchange(urlRequest, HttpMethod.GET, requestEntity,
					ZpPreScreening[].class, username);
			// convert the array to a list and return it
			mPreTamizajes = Arrays.asList(responseEntityZpPreScreening.getBody());
			//Descargar tamizajes
			urlRequest = url + "/movil/zp00Screenings/{username}";
			publishProgress("Solicitando tamizajes","2","14");
			// Perform the HTTP GET request
			ResponseEntity<Zp00Screening[]> responseEntityZp00Screening = restTemplate.exchange(urlRequest, HttpMethod.GET, requestEntity,
					Zp00Screening[].class, username);
			// convert the array to a list and return it
			mTamizajes = Arrays.asList(responseEntityZp00Screening.getBody());
			//Descargar ingresos parte 1
			urlRequest = url + "/movil/zp01StudyEntrySectionAtoDs/{username}";
			publishProgress("Solicitando ingresos (1)","3","14");
			// Perform the HTTP GET request
			ResponseEntity<Zp01StudyEntrySectionAtoD[]> responseEntityZp01StudyEntrySectionAtoD = restTemplate.exchange(urlRequest, HttpMethod.GET, requestEntity,
					Zp01StudyEntrySectionAtoD[].class, username);
			// convert the array to a list and return it
			mIngresosAD = Arrays.asList(responseEntityZp01StudyEntrySectionAtoD.getBody());
			//Descargar ingresos parte 2
			urlRequest = url + "/movil/zp01StudyEntrySectionEs/{username}";
			publishProgress("Solicitando ingresos (2)","4","14");
			// Perform the HTTP GET request
			ResponseEntity<Zp01StudyEntrySectionE[]> responseEntityZp01StudyEntrySectionE = restTemplate.exchange(urlRequest, HttpMethod.GET, requestEntity,
					Zp01StudyEntrySectionE[].class, username);
			// convert the array to a list and return it
			mIngresosE = Arrays.asList(responseEntityZp01StudyEntrySectionE.getBody());
			//Descargar ingresos parte 3
			urlRequest = url + "/movil/zp01StudyEntrySectionFtoKs/{username}";
			publishProgress("Solicitando ingresos (3)","5","14");
			// Perform the HTTP GET request
			ResponseEntity<Zp01StudyEntrySectionFtoK[]> responseZp01StudyEntrySectionFtoK = restTemplate.exchange(urlRequest, HttpMethod.GET, requestEntity,
					Zp01StudyEntrySectionFtoK[].class, username);
			// convert the array to a list and return it
			mIngresosFK = Arrays.asList(responseZp01StudyEntrySectionFtoK.getBody());
			//Descargar muestras
			urlRequest = url + "/movil/zp02BiospecimenCollections/{username}";
			publishProgress("Solicitando muestras","6","14");
			// Perform the HTTP GET request
			ResponseEntity<Zp02BiospecimenCollection[]> responseZp02BiospecimenCollection = restTemplate.exchange(urlRequest, HttpMethod.GET, requestEntity,
					Zp02BiospecimenCollection[].class, username);
			// convert the array to a list and return it
			mCollections = Arrays.asList(responseZp02BiospecimenCollection.getBody());
			//Descargar visitas mensuales
			urlRequest = url + "/movil/zp03MonthlyVisits/{username}";
			publishProgress("Solicitando visitas mensuales","7","14");
			// Perform the HTTP GET request
			ResponseEntity<Zp03MonthlyVisit[]> responseZp03MonthlyVisit = restTemplate.exchange(urlRequest, HttpMethod.GET, requestEntity,
					Zp03MonthlyVisit[].class, username);
			// convert the array to a list and return it
			mMonthlyVisits = Arrays.asList(responseZp03MonthlyVisit.getBody());
			return null;
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
			return e.getLocalizedMessage();	
		}
	}
}
