package ni.org.ics.zip.appmovil.activities.paginas.agenda;

import android.app.ActionBar;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ni.org.ics.zip.appmovil.AbstractAsyncActivity;
import ni.org.ics.zip.appmovil.MainActivity;
import ni.org.ics.zip.appmovil.MyZipApplication;
import ni.org.ics.zip.appmovil.R;
import ni.org.ics.zip.appmovil.activities.DataEnterActivity;
import ni.org.ics.zip.appmovil.activities.utils.Dialogs;
import ni.org.ics.zip.appmovil.database.ZipAdapter;
import ni.org.ics.zip.appmovil.domain.Parametro;
import ni.org.ics.zip.appmovil.domain.Provider;
import ni.org.ics.zip.appmovil.domain.ZpAgendaEstudio;
import ni.org.ics.zip.appmovil.domain.ZpCenter;
import ni.org.ics.zip.appmovil.domain.ZpSpecialities;
import ni.org.ics.zip.appmovil.preferences.PreferencesActivity;
import ni.org.ics.zip.appmovil.utils.Constants;
import ni.org.ics.zip.appmovil.utils.DeviceInfo;
import ni.org.ics.zip.appmovil.utils.FileUtils;
import ni.org.ics.zip.appmovil.utils.MainDBConstants;
import ni.org.ics.zip.appmovil.utils.ZpCenterConstans;
import java.util.Calendar;

public class NewAppointment extends AbstractAsyncActivity implements  OnClickListener {

