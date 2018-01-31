package ni.org.ics.zip.appmovil.activities.paginas.agenda;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Calendar;
import ni.org.ics.zip.appmovil.AbstractAsyncListActivity;
import ni.org.ics.zip.appmovil.MainActivity;
import ni.org.ics.zip.appmovil.MyZipApplication;
import ni.org.ics.zip.appmovil.R;
import ni.org.ics.zip.appmovil.activities.paginas.MenuEmbarazadasActivity;
import ni.org.ics.zip.appmovil.activities.server.DownloadAgendaActivity;
import ni.org.ics.zip.appmovil.activities.server.UploadAllActivity;
import ni.org.ics.zip.appmovil.activities.utils.Dialogs;
import ni.org.ics.zip.appmovil.adapters.ZpAgendaEstudioAdapter;
import ni.org.ics.zip.appmovil.database.ZipAdapter;
import ni.org.ics.zip.appmovil.domain.Zp00Screening;
import ni.org.ics.zip.appmovil.domain.ZpAgendaEstudio;
import ni.org.ics.zip.appmovil.utils.Constants;
import ni.org.ics.zip.appmovil.utils.MainDBConstants;

public class Appointments extends AbstractAsyncListActivity implements View.OnClickListener {

    private EditText mParametroView;
    private ImageButton mFindButton;
    private Button mAddButton;
    private Button mDownloadButton;
    private SimpleDateFormat sdf;
    private ZipAdapter zipA;
    private List<ZpAgendaEstudio> mCitas = new ArrayList<ZpAgendaEstudio>();
    private AlertDialog alertDialog;
    private static final int EXIT = 1;
    private static final int DOWNLOAD = 2;
    private static final int VERIFY = 4;
    private static final int UPDATE_EQUIPO = 11;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);
        sdf = new SimpleDateFormat("dd-MM-yyyy");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar actionBar = getActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        String mPass = ((MyZipApplication) this.getApplication()).getPassApp();
        zipA = new ZipAdapter(this.getApplicationContext(),mPass,false,false);

        List<String> list = new ArrayList<String>();
        list.add(getString(R.string.desc_barcode));
        list.add(getString(R.string.enter)+" "+getString(R.string.mat_id));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);


        mParametroView = (EditText) findViewById(R.id.parametro_edit_text);


        mFindButton = (ImageButton) findViewById(R.id.find_button);
        mAddButton = (Button) findViewById(R.id.add_button);
        mDownloadButton = (Button) findViewById(R.id.update_button);
        mParametroView.setVisibility(View.VISIBLE);
        mFindButton.setVisibility(View.VISIBLE);
       // mBarcodeButton.setVisibility(View.GONE);

        mParametroView.setHint(getString(R.string._appointment_date));


        mAddButton.setText(getString(R.string.add)+ " " +getString(R.string._appointment));


        mParametroView.setText(sdf.format (new Date()));


        /*
        mParametroView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    datePicker((EditText)v);
                }
            }
        });
*/
        mFindButton.requestFocus();

        mParametroView.setOnClickListener(this);
        mDownloadButton.setOnClickListener(this);

        mFindButton.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
                mCitas.clear();
                if ((mParametroView.getText().toString()== null) || (mParametroView.getText().toString().matches(""))){
                    mParametroView.requestFocus();
                    mParametroView.setError(getString(R.string.search_hint));
                    return;
                }
                String text = mParametroView.getText().toString();
                /* validar */
                if(!mParametroView.getText().toString().matches("\\d{2}-\\d{2}-\\d{4}")){
                    mParametroView.requestFocus();
                    mParametroView.setError(getString(R.string.code_error));
                    return;
                }
                String fechaSt =  text;
                Date fechaBusq = new Date();
                try
                {
                    fechaBusq = sdf.parse(fechaSt);
                }
                catch (ParseException ex){

                }
                buscarCitas(fechaBusq);
            }
        });


        mFindButton.setVisibility(View.VISIBLE);

        mAddButton.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),
                        NewAppointment.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });


    }

    private void datePicker(EditText mEditText) {

        Dialogs.ShowDatePickerDialog(this,mEditText);

    }


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



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.general, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
            return true;
        }
        else if(item.getItemId()==R.id.MENU_BACK){
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



    public void buscarCitas(Date parametro){
        Calendar c = Calendar.getInstance();
        c.setTime(parametro);
        c.add(Calendar.DAY_OF_YEAR, 1);
        Date fechaF = c.getTime();

        String filtro = MainDBConstants.appointmentDateTime + ">=" + String.valueOf(parametro.getTime()) + " AND "
                + MainDBConstants.appointmentDateTime + " < "+ String.valueOf(fechaF.getTime());
        new Appointments.FetchDataTask().execute(filtro);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.parametro_edit_text:
                datePicker((EditText) v);
                break;
            case R.id.update_button:
                createDialog(DOWNLOAD);
                break;

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_CANCELED) {
            if (requestCode == UPDATE_EQUIPO){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getApplicationContext().getString(R.string.error));
                builder.setIcon(R.drawable.ic_error);
                builder.setMessage(intent.getStringExtra("resultado"))
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                return;
            }
        }
        else{
            if (requestCode == UPDATE_EQUIPO){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getApplicationContext().getString(R.string.confirm));
                builder.setIcon(R.drawable.ic_ok);
                builder.setMessage(getApplicationContext().getString(R.string.success))
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
            return;
        }
    }

    private void createDialog(int dialog) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch(dialog){
            case EXIT:
                builder.setTitle(this.getString(R.string.confirm));
                builder.setMessage(this.getString(R.string.exiting));
                builder.setPositiveButton(this.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Finish app
                        dialog.dismiss();
                        finish();
                    }
                });
                builder.setNegativeButton(this.getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                break;
            case DOWNLOAD:
                builder.setTitle(this.getString(R.string.confirm));
                builder.setMessage(this.getString(R.string.downloadingAgenda));
                builder.setIcon(android.R.drawable.ic_menu_help);
                builder.setPositiveButton(this.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        zipA.open();
                        if(zipA.verificarAgendaData()){
                            createDialog(VERIFY);
                        }
                        else{
                            Intent ie = new Intent(getApplicationContext(), DownloadAgendaActivity.class);
                            startActivityForResult(ie, UPDATE_EQUIPO);
                        }
                        zipA.close();
                    }
                });
                builder.setNegativeButton(this.getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        dialog.dismiss();
                    }
                });
                break;

            case VERIFY:
                builder.setTitle(this.getString(R.string.confirm));
                builder.setMessage(this.getString(R.string.data_not_sent));
                builder.setIcon(android.R.drawable.ic_menu_help);
                builder.setPositiveButton(this.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent ie = new Intent(getApplicationContext(), DownloadAgendaActivity.class);
                        startActivityForResult(ie, UPDATE_EQUIPO);
                    }
                });
                builder.setNegativeButton(this.getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        dialog.dismiss();
                    }
                });
                break;
            default:
                break;
        }
        alertDialog = builder.create();
        alertDialog.show();
    }



    // ***************************************
    // Private classes
    // ***************************************
    private class FetchDataTask extends AsyncTask<String, Void, String> {
        private String filtro = null;
        @Override
        protected void onPreExecute() {
            // before the request begins, show a progress indicator
            showLoadingProgressDialog();
        }

        @Override
        protected String doInBackground(String... values) {
            filtro = values[0];
            try {
                zipA.open();
                mCitas = zipA.getAgendaStudios(filtro, MainDBConstants.appointmentDateTime);
                /*
                if(mCitas.size() == 0)
                    mCitas.add(new ZpAgendaEstudio()); // Tratar de evitar el error
                    */
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
        try {

            if (mCitas.isEmpty())
                showToast(getString(R.string.no_items));
            else{
                ZpAgendaEstudioAdapter adapter = new ZpAgendaEstudioAdapter(this, R.layout.complex_list_item, mCitas);
                setListAdapter(adapter);
            }

        }
        catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage(), e);

        }
    }


}


