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
import ni.org.ics.zip.appmovil.domain.Zp02BiospecimenCollection;
import ni.org.ics.zip.appmovil.parsers.Zp02BiospecimenCollectionXml;
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
public class NewZp02BiospecimenCollectionActivity extends AbstractAsyncActivity {

    protected static final String TAG = NewZp02BiospecimenCollectionActivity.class.getSimpleName();

    private ZipAdapter zipA;
    private static Zp02BiospecimenCollection mCollection = new Zp02BiospecimenCollection();

    public static final int ADD_TAMIZAJE_ODK = 1;

    Dialog dialogInit;
    private SharedPreferences settings;
    private String username;
    private String mRecordId = "";
    private String event;

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
        mRecordId = getIntent().getExtras().getString(Constants.RECORDID);
        mCollection = (Zp02BiospecimenCollection) getIntent().getExtras().getSerializable(Constants.OBJECTO_ZP02);
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
        if (mCollection!=null){
            message.setText(getString(R.string.edit)+ " " + getString(R.string.main_specimen));
        }
        else{
            message.setText(getString(R.string.add)+ " " + getString(R.string.main_specimen));
        }

        //add some action to the buttons

        Button yes = (Button) dialogInit.findViewById(R.id.yesnoYes);
        yes.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                dialogInit.dismiss();
                addZp02BiospecimenCollection();
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
                    parseZp02BiospecimenCollection(idInstancia, instanceFilePath);
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

