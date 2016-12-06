package ni.org.ics.zip.appmovil.activities.nuevos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import ni.org.ics.zip.appmovil.AbstractAsyncActivity;
import ni.org.ics.zip.appmovil.MainActivity;
import ni.org.ics.zip.appmovil.MyZipApplication;
import ni.org.ics.zip.appmovil.R;
import ni.org.ics.zip.appmovil.activities.nuevos.NewZpControlConsentimientosRecepcionActivity;
import ni.org.ics.zip.appmovil.database.ZipAdapter;
import ni.org.ics.zip.appmovil.domain.ZpControlConsentimientosRecepcion;
import ni.org.ics.zip.appmovil.preferences.PreferencesActivity;
import ni.org.ics.zip.appmovil.utils.Constants;
import ni.org.ics.zip.appmovil.utils.DeviceInfo;
import ni.org.ics.zip.appmovil.utils.MainDBConstants;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

public class NewZpControlConsentimientosRecepcionActivity extends AbstractAsyncActivity {

	private TextView mHeader;
	private Spinner mLugarView;
	private Spinner mPersonaView;
	private EditText mCodigoView;
	private Button mSaveButton;
	private Button mFinishButton;
	private ImageButton mBarcodeButton;
	private String mCodigo;
	private Date todayWithZeroTime = null;
	
	private static final int EXIT = 1;
	private static final int BARCODE_CAPTURE = 2;
	
	private AlertDialog alertDialog;

	private ZipAdapter zipA;
	private ZpControlConsentimientosRecepcion mRecepcionCons = null;
	private String username;
	private SharedPreferences settings;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.consentimientos);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
		
		settings =
				PreferenceManager.getDefaultSharedPreferences(this);
		username =
				settings.getString(PreferencesActivity.KEY_USERNAME,
						null);
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Date today = new Date();
		try {
			todayWithZeroTime =formatter.parse(formatter.format(today));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String mPass = ((MyZipApplication) this.getApplication()).getPassApp();
		zipA = new ZipAdapter(this.getApplicationContext(),mPass,false,false);
		zipA.open();
		mHeader = (TextView) findViewById(R.id.label_header);
		mLugarView = (Spinner) findViewById(R.id.lugar);
		mPersonaView = (Spinner) findViewById(R.id.persona);
		mCodigoView = (EditText) findViewById(R.id.codigo);
		mHeader.setText(R.string.consent_2);
		
		String[] list_lugares = getResources().getStringArray(R.array.list_lugares);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list_lugares);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
		mLugarView.setAdapter(dataAdapter);
		
		String[] list_personas = getResources().getStringArray(R.array.list_drivers);
		ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list_personas);
		dataAdapter2.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
		mPersonaView.setAdapter(dataAdapter2);

		mSaveButton = (Button) findViewById(R.id.saveButton);
		mSaveButton.setText(R.string.save);

		mSaveButton.setOnClickListener(new View.OnClickListener()  {
			@Override
			public void onClick(View v) {
				if(validarEntrada()){
					new SaveDataTask().execute(mLugarView.getSelectedItem().toString(),
							mPersonaView.getSelectedItem().toString(),
							mCodigoView.getText().toString());
				}
				
			}
		});
		
		mFinishButton = (Button) findViewById(R.id.cancelButton);
		mFinishButton.setText(R.string.finish);
		mFinishButton.setOnClickListener(new View.OnClickListener()  {
			@Override
			public void onClick(View v) {
				createDialog(EXIT);
			}
		});
		
		mBarcodeButton = (ImageButton) findViewById(R.id.barcode_button);
		mBarcodeButton.setOnClickListener(new View.OnClickListener()  {
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
		

	}
	
	private boolean validarEntrada() {
		//Valida la entrada 
		if (mLugarView.getSelectedItemPosition()==0){
			showToast(this.getString( R.string.error_lugar));
			return false;
		}
		if (mPersonaView.getSelectedItemPosition()==0){
			showToast(this.getString( R.string.error_persona));
			return false;
		}
		if (mCodigo==null){
			showToast(this.getString( R.string.scan_error));
			return false;
		}
		else{
			return true;
		}
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
			createDialog(EXIT);
			return true;
		}
		else if(item.getItemId()==R.id.MENU_BACK){
			createDialog(EXIT);
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
	public void onBackPressed (){
		createDialog(EXIT);
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
					zipA.close();
					finish();
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		if (requestCode == BARCODE_CAPTURE && intent != null) {
			String sb = intent.getStringExtra("SCAN_RESULT");
			if (sb != null && sb.length() > 0) {
				try{
					if(!(sb.matches("^07[0-9][0-9][0-9][0-9][0-3][A-Y]$"))){
						showToast(sb + " " + getString(R.string.scan_error));
						mCodigoView.setText(null);
						mCodigo=null;
						return;
					}
					mCodigoView.setText(sb);
					mCodigo = sb;
				}
				catch(Exception e){
					showToast(e.getLocalizedMessage());
					return;
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, intent);

	}
	
	// ***************************************
	// Private classes
	// ***************************************
	private class SaveDataTask extends AsyncTask<String, Void, String> {
		private String lugar = null;
		private String persona = null;
		private String codigo = null;
		private ZpControlConsentimientosRecepcion zpVerificacion = null;
		@Override
		protected void onPreExecute() {
			// before the request begins, show a progress indicator
			showLoadingProgressDialog();
		}

		@Override
		protected String doInBackground(String... values) {
			lugar = values[0];
			persona = values[1];
			codigo = values[2];
			try {
				Date fecha = new Date();
				mRecepcionCons = new ZpControlConsentimientosRecepcion();
				mRecepcionCons.setLugarLlegada(lugar);
				mRecepcionCons.setPersona(persona);
				mRecepcionCons.setCodigo(codigo);
				mRecepcionCons.setFechaHoraLLegada(todayWithZeroTime);
				mRecepcionCons.setRecordDate(fecha);
				mRecepcionCons.setRecordUser(username);
				mRecepcionCons.setDeviceid(new DeviceInfo(NewZpControlConsentimientosRecepcionActivity.this).getDeviceId());
				mRecepcionCons.setEstado(Constants.STATUS_NOT_SUBMITTED);
				mRecepcionCons.setToday(fecha);
				zpVerificacion = zipA.getZpControlConsentimientosRecepcion(MainDBConstants.codigo + "='" + mRecepcionCons.getCodigo() + "' and " + MainDBConstants.fechaHoraLLegada + "=" + mRecepcionCons.getFechaHoraLLegada().getTime(), null);
				if(zpVerificacion!=null){
					return "Código ya fue ingresado hoy";
				}
				else{
					zipA.crearZpControlConsentimientosRecepcion(mRecepcionCons);
				}
				zpVerificacion = zipA.getZpControlConsentimientosRecepcion(MainDBConstants.codigo + "='" + mRecepcionCons.getCodigo() + "' and " + MainDBConstants.fechaHoraLLegada + "=" + mRecepcionCons.getFechaHoraLLegada().getTime(), null);
				if(zpVerificacion!=null){
					return "exito";
				}
				else{
					return "Error no capturado";
				}
			} catch (Exception e) {
				Log.e(TAG, e.getLocalizedMessage(), e);
				return e.getLocalizedMessage();
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
		if(resultado.matches("exito")){
			Toast.makeText(getApplicationContext(),	R.string.success, Toast.LENGTH_LONG).show();
			mCodigoView.setText(null);
			mCodigo=null;
		}		
		else{
			showToast(resultado);
		}
	}	

	
	
}
