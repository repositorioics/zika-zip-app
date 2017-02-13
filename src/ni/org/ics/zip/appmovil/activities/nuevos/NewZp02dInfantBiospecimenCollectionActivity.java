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
import ni.org.ics.zip.appmovil.domain.Zp02dInfantBiospecimenCollection;
import ni.org.ics.zip.appmovil.parsers.Zp02dInfantBiospecimenCollectionXml;
import ni.org.ics.zip.appmovil.preferences.PreferencesActivity;
import ni.org.ics.zip.appmovil.utils.Constants;
import ni.org.ics.zip.appmovil.utils.FileUtils;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.util.Date;

/**
 * Created by FIRSTICT on 2/2/2017.
 * V1.0
 */
public class NewZp02dInfantBiospecimenCollectionActivity extends AbstractAsyncActivity {

    protected static final String TAG = NewZp02dInfantBiospecimenCollectionActivity.class.getSimpleName();

    private ZipAdapter zipA;
    private static Zp02dInfantBiospecimenCollection mInfantBioCollection = null;

    public static final int ADD_ZP02D_ODK = 1;
    public static final int EDIT_ZP02D_ODK = 2;

    Dialog dialogInit;
    private SharedPreferences settings;
    private String username;
    private String mRecordId = "";
    private Integer accion = 0;
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
        zipA = new ZipAdapter(this.getApplicationContext(),mPass,false,false);
        mRecordId = getIntent().getExtras().getString(Constants.RECORDID);
        mInfantBioCollection = (Zp02dInfantBiospecimenCollection) getIntent().getExtras().getSerializable(Constants.OBJECTO_ZP02D);
        event = getIntent().getExtras().getString(Constants.EVENT);
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
        if (mInfantBioCollection !=null){
            message.setText(getString(R.string.edit)+ " " + getString(R.string.infant_b_2)+"?");
        }
        else{
            message.setText(getString(R.string.add)+ " " + getString(R.string.infant_b_2)+"?");
        }

        //add some action to the buttons

