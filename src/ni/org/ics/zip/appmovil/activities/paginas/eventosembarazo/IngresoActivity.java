package ni.org.ics.zip.appmovil.activities.paginas.eventosembarazo;

import java.text.SimpleDateFormat;

import ni.org.ics.zip.appmovil.AbstractAsyncActivity;
import ni.org.ics.zip.appmovil.MainActivity;
import ni.org.ics.zip.appmovil.MyZipApplication;
import ni.org.ics.zip.appmovil.R;
import ni.org.ics.zip.appmovil.activities.nuevos.NewZp01StudyEntrySectionAtoDActivity;
import ni.org.ics.zip.appmovil.adapters.eventosembarazo.IngresoAdapter;
import ni.org.ics.zip.appmovil.database.ZipAdapter;
import ni.org.ics.zip.appmovil.domain.Zp00Screening;
import ni.org.ics.zip.appmovil.domain.Zp01StudyEntrySectionAtoD;
import ni.org.ics.zip.appmovil.domain.Zp02BiospecimenCollection;
import ni.org.ics.zip.appmovil.domain.ZpEstadoEmbarazada;
import ni.org.ics.zip.appmovil.utils.Constants;
import ni.org.ics.zip.appmovil.utils.MainDBConstants;
import ni.org.ics.zip.appmovil.utils.Zp02DBConstants;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

public class IngresoActivity extends AbstractAsyncActivity {
	private ZipAdapter zipA;
	private static Zp00Screening zp00 = new Zp00Screening();
	private static ZpEstadoEmbarazada zpEstado = new ZpEstadoEmbarazada();
	private static Zp01StudyEntrySectionAtoD zp01a = new Zp01StudyEntrySectionAtoD();
	private static Zp02BiospecimenCollection zp02 = new Zp02BiospecimenCollection();
	
	private SimpleDateFormat mDateFormat = new SimpleDateFormat("MMM dd, yyyy");
	private static String evento;
	private GridView gridView;
	private TextView textView;
	private AlertDialog alertDialog;
	private static final int EXIT = 1;
	private boolean mExitShowing;
	private boolean pendiente = false;
	private static final String EXIT_SHOWING = "exitshowing";

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_maternal);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey(EXIT_SHOWING)) {
				mExitShowing = savedInstanceState.getBoolean(EXIT_SHOWING, false);
			}
		}
		String mPass = ((MyZipApplication) this.getApplication()).getPassApp();
		zipA = new ZipAdapter(this.getApplicationContext(),mPass,false);
		/*Aca se recupera evento, tamizaje y estado*/
		evento = getIntent().getStringExtra(Constants.EVENT);
		zp00 = (Zp00Screening) getIntent().getExtras().getSerializable(Constants.OBJECTO_ZP00);
		zpEstado = (ZpEstadoEmbarazada) getIntent().getExtras().getSerializable(Constants.OBJECTO_ZPEST);
		//Aca se recupera los datos de los formularios para ver si estan realizados o no...
		new FetchDataIngresoTask().execute(evento);
		textView = (TextView) findViewById(R.id.label);
		textView.setText(getString(R.string.forms)+"\n"+
				getString(R.string.mat_id)+": "+zp00.getRecordId()+"\n"+
						getString(R.string.mat_fec)+": "+ mDateFormat.format(zp00.getScrVisitDate()));
		String[] menu_maternal_info = getResources().getStringArray(R.array.menu_maternal_ingreso);
		gridView = (GridView) findViewById(R.id.gridView1);
		gridView.setAdapter(new IngresoAdapter(this, R.layout.menu_item_2, menu_maternal_info, zp01a, zp02));
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Bundle arguments = new Bundle();
				Intent i;
				switch(position){
				
				case 0:
					i = new Intent(getApplicationContext(),
							NewZp01StudyEntrySectionAtoDActivity.class);
					//Se pone el evento y el objeto en caso de que no sea nulo...
					arguments.putString(Constants.EVENT, Constants.ENTRY);
					if (zp01a!=null) arguments.putSerializable(Constants.OBJECTO_ZP01A, zp01a);
					i.putExtras(arguments);
					startActivity(i);
					break;
				default:					
					break;
				}
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.general, menu);
		return true;
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(EXIT_SHOWING, mExitShowing);
	}

	@Override
	protected void onResume() {
		//getParticipanteData();
		if (mExitShowing) {
			createDialog(EXIT);
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (alertDialog != null && alertDialog.isShowing()) {
			alertDialog.dismiss();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.MENU_BACK:
			if (pendiente){
				createDialog(EXIT);
			}
			else{
				finish();
			}
			return true;
		case R.id.MENU_HOME:
			if (pendiente){
				createDialog(EXIT);
			}
			else{
				i = new Intent(getApplicationContext(),
						MainActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				finish();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed (){
		if (pendiente){
			createDialog(EXIT);
		}
		else{
			finish();
		}
	}

	private void createDialog(int dialog) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		switch(dialog){
		case EXIT:
			builder.setTitle(this.getString(R.string.confirm));
			builder.setMessage(this.getString(R.string.ok));
			builder.setPositiveButton(this.getString(R.string.yes), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// Finish app
					dialog.dismiss();
					
					mExitShowing=false;
				}

			});
			builder.setNegativeButton(this.getString(R.string.no), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// Do nothing
					dialog.dismiss();
					mExitShowing=false;
				}
			});
			mExitShowing=true;
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
		private class FetchDataIngresoTask extends AsyncTask<String, Void, String> {
			private String eventoaFiltrar = null;
			private String filtro = null;
			@Override
			protected void onPreExecute() {
				// before the request begins, show a progress indicator
				showLoadingProgressDialog();
			}

			@Override
			protected String doInBackground(String... values) {
				eventoaFiltrar = values[0];
				try {
					zipA.open();
					filtro = MainDBConstants.recordId + "='" + zp00.getRecordId() + "'";
					zp01a = zipA.getZp01StudyEntrySectionAtoD(filtro, null);
					filtro = MainDBConstants.recordId + "='" + zp00.getRecordId() + "' and " + Zp02DBConstants.redcapEventName + "='" + eventoaFiltrar +"'";
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
			}

		}

}
	
