package ni.org.ics.zip.appmovil.activities.nuevos;

import android.app.Dialog;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.*;
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
    private static Zp02BiospecimenCollection mZp02 = new Zp02BiospecimenCollection();

    public static final int ADD_ZP02_ODK = 1;
    public static final int EDIT_ZP02_ODK = 2;

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
            Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.error ) + "," + getString(R.string.storage_error), Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
        settings =
                PreferenceManager.getDefaultSharedPreferences(this);
        username =
                settings.getString(PreferencesActivity.KEY_USERNAME,
                        null);
        String mPass = ((MyZipApplication) this.getApplication()).getPassApp();
        zipA = new ZipAdapter(this.getApplicationContext(), mPass, false, false);
        mRecordId = getIntent().getExtras().getString(Constants.RECORDID);
        event = getIntent().getExtras().getString(Constants.EVENT);
        mZp02 = (Zp02BiospecimenCollection) getIntent().getExtras().getSerializable(Constants.OBJECTO_ZP02);
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
        TextView message = (TextView) dialogInit.findViewById(R.id.yesnotext);
        if (mZp02 != null) {
            message.setText(getString(R.string.edit) + " " + getString(R.string.maternal_b_4) + "?");
        } else {
            message.setText(getString(R.string.add) + " " + getString(R.string.maternal_b_4) + "?");
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
        if (item.getItemId() == R.id.MENU_BACK) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.MENU_HOME) {
            Intent i = new Intent(getApplicationContext(),
                    MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if (requestCode == ADD_ZP02_ODK || requestCode == EDIT_ZP02_ODK) {
            if (resultCode == RESULT_OK) {
                Uri instanceUri = intent.getData();
                //Busca la instancia resultado
                String[] projection = new String[]{
                        "_id", "instanceFilePath", "status", "displaySubtext"};
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
                if (complete.matches("complete")) {
                    //Parsear el resultado obteniendo un tamizaje si esta completo
                    parseZp02BiospecimenCollection(idInstancia, instanceFilePath, accion);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.err_not_completed), Toast.LENGTH_LONG).show();
                }
            } else {
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    private void addZp02BiospecimenCollection() {
        try {
            Uri formUri;
            if (mZp02 == null) {
                //campos de proveedor de collect
                String[] projection = new String[]{
                        "_id", "jrFormId", "displayName"};
                //cursor que busca el formulario
                Cursor c = getContentResolver().query(Constants.CONTENT_URI, projection,
                        "jrFormId = 'ZP02A__BiospecimenCollection' and displayName = 'Estudio ZIP Formulario para recoleccion de muestras biologicas'", null, null);
                c.moveToFirst();
                //captura el id del formulario
                Integer id = Integer.parseInt(c.getString(0));
                //cierra el cursor
                if (c != null) {
                    c.close();
                }
                formUri = ContentUris.withAppendedId(Constants.CONTENT_URI, id);
                accion = ADD_ZP02_ODK;
            } else {
                //forma el uri para la instancia en ODK Collect
                Integer id = mZp02.getIdInstancia();
                formUri = ContentUris.withAppendedId(Constants.CONTENT_URI_I, id);
                accion = EDIT_ZP02_ODK;
            }
            Intent odkA = new Intent(Intent.ACTION_EDIT, formUri);
            //Arranca la actividad proveedor de instancias de ODK Collect en busca de resultado
            startActivityForResult(odkA, accion);
        } catch (Exception e) {
            //No existe el formulario en el equipo
            Log.e(TAG, e.getMessage(), e);
            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void parseZp02BiospecimenCollection(Integer idInstancia, String instanceFilePath, Integer accion) {
        Serializer serializer = new Persister();
        File source = new File(instanceFilePath);
        try {
            Zp02BiospecimenCollectionXml zp02Xml = serializer.read(Zp02BiospecimenCollectionXml.class, source);
            if (accion == ADD_ZP02_ODK) mZp02 = new Zp02BiospecimenCollection();
            mZp02.setRecordId(mRecordId);
            mZp02.setRedcapEventName(event);
            mZp02.setBscDov(zp02Xml.getBscDov());
            mZp02.setBscVisit(zp02Xml.getBscVisit());
            mZp02.setBscMatBldCol(zp02Xml.getBscMatBldCol());
            mZp02.setBscMatBldRsn(zp02Xml.getBscMatBldRsn());
            mZp02.setBscMatBldSpecify(zp02Xml.getBscMatBldSpecify());
            mZp02.setBscMatBldTyp1(zp02Xml.getBscMatBldTyp1());
            mZp02.setBscMatBldId1(zp02Xml.getBscMatBldId1());
            mZp02.setBscMatBldVol1(zp02Xml.getBscMatBldVol1());
            mZp02.setBscMatBldTyp2(zp02Xml.getBscMatBldTyp2());
            mZp02.setBscMatBldId2(zp02Xml.getBscMatBldId2());
            mZp02.setBscMatBldVol2(zp02Xml.getBscMatBldVol2());
            mZp02.setBscMatBldTyp3(zp02Xml.getBscMatBldTyp3());
            mZp02.setBscMatBldId3(zp02Xml.getBscMatBldId3());
            mZp02.setBscMatBldVol3(zp02Xml.getBscMatBldVol3());
            mZp02.setBscMatBldTyp4(zp02Xml.getBscMatBldTyp4());
            mZp02.setBscMatBldId4(zp02Xml.getBscMatBldId4());
            mZp02.setBscMatBldVol4(zp02Xml.getBscMatBldVol4());
            mZp02.setBscMatBldTyp5(zp02Xml.getBscMatBldTyp5());
            mZp02.setBscMatBldId5(zp02Xml.getBscMatBldId5());
            mZp02.setBscMatBldVol5(zp02Xml.getBscMatBldVol5());
            mZp02.setBscMatBldTyp6(zp02Xml.getBscMatBldTyp6());
            mZp02.setBscMatBldId6(zp02Xml.getBscMatBldId6());
            mZp02.setBscMatBldVol6(zp02Xml.getBscMatBldVol6());
            mZp02.setBscMatBldTyp7(zp02Xml.getBscMatBldTyp7());
            mZp02.setBscMatBldId7(zp02Xml.getBscMatBldId7());
            mZp02.setBscMatBldVol7(zp02Xml.getBscMatBldVol7());
            mZp02.setBscMatBldTyp8(zp02Xml.getBscMatBldTyp8());
            mZp02.setBscMatBldId8(zp02Xml.getBscMatBldId8());
            mZp02.setBscMatBldVol8(zp02Xml.getBscMatBldVol8());
            mZp02.setBscMatBldTotVol(zp02Xml.getBscMatBldTotVol());
            mZp02.setBscMatBldTime(zp02Xml.getBscMatBldTime());
            mZp02.setBscMatBldCom(zp02Xml.getBscMatBldCom());
            mZp02.setBscMatSlvaCol(zp02Xml.getBscMatSlvaCol());
            mZp02.setBscMatSlvaRsn(zp02Xml.getBscMatSlvaRsn());
            mZp02.setBscMatSlvaSpecify(zp02Xml.getBscMatSlvaSpecify());
            mZp02.setBscMatSlvaId(zp02Xml.getBscMatSlvaId());
            mZp02.setBscMatSlvaTime(zp02Xml.getBscMatSlvaTime());
            mZp02.setBscMatSlvaCom(zp02Xml.getBscMatSlvaCom());
            mZp02.setBscMatVagCol(zp02Xml.getBscMatVagCol());
            mZp02.setBscMatVagRsn(zp02Xml.getBscMatVagRsn());
            mZp02.setBscMatVagSpecify(zp02Xml.getBscMatVagSpecify());
            mZp02.setBscMatVagId(zp02Xml.getBscMatVagId());
            mZp02.setBscMatVagTime(zp02Xml.getBscMatVagTime());
            mZp02.setBscMatVagCom(zp02Xml.getBscMatVagCom());
            mZp02.setBscMatVstUrnCol(zp02Xml.getBscMatVstUrnCol());
            mZp02.setBscMatVstUrnRsn(zp02Xml.getBscMatVstUrnRsn());
            mZp02.setBscMatVstUrnSpecify(zp02Xml.getBscMatVstUrnSpecify());
            mZp02.setBscMatVstUrnId(zp02Xml.getBscMatVstUrnId());
            mZp02.setBscMatVstUrnTime(zp02Xml.getBscMatVstUrnTime());
            mZp02.setBscMatVstUrnCom(zp02Xml.getBscMatVstUrnCom());
            mZp02.setBscMatHomUrnCol(zp02Xml.getBscMatHomUrnCol());
            mZp02.setBscMatHomUrnRsn(zp02Xml.getBscMatHomUrnRsn());
            mZp02.setBscMatHomUrnSpecify(zp02Xml.getBscMatHomUrnSpecify());
            mZp02.setBscMatHomUrnNum(zp02Xml.getBscMatHomUrnNum());
            mZp02.setBscMatHomUrnId1(zp02Xml.getBscMatHomUrnId1());
            mZp02.setBscMatHomUrnDat1(zp02Xml.getBscMatHomUrnDat1());
            mZp02.setBscMatHomUrnTime1(zp02Xml.getBscMatHomUrnTime1());
            mZp02.setBscMatHomUrnCom1(zp02Xml.getBscMatHomUrnCom1());
            mZp02.setBscMatHomUrnId2(zp02Xml.getBscMatHomUrnId2());
            mZp02.setBscMatHomUrnDat2(zp02Xml.getBscMatHomUrnDat2());
            mZp02.setBscMatHomUrnTime2(zp02Xml.getBscMatHomUrnTime2());
            mZp02.setBscMatHomUrnCom2(zp02Xml.getBscMatHomUrnCom2());
            mZp02.setBscMatHomUrnId3(zp02Xml.getBscMatHomUrnId3());
            mZp02.setBscMatHomUrnDat3(zp02Xml.getBscMatHomUrnDat3());
            mZp02.setBscMatHomUrnTime3(zp02Xml.getBscMatHomUrnTime3());
            mZp02.setBscMatHomUrnCom3(zp02Xml.getBscMatHomUrnCom3());
            mZp02.setBscMatHomUrnId4(zp02Xml.getBscMatHomUrnId4());
            mZp02.setBscMatHomUrnDat4(zp02Xml.getBscMatHomUrnDat4());
            mZp02.setBscMatHomUrnTime4(zp02Xml.getBscMatHomUrnTime4());
            mZp02.setBscMatHomUrnCom4(zp02Xml.getBscMatHomUrnCom4());
            mZp02.setBscMatOtherCol(zp02Xml.getBscMatOtherCol());
            mZp02.setBscMatOtherType(zp02Xml.getBscMatOtherType()); //multiple
            mZp02.setBscMatOtherTypeSpecify(zp02Xml.getBscMatOtherTypeSpecify());
            mZp02.setBscMatAmfCol(zp02Xml.getBscMatAmfCol());
            mZp02.setBscMatAmfRsn(zp02Xml.getBscMatAmfRsn());
            mZp02.setBscMatAmfSpecify(zp02Xml.getBscMatAmfSpecify());
            mZp02.setBscMatAmfId(zp02Xml.getBscMatAmfId());
            mZp02.setBscMatAmfAmount(zp02Xml.getBscMatAmfAmount());
            mZp02.setBscMatAmfTime(zp02Xml.getBscMatAmfTime());
            mZp02.setBscMatAmfCom(zp02Xml.getBscMatAmfCom());
            mZp02.setBscMatCordCol(zp02Xml.getBscMatCordCol());
            mZp02.setBscMatCordRsn(zp02Xml.getBscMatCordRsn());
            mZp02.setBscMatCordSpecify(zp02Xml.getBscMatCordSpecify());
            mZp02.setBscMatCordId(zp02Xml.getBscMatCordId());
            mZp02.setBscMatCordTime(zp02Xml.getBscMatCordTime());
            mZp02.setBscMatCordAmount(zp02Xml.getBscMatCordAmount());
            mZp02.setBscMatCordCom(zp02Xml.getBscMatCordCom());
            mZp02.setBscMatPlacenCol(zp02Xml.getBscMatPlacenCol());
            mZp02.setBscMatPlacenRsn(zp02Xml.getBscMatPlacenRsn());
            mZp02.setBscMatPlacenSpecify(zp02Xml.getBscMatPlacenSpecify());
            mZp02.setBscMatPlacenCircum(zp02Xml.getBscMatPlacenCircum());
            mZp02.setBscMatPlacenId(zp02Xml.getBscMatPlacenId());
            mZp02.setBscMatPlacenTime(zp02Xml.getBscMatPlacenTime());
            mZp02.setBscMatPlacenCom(zp02Xml.getBscMatPlacenCom());
            mZp02.setBscMatBreastmCol(zp02Xml.getBscMatBreastmCol());
            mZp02.setBscMatBreastmRsn(zp02Xml.getBscMatBreastmRsn());
            mZp02.setBscMatBreastmSpecify(zp02Xml.getBscMatBreastmSpecify());
            mZp02.setBscMatBreastmId(zp02Xml.getBscMatBreastmId());
            mZp02.setBscMatBreastmTime(zp02Xml.getBscMatBreastmTime());
            mZp02.setBscMatBreastmAmount(zp02Xml.getBscMatBreastmAmount());
            mZp02.setBscMatBreastmCom(zp02Xml.getBscMatBreastmCom());
            mZp02.setBscMatMiscarr(zp02Xml.getBscMatMiscarr());
            mZp02.setBscMatFetaltCol(zp02Xml.getBscMatFetaltCol());
            mZp02.setBscMatFetaltId(zp02Xml.getBscMatFetaltId());
            mZp02.setBscMatFetaltTime(zp02Xml.getBscMatFetaltTime());
            mZp02.setBscMatFetaltCom(zp02Xml.getBscMatFetaltCom());
            mZp02.setBscMatdBreastmCol(zp02Xml.getBscMatdBreastmCol());
            mZp02.setBscMatdBreastmRsn(zp02Xml.getBscMatdBreastmRsn());
            mZp02.setBscMatdBreastmSpecify(zp02Xml.getBscMatdBreastmSpecify());
            mZp02.setBscMatdBreastmId(zp02Xml.getBscMatdBreastmId());
            mZp02.setBscMatdBreastmTime(zp02Xml.getBscMatdBreastmTime());
            mZp02.setBscMatdBreastmAmou(zp02Xml.getBscMatdBreastmAmou());
            mZp02.setBscMatdBreastmCom(zp02Xml.getBscMatdBreastmCom());
            mZp02.setBscPerson1(username);
            mZp02.setBscCompleteDate1(new Date());
            mZp02.setBscPerson2(username);
            mZp02.setBscCompleteDate2(new Date());
            mZp02.setBscPerson3(username);
            mZp02.setBscCompleteDate3(new Date());

            //Version 2
            mZp02.setAddtAnemiaTest(zp02Xml.getAddtAnemiaTest());
            mZp02.setAddtLastAnemiaTest(zp02Xml.getAddtLastAnemiaTest());
            mZp02.setAddtLastAnemiaTestUk(zp02Xml.getAddtLastAnemiaTestUk());
            mZp02.setAddtAnemiaTestAv(zp02Xml.getAddtAnemiaTestAv());
            mZp02.setAddtHematocrit(zp02Xml.getAddtHematocrit());
            mZp02.setAddtHemoglobin(zp02Xml.getAddtHemoglobin());
            //Termina version 2

            mZp02.setRecordDate(new Date());
            mZp02.setRecordUser(username);
            mZp02.setIdInstancia(idInstancia);
            mZp02.setInstancePath(instanceFilePath);
            mZp02.setEstado(Constants.STATUS_NOT_SUBMITTED);
            mZp02.setStart(zp02Xml.getStart());
            mZp02.setEnd(zp02Xml.getEnd());
            mZp02.setDeviceid(zp02Xml.getDeviceid());
            mZp02.setSimserial(zp02Xml.getSimserial());
            mZp02.setPhonenumber(zp02Xml.getPhonenumber());
            mZp02.setToday(zp02Xml.getToday());

            //validations
            Integer tubes = 0;
            Integer codEvent = 0;
            String msj = null;
            String tb = zp02Xml.getQuestion1();
            if (tb != null){
               tubes = Integer.parseInt(tb);
            }


            if (mZp02.getRedcapEventName() != null) {

                if (mZp02.getRedcapEventName().equals(Constants.SCREENING) || mZp02.getRedcapEventName().equals(Constants.ENTRY)) {
                    codEvent = 1;
                } else if (mZp02.getRedcapEventName().equals(Constants.WEEK2) || mZp02.getRedcapEventName().equals(Constants.WEEK4)) {
                    codEvent = 2;
                } else if (mZp02.getRedcapEventName().equals(Constants.WEEK6) || mZp02.getRedcapEventName().equals(Constants.WEEK8)) {
                    codEvent = 3;
                } else if (mZp02.getRedcapEventName().equals(Constants.WEEK10) || mZp02.getRedcapEventName().equals(Constants.WEEK12)) {
                    codEvent = 4;
                } else if (mZp02.getRedcapEventName().equals(Constants.WEEK14) || mZp02.getRedcapEventName().equals(Constants.WEEK16)) {
                    codEvent = 5;
                } else if (mZp02.getRedcapEventName().equals(Constants.WEEK18) || mZp02.getRedcapEventName().equals(Constants.WEEK20)) {
                    codEvent = 6;
                } else if (mZp02.getRedcapEventName().equals(Constants.WEEK22) || mZp02.getRedcapEventName().equals(Constants.WEEK24)) {
                    codEvent = 7;
                } else if (mZp02.getRedcapEventName().equals(Constants.WEEK26) || mZp02.getRedcapEventName().equals(Constants.WEEK28)) {
                    codEvent = 8;
                } else if (mZp02.getRedcapEventName().equals(Constants.WEEK30) || mZp02.getRedcapEventName().equals(Constants.WEEK32)) {
                    codEvent = 9;
                } else if (mZp02.getRedcapEventName().equals(Constants.WEEK34) || mZp02.getRedcapEventName().equals(Constants.WEEK36)) {
                    codEvent = 10;
                } else if (mZp02.getRedcapEventName().equals(Constants.WEEK38) || mZp02.getRedcapEventName().equals(Constants.WEEK40)) {
                    codEvent = 11;
                } else if (mZp02.getRedcapEventName().equals(Constants.WEEK42) || mZp02.getRedcapEventName().equals(Constants.WEEK44)) {
                    codEvent = 12;
                } else if (mZp02.getRedcapEventName().equals(Constants.DELIVERY)) {
                    codEvent = 13;
                } else if (mZp02.getRedcapEventName().equals(Constants.AFTERDELIVERY)) {
                    codEvent = 14;
                }

            }

            if (codEvent != 0) {
                //blood samples
                if (tubes > 0) {
                    for (int i = 1; i <= tubes; i++) {
                        String sampleID = null;
                        switch (i) {
                            case 1:
                                sampleID = mZp02.getBscMatBldId1();
                                break;
                            case 2:
                                sampleID = mZp02.getBscMatBldId2();
                                break;
                            case 3:
                                sampleID = mZp02.getBscMatBldId3();
                                break;
                            case 4:
                                sampleID = mZp02.getBscMatBldId4();
                                break;
                            case 5:
                                sampleID = mZp02.getBscMatBldId5();
                                break;
                            case 6:
                                sampleID = mZp02.getBscMatBldId6();
                                break;
                            case 7:
                                sampleID = mZp02.getBscMatBldId7();
                                break;
                            case 8:
                                sampleID = mZp02.getBscMatBldId8();
                                break;
                        }

                        if (sampleID != null) {
                            //validate codes
                            msj = validateCodes(codEvent, sampleID);

                            if (msj != null) {
                                showToast(msj);
                            }
                        }

                    }
                }


                //saliva sample
                String saliva = mZp02.getBscMatSlvaId();
                if (saliva != null) {
                    //validate codes
                    msj = validateCodes(codEvent, saliva);

                    if (msj != null) {
                        showToast(msj);
                    }
                }

                //vaginal swab
                String swabId = mZp02.getBscMatVagId();
                if (swabId != null) {
                    //vslidate codes
                    msj = validateCodes(codEvent, swabId);

                    if (msj != null) {
                        showToast(msj);
                    }
                }

                //urine sample
                String urineId = mZp02.getBscMatVstUrnId();
                if (urineId != null) {
                    //validate codes
                    msj = validateCodes(codEvent, urineId);

                    if (msj != null) {
                        showToast(msj);
                    }
                }


                //home urine samples
                Integer numMx = mZp02.getBscMatHomUrnNum();
                if (numMx != null) {
                    for (int i = 1; i <= numMx; i++) {
                        String homUrnId = null;
                        switch (i) {
                            case 1:
                                homUrnId = mZp02.getBscMatHomUrnId1();
                                break;
                            case 2:
                                homUrnId = mZp02.getBscMatHomUrnId2();
                                break;
                            case 3:
                                homUrnId = mZp02.getBscMatHomUrnId3();
                                break;
                            case 4:
                                homUrnId = mZp02.getBscMatHomUrnId4();
                                break;
                        }

                        if (homUrnId != null) {
                            //validate codes
                            msj = validateCodes(codEvent, homUrnId);
                        }
                        if (msj != null) {
                            showToast(msj);
                        }
                    }
                }

                //amniotic fluid
                String amnioticFluidId = mZp02.getBscMatAmfId();
                if (amnioticFluidId != null) {
                    //validate codes
                    msj = validateCodes(codEvent, amnioticFluidId);

                    if (msj != null) {
                        showToast(msj);
                    }

                }

                //cord blood
                String cordBloodId = mZp02.getBscMatCordId();
                if (cordBloodId != null) {
                    //validate codes
                    msj = validateCodes(codEvent, cordBloodId);

                    if (msj != null) {
                        showToast(msj);
                    }
                }

                //placenta
                String placentaId = mZp02.getBscMatPlacenId();
                if (placentaId != null) {
                    //validate codes
                    msj = validateCodes(codEvent, placentaId);

                    if (msj != null) {
                        showToast(msj);
                    }
                }

                //breastmilk
                String breastmilkId = mZp02.getBscMatBreastmId();
                if (breastmilkId != null){
                    //validate codes
                    msj = validateCodes(codEvent, breastmilkId);

                    if (msj != null) {
                        showToast(msj);
                    }
                }

                //fetal
                String fetalId = mZp02.getBscMatFetaltId();
                if (fetalId != null){
                    //validate codes
                    msj = validateCodes(codEvent, fetalId);

                    if (msj != null) {
                        showToast(msj);
                    }
                }

                //breastmilk2
                String brestmilkId2 = mZp02.getBscMatdBreastmId();

                if(brestmilkId2!= null){
                    //validate codes
                    msj = validateCodes(codEvent, brestmilkId2);

                    if (msj != null) {
                        showToast(msj);
                    }
                }

            }


            new SaveDataTask().execute(accion);

        } catch (Exception e) {
            // Presenta el error al parsear el xml
            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
            finish();
        }
    }

    private String validateCodes(int codEvent, String idMx) {
        String pregnantId = idMx.substring(0, 8);
        String event = idMx.substring(9, 11);
        String msj = null;

        //validate pregnantId
        if (!(pregnantId.matches(mRecordId))) {
            msj = idMx + " " + getString(R.string.code_error_mx);
        }

        //validate event
        if (codEvent < 10) {
            if (!(event.matches("0" + codEvent) || event.matches("[5-9][0-9]"))) {
                if (msj != null) {
                    msj = msj + " " + getString(R.string.code_error_event_mx1);
                } else {
                    msj = idMx + " " + getString(R.string.code_error_event_mx);
                }
            }
        } else {
            if (!(event.matches(String.valueOf(codEvent)) || event.matches("[5-9][0-9]"))) {
                if (msj != null) {
                    msj = msj + " " + getString(R.string.code_error_event_mx1);
                } else {
                    msj = idMx + " " + getString(R.string.code_error_event_mx);
                }
            }
        }

        return msj;
    }

    private void showToast(String mensaje) {
        LayoutInflater inflater = getLayoutInflater();

        View layout = inflater.inflate(R.layout.toast,
                (ViewGroup) findViewById(R.id.toast_layout_root));

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(mensaje);

        final Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 1000 * 25);


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
            accionaRealizar = values[0];
            try {
                zipA.open();
                if (accionaRealizar == ADD_ZP02_ODK) {
                    zipA.crearZp02BiospecimenCollection(mZp02);
                } else {
                    zipA.editarZp02BiospecimenCollection(mZp02);
                }
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
        Toast.makeText(getApplicationContext(), resultado, Toast.LENGTH_LONG).show();
        finish();
    }
}
