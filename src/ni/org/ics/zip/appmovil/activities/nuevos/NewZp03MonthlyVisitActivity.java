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
import ni.org.ics.zip.appmovil.domain.Zp03MonthlyVisit;
import ni.org.ics.zip.appmovil.parsers.Zp03MonthlyVisitXml;
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
public class NewZp03MonthlyVisitActivity extends AbstractAsyncActivity {

    protected static final String TAG = NewZp03MonthlyVisitActivity.class.getSimpleName();

    private ZipAdapter zipA;
    private static Zp03MonthlyVisit mVisit = new Zp03MonthlyVisit();

    public static final int ADD_TAMIZAJE_ODK = 1;

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
                addZp03MonthlyVisit();
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
                    parseZp03MonthlyVisit(idInstancia, instanceFilePath);
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

    private void addZp03MonthlyVisit() {
        try{
            //campos de proveedor de collect
            String[] projection = new String[] {
                    "_id","jrFormId","displayName"};
            //cursor que busca el formulario
            Cursor c = getContentResolver().query(Constants.CONTENT_URI, projection,
                    "jrFormId = 'ZP03_Monthly_Visit' and displayName = 'Estudio ZIP Visita Cuestionario Mensual'", null, null);
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

    private void parseZp03MonthlyVisit(Integer idInstancia, String instanceFilePath) {
        Serializer serializer = new Persister();
        File source = new File(instanceFilePath);
        try {
            Zp03MonthlyVisitXml zp03Xml = serializer.read(Zp03MonthlyVisitXml.class, source);
            mVisit.setRecordId(mRecordId);
            mVisit.setRedcapEventName("XXXX");
            mVisit.setMonVisitDate(zp03Xml.getMonVisitDate());
            mVisit.setMonVisitType(zp03Xml.getMonVisitType());
            mVisit.setMonReasonMissed(zp03Xml.getMonReasonMissed());
            mVisit.setMonOtherReschedu(zp03Xml.getMonOtherReschedu());
            mVisit.setMonReasonUnscheduled(zp03Xml.getMonReasonUnscheduled());
            mVisit.setMonOtherUnscheduled(zp03Xml.getMonOtherUnscheduled());
            mVisit.setMonMotherWt(zp03Xml.getMonMotherWt());
            mVisit.setMonWtUnit(zp03Xml.getMonWtUnit());
            mVisit.setMonSystolic(zp03Xml.getMonSystolic());
            mVisit.setMonDiastolic(zp03Xml.getMonDiastolic());
            mVisit.setMonTemperature(zp03Xml.getMonTemperature());
            mVisit.setMonTempUnit(zp03Xml.getMonTempUnit());
            mVisit.setMonPregnancyLoss(zp03Xml.getMonPregnancyLoss());
            mVisit.setMonDateLoss(zp03Xml.getMonDateLoss());
            mVisit.setMonPersisHeadache(zp03Xml.getMonPersisHeadache());
            mVisit.setMonDizziness(zp03Xml.getMonDizziness());
            mVisit.setMonNausea(zp03Xml.getMonNausea());
            mVisit.setMonVomiting(zp03Xml.getMonVomiting());
            mVisit.setMonLights(zp03Xml.getMonLights());
            mVisit.setMonLightsSpecify(zp03Xml.getMonLightsSpecify());
            mVisit.setMonSwelling(zp03Xml.getMonSwelling());
            mVisit.setMonFetalMovement(zp03Xml.getMonFetalMovement());
            mVisit.setMonMoveUsual(zp03Xml.getMonMoveUsual());
            mVisit.setMonMoveDecrease(zp03Xml.getMonMoveDecrease());
            mVisit.setMonContractions(zp03Xml.getMonContractions());
            mVisit.setMonContractWeek(zp03Xml.getMonContractWeek());
            mVisit.setMonContractDay(zp03Xml.getMonContractDay());
            mVisit.setMonContractHour(zp03Xml.getMonContractHour());
            mVisit.setMonContract10min(zp03Xml.getMonContract10min());
            mVisit.setMonVaginalDischarge(zp03Xml.getMonVaginalDischarge());
            mVisit.setMonCharacDischarge(zp03Xml.getMonCharacDischarge());
            mVisit.setMonBleeding(zp03Xml.getMonBleeding());
            mVisit.setMonBleedingCharac(zp03Xml.getMonBleedingCharac());
            mVisit.setMonUtiTold(zp03Xml.getMonUtiTold());
            mVisit.setMonPrenatalDay(zp03Xml.getMonPrenatalDay());
            mVisit.setMonPrenatalMonth(zp03Xml.getMonPrenatalMonth());
            mVisit.setMonPrenatalYear(zp03Xml.getMonPrenatalYear());
            mVisit.setMonFeverSymptom(zp03Xml.getMonFeverSymptom());
            mVisit.setMonRash(zp03Xml.getMonRash());
            mVisit.setMonItch(zp03Xml.getMonItch());
            mVisit.setMonRashFirst(zp03Xml.getMonRashFirst());
            mVisit.setMonRashDay(zp03Xml.getMonRashDay());
            mVisit.setMonRashMonth(zp03Xml.getMonRashMonth());
            mVisit.setMonRashYear(zp03Xml.getMonRashYear());
            mVisit.setMonRashDuration(zp03Xml.getMonRashDuration());
            mVisit.setMonRashSpread(zp03Xml.getMonRashSpread());
            mVisit.setMonSpreadPart(zp03Xml.getMonSpreadPart());
            mVisit.setMonFeverExperience(zp03Xml.getMonFeverExperience());
            mVisit.setMonTempMeasure(zp03Xml.getMonTempMeasure());
            mVisit.setMonHighTemp(zp03Xml.getMonHighTemp());
            mVisit.setMonHightemUnit(zp03Xml.getMonHightemUnit());
            mVisit.setMonTempunknown(zp03Xml.getMonTempunknown());
            mVisit.setMonFeverDay(zp03Xml.getMonFeverDay());
            mVisit.setMonFeverMonth(zp03Xml.getMonFeverMonth());
            mVisit.setMonFeverYear(zp03Xml.getMonFeverYear());
            mVisit.setMonFeverDuration(zp03Xml.getMonFeverDuration());
            mVisit.setMonRedeyes(zp03Xml.getMonRedeyes());
            mVisit.setMonRedeyesDay(zp03Xml.getMonRedeyesDay());
            mVisit.setMonRedeyesMonth(zp03Xml.getMonRedeyesMonth());
            mVisit.setMonRedeyesYear(zp03Xml.getMonRedeyesYear());
            mVisit.setMonRedeyesDuration(zp03Xml.getMonRedeyesDuration());
            mVisit.setMonJoint(zp03Xml.getMonJoint());
            mVisit.setMonJointDay(zp03Xml.getMonJointDay());
            mVisit.setMonJointMonth(zp03Xml.getMonJointMonth());
            mVisit.setMonJointYear(zp03Xml.getMonJointYear());
            mVisit.setMonJointDuration(zp03Xml.getMonJointDuration());
            mVisit.setMonHeadache(zp03Xml.getMonHeadache());
            mVisit.setMonHeadacheDay(zp03Xml.getMonHeadacheDay());
            mVisit.setMonHeadacheMonth(zp03Xml.getMonHeadacheMonth());
            mVisit.setMonHeadacheYear(zp03Xml.getMonHeadacheYear());
            mVisit.setMonHeadacheDuration(zp03Xml.getMonHeadacheDuration());
            mVisit.setMonSymptomOther(zp03Xml.getMonSymptomOther());
            mVisit.setMonSpecifySymptom(zp03Xml.getMonSpecifySymptom());
            mVisit.setMonOtherSymptom(zp03Xml.getMonOtherSymptom());
            mVisit.setMonMedicare(zp03Xml.getMonMedicare());
            mVisit.setMonCareDay(zp03Xml.getMonCareDay());
            mVisit.setMonCareMonth(zp03Xml.getMonCareMonth());
            mVisit.setMonCareYear(zp03Xml.getMonCareYear());
            mVisit.setMonCareFacility(zp03Xml.getMonCareFacility());
            mVisit.setMonHospitalized(zp03Xml.getMonHospitalized());
            mVisit.setMonHospital(zp03Xml.getMonHospital());
            mVisit.setMonDiagRubella(zp03Xml.getMonDiagRubella());
            mVisit.setMonDiagDengue(zp03Xml.getMonDiagDengue());
            mVisit.setMonDiagChikung(zp03Xml.getMonDiagChikung());
            mVisit.setMonDiagZika(zp03Xml.getMonDiagZika());
            mVisit.setMonDiagCytome(zp03Xml.getMonDiagCytome());
            mVisit.setMonMedicine(zp03Xml.getMonMedicine());
            mVisit.setMonMedcineName(zp03Xml.getMonMedcineName());
            mVisit.setMonSymptomDiary(zp03Xml.getMonSymptomDiary());
            mVisit.setMonGuillainbarre(zp03Xml.getMonGuillainbarre());
            mVisit.setMonTingling(zp03Xml.getMonTingling());
            mVisit.setMonTinglingArm(zp03Xml.getMonTinglingArm());
            mVisit.setMonTinglingLeg(zp03Xml.getMonTinglingLeg());
            mVisit.setMonTinglingHand(zp03Xml.getMonTinglingHand());
            mVisit.setMonTinglingFoot(zp03Xml.getMonTinglingFoot());
            mVisit.setMonTinglingFace(zp03Xml.getMonTinglingFace());
            mVisit.setMonTinglingOther(zp03Xml.getMonTinglingOther());
            mVisit.setMonSensationMin(zp03Xml.getMonSensationMin());
            mVisit.setMonSensationHr(zp03Xml.getMonSensationHr());
            mVisit.setMonSenstaionDay(zp03Xml.getMonSenstaionDay());
            mVisit.setMonInjury(zp03Xml.getMonInjury());
            mVisit.setMonTinglingDay(zp03Xml.getMonTinglingDay());
            mVisit.setMonTinglingMonth(zp03Xml.getMonTinglingMonth());
            mVisit.setMonTinglingYear(zp03Xml.getMonTinglingYear());
            mVisit.setMonTinglingDuration(zp03Xml.getMonTinglingDuration());
            mVisit.setMonNumbness(zp03Xml.getMonNumbness());
            mVisit.setMonNumbArm(zp03Xml.getMonNumbArm());
            mVisit.setMonNumbLeg(zp03Xml.getMonNumbLeg());
            mVisit.setMonNumbHand(zp03Xml.getMonNumbHand());
            mVisit.setMonNumbFoot(zp03Xml.getMonNumbFoot());
            mVisit.setMonNumbFace(zp03Xml.getMonNumbFace());
            mVisit.setMonNumbOther(zp03Xml.getMonNumbOther());
            mVisit.setMonNumbDay(zp03Xml.getMonNumbDay());
            mVisit.setMonNumbMonth(zp03Xml.getMonNumbMonth());
            mVisit.setMonNumbYear(zp03Xml.getMonNumbYear());
            mVisit.setMonNumbDuration(zp03Xml.getMonNumbDuration());
            mVisit.setMonParalysis(zp03Xml.getMonParalysis());
            mVisit.setMonParaArm(zp03Xml.getMonParaArm());
            mVisit.setMonParaLeg(zp03Xml.getMonParaLeg());
            mVisit.setMonParaHand(zp03Xml.getMonParaHand());
            mVisit.setMonParaFoot(zp03Xml.getMonParaFoot());
            mVisit.setMonParaFace(zp03Xml.getMonParaFace());
            mVisit.setMonParaOther(zp03Xml.getMonParaOther());
            mVisit.setMonParaDay(zp03Xml.getMonParaDay());
            mVisit.setMonParaMonth(zp03Xml.getMonParaMonth());
            mVisit.setMonParaYear(zp03Xml.getMonParaYear());
            mVisit.setMonParaDuration(zp03Xml.getMonParaDuration());
            mVisit.setMonResultsProvided(zp03Xml.getMonResultsProvided());
            mVisit.setMonCounseling(zp03Xml.getMonCounseling());
            mVisit.setMonResultsOther(zp03Xml.getMonResultsOther());
            mVisit.setMonOneweekDate(zp03Xml.getMonOneweekDate());
            mVisit.setMonOneweekTime(zp03Xml.getMonOneweekTime());
            mVisit.setMonProvideSym(zp03Xml.getMonProvideSym());
            mVisit.setMonReminderPreg(zp03Xml.getMonReminderPreg());
            mVisit.setMonReminderProvided(zp03Xml.getMonReminderProvided());
            mVisit.setMonNextDate(zp03Xml.getMonNextDate());
            mVisit.setMonNextTime(zp03Xml.getMonNextTime());
            mVisit.setMonIdCompleting(username);
            mVisit.setMonDateCompleted(new Date());
            mVisit.setMonIdReviewer(username);
            mVisit.setMonDateReviewed(new Date());
            mVisit.setMonIdDataEntry(username);
            mVisit.setMonDateEntered(new Date());

            mVisit.setRecordDate(new Date());
            mVisit.setRecordUser(username);
            mVisit.setIdInstancia(idInstancia);
            mVisit.setInstancePath(instanceFilePath);
            mVisit.setEstado(Constants.STATUS_NOT_SUBMITTED);
            mVisit.setStart(zp03Xml.getStart());
            mVisit.setEnd(zp03Xml.getEnd());
            mVisit.setDeviceid(zp03Xml.getDeviceid());
            mVisit.setSimserial(zp03Xml.getSimserial());
            mVisit.setPhonenumber(zp03Xml.getPhonenumber());
            mVisit.setToday(zp03Xml.getToday());

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
                zipA.crearZp03MonthlyVisit(mVisit);
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
