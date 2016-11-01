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
import ni.org.ics.zip.appmovil.domain.Zp04TrimesterVisitSectionE;
import ni.org.ics.zip.appmovil.parsers.Zp04TrimesterVisitSectionEXml;
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
public class NewZp04TrimesterVisitSectionEActivity extends AbstractAsyncActivity {

    protected static final String TAG = NewZp04TrimesterVisitSectionAtoDActivity.class.getSimpleName();

    private ZipAdapter zipA;
    private static Zp04TrimesterVisitSectionE mIngreso = new Zp04TrimesterVisitSectionE();

    public static final int ADD_TAMIZAJE_ODK = 1;
    public static final int BARCODE_CAPTURE_TAM = 2;

    Dialog dialogInit;
    private SharedPreferences settings;
    private String username;
    private String mRecordId = "";
    private boolean hecho =  false;

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
        hecho = getIntent().getExtras().getBoolean(Constants.DONE);
        Zp00Screening screening = (Zp00Screening) getIntent().getExtras().getSerializable(Constants.OBJECTO);
        mRecordId = screening.getRecordId();
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
        if (hecho){
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
                    "jrFormId = 'ZP04_Trimester_Visit_E' and displayName = 'Estudio ZIP Visita Cuestionario Trimestral_E'", null, null);
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
            Zp04TrimesterVisitSectionEXml zp04Xml = new Zp04TrimesterVisitSectionEXml();
            zp04Xml = serializer.read(Zp04TrimesterVisitSectionEXml.class, source);
            mIngreso.setRecordId(mRecordId);
       //   mIngreso.setRedcapEventName(zp04Xml.get());
            mIngreso.setTri24HrsDeodorant(zp04Xml.getTri24HrsDeodorant());
            mIngreso.setTriBrandDeodorant(zp04Xml.getTriBrandDeodorant());
            mIngreso.setTriFragFreeDeodorant(zp04Xml.getTriFragFreeDeodorant());
            mIngreso.setTri24HrsHairGel(zp04Xml.getTri24HrsHairGel());
            mIngreso.setTriBrandHairGel(zp04Xml.getTriBrandHairGel());
            mIngreso.setTriFragFreeHairGel(zp04Xml.getTriFragFreeHairGel());
            mIngreso.setTri24HrsConditioner(zp04Xml.getTri24HrsConditioner());
            mIngreso.setTriBrandConditioner(zp04Xml.getTriBrandConditioner());
            mIngreso.setTriFragFreeConditioner(zp04Xml.getTriFragFreeConditioner());
            mIngreso.setTri24HrsShampoo(zp04Xml.getTri24HrsShampoo());
            mIngreso.setTriBrandShampoo(zp04Xml.getTriBrandShampoo());
            mIngreso.setTriFragFreeShampoo(zp04Xml.getTriFragFreeShampoo());
            mIngreso.setTri24HrsOtherHair(zp04Xml.getTri24HrsOtherHair());
            mIngreso.setTriBrandOtherHair(zp04Xml.getTriBrandOtherHair());
            mIngreso.setTriFragFreeOtherHair(zp04Xml.getTriFragFreeOtherHair());
            mIngreso.setTri24HrsPerfume(zp04Xml.getTri24HrsPerfume());
            mIngreso.setTriBrandPerfume(zp04Xml.getTriBrandPerfume());
            mIngreso.setTriFragFreePerfume(zp04Xml.getTriFragFreePerfume());
            mIngreso.setTri24HrsBarSoap(zp04Xml.getTri24HrsBarSoap());
            mIngreso.setTriBrandBarSoap(zp04Xml.getTriBrandBarSoap());
            mIngreso.setTriFragFreeBarSoap(zp04Xml.getTriFragFreeBarSoap());
            mIngreso.setTri24HrsLiqSoap(zp04Xml.getTri24HrsLiqSoap());
            mIngreso.setTriBrandLiqSoap(zp04Xml.getTriBrandLiqSoap());
            mIngreso.setTriFragFreeLiqSoap(zp04Xml.getTriFragFreeLiqSoap());
            mIngreso.setTri24HrsLotion(zp04Xml.getTri24HrsLotion());
            mIngreso.setTriBrandLotion(zp04Xml.getTriBrandLotion());
            mIngreso.setTriFragFreeLotion(zp04Xml.getTriFragFreeLotion());
            mIngreso.setTri24HrsShavCream(zp04Xml.getTri24HrsShavCream());
            mIngreso.setTriBrandShavCream(zp04Xml.getTriBrandShavCream());
            mIngreso.setTriFragFreeShavCream(zp04Xml.getTriFragFreeShavCream());
            mIngreso.setTri24HrsColorCos(zp04Xml.getTri24HrsColorCos());
            mIngreso.setTriBrandColorCos(zp04Xml.getTriBrandColorCos());
            mIngreso.setTriFragFreeColorCos(zp04Xml.getTriFragFreeColorCos());
            mIngreso.setTri24HrsSunscreen(zp04Xml.getTri24HrsSunscreen());
            mIngreso.setTriBrandSunscreen(zp04Xml.getTriBrandSunscreen());
            mIngreso.setTriFragFreeSunscreen(zp04Xml.getTriFragFreeSunscreen());
            mIngreso.setTri24HrsNailPol(zp04Xml.getTri24HrsNailPol());
            mIngreso.setTriBrandNailPol(zp04Xml.getTriBrandNailPol());
            mIngreso.setTriFragFreeNailPol(zp04Xml.getTriFragFreeNailPol());
            mIngreso.setTri24HrsFurnPol(zp04Xml.getTri24HrsFurnPol());
            mIngreso.setTriBrandFurnPol(zp04Xml.getTriBrandFurnPol());
            mIngreso.setTriFragFreeFurnPol(zp04Xml.getTriFragFreeFurnPol());
            mIngreso.setTri24HrsCleaners(zp04Xml.getTri24HrsCleaners());
            mIngreso.setTriBrandCleaners(zp04Xml.getTriBrandCleaners());
            mIngreso.setTriFragFreeCleaners(zp04Xml.getTriFragFreeCleaners());
            mIngreso.setTri24HrsFloorWax(zp04Xml.getTri24HrsFloorWax());
            mIngreso.setTriBrandFloorWax(zp04Xml.getTriBrandFloorWax());
            mIngreso.setTriFragFreeFloorWax(zp04Xml.getTriFragFreeFloorWax());
            mIngreso.setTri24HrsSoftnr(zp04Xml.getTri24HrsSoftnr());
            mIngreso.setTriBrandSoftnr(zp04Xml.getTriBrandSoftnr());
            mIngreso.setTriFragFreeSoftnr(zp04Xml.getTriFragFreeSoftnr());
            mIngreso.setTri24HrsLaundDet(zp04Xml.getTri24HrsLaundDet());
            mIngreso.setTriBrandLaundDet(zp04Xml.getTriBrandLaundDet());
            mIngreso.setTriFragFreeLaundDet(zp04Xml.getTriFragFreeLaundDet());
            mIngreso.setTri24HrsLaundStrch(zp04Xml.getTri24HrsLaundStrch());
            mIngreso.setTriBrandLaundStrch(zp04Xml.getTriBrandLaundStrch());
            mIngreso.setTriFragFreeLaundStrch(zp04Xml.getTriFragFreeLaundStrch());
            mIngreso.setTri24HrsPaint(zp04Xml.getTri24HrsPaint());
            mIngreso.setTriBrandPaint(zp04Xml.getTriBrandPaint());
            mIngreso.setTriFragFreePaint(zp04Xml.getTriFragFreePaint());
            mIngreso.setTri24HrsSolvents(zp04Xml.getTri24HrsSolvents());
            mIngreso.setTriBrandSolvents(zp04Xml.getTriBrandSolvents());
            mIngreso.setTriFragFreeSolvents(zp04Xml.getTriFragFreeSolvents());
            mIngreso.setTri24HrsCarWax(zp04Xml.getTri24HrsCarWax());
            mIngreso.setTriBrandCarWax(zp04Xml.getTriBrandCarWax());
            mIngreso.setTriFragFreeCarWax(zp04Xml.getTriFragFreeCarWax());
            mIngreso.setTri24HrsPest(zp04Xml.getTri24HrsPest());
            mIngreso.setTriBrandPest(zp04Xml.getTriBrandPest());
            mIngreso.setTriFragFreePest(zp04Xml.getTriFragFreePest());
            mIngreso.setTri24HrsFleaTickPrev(zp04Xml.getTri24HrsFleaTickPrev());
            mIngreso.setTriBrandFleaTickPrev(zp04Xml.getTriBrandFleaTickPrev());
            mIngreso.setTriFleaTickPrev(zp04Xml.getTriFleaTickPrev());
            mIngreso.setTri24HrsFleaTickSpry(zp04Xml.getTri24HrsFleaTickSpry());
            mIngreso.setTriBrandFleaTickSpry(zp04Xml.getTriBrandFleaTickSpry());
            mIngreso.setTriFleaTickSpry(zp04Xml.getTriFleaTickSpry());
            mIngreso.setTri24HrsCaritin(zp04Xml.getTri24HrsCaritin());
            mIngreso.setTriBrandCaritin(zp04Xml.getTriBrandCaritin());
            mIngreso.setTriFragFreeCaritin(zp04Xml.getTriFragFreeCaritin());
            mIngreso.setTri24HrsZyrtec(zp04Xml.getTri24HrsZyrtec());
            mIngreso.setTriBrandZyrtec(zp04Xml.getTriBrandZyrtec());
            mIngreso.setTriFragFreeZyrtec(zp04Xml.getTriFragFreeZyrtec());
            mIngreso.setTri24HrsVita(zp04Xml.getTri24HrsVita());
            mIngreso.setTriBrandVita(zp04Xml.getTriBrandVita());
            mIngreso.setTriFragFreeVita(zp04Xml.getTriFragFreeVita());
            mIngreso.setTri24HrsHerbal(zp04Xml.getTri24HrsHerbal());
            mIngreso.setTriBrandHerbal(zp04Xml.getTriBrandHerbal());
            mIngreso.setTriFragFreeHerbal(zp04Xml.getTriFragFreeHerbal());
            mIngreso.setTri24HrsCreatine(zp04Xml.getTri24HrsCreatine());
            mIngreso.setTriBrandCreatine(zp04Xml.getTriBrandCreatine());
            mIngreso.setTriFragFreeCreatine(zp04Xml.getTriFragFreeCreatine());
            mIngreso.setTri24HrsProtein(zp04Xml.getTri24HrsProtein());
            mIngreso.setTriBrandProtein(zp04Xml.getTriBrandProtein());
            mIngreso.setTriFragFreeProtein(zp04Xml.getTriFragFreeProtein());
            mIngreso.setTri24HrsDiuretic(zp04Xml.getTri24HrsDiuretic());
            mIngreso.setTriBrandDiuretic(zp04Xml.getTriBrandDiuretic());
            mIngreso.setTriFragFreeDiuretic(zp04Xml.getTriFragFreeDiuretic());
            mIngreso.setTri24HrsInsulin(zp04Xml.getTri24HrsInsulin());
            mIngreso.setTriBrandInsulin(zp04Xml.getTriBrandInsulin());
            mIngreso.setTriFragFreeInsulin(zp04Xml.getTriFragFreeInsulin());
            mIngreso.setTri24HrsMouthwsh(zp04Xml.getTri24HrsMouthwsh());
            mIngreso.setTriBrandMouthwsh(zp04Xml.getTriBrandMouthwsh());
            mIngreso.setTriFragFreeMouthwsh(zp04Xml.getTriFragFreeMouthwsh());
            mIngreso.setTriFragFreeOverall(zp04Xml.getTriFragFreeOverall());
            mIngreso.setTriSurgInd(zp04Xml.getTriSurgInd());
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
                zipA.crearZp04TrimesterVisitSectionE(mIngreso);
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
