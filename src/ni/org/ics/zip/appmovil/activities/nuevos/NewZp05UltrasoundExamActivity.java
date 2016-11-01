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
import ni.org.ics.zip.appmovil.domain.Zp05UltrasoundExam;
import ni.org.ics.zip.appmovil.parsers.Zp05UltrasoundExamXml;
import ni.org.ics.zip.appmovil.preferences.PreferencesActivity;
import ni.org.ics.zip.appmovil.utils.Constants;
import ni.org.ics.zip.appmovil.utils.FileUtils;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.util.Date;

/**
 * Created by FIRSTICT on 10/31/2016.
 * V1.0
 */
public class NewZp05UltrasoundExamActivity extends AbstractAsyncActivity {
    protected static final String TAG = NewZp05UltrasoundExamActivity.class.getSimpleName();

    private ZipAdapter zipA;
    private static Zp05UltrasoundExam mUltrasoun = new Zp05UltrasoundExam();

    public static final int ADD_TAMIZAJE_ODK = 1;

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
        mUltrasoun = (Zp05UltrasoundExam) getIntent().getExtras().getSerializable(Constants.OBJECTO_ZP05);
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
        if (mUltrasoun!=null){
            message.setText(getString(R.string.edit)+ " " + getString(R.string.main_review));
        }
        else{
            message.setText(getString(R.string.add)+ " " + getString(R.string.main_maternal));
        }

        //add some action to the buttons

        Button yes = (Button) dialogInit.findViewById(R.id.yesnoYes);
        yes.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                dialogInit.dismiss();
                addZp05UltrasoundExam();
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
                    parseZp05UltrasoundExam(idInstancia, instanceFilePath);
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

