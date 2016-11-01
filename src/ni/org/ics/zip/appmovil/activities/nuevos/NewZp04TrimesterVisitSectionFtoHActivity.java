package ni.org.ics.zip.appmovil.activities.nuevos;

import android.app.Dialog;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import ni.org.ics.zip.appmovil.AbstractAsyncActivity;
import ni.org.ics.zip.appmovil.MainActivity;
import ni.org.ics.zip.appmovil.MyZipApplication;
import ni.org.ics.zip.appmovil.R;
import ni.org.ics.zip.appmovil.database.ZipAdapter;
import ni.org.ics.zip.appmovil.domain.Zp00Screening;
import ni.org.ics.zip.appmovil.domain.Zp04TrimesterVisitSectionFtoH;
import ni.org.ics.zip.appmovil.parsers.Zp04TrimesterVisitSectionFtoHXml;
import ni.org.ics.zip.appmovil.preferences.PreferencesActivity;
import ni.org.ics.zip.appmovil.utils.Constants;
import ni.org.ics.zip.appmovil.utils.FileUtils;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.util.Date;

/**
 * Created by ics on 31/10/2016.
 */
public class NewZp04TrimesterVisitSectionFtoHActivity extends AbstractAsyncActivity {

    protected static final String TAG = NewZp01StudyEntrySectionEActivity.class.getSimpleName();

    private ZipAdapter zipA;
    private static Zp04TrimesterVisitSectionFtoH mIngreso = new Zp04TrimesterVisitSectionFtoH();

    public static final int ADD_TAMIZAJE_ODK = 1;
    public static final int BARCODE_CAPTURE_TAM = 2;