        Button yes = (Button) dialogInit.findViewById(R.id.yesnoYes);
        yes.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                dialogInit.dismiss();
                addZp02dInfantBiospecimenCollection();
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
        if(requestCode == ADD_ZP02D_ODK ||requestCode == EDIT_ZP02D_ODK) {
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
                    parseZp02dInfantBiospecimenCollection(idInstancia, instanceFilePath, accion);
                }
                else{
                    Toast.makeText(getApplicationContext(),	getString(R.string.err_not_completed), Toast.LENGTH_LONG).show();
                }
            }
            else{
            	finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    private void addZp02dInfantBiospecimenCollection() {
        try{
            Uri formUri;
            if(mInfantBioCollection ==null){
                //campos de proveedor de collect
                String[] projection = new String[] {
                        "_id","jrFormId","displayName"};
                //cursor que busca el formulario
                Cursor c = getContentResolver().query(Constants.CONTENT_URI, projection,
                        "jrFormId = 'zp02d_infant_biospecimen_collection' and displayName = 'Formulario para recoleccion de muestras biologicas - infantes'", null, null);
                c.moveToFirst();
                //captura el id del formulario
                Integer id = Integer.parseInt(c.getString(0));
                //cierra el cursor
                if (c != null) {
                    c.close();
                }
                //forma el uri para ODK Collect
                formUri = ContentUris.withAppendedId(Constants.CONTENT_URI, id);
                accion = ADD_ZP02D_ODK;
            }
            else{
                //forma el uri para la instancia en ODK Collect
                Integer id = mInfantBioCollection.getIdInstancia();
                formUri = ContentUris.withAppendedId(Constants.CONTENT_URI_I,id);
                accion = EDIT_ZP02D_ODK;
            }
            //Arranca la actividad ODK Collect en busca de resultado
            Intent odkA =  new Intent(Intent.ACTION_EDIT,formUri);
            startActivityForResult(odkA, accion);
        }
        catch(Exception e){
            //No existe el formulario en el equipo
            Log.e(TAG, e.getMessage(), e);
            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void parseZp02dInfantBiospecimenCollection(Integer idInstancia, String instanceFilePath, Integer accion) {
        Serializer serializer = new Persister();
        File source = new File(instanceFilePath);
        try {
            Zp02dInfantBiospecimenCollectionXml zp02dXml = serializer.read(Zp02dInfantBiospecimenCollectionXml.class, source);
            if (accion== ADD_ZP02D_ODK) mInfantBioCollection = new Zp02dInfantBiospecimenCollection();
            mInfantBioCollection.setRecordId(mRecordId);
            mInfantBioCollection.setRedcapEventName(event);

            mInfantBioCollection.setInfantDov(zp02dXml.getInfantDov());
            mInfantBioCollection.setWhomAddtVisit(zp02dXml.getWhomAddtVisit());
            mInfantBioCollection.setInfantAddtVisit(zp02dXml.getInfantAddtVisit());
            mInfantBioCollection.setInfantAddtVisitOther(zp02dXml.getInfantAddtVisitOther());
            mInfantBioCollection.setInfantMatBldCol(zp02dXml.getInfantMatBldCol());
            mInfantBioCollection.setInfantMatBldRsn(zp02dXml.getInfantMatBldRsn());
            mInfantBioCollection.setInfantMatBldSpecify(zp02dXml.getInfantMatBldSpecify());
            mInfantBioCollection.setInfantMatBldTyp1(zp02dXml.getInfantMatBldTyp1());
            mInfantBioCollection.setInfantMatBldId1(zp02dXml.getInfantMatBldId1());
            mInfantBioCollection.setInfantMatBldVol1(zp02dXml.getInfantMatBldVol1());
            mInfantBioCollection.setInfantMatBldTyp2(zp02dXml.getInfantMatBldTyp2());
            mInfantBioCollection.setInfantMatBldId2(zp02dXml.getInfantMatBldId2());
            mInfantBioCollection.setInfantMatBldVol2(zp02dXml.getInfantMatBldVol2());
            mInfantBioCollection.setInfantMatBldTyp3(zp02dXml.getInfantMatBldTyp3());
            mInfantBioCollection.setInfantMatBldId3(zp02dXml.getInfantMatBldId3());
            mInfantBioCollection.setInfantMatBldVol3(zp02dXml.getInfantMatBldVol3());
            mInfantBioCollection.setInfantMatBldTyp4(zp02dXml.getInfantMatBldTyp4());
            mInfantBioCollection.setInfantMatBldId4(zp02dXml.getInfantMatBldId4());
            mInfantBioCollection.setInfantMatBldVol4(zp02dXml.getInfantMatBldVol4());
            mInfantBioCollection.setInfantMatBldTyp5(zp02dXml.getInfantMatBldTyp5());
            mInfantBioCollection.setInfantMatBldId5(zp02dXml.getInfantMatBldId5());
            mInfantBioCollection.setInfantMatBldVol5(zp02dXml.getInfantMatBldVol5());
            mInfantBioCollection.setInfantMatBldTyp6(zp02dXml.getInfantMatBldTyp6());
            mInfantBioCollection.setInfantMatBldId6(zp02dXml.getInfantMatBldId6());
            mInfantBioCollection.setInfantMatBldVol6(zp02dXml.getInfantMatBldVol6());
            mInfantBioCollection.setInfantMatBldTyp7(zp02dXml.getInfantMatBldTyp7());
            mInfantBioCollection.setInfantMatBldId7(zp02dXml.getInfantMatBldId7());
            mInfantBioCollection.setInfantMatBldVol7(zp02dXml.getInfantMatBldVol7());
            mInfantBioCollection.setInfantMatBldTyp8(zp02dXml.getInfantMatBldTyp8());
            mInfantBioCollection.setInfantMatBldId8(zp02dXml.getInfantMatBldId8());
            mInfantBioCollection.setInfantMatBldVol8(zp02dXml.getInfantMatBldVol8());
            mInfantBioCollection.setInfantMatBldTotVol(zp02dXml.getInfantMatBldTotVol());
            mInfantBioCollection.setInfantMatBldTime(zp02dXml.getInfantMatBldTime());
            mInfantBioCollection.setInfantMatBldCom(zp02dXml.getInfantMatBldCom());
            mInfantBioCollection.setInfantMatSlvaCol(zp02dXml.getInfantMatSlvaCol());
            mInfantBioCollection.setInfantMatSlvaRsn(zp02dXml.getInfantMatSlvaRsn());
            mInfantBioCollection.setInfantMatSlvaSpecify(zp02dXml.getInfantMatSlvaSpecify());
            mInfantBioCollection.setInfantMatSlvaId(zp02dXml.getInfantMatSlvaId());
            mInfantBioCollection.setInfantMatSlvaTime(zp02dXml.getInfantMatSlvaTime());
            mInfantBioCollection.setInfantMatSlvaCom(zp02dXml.getInfantMatSlvaCom());
            mInfantBioCollection.setInfantMatVstUrnCol(zp02dXml.getInfantMatVstUrnCol());
            mInfantBioCollection.setInfantMatVstUrnRsn(zp02dXml.getInfantMatVstUrnRsn());
            mInfantBioCollection.setInfantMatVstUrnSpecify(zp02dXml.getInfantMatVstUrnSpecify());
            mInfantBioCollection.setInfantMatVstUrnId(zp02dXml.getInfantMatVstUrnId());
            mInfantBioCollection.setInfantMatVstUrnTime(zp02dXml.getInfantMatVstUrnTime());
            mInfantBioCollection.setInfantMatVstUrnCom(zp02dXml.getInfantMatVstUrnCom());
            mInfantBioCollection.setInfantPerson1(zp02dXml.getInfantPerson1());
            mInfantBioCollection.setInfantCompleteDate1(zp02dXml.getInfantCompleteDate1());
            mInfantBioCollection.setInfantPerson2(zp02dXml.getInfantPerson2());
            mInfantBioCollection.setInfantCompleteDate2(zp02dXml.getInfantCompleteDate2());
            mInfantBioCollection.setInfantPerson3(zp02dXml.getInfantPerson3());
            mInfantBioCollection.setInfantCompleteDate3(zp02dXml.getInfantCompleteDate3());

            mInfantBioCollection.setRecordDate(new Date());
            mInfantBioCollection.setRecordUser(username);
            mInfantBioCollection.setIdInstancia(idInstancia);
            mInfantBioCollection.setInstancePath(instanceFilePath);
            mInfantBioCollection.setEstado(Constants.STATUS_NOT_SUBMITTED);
            mInfantBioCollection.setStart(zp02dXml.getStart());
            mInfantBioCollection.setEnd(zp02dXml.getEnd());
            mInfantBioCollection.setDeviceid(zp02dXml.getDeviceid());
            mInfantBioCollection.setSimserial(zp02dXml.getSimserial());
            mInfantBioCollection.setPhonenumber(zp02dXml.getPhonenumber());
            mInfantBioCollection.setToday(zp02dXml.getToday());

            new SaveDataTask().execute(accion);

        } catch (Exception e) {
            // Presenta el error al parsear el xml
            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
            finish();
        }
    }

    // ***************************************
    // Private classes
    // ***************************************
    private class SaveDataTask extends AsyncTask<Integer, Void, String> {
        private Integer accionaRealizar = null;
        @Override
        protected void onPreExecute() {
            // before the request begins, show a progress indicator
            showLoadingProgressDialog();
        }

        @Override
        protected String doInBackground(Integer... values) {
            try {
                accionaRealizar = values[0];
                try {
                    zipA.open();
                    if (accionaRealizar == ADD_ZP02D_ODK){
                        zipA.crearZp02dInfantBiospecimenCollection(mInfantBioCollection);
                    }
                    else{
                        zipA.editarZp02dInfantBiospecimenCollection(mInfantBioCollection);
                    }
                    zipA.close();
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage(), e);
                    return "error";
                }
                return "exito";
            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
                return "error";
            }
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
        finish();
    }
}