    private void addZp05UltrasoundExam() {
        try{
            //campos de proveedor de collect
            String[] projection = new String[] {
                    "_id","jrFormId","displayName"};
            //cursor que busca el formulario
            Cursor c = getContentResolver().query(Constants.CONTENT_URI, projection,
                    "jrFormId = 'ZP05_Ultrasound' and displayName = 'Estudio ZIP Examen de Ultrasonido'", null, null);
            c.moveToFirst();
            //captura el id del formulario
            Integer id = Integer.parseInt(c.getString(0));
            //cierra el cursor
            if (c != null) {
                c.close();
            }
            //forma el uri para ODK Collect
            Uri formUri = ContentUris.withAppendedId(Constants.CONTENT_URI, id);
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

    private void parseZp05UltrasoundExam(Integer idInstancia, String instanceFilePath) {
        Serializer serializer = new Persister();
        File source = new File(instanceFilePath);
        try {
            Zp05UltrasoundExamXml zp05Xml = serializer.read(Zp05UltrasoundExamXml.class, source);
            mUltrasoun.setRecordId(mRecordId);
            mUltrasoun.setRedcapEventName("XXXX");

            mUltrasoun.setUltDate(zp05Xml.getUltDate());
            mUltrasoun.setUltGaWeeks(zp05Xml.getUltGaWeeks());
            mUltrasoun.setUltGaDays(zp05Xml.getUltGaDays());
            mUltrasoun.setUltGeDetermined(zp05Xml.getUltGeDetermined());
            mUltrasoun.setUltReason(zp05Xml.getUltReason());
            mUltrasoun.setUltReasonOther(zp05Xml.getUltReasonOther());
            mUltrasoun.setUltTime(zp05Xml.getUltTime());
            mUltrasoun.setUltNameFacility(zp05Xml.getUltNameFacility());
            mUltrasoun.setUltFacilityid(zp05Xml.getUltFacilityid());
            mUltrasoun.setUltIdSonographer(zp05Xml.getUltIdSonographer());
            mUltrasoun.setUltIdNa(zp05Xml.getUltIdNa());
            mUltrasoun.setUltFestiGaWeeks1(zp05Xml.getUltFestiGaWeeks1());
            mUltrasoun.setUltFestiGaDays1(zp05Xml.getUltFestiGaDays1());
            mUltrasoun.setUltFestiDelivery1(zp05Xml.getUltFestiDelivery1());
            mUltrasoun.setUltFirstYesno1(zp05Xml.getUltFirstYesno1());
            mUltrasoun.setUltFabnormal1(zp05Xml.getUltFabnormal1());
            mUltrasoun.setUltFyesSpecify1(zp05Xml.getUltFyesSpecify1());
            mUltrasoun.setUltFotherFindings1(zp05Xml.getUltFotherFindings1());
            mUltrasoun.setUltFurtherTesting1(zp05Xml.getUltFurtherTesting1());
            mUltrasoun.setUltFnumFetuses(zp05Xml.getUltFnumFetuses());
            mUltrasoun.setUltFfetusViable1(zp05Xml.getUltFfetusViable1());
            mUltrasoun.setUltFfetalCardia1(zp05Xml.getUltFfetalCardia1());
            mUltrasoun.setUltFfetalHeart1(zp05Xml.getUltFfetalHeart1());
            mUltrasoun.setUltFcrl1(zp05Xml.getUltFcrl1());
            mUltrasoun.setUltFcrlNa1(zp05Xml.getUltFcrlNa1());
            mUltrasoun.setUltFfetusViable2(zp05Xml.getUltFfetusViable2());
            mUltrasoun.setUltFfetalCardia2(zp05Xml.getUltFfetalCardia2());
            mUltrasoun.setUltFfetalHeart2(zp05Xml.getUltFfetalHeart2());
            mUltrasoun.setUltFcrl2(zp05Xml.getUltFcrl2());
            mUltrasoun.setUltFcrlNa2(zp05Xml.getUltFcrlNa2());
            mUltrasoun.setUltFfetusViable3(zp05Xml.getUltFfetusViable3());
            mUltrasoun.setUltFfetalCardia3(zp05Xml.getUltFfetalCardia3());
            mUltrasoun.setUltFfetalHeart3(zp05Xml.getUltFfetalHeart3());
            mUltrasoun.setUltFcrl3(zp05Xml.getUltFcrl3());
            mUltrasoun.setUltFcrlNa3(zp05Xml.getUltFcrlNa3());
            mUltrasoun.setUltSfindings1(zp05Xml.getUltSfindings1());
            mUltrasoun.setUltSspecify1(zp05Xml.getUltSspecify1());
            mUltrasoun.setUltSfindingsSpecify1(zp05Xml.getUltSfindingsSpecify1());
            mUltrasoun.setUltFurtherExamination1(zp05Xml.getUltFurtherExamination1());
            mUltrasoun.setUltSplacental1(zp05Xml.getUltSplacental1());
            mUltrasoun.setUltSnumFetuses(zp05Xml.getUltSnumFetuses());
            mUltrasoun.setUltSfetusViable1(zp05Xml.getUltSfetusViable1());
            mUltrasoun.setUltSfetalCardia1(zp05Xml.getUltSfetalCardia1());
            mUltrasoun.setUltSfetalHeart1(zp05Xml.getUltSfetalHeart1());
            mUltrasoun.setUltSbiparietal1(zp05Xml.getUltSbiparietal1());
            mUltrasoun.setUltShead1(zp05Xml.getUltShead1());
            mUltrasoun.setUltMicroceph1(zp05Xml.getUltMicroceph1());
            mUltrasoun.setUltSevMicroceph1(zp05Xml.getUltSevMicroceph1());
            mUltrasoun.setUltSabdominal1(zp05Xml.getUltSabdominal1());
            mUltrasoun.setUltSfemur1(zp05Xml.getUltSfemur1());
            mUltrasoun.setUltSfetalWt1(zp05Xml.getUltSfetalWt1());
            mUltrasoun.setUltSpresentation1(zp05Xml.getUltSpresentation1());
            mUltrasoun.setUltSfetusViable2(zp05Xml.getUltSfetusViable2());
            mUltrasoun.setUltSfetalCardia2(zp05Xml.getUltSfetalCardia2());
            mUltrasoun.setUltSfetalHeart2(zp05Xml.getUltSfetalHeart2());
            mUltrasoun.setUltSbiparietal2(zp05Xml.getUltSbiparietal2());
            mUltrasoun.setUltShead2(zp05Xml.getUltShead2());
            mUltrasoun.setUltMicroceph2(zp05Xml.getUltMicroceph2());
            mUltrasoun.setUltSevMicroceph2(zp05Xml.getUltSevMicroceph2());
            mUltrasoun.setUltSabdominal2(zp05Xml.getUltSabdominal2());
            mUltrasoun.setUltSfemur2(zp05Xml.getUltSfemur2());
            mUltrasoun.setUltSfetalWt2(zp05Xml.getUltSfetalWt2());
            mUltrasoun.setUltSpresentation2(zp05Xml.getUltSpresentation2());
            mUltrasoun.setUltSfetusViable3(zp05Xml.getUltSfetusViable3());
            mUltrasoun.setUltSfetalCardia3(zp05Xml.getUltSfetalCardia3());
            mUltrasoun.setUltSfetalHeart3(zp05Xml.getUltSfetalHeart3());
            mUltrasoun.setUltSbiparietal3(zp05Xml.getUltSbiparietal3());
            mUltrasoun.setUltShead3(zp05Xml.getUltShead3());
            mUltrasoun.setUltMicroceph3(zp05Xml.getUltMicroceph3());
            mUltrasoun.setUltSevMicroceph3(zp05Xml.getUltSevMicroceph3());
            mUltrasoun.setUltSabdominal3(zp05Xml.getUltSabdominal3());
            mUltrasoun.setUltSfemur3(zp05Xml.getUltSfemur3());
            mUltrasoun.setUltSfetalWt3(zp05Xml.getUltSfetalWt3());
            mUltrasoun.setUltSpresentation3(zp05Xml.getUltSpresentation3());
            mUltrasoun.setUltIdCompleting(username);
            mUltrasoun.setUltDateCompleted(new Date());
            mUltrasoun.setUltIdReviewer(username);
            mUltrasoun.setUltDateReviewed(new Date());
            mUltrasoun.setUltIdDataEntry(username);
            mUltrasoun.setUltDateEntered(new Date());

            mUltrasoun.setRecordDate(new Date());
            mUltrasoun.setRecordUser(username);
            mUltrasoun.setIdInstancia(idInstancia);
            mUltrasoun.setInstancePath(instanceFilePath);
            mUltrasoun.setEstado(Constants.STATUS_NOT_SUBMITTED);
            mUltrasoun.setStart(zp05Xml.getStart());
            mUltrasoun.setEnd(zp05Xml.getEnd());
            mUltrasoun.setDeviceid(zp05Xml.getDeviceid());
            mUltrasoun.setSimserial(zp05Xml.getSimserial());
            mUltrasoun.setPhonenumber(zp05Xml.getPhonenumber());
            mUltrasoun.setToday(zp05Xml.getToday());

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
                zipA.crearZp05UltrasoundExam(mUltrasoun);
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
