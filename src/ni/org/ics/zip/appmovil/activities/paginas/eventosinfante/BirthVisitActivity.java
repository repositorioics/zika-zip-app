package ni.org.ics.zip.appmovil.activities.paginas.eventosinfante;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import ni.org.ics.zip.appmovil.AbstractAsyncActivity;
import ni.org.ics.zip.appmovil.MainActivity;
import ni.org.ics.zip.appmovil.MyZipApplication;
import ni.org.ics.zip.appmovil.R;
import ni.org.ics.zip.appmovil.activities.nuevos.*;
import ni.org.ics.zip.appmovil.adapters.eventosinfante.InfantVisitAdapter;
import ni.org.ics.zip.appmovil.database.ZipAdapter;
import ni.org.ics.zip.appmovil.domain.*;
import ni.org.ics.zip.appmovil.utils.Constants;
import ni.org.ics.zip.appmovil.utils.MainDBConstants;
import ni.org.ics.zip.appmovil.utils.Zp02DBConstants;

import java.text.SimpleDateFormat;

public class BirthVisitActivity extends AbstractAsyncActivity {
	private ZipAdapter zipA;
	private static ZpInfantData zpInfante = new ZpInfantData();
	private static ZpEstadoInfante zpEstado = new ZpEstadoInfante();
	private static Zp02dInfantBiospecimenCollection zp02d = null;
	private static Zp07InfantAssessmentVisit zp07 = null;
	private static Zp07InfantOtoacousticEmissions zp07OtoE = null;
	
	private SimpleDateFormat mDateFormat = new SimpleDateFormat("MMM dd, yyyy");
	private static String evento;
	private GridView gridView;
	private TextView textView;
	private AlertDialog alertDialog;
	private static final int EXIT = 1;
	private boolean mExitShowing;
	private boolean pendiente = false;
	private static final String EXIT_SHOWING = "exitshowing";
	String[] menu_infante_info;

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
		zipA = new ZipAdapter(this.getApplicationContext(),mPass,false,false);
		/*Aca se recupera evento, tamizaje y estado*/
		evento = getIntent().getStringExtra(Constants.EVENT);
		zpInfante = (ZpInfantData) getIntent().getExtras().getSerializable(Constants.OBJECTO_ZPINFDATA);
		zpEstado = (ZpEstadoInfante) getIntent().getExtras().getSerializable(Constants.OBJECTO_ZPESTINF);
		//Aca se recupera los datos de los formularios para ver si estan realizados o no...
		new FetchVisitInfanteTask().execute(evento);
		textView = (TextView) findViewById(R.id.label);
		textView.setText(getString(R.string.forms)+"\n"+
				getString(R.string.inf_id)+": "+zpInfante.getRecordId()+"\n"+
						getString(R.string.inf_dob)+": "+ mDateFormat.format(zpInfante.getInfantBirthDate()));
		menu_infante_info = getResources().getStringArray(R.array.menu_infant_visit1);
		gridView = (GridView) findViewById(R.id.gridView1);
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Bundle arguments = new Bundle();
				Intent i;
				arguments.putString(Constants.EVENT, evento);
                arguments.putString(Constants.RECORDID, zpInfante.getRecordId());
				switch(position){
					case 0: //MUESTRAS
						i = new Intent(getApplicationContext(),
								NewZp02dInfantBiospecimenCollectionActivity.class);
						if (zp02d != null) arguments.putSerializable(Constants.OBJECTO_ZP02D, zp02d);
						i.putExtras(arguments);
						startActivity(i);
						break;
					case 1: //EVALUACION
						i = new Intent(getApplicationContext(),
								NewZp07InfantAssessmentVisitActivity.class);
						if (zp07 != null) arguments.putSerializable(Constants.OBJECTO_ZP07, zp07);
						i.putExtras(arguments);
						startActivity(i);
						break;
					case 2: //EVALUACION OFTALMOLOGICA
						i = new Intent(getApplicationContext(),
								NewZp07InfantAssessmentVisitOphtActivity.class);
						if (zp07 != null) arguments.putSerializable(Constants.OBJECTO_ZP07, zp07);
						i.putExtras(arguments);
						startActivity(i);
						break;
					case 3: //EVALUACION OTOACUSTICA
						i = new Intent(getApplicationContext(),
								NewZp07InfantOtoacousticEmissionsActivity.class);
						if (zp07OtoE != null) arguments.putSerializable(Constants.OBJECTO_ZP07OtoE, zp07OtoE);
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
		new FetchVisitInfanteTask().execute(evento);
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
		private class FetchVisitInfanteTask extends AsyncTask<String, Void, String> {
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
					filtro = MainDBConstants.recordId + "='" + zpInfante.getRecordId() + "' and " + Zp02DBConstants.redcapEventName + "='" + eventoaFiltrar +"'";
					zp02d = zipA.getZp02dInfantBiospecimenCollection(filtro, MainDBConstants.recordId);
					zp07 = zipA.getZp07InfantAssessmentVisit(filtro, MainDBConstants.recordId);
					zp07OtoE = zipA.getZp07InfantOtoacousticE(filtro, MainDBConstants.recordId);

					if (zp02d!=null && zp07!=null && zp07OtoE!=null){
						if(eventoaFiltrar.matches(Constants.BIRTH)){
							zpEstado.setNacimiento('1');
						}
						if(eventoaFiltrar.matches(Constants.MONTH3)){
							zpEstado.setMes3('1');
						}
						if(eventoaFiltrar.matches(Constants.MONTH6)){
							zpEstado.setMes6('1');
						}
						if(eventoaFiltrar.matches(Constants.MONTH12)){
							zpEstado.setMes12('1');
						}
						zipA.editarZpEstadoInfante(zpEstado);
					}
					zipA.close();
				} catch (Exception e) {
					Log.e(TAG, e.getLocalizedMessage(), e);
					return "Error";
				}
				return "Exito";
			}

			protected void onPostExecute(String resultado) {
				// after the network request completes, hide the progress indicator
				gridView.setAdapter(new InfantVisitAdapter(getApplicationContext(), R.layout.menu_item_2, menu_infante_info,
						zp02d, zp07, zp07OtoE));
				dismissProgressDialog();
			}

		}

}
	
