package ni.org.ics.zip.appmovil.tasks.uploads;

import java.util.ArrayList;
import java.util.List;

import ni.org.ics.zip.appmovil.database.ZipAdapter;
import ni.org.ics.zip.appmovil.domain.Zp00Screening;
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

}