    private Spinner mMetodoView;
    private Spinner mTipoCita;
    private Spinner mCentro;
    private Spinner mTurno;
    private Spinner mProveedor;
    private ToggleButton mPermiteSMS;
    private EditText mCodigoView;
    private String mCodigo;
    private EditText mComentario;
    private EditText mFecha;
    private EditText mHora;
    private EditText mNumeroCelular;
    private ImageButton mBarcodeButton;
    private Button mSaveButton;
    private Button mCancelButton;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    private SimpleDateFormat shf = new SimpleDateFormat("hh:mma");
    private SimpleDateFormat thf = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
    public static final int BARCODE_CAPTURE = 2;
    public static final int MINUTOS_CITA = 30;
    protected static final String TAG = NewAppointment.class.getSimpleName();
    public static final int ADD_DATOS = 1;
    private DeviceInfo infoMovil;
    private String mRecordId;
    private SharedPreferences settings;
    private String username;
    private ZipAdapter zipA;
    private ZpAgendaEstudio mZpDatos = null;
    private Date mfechaval;
    private Date mFechaEndVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newappointment);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar actionBar = getActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (!FileUtils.storageReady()) {
            Toast toast = Toast.makeText(getApplicationContext(),getString(R.string.error, R.string.storage_error), Toast.LENGTH_LONG);
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

        mMetodoView = (Spinner) findViewById(R.id.metodo_busqueda);
        mTipoCita = (Spinner) findViewById(R.id.lista_tipocita);
        mCentro = (Spinner) findViewById(R.id.lista_centros);
        mTurno = (Spinner) findViewById(R.id.lista_turno);
        mProveedor = (Spinner) findViewById(R.id.lista_proveedor);
        mNumeroCelular = (EditText) findViewById(R.id.numerosms);
        mComentario = (EditText) findViewById(R.id.comentario);
        InitToggleButton();

        List<String> list = new ArrayList<String>();
        list.add(getString(R.string.desc_barcode));
        list.add(getString(R.string.enter) + " " + getString(R.string.mat_id));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        mMetodoView.setAdapter(dataAdapter);

        mCodigoView = (EditText) findViewById(R.id.codigo);
        mFecha = (EditText)   findViewById(R.id.fecha);
        mFecha.setOnClickListener(this);
       // mHora = (EditText) findViewById(R.id.hora);
        Date fecha = new Date();
        fecha.setMinutes(0);
        mFecha.setText(sdf.format(fecha));
       /* mHora.setText(shf.format(fecha));
        mHora.setOnClickListener(this);
        mHora.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    timePicker();
                }
            }
        });
        */
        mMetodoView.requestFocus();

        mMetodoView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == 0) {
                    mCodigoView.setVisibility(View.GONE);
                    //  mFindButton.setVisibility(View.GONE);
                    mBarcodeButton.setVisibility(View.VISIBLE);
                } else {
                    mCodigoView.setVisibility(View.VISIBLE);
                    // mFindButton.setVisibility(View.VISIBLE);
                    mBarcodeButton.setVisibility(View.GONE);
                    mCodigoView.requestFocus();
                    mCodigoView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                    mCodigoView.setHint(getString(R.string.mat_id));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });

        mCodigoView.setVisibility(View.GONE);


        mBarcodeButton = (ImageButton) findViewById(R.id.barcode_button);
        mCancelButton = (Button) findViewById(R.id.cancelButton);
        mCancelButton.setOnClickListener(this);

        InitSaveButton();

        mBarcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent("com.google.zxing.client.android.SCAN");
                try {
                    startActivityForResult(i, BARCODE_CAPTURE);
                } catch (ActivityNotFoundException e) {
                    Toast t = Toast.makeText(getApplicationContext(),
                            getString(R.string.error, R.string.barcode_error),
                            Toast.LENGTH_LONG);
                    t.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    t.show();
                }
            }
        });


        infoMovil = new DeviceInfo(NewAppointment.this);
        mRecordId = getIntent().getStringExtra(MainDBConstants.record_Id);
        mZpDatos = new ZpAgendaEstudio();// (ZpAgendaEstudio) getIntent().getExtras().getSerializable(Constants.OBJECTO_ZPDATA);


        // Agregar datos para seleccion
        List<Provider> mProvideers = new ArrayList<Provider>();
        List<ZpCenter> mZpCenters = new ArrayList<ZpCenter>();
        List<ZpSpecialities> mZpSpecialities = new ArrayList<ZpSpecialities>();
        try {
            zipA.open();
            mProvideers = zipA.getProveedores(null,"name");
            mZpCenters = zipA.getCenters("cs <> ''", ZpCenterConstans.cs);
            mZpSpecialities = zipA.getEspecialidades("especialidad <> ''","especialidad");

        } catch (Exception ex) {
        } finally {
            zipA.close();
        }
        // PROVEEDORES
        list = new ArrayList<String>();
        for (Provider proveedor : mProvideers) {
            list.add(proveedor.getName());
        }
        ArrayAdapter<String> dataAdapterProviders = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapterProviders.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        mProveedor.setAdapter(dataAdapterProviders);

        list = new ArrayList<String>();
        for (ZpCenter center : mZpCenters) {
            list.add(center.getCs());
        }
        ArrayAdapter<String> dataAdapterCenters = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapterCenters.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        mCentro.setAdapter(dataAdapterCenters);
        /// Especialidades
