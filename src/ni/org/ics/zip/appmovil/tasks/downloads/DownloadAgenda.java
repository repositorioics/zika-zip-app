package ni.org.ics.zip.appmovil.tasks.downloads;

import android.content.Context;
import android.util.Log;

import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import ni.org.ics.zip.appmovil.database.ZipAdapter;
import ni.org.ics.zip.appmovil.domain.Parametro;
import ni.org.ics.zip.appmovil.domain.Provider;
import ni.org.ics.zip.appmovil.domain.ZpAgendaEstudio;
import ni.org.ics.zip.appmovil.domain.ZpCenter;
import ni.org.ics.zip.appmovil.domain.ZpPreScreening;
import ni.org.ics.zip.appmovil.domain.ZpSpecialities;
import ni.org.ics.zip.appmovil.tasks.DownloadTask;

/**
 * Created by A.L. on 30/11/2017.
 */

public class DownloadAgenda extends DownloadTask {

    private final Context mContext;

    public DownloadAgenda(Context context) {
        mContext = context;
    }

    protected static final String TAG = DownloadAllTask.class.getSimpleName();
    private static final String TOTAL_TASK = "5";
    private ZipAdapter zipA = null;

    private List<ZpAgendaEstudio> agendaStudioResult = null;
    //
    private List<ZpCenter> centerResult = null;
    private List<ZpSpecialities> especialidadResult = null;
    //
    private List<Parametro> parametrosResult = null;
    private List<Provider> ProviderResult = null;

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
        zipA = new ZipAdapter(mContext, password, false,false);
        zipA.open();
        //Borrar los datos de la base de datos

        zipA.borrarZpAgendaStudio();

        zipA.borrarZpCenter();
        zipA.borrarZpEspecialidad();
        zipA.borrarProviders();
        zipA.borrarParametro();

        try {

            if(agendaStudioResult != null){
                v = agendaStudioResult.size();
                ListIterator<ZpAgendaEstudio> iter = agendaStudioResult.listIterator();
                while(iter.hasNext()){
                    zipA.crearZpAgendaStudio(iter.next());
                    publishProgress("Insertando agenda de estudios...", Integer.valueOf(iter.nextIndex()).toString(), Integer
                            .valueOf(v).toString());

                }
            }
            ///// Catalogos
            if(centerResult != null){
                v = centerResult.size();
                ListIterator<ZpCenter> iter = centerResult.listIterator();
                while(iter.hasNext()){
                    zipA.crearZpCentro(iter.next());
                    publishProgress("Insertando unidades de salud...", Integer.valueOf(iter.nextIndex()).toString(), Integer
                            .valueOf(v).toString());

                }
            }

            if(especialidadResult != null){
                v = especialidadResult.size();
                ListIterator<ZpSpecialities> iter = especialidadResult.listIterator();
                while(iter.hasNext()){
                    zipA.crearZpEspecialidad(iter.next());
                    publishProgress("Insertando especialidad ...", Integer.valueOf(iter.nextIndex()).toString(), Integer
                            .valueOf(v).toString());

                }
            }

            if(parametrosResult != null){
                v = parametrosResult.size();
                ListIterator<Parametro> iter = parametrosResult.listIterator();
                while(iter.hasNext()){
                    zipA.crearParametro(iter.next());
                    publishProgress("Insertando Parametros para Agenda ...", Integer.valueOf(iter.nextIndex()).toString(), Integer
                            .valueOf(v).toString());

                }
            }

            if(ProviderResult != null){
                v = ProviderResult.size();
                ListIterator<Provider> iter = ProviderResult.listIterator();
                while(iter.hasNext()){
                    zipA.crearProvider(iter.next());
                    publishProgress("Insertando Proveedor para Agenda ...", Integer.valueOf(iter.nextIndex()).toString(), Integer
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

            // convert the array to a list and return it


            urlRequest = url + "/movil/zpAgendaStudio/{username}";
            publishProgress("Solicitando agenda de estudios","1",TOTAL_TASK);
            // Perform the HTTP GET request
            ResponseEntity<ZpAgendaEstudio[]> responseZpAgendaStudio = restTemplate.exchange(urlRequest, HttpMethod.GET, requestEntity,
                    ZpAgendaEstudio[].class, username);
            // convert the array to a list and return it
            agendaStudioResult = Arrays.asList(responseZpAgendaStudio.getBody());

            // Descargar la centros de salud por usuario  | A.L. 17/11/2017
            urlRequest = url + "/movil/centers/{username}";
            publishProgress("Solicitando unidades de salud","2",TOTAL_TASK);
            // Perform the HTTP GET request
            ResponseEntity<ZpCenter[]> responseZpCenters = restTemplate.exchange(urlRequest, HttpMethod.GET, requestEntity,
                    ZpCenter[].class, username);
            // convert the array to a list and return it
            centerResult = Arrays.asList(responseZpCenters.getBody());

            // Descargar la ESPECIALIDADES de salud por usuario  | A.L. 17/11/2017
            urlRequest = url + "/movil/specialities/{username}";
            publishProgress("Solicitando especialidades","3",TOTAL_TASK);
            // Perform the HTTP GET request
            ResponseEntity<ZpSpecialities[]> responseZpEspecialidades = restTemplate.exchange(urlRequest, HttpMethod.GET, requestEntity,
                    ZpSpecialities[].class, username);
            // convert the array to a list and return it
            especialidadResult = Arrays.asList(responseZpEspecialidades.getBody());

            // Descargar la Parametros de agenda  | A.L. 23/11/2017
            urlRequest = url + "/movil/paramerters/{username}";
            publishProgress("Solicitando parametros","4",TOTAL_TASK);
            ResponseEntity<Parametro[]> responseParameters = restTemplate.exchange(urlRequest, HttpMethod.GET, requestEntity,
                    Parametro[].class, username);
            parametrosResult = Arrays.asList(responseParameters.getBody());

            // Descargar la Proveedores   | A.L. 23/11/2017
            urlRequest = url + "/movil/providers/{username}";
            publishProgress("Solicitando Proveedores","5",TOTAL_TASK);
            ResponseEntity<Provider[]> responseProviders = restTemplate.exchange(urlRequest, HttpMethod.GET, requestEntity,
                    Provider[].class, username);
            ProviderResult = Arrays.asList(responseProviders.getBody());


            return null;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            return e.getLocalizedMessage();
        }
    }

}