    Dialog dialogInit;
    private SharedPreferences settings;
    private String username;
    private String mRecordId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!FileUtils.storageReady()) {
            Toast toast = Toast.makeText(getApplicationContext(),getString(R.string.error, R.string.storage_error),Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
        settings =
                PreferenceManager.getDefaultSharedPreferences(this);
        username =
                settings.getString(PreferencesActivity.KEY_USERNAME,
                        null);
        String mPass = ((MyZipApplication) this.getApplication()).getPassApp();
        zipA = new ZipAdapter(this.getApplicationContext(),mPass,false);
        mIngreso = (Zp04TrimesterVisitSectionFtoH) getIntent().getExtras().getSerializable(Constants.OBJECTO_ZP04F);
        mRecordId = getIntent().getExtras().getString(Constants.RECORDID);
        createInitDialog();
    }

    /**
     * Presenta dialogo inicial
     */

    private void createInitDialog() {
        dialogInit = new Dialog(this, R.style.FullHeightDialog);
        dialogInit.setContentView(R.layout.yesno);
        dialogInit.setCancelable(false);

        //to set the message
        TextView message =(TextView) dialogInit.findViewById(R.id.yesnotext);
        if (mIngreso!=null){
            message.setText(getString(R.string.edit)+ " " + getString(R.string.main_maternal));
        }
        else{
            message.setText(getString(R.string.add)+ " " + getString(R.string.main_maternal));
        }

        //add some action to the buttons

        Button yes = (Button) dialogInit.findViewById(R.id.yesnoYes);
        yes.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                dialogInit.dismiss();
                addTrimesterVisit();
            }
        });

        Button no = (Button) dialogInit.findViewById(R.id.yesnoNo);
        no.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Cierra
                dialogInit.dismiss();
                finish();
            }
        });
        dialogInit.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.general, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.MENU_BACK){
            finish();
            return true;
        }
        else if(item.getItemId()==R.id.MENU_HOME){
            Intent i = new Intent(getApplicationContext(),
                    MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
            return true;
        }
        else{
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if(requestCode == ADD_TAMIZAJE_ODK) {
            if(resultCode == RESULT_OK) {
                Uri instanceUri = intent.getData();
                //Busca la instancia resultado
                String[] projection = new String[] {
                        "_id","instanceFilePath", "status","displaySubtext"};
                Cursor c = getContentResolver().query(instanceUri, projection,
                        null, null, null);
                c.moveToFirst();
                //Captura la id de la instancia y la ruta del archivo para agregarlo al participante
                Integer idInstancia = c.getInt(c.getColumnIndex("_id"));
                String instanceFilePath = c.getString(c.getColumnIndex("instanceFilePath"));
                String complete = c.getString(c.getColumnIndex("status"));
                //cierra el cursor
                if (c != null) {
                    c.close();
                }
                if (complete.matches("complete")){
                    //Parsear el resultado obteniendo un tamizaje si esta completo
                    parseTrimesterVisit(idInstancia,instanceFilePath);
                }
                else{
                    Toast.makeText(getApplicationContext(),	getString(R.string.err_not_completed), Toast.LENGTH_LONG).show();
                }
            }
            else{

            }
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    /**
     *
     */
    private void addTrimesterVisit() {
        try{
            //campos de proveedor de collect
            String[] projection = new String[] {
                    "_id","jrFormId","displayName"};
            //cursor que busca el formulario
            Cursor c = getContentResolver().query(Constants.CONTENT_URI, projection,
                    "jrFormId = 'ZP04_Trimester_Visit_F_H' and displayName = 'Estudio ZIP Visita Cuestionario Trimestral_F_H'", null, null);
            c.moveToFirst();
            //captura el id del formulario
            Integer id = Integer.parseInt(c.getString(0));
            //cierra el cursor
            if (c != null) {
                c.close();
            }
            //forma el uri para ODK Collect
            Uri formUri = ContentUris.withAppendedId(Constants.CONTENT_URI,id);
            //Arranca la actividad ODK Collect en busca de resultado
            Intent odkA =  new Intent(Intent.ACTION_EDIT,formUri);
            startActivityForResult(odkA, ADD_TAMIZAJE_ODK);
        }
        catch(Exception e){
            //No existe el formulario en el equipo
            Log.e(TAG, e.getMessage(), e);
            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void parseTrimesterVisit(Integer idInstancia, String instanceFilePath) {
        Serializer serializer = new Persister();
        File source = new File(instanceFilePath);
        try {
            Zp04TrimesterVisitSectionFtoHXml zp04Xml = new Zp04TrimesterVisitSectionFtoHXml();
            zp04Xml = serializer.read(Zp04TrimesterVisitSectionFtoHXml.class, source);
            mIngreso.setRecordId(mRecordId);
          //  mIngreso.setRedcapEventName(zp04Xml.get());
            mIngreso.setTriBugNuisInd(zp04Xml.getTriBugNuisInd());
            mIngreso.setTriPestStorHomeInd(zp04Xml.getTriPestStorHomeInd());
            mIngreso.setTriPestAppHomeInd(zp04Xml.getTriPestAppHomeInd());
            mIngreso.setTriPestAppDay(zp04Xml.getTriPestAppDay());
            mIngreso.setTriPestAppMonth(zp04Xml.getTriPestAppMonth());
            mIngreso.setTriPestAppYear(zp04Xml.getTriPestAppYear());
            mIngreso.setTriPestAppName(zp04Xml.getTriPestAppName());
            mIngreso.setTriHomeTrtdInsctInd(zp04Xml.getTriHomeTrtdInsctInd());
            mIngreso.setTriHomeTrtdLoc(zp04Xml.getTriHomeTrtdLoc());
            mIngreso.setTriHomeTrtdEntity(zp04Xml.getTriHomeTrtdEntity());
            mIngreso.setTriHomeTrtdNames(zp04Xml.getTriHomeTrtdNames());
            mIngreso.setTriTrtmntAppDay(zp04Xml.getTriTrtmntAppDay());
            mIngreso.setTriTrtmntAppMonth(zp04Xml.getTriTrtmntAppMonth());
            mIngreso.setTriTrtmntAppYear(zp04Xml.getTriTrtmntAppYear());
            mIngreso.setTriLwnTrtmntAppInd(zp04Xml.getTriLwnTrtmntAppInd());
            mIngreso.setTriLwnTrtmntAppDay(zp04Xml.getTriLwnTrtmntAppDay());
            mIngreso.setTriLwnTrtmntAppMonth(zp04Xml.getTriLwnTrtmntAppMonth());
            mIngreso.setTriLwnTrtmntAppYear(zp04Xml.getTriLwnTrtmntAppYear());
            mIngreso.setTriLwnTrtmntAppName(zp04Xml.getTriLwnTrtmntAppName());
            mIngreso.setTriMosqRepInd(zp04Xml.getTriMosqRepInd());
            mIngreso.setTriMosqRepTyp(zp04Xml.getTriMosqRepTyp());
            mIngreso.setTriMosqRepNameSpray(zp04Xml.getTriMosqRepNameSpray());
            mIngreso.setTriMosqRepDkSpray(zp04Xml.getTriMosqRepDkSpray());
            mIngreso.setTriMosqRepNameLotion(zp04Xml.getTriMosqRepNameLotion());
            mIngreso.setTriMosqRepDkLotion(zp04Xml.getTriMosqRepDkLotion());
            mIngreso.setTriMosqRepNameSpiral(zp04Xml.getTriMosqRepNameSpiral());
            mIngreso.setTriMosqRepDkSpiral(zp04Xml.getTriMosqRepDkSpiral());
            mIngreso.setTriMosqRepNamePlugin(zp04Xml.getTriMosqRepNamePlugin());
            mIngreso.setTriMosqRepDkPlugin(zp04Xml.getTriMosqRepDkPlugin());
            mIngreso.setTriMosqRepNameOther(zp04Xml.getTriMosqRepNameOther());
            mIngreso.setTriMosqRepDkOther(zp04Xml.getTriMosqRepDkOther());
            mIngreso.setTriNextVisitDat(zp04Xml.getTriNextVisitDat());
            mIngreso.setTriNextVisitTime(zp04Xml.getTriNextVisitTime());
            mIngreso.setTriCompId(username);
            mIngreso.setTriCompDat(new Date());
            mIngreso.setTriRevId(username);
            mIngreso.setTriRevDat(new Date());
            mIngreso.setTriEntId(username);
            mIngreso.setTriEntDat(new Date());
            mIngreso.setRecordDate(new Date());
            mIngreso.setRecordUser(username);
            mIngreso.setIdInstancia(idInstancia);
            mIngreso.setInstancePath(instanceFilePath);
            mIngreso.setEstado(Constants.STATUS_NOT_SUBMITTED);
            mIngreso.setStart(zp04Xml.getStart());
            mIngreso.setEnd(zp04Xml.getEnd());
            mIngreso.setDeviceid(zp04Xml.getDeviceid());
            mIngreso.setSimserial(zp04Xml.getSimserial());
            mIngreso.setPhonenumber(zp04Xml.getPhonenumber());
            mIngreso.setToday(zp04Xml.getToday());
            new SaveDataTask().execute();

        } catch (Exception e) {
            // Presenta el error al parsear el xml
            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    // ***************************************
    // Private classes
    // ***************************************
    private class SaveDataTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            // before the request begins, show a progress indicator
            showLoadingProgressDialog();
        }

        @Override
        protected String doInBackground(String... values) {
            try {
                zipA.open();
                zipA.crearZp04TrimesterVisitSectionFtoH(mIngreso);
                zipA.close();
            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
                return "error";
            }
            return "exito";
        }

        protected void onPostExecute(String resultado) {
            // after the network request completes, hide the progress indicator
            dismissProgressDialog();
            showResult(resultado);
        }

    }

    // ***************************************
    // Private methods
    // ***************************************
    private void showResult(String resultado) {
        Toast.makeText(getApplicationContext(),	resultado, Toast.LENGTH_LONG).show();
    }




}