    private void addZp02BiospecimenCollection() {
        try{
            //campos de proveedor de collect
            String[] projection = new String[] {
                    "_id","jrFormId","displayName"};
            //cursor que busca el formulario
            Cursor c = getContentResolver().query(Constants.CONTENT_URI, projection,
                    "jrFormId = 'ZP02A__BiospecimenCollection' and displayName = 'Estudio ZIP Formulario para recolección de muestras biológicas'", null, null);
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

    private void parseZp02BiospecimenCollection(Integer idInstancia, String instanceFilePath) {
        Serializer serializer = new Persister();
        File source = new File(instanceFilePath);
        try {
            Zp02BiospecimenCollectionXml zp02Xml = serializer.read(Zp02BiospecimenCollectionXml.class, source);
            mCollection.setRecordId(mRecordId);
            mCollection.setRedcapEventName(event);
            mCollection.setBscDov(zp02Xml.getBscDov());
            mCollection.setBscVisit(zp02Xml.getBscVisit());
            mCollection.setBscMatBldCol(zp02Xml.getBscMatBldCol());
            mCollection.setBscMatBldRsn(zp02Xml.getBscMatBldRsn());
            mCollection.setBscMatBldSpecify(zp02Xml.getBscMatBldSpecify());
            mCollection.setBscMatBldTyp1(zp02Xml.getBscMatBldTyp1());
            mCollection.setBscMatBldId1(zp02Xml.getBscMatBldId1());
            mCollection.setBscMatBldVol1(zp02Xml.getBscMatBldVol1());
            mCollection.setBscMatBldTyp2(zp02Xml.getBscMatBldTyp2());
            mCollection.setBscMatBldId2(zp02Xml.getBscMatBldId2());
            mCollection.setBscMatBldVol2(zp02Xml.getBscMatBldVol2());
            mCollection.setBscMatBldTyp3(zp02Xml.getBscMatBldTyp3());
            mCollection.setBscMatBldId3(zp02Xml.getBscMatBldId3());
            mCollection.setBscMatBldVol3(zp02Xml.getBscMatBldVol3());
            mCollection.setBscMatBldTyp4(zp02Xml.getBscMatBldTyp4());
            mCollection.setBscMatBldId4(zp02Xml.getBscMatBldId4());
            mCollection.setBscMatBldVol4(zp02Xml.getBscMatBldVol4());
            mCollection.setBscMatBldTyp5(zp02Xml.getBscMatBldTyp5());
            mCollection.setBscMatBldId5(zp02Xml.getBscMatBldId5());
            mCollection.setBscMatBldVol5(zp02Xml.getBscMatBldVol5());
            mCollection.setBscMatBldTyp6(zp02Xml.getBscMatBldTyp6());
            mCollection.setBscMatBldId6(zp02Xml.getBscMatBldId6());
            mCollection.setBscMatBldVol6(zp02Xml.getBscMatBldVol6());
            mCollection.setBscMatBldTyp7(zp02Xml.getBscMatBldTyp7());
            mCollection.setBscMatBldId7(zp02Xml.getBscMatBldId7());
            mCollection.setBscMatBldVol7(zp02Xml.getBscMatBldVol7());
            mCollection.setBscMatBldTyp8(zp02Xml.getBscMatBldTyp8());
            mCollection.setBscMatBldId8(zp02Xml.getBscMatBldId8());
            mCollection.setBscMatBldVol8(zp02Xml.getBscMatBldVol8());
            mCollection.setBscMatBldTotVol(zp02Xml.getBscMatBldTotVol());
            mCollection.setBscMatBldTime(zp02Xml.getBscMatBldTime());
            mCollection.setBscMatBldCom(zp02Xml.getBscMatBldCom());
            mCollection.setBscMatSlvaCol(zp02Xml.getBscMatSlvaCol());
            mCollection.setBscMatSlvaRsn(zp02Xml.getBscMatSlvaRsn());
            mCollection.setBscMatSlvaSpecify(zp02Xml.getBscMatSlvaSpecify());
            mCollection.setBscMatSlvaId(zp02Xml.getBscMatSlvaId());
            mCollection.setBscMatSlvaTime(zp02Xml.getBscMatSlvaTime());
            mCollection.setBscMatSlvaCom(zp02Xml.getBscMatSlvaCom());
            mCollection.setBscMatVagCol(zp02Xml.getBscMatVagCol());
            mCollection.setBscMatVagRsn(zp02Xml.getBscMatVagRsn());
            mCollection.setBscMatVagSpecify(zp02Xml.getBscMatVagSpecify());
            mCollection.setBscMatVagId(zp02Xml.getBscMatVagId());
            mCollection.setBscMatVagTime(zp02Xml.getBscMatVagTime());
            mCollection.setBscMatVagCom(zp02Xml.getBscMatVagCom());
            mCollection.setBscMatVstUrnCol(zp02Xml.getBscMatVstUrnCol());
            mCollection.setBscMatVstUrnRsn(zp02Xml.getBscMatVstUrnRsn());
            mCollection.setBscMatVstUrnSpecify(zp02Xml.getBscMatVstUrnSpecify());
            mCollection.setBscMatVstUrnId(zp02Xml.getBscMatVstUrnId());
            mCollection.setBscMatVstUrnTime(zp02Xml.getBscMatVstUrnTime());
            mCollection.setBscMatVstUrnCom(zp02Xml.getBscMatVstUrnCom());
            mCollection.setBscMatHomUrnCol(zp02Xml.getBscMatHomUrnCol());
            mCollection.setBscMatHomUrnRsn(zp02Xml.getBscMatHomUrnRsn());
            mCollection.setBscMatHomUrnSpecify(zp02Xml.getBscMatHomUrnSpecify());
            mCollection.setBscMatHomUrnNum(zp02Xml.getBscMatHomUrnNum());
            mCollection.setBscMatHomUrnId1(zp02Xml.getBscMatHomUrnId1());
            mCollection.setBscMatHomUrnDat1(zp02Xml.getBscMatHomUrnDat1());
            mCollection.setBscMatHomUrnTime1(zp02Xml.getBscMatHomUrnTime1());
            mCollection.setBscMatHomUrnCom1(zp02Xml.getBscMatHomUrnCom1());
            mCollection.setBscMatHomUrnId2(zp02Xml.getBscMatHomUrnId2());
            mCollection.setBscMatHomUrnDat2(zp02Xml.getBscMatHomUrnDat2());
            mCollection.setBscMatHomUrnTime2(zp02Xml.getBscMatHomUrnTime2());
            mCollection.setBscMatHomUrnCom2(zp02Xml.getBscMatHomUrnCom2());
            mCollection.setBscMatHomUrnId3(zp02Xml.getBscMatHomUrnId3());
            mCollection.setBscMatHomUrnDat3(zp02Xml.getBscMatHomUrnDat3());
            mCollection.setBscMatHomUrnTime3(zp02Xml.getBscMatHomUrnTime3());
            mCollection.setBscMatHomUrnCom3(zp02Xml.getBscMatHomUrnCom3());
            mCollection.setBscMatHomUrnId4(zp02Xml.getBscMatHomUrnId4());
            mCollection.setBscMatHomUrnDat4(zp02Xml.getBscMatHomUrnDat4());
            mCollection.setBscMatHomUrnTime4(zp02Xml.getBscMatHomUrnTime4());
            mCollection.setBscMatHomUrnCom4(zp02Xml.getBscMatHomUrnCom4());
            mCollection.setBscMatOtherCol(zp02Xml.getBscMatOtherCol());
            mCollection.setBscMatOtherType(zp02Xml.getBscMatOtherType()); //multiple
            mCollection.setBscMatOtherTypeSpecify(zp02Xml.getBscMatOtherTypeSpecify());
            mCollection.setBscMatAmfCol(zp02Xml.getBscMatAmfCol());
            mCollection.setBscMatAmfRsn(zp02Xml.getBscMatAmfRsn());
            mCollection.setBscMatAmfSpecify(zp02Xml.getBscMatAmfSpecify());
            mCollection.setBscMatAmfId(zp02Xml.getBscMatAmfId());
            mCollection.setBscMatAmfAmount(zp02Xml.getBscMatAmfAmount());
            mCollection.setBscMatAmfTime(zp02Xml.getBscMatAmfTime());
            mCollection.setBscMatAmfCom(zp02Xml.getBscMatAmfCom());
            mCollection.setBscMatCordCol(zp02Xml.getBscMatCordCol());
            mCollection.setBscMatCordRsn(zp02Xml.getBscMatCordRsn());
            mCollection.setBscMatCordSpecify(zp02Xml.getBscMatCordSpecify());
            mCollection.setBscMatCordId(zp02Xml.getBscMatCordId());
            mCollection.setBscMatCordTime(zp02Xml.getBscMatCordTime());
            mCollection.setBscMatCordAmount(zp02Xml.getBscMatCordAmount());
            mCollection.setBscMatCordCom(zp02Xml.getBscMatCordCom());
            mCollection.setBscMatPlacenCol(zp02Xml.getBscMatPlacenCol());
            mCollection.setBscMatPlacenRsn(zp02Xml.getBscMatPlacenRsn());
            mCollection.setBscMatPlacenSpecify(zp02Xml.getBscMatPlacenSpecify());
            mCollection.setBscMatPlacenCircum(zp02Xml.getBscMatPlacenCircum());
            mCollection.setBscMatPlacenId(zp02Xml.getBscMatPlacenId());
            mCollection.setBscMatPlacenTime(zp02Xml.getBscMatPlacenTime());
            mCollection.setBscMatPlacenCom(zp02Xml.getBscMatPlacenCom());
            mCollection.setBscMatBreastmCol(zp02Xml.getBscMatBreastmCol());
            mCollection.setBscMatBreastmRsn(zp02Xml.getBscMatBreastmRsn());
            mCollection.setBscMatBreastmSpecify(zp02Xml.getBscMatBreastmSpecify());
            mCollection.setBscMatBreastmId(zp02Xml.getBscMatBreastmId());
            mCollection.setBscMatBreastmTime(zp02Xml.getBscMatBreastmTime());
            mCollection.setBscMatBreastmAmount(zp02Xml.getBscMatBreastmAmount());
            mCollection.setBscMatBreastmCom(zp02Xml.getBscMatBreastmCom());
            mCollection.setBscMatMiscarr(zp02Xml.getBscMatMiscarr());
            mCollection.setBscMatFetaltCol(zp02Xml.getBscMatFetaltCol());
            mCollection.setBscMatFetaltId(zp02Xml.getBscMatFetaltId());
            mCollection.setBscMatFetaltTime(zp02Xml.getBscMatFetaltTime());
            mCollection.setBscMatFetaltCom(zp02Xml.getBscMatFetaltCom());
            mCollection.setBscMatdBreastmCol(zp02Xml.getBscMatdBreastmCol());
            mCollection.setBscMatdBreastmRsn(zp02Xml.getBscMatdBreastmRsn());
            mCollection.setBscMatdBreastmSpecify(zp02Xml.getBscMatdBreastmSpecify());
            mCollection.setBscMatdBreastmId(zp02Xml.getBscMatdBreastmId());
            mCollection.setBscMatdBreastmTime(zp02Xml.getBscMatdBreastmTime());
            mCollection.setBscMatdBreastmAmou(zp02Xml.getBscMatdBreastmAmou());
            mCollection.setBscMatdBreastmCom(zp02Xml.getBscMatdBreastmCom());
            mCollection.setBscPerson1(username);
            mCollection.setBscCompleteDate1(new Date());
            mCollection.setBscPerson2(username);
            mCollection.setBscCompleteDate2(new Date());
            mCollection.setBscPerson3(username);
            mCollection.setBscCompleteDate3(new Date());

            mCollection.setRecordDate(new Date());
            mCollection.setRecordUser(username);
            mCollection.setIdInstancia(idInstancia);
            mCollection.setInstancePath(instanceFilePath);
            mCollection.setEstado(Constants.STATUS_NOT_SUBMITTED);
            mCollection.setStart(zp02Xml.getStart());
            mCollection.setEnd(zp02Xml.getEnd());
            mCollection.setDeviceid(zp02Xml.getDeviceid());
            mCollection.setSimserial(zp02Xml.getSimserial());
            mCollection.setPhonenumber(zp02Xml.getPhonenumber());
            mCollection.setToday(zp02Xml.getToday());

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
                zipA.crearZp02BiospecimenCollection(mCollection);
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
