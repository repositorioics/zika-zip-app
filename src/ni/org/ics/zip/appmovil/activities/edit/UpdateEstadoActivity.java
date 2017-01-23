package ni.org.ics.zip.appmovil.activities.edit;

import java.text.SimpleDateFormat;
import ni.org.ics.zip.appmovil.AbstractAsyncActivity;
import ni.org.ics.zip.appmovil.MainActivity;
import ni.org.ics.zip.appmovil.MyZipApplication;
import ni.org.ics.zip.appmovil.R;
import ni.org.ics.zip.appmovil.activities.paginas.MenuEmbarazadasActivity;
import ni.org.ics.zip.appmovil.database.ZipAdapter;
import ni.org.ics.zip.appmovil.domain.Zp00Screening;
import ni.org.ics.zip.appmovil.preferences.PreferencesActivity;
import ni.org.ics.zip.appmovil.utils.Constants;
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
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

public class UpdateEstadoActivity extends AbstractAsyncActivity {

	private TextView mHeader;
	private Button mSaveButton;
	private Button mFinishButton;
	private RadioButton radioNoDef;
	private RadioButton radioInm;
	private RadioButton radioNoInm;
	
	private static final int EXIT = 1;
	private static final int HOME = 0;
	
	private AlertDialog alertDialog;

	private ZipAdapter zipA;
	private static Zp00Screening zp00 = new Zp00Screening();
	private String username;
	private String estado;
	private SharedPreferences settings;
	private SimpleDateFormat mDateFormat = new SimpleDateFormat("MMM dd, yyyy");

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.estado);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
		
		settings =
				PreferenceManager.getDefaultSharedPreferences(this);
		username =
				settings.getString(PreferencesActivity.KEY_USERNAME,
						null);
		
		zp00 = (Zp00Screening) getIntent().getExtras().getSerializable(Constants.OBJECTO_ZP00);
		estado = String.valueOf(zp00.getStudyInm());
		String mPass = ((MyZipApplication) this.getApplication()).getPassApp();
		zipA = new ZipAdapter(this.getApplicationContext(),mPass,false,false);
		zipA.open();
		
		mHeader = (TextView) findViewById(R.id.label_header);
		
		mHeader.setText(getString(R.string.status)+"\n"+
								getString(R.string.mat_id)+": "+zp00.getRecordId()+"\n"+
										getString(R.string.mat_fec)+": "+ mDateFormat.format(zp00.getScrVisitDate()));
		radioNoDef = (RadioButton) findViewById(R.id.radio_nodef);
		radioInm = (RadioButton) findViewById(R.id.radio_inm);
		radioNoInm = (RadioButton) findViewById(R.id.radio_noinm);
		
		if(estado.matches("0")){
			radioNoInm.setChecked(true);
		}
		else if(estado.matches("1")){
			radioInm.setChecked(true);
		}
		else if(estado.matches("2")){
			radioNoDef.setChecked(true);
		}
		
		mSaveButton = (Button) findViewById(R.id.saveButton);
		mSaveButton.setText(R.string.save);

		mSaveButton.setOnClickListener(new View.OnClickListener()  {
			@Override
			public void onClick(View v) {
				new SaveDataTask().execute(estado);
				
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
		
	}
	
	public void onRadioButtonClicked(View view) {
	    // Is the button now checked?
	    boolean checked = ((RadioButton) view).isChecked();
	    // Check which radio button was clicked
	    switch(view.getId()) {
	        case R.id.radio_nodef:
	            if (checked)
	            	estado = "2";
	            break;
	        case R.id.radio_inm:
	            if (checked)
	            	estado = "1";
	            break;
	        case R.id.radio_noinm:
	            if (checked)
	            	estado = "0";
	            break;
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
		if(item.getItemId()==R.id.MENU_BACK){
			createDialog(EXIT);
			return true;
		}
		else if(item.getItemId()==R.id.MENU_HOME){
			createDialog(HOME);
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
					Bundle arguments = new Bundle();
			        if (zp00!=null) arguments.putSerializable(Constants.OBJECTO_ZP00 , zp00);
			        Intent i = new Intent(getApplicationContext(),
			                MenuEmbarazadasActivity.class);
			        i.putExtras(arguments);
			        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			        startActivity(i);
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
		case HOME:
			builder.setTitle(this.getString(R.string.confirm));
			builder.setMessage(this.getString(R.string.exiting));
			builder.setPositiveButton(this.getString(R.string.yes), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// Finish app
					dialog.dismiss();
					zipA.close();
					Intent i = new Intent(getApplicationContext(),
							MainActivity.class);
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);
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
		super.onActivityResult(requestCode, resultCode, intent);

	}
	
	// ***************************************
	// Private classes
	// ***************************************
	private class SaveDataTask extends AsyncTask<String, Void, String> {
		private String estadoMod = null;
		@Override
		protected void onPreExecute() {
			// before the request begins, show a progress indicator
			showLoadingProgressDialog();
		}

		@Override
		protected String doInBackground(String... values) {
			estadoMod = values[0];
			try {
				zp00.setStudyInm(estadoMod.charAt(0));
				zp00.setEstado(Constants.STATUS_NOT_SUBMITTED);
				zp00.setRecordUser(username);
				zipA.editarZp00Screening(zp00);
				return "exito";
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
		showToast(resultado);
	}	
		
	
}