/*
        list = new ArrayList<String>();
        for (ZpSpecialities obj : mZpSpecialities) {
            list.add(obj.getSpeciality());
        }

        ArrayAdapter<String> dataAdapterEspecialidades = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapterEspecialidades.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        mEspecialidad.setAdapter(dataAdapterEspecialidades);

*/

    }

    @Override
    protected void onResume(){
        super.onResume();
        mBarcodeButton.requestFocus();
        mFecha.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    datePicker();
                }
            }
        });
    }

    private boolean validarEntrada() {
        //Valida la entrada
        if (mCentro.getSelectedItemPosition() < 0) {
            showToast(this.getString(R.string.error_lugar));
            return false;
        }
        if (mTipoCita.getSelectedItemPosition() < 0) {
            showToast(this.getString(R.string.error_persona));
            return false;
        }
        mCodigo = mCodigoView.getText().toString();
        if (mCodigo == null) {
            showToast(this.getString(R.string.code_error));
            return false;
        }
        if (!(mCodigo.matches("^07[0-9][0-9][0-9][0-9][0-3][A-Y]$"))) {
            showToast(mCodigo + " " + getString(R.string.code_error));
            mCodigoView.setText(null);

            return false;
        }
        mfechaval = new Date();
        try{
            zipA.open();
            String timepicker = "07:00 am";
            String fechaSt =  mFecha.getText().toString() + " " + timepicker;
            mfechaval = sdf.parse(mFecha.getText().toString());
            Calendar c = Calendar.getInstance();
            c.setTime(mfechaval);
            c.add(Calendar.DAY_OF_YEAR,1);
            //Buscar Cuantas citas se han realizado en el dia

            List<ZpAgendaEstudio> citasPordia = zipA.getAgendaStudios("FECHA_HORA_CITA  >= "
                                                + String.valueOf(mfechaval.getTime()) + " AND FECHA_HORA_CITA < "
                                                + String.valueOf((c.getTime()).getTime()),"FECHA_HORA_CITA");
            mfechaval = thf.parse(fechaSt);
            c.setTime(mfechaval);
            c.add(Calendar.MINUTE, (citasPordia.size()) * 15);
            mfechaval = c.getTime();
            c.add(Calendar.MINUTE,15);
            mFechaEndVal = c.getTime();
/*
            if(mfechaval.getTime() < (new Date()).getTime()){
                showToast(this.getString(R.string.fechahora_no_valida));
                return false;
            }*/
            Calendar cal = Calendar.getInstance();
            cal.setTime(mfechaval);
            if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
                showToast(this.getString(R.string.fechahora_no_laboral));
                return false;
            }

            // Verificar que la fecha no sea feriada

            try
            {

                Parametro parametro = zipA.getParametro("name  = 'agenda_dias_feriados'","name");
                if(parametro != null){
                    List<DateNoSet> lista = new ArrayList<DateNoSet>();
                    JSONArray jsonArray = new JSONArray(parametro.getValue());

                    for(int i = 0; i < jsonArray.length();i++) {

                        lista.add(new DateNoSet(Integer.parseInt(jsonArray.getJSONObject(i).get("dia").toString())
                                , Integer.parseInt(jsonArray.getJSONObject(i).get("mes").toString())));
                    }
                    int currentMonth = cal.get(Calendar.MONTH) ,
                            currentDay = cal.get(Calendar.DAY_OF_MONTH);
                    for(DateNoSet obj:lista){

                        if(  currentMonth == obj.getMes()
                                && currentDay == obj.getDia())
                        {
                            showToast(this.getString(R.string.fechahora_no_laboral));
                            return false;
                        }
                    }
                }

                // Verificar que la fecha seleccionada no se encuentre agendada
                //NO APLICA DEBIDO A QUE SE ESTA USANDO EL TURNO
               // List<ZpAgendaEstudio> listaAgenda = zipA.getAgendaStudios("FECHA_HORA_CITA  = " + String.valueOf(mfechaval.getTime()),"FECHA_HORA_CITA");

                //if(listaAgenda.size() > 0){

                //    showToast(this.getString(R.string.fechahora_ya_existe));
               //     return false;
               // }

            }
            catch (Exception ex){

                showToast(this.getString(R.string.fechahora_error));
                //return false;
            }


        }
        catch (Exception ex){

            showToast(this.getString(R.string.fechahora_error));
            return false;
        }
        finally { zipA.close();
        }
       // SimpleDateFormat df = new SimpleDateFormat("mm");
        /*
        int minutos = Integer.parseInt(df.format(mfechaval));

        if( (minutos % MINUTOS_CITA) != 0  || minutos > 59) {
            showToast(this.getString(R.string.minutos_error));
            return false;
        }
*/


        boolean permiteSMS = mPermiteSMS.isChecked();
        String numeroCel = mNumeroCelular.getText().toString();
        if(permiteSMS){
            if(numeroCel == null){
                showToast(this.getString(R.string.numerocel_error));
                return false;
            }
            if (!(numeroCel.matches("^\\d\\d\\d\\d\\d\\d\\d\\d"))) {
                showToast(getString(R.string.numerocel_error));
                return false;
            }
        }

        return true;

    }

    private void  InitSaveButton() {

        mSaveButton = (Button) findViewById(R.id.saveButton);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Validar los datos
                if (validarEntrada()) {
                    // Guardar los datos

                    String subjectTypeMujer = getString(R.string.tipo_agenda_mujer),
                            subjectTypeInfante = getString(R.string.tipo_agenda_infante);

                    mZpDatos.setAppointmentDateTime(mfechaval);

                    mZpDatos.setTurno(mTurno.getSelectedItem().toString());

                    // Determinar si es mujer o infante
                    String cod = mCodigo;
                    cod = cod.substring(6,7);
                    if (cod.equals("0")) mZpDatos.setSubjectType(subjectTypeMujer);
                    else mZpDatos.setSubjectType(subjectTypeInfante);

                    mZpDatos.setSMSNumber(mNumeroCelular.getText().toString());
                    mZpDatos.setAsistio("N");
                    mZpDatos.setHealtUnit(mCentro.getSelectedItem().toString());
                    mZpDatos.setCellNumAuth((mPermiteSMS.isChecked() ? "S" : "N"));
                    mZpDatos.setAppointmentType(mTipoCita.getSelectedItem().toString());
                    mZpDatos.setRecordId(mCodigo);
                    mZpDatos.setProvider(mProveedor.getSelectedItem().toString());
                    mZpDatos.setAppointmentEndTime(mFechaEndVal);
                    mZpDatos.setRecordDate((new Date()));
                    mZpDatos.setRecordUser(username);
                    mZpDatos.setDeviceid(new DeviceInfo(NewAppointment.this).getDeviceId());
                    mZpDatos.setEstado(Constants.STATUS_NOT_SUBMITTED);
                    mZpDatos.setToday(new Date());
                    mZpDatos.setObs(mComentario.getText().toString());
                    try {

                        zipA.open();
                        Provider proveedor = zipA.getProveedorFromDd("name = '" + mProveedor.getSelectedItem().toString()+"'","name");
                        mZpDatos.setSpecialityType(proveedor.getSpeciality());
                        List<ZpAgendaEstudio> mCitas = zipA.getAgendaStudios( "id > 0","id");
                        int id = mCitas.size() + 1;
                        mCitas =  zipA.getAgendaStudios( "id =" + String.valueOf(id),"id");
                        while(mCitas.size() > 0){
                            id ++;
                            mCitas =  zipA.getAgendaStudios( "id =" + String.valueOf(id),"id");
                        }
                        mZpDatos.setId(id);
                        zipA.crearZpAgendaStudio(mZpDatos);
                        showToast(getString(R.string.saved_cita));
                        Intent i = new Intent(getApplicationContext(),
                                MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);

                    } catch (Exception e) {
                        Log.e(TAG, e.getLocalizedMessage(), e);

                    }
                    finally {
                        zipA.close();
                    }
                }

            }
        });
    }

    private  void  InitToggleButton(){
        mPermiteSMS = (ToggleButton) findViewById(R.id.autorizasms);

        mPermiteSMS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked) {
                    mNumeroCelular.setText("");
                    mNumeroCelular.setEnabled(true);
                }
                else{
                    mNumeroCelular.setText("");
                    mNumeroCelular.setEnabled(false);

                }
            }
        });

    }


    /**
     * Presenta dialogo de informacion    */

    private void showToast(String mensaje){
        LayoutInflater inflater = getLayoutInflater();

        View layout = inflater.inflate(R.layout.toast,
                (ViewGroup) findViewById(R.id.toast_layout_root));

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(mensaje);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }



    private void datePicker() {

        Dialogs.ShowDatePickerDialog(this,mFecha);

    }

    private void timePicker(){

      //  Dialogs.ShowTimePickerDialog(this,mHora);

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

        if(requestCode == ADD_DATOS) {
            if(resultCode == RESULT_OK) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
                String fechahora = intent.getExtras().getString("fecha");
                fechahora += " " + intent.getExtras().getString("fecha");
                java.util.Date fechaCita = new Date();
                try{
                    fechaCita = formatter.parse(fechahora);
                } catch (Exception ex){

                }
                mZpDatos.setRecordId(mRecordId);
                mZpDatos.setHealtUnit(intent.getExtras().getString(this.getString(R.string.cs)));
                mZpDatos.setCellNumAuth(intent.getExtras().getString(this.getString(R.string.autorizasms)));
                mZpDatos.setSMSNumber(intent.getExtras().getString(this.getString(R.string.numero_celular)));
                mZpDatos.setAppointmentType(intent.getExtras().getString(this.getString(R.string.tipocita)));
                mZpDatos.setSpecialityType(intent.getExtras().getString(this.getString(R.string.especialidad)));
                mZpDatos.setAppointmentDateTime(fechaCita);
              //  mZpDatos.setTelefonoContacto(intent.getExtras().getString(this.getString(R.string.telefonoContacto)));
                mZpDatos.setRecordDate(new Date());
                mZpDatos.setRecordUser(username);
                mZpDatos.setEstado(Constants.STATUS_NOT_SUBMITTED);
                mZpDatos.setDeviceid(infoMovil.getDeviceId());
                mZpDatos.setToday(new Date());
                new NewAppointment.SaveDataTask().execute();
            }
            else{
                finish();
            }
        }
        Intent i = new Intent(getApplicationContext(),
                MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
        super.onActivityResult(requestCode, resultCode, intent);
    }

    /**
     *
     */
    private void addDatosAgenda() {
        try{
            Bundle arguments = new Bundle();
            Intent i = new Intent(getApplicationContext(),
                    DataEnterActivity.class);
            arguments.putString(Constants.FORM_NAME, Constants.FORM_DATOS_AGENDA);
            if (mZpDatos!=null) arguments.putSerializable(Constants.OBJECTO_ZPDATAAGENDA , mZpDatos);
            i.putExtras(arguments);
            startActivityForResult(i , ADD_DATOS);
        }
        catch(Exception e){
            Log.e(TAG, e.getMessage(), e);
            finish();
        }
    }

   @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fecha:
                datePicker();
                break;
          /*  case R.id.hora:
                timePicker();
                break;*/
            case R.id.cancelButton:
                finish();
                break;
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
            String res = "exito";
            try {
                zipA.open();
                zipA.crearZpAgendaStudio(mZpDatos);
                zipA.close();
            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
                res = "error";
            }
            return res;
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

    // ***************************************
    // Private classes
    // ***************************************
    private class DateNoSet{
        public int getDia() {
            return dia;
        }

        public int getMes() {
            switch (mes){
                case 1:
                    return Calendar.JANUARY;
                case 2:
                    return Calendar.FEBRUARY;
                case 3:
                    return Calendar.MARCH;
                case 4:
                    return Calendar.APRIL;
                case 5:
                    return Calendar.MAY;
                case 6:
                    return Calendar.JUNE;
                case 7:
                    return Calendar.JULY;
                case 8:
                    return Calendar.AUGUST;
                case 9:
                    return Calendar.SEPTEMBER;
                case 10:
                    return Calendar.OCTOBER;
                case 11:
                    return Calendar.NOVEMBER;
                default:
                    return Calendar.DECEMBER;
            }

        }

        private int dia;
        private int mes;

        public DateNoSet(int dia, int mes) {
            this.dia = dia;
            this.mes = mes;

        }
    }

}
