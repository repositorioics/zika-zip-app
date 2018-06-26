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
import ni.org.ics.zip.appmovil.activities.nuevos.NewZp07cInfantImageStudiesActivity;
import ni.org.ics.zip.appmovil.adapters.eventosinfante.Infant07aVisitAdapter;
import ni.org.ics.zip.appmovil.adapters.eventosinfante.Infant07cVisitAdapter;
import ni.org.ics.zip.appmovil.database.ZipAdapter;
import ni.org.ics.zip.appmovil.domain.*;
import ni.org.ics.zip.appmovil.utils.Constants;
import ni.org.ics.zip.appmovil.utils.MainDBConstants;
import ni.org.ics.zip.appmovil.utils.Zp02DBConstants;

import java.text.SimpleDateFormat;
import java.util.List;

public class Infant07cVisitActivity extends AbstractAsyncActivity {
	private ZipAdapter zipA;
	private static ZpInfantData zpInfante = new ZpInfantData();
	private static List<Zp07cInfantImageStudies> zp07c = null;
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
		//evento = getIntent().getStringExtra(Constants.EVENT);
		zpInfante = (ZpInfantData) getIntent().getExtras().getSerializable(Constants.OBJECTO_ZPINFDATA);
		//Aca se recupera los datos de los formularios para ver si estan realizados o no...
		new FetchUnshedVisitaInfanteTask().execute(evento);
		textView = (TextView) findViewById(R.id.label);
		textView.setText(getString(R.string.forms)+"\n"+
				getString(R.string.inf_id)+": "+zpInfante.getRecordId()+"\n"+
						getString(R.string.inf_dob)+": "+ mDateFormat.format(zpInfante.getInfantBirthDate()));
		menu_infante_info = getResources().getStringArray(R.array.menu_infant_exam);
		gridView = (GridView) findViewById(R.id.gridView1);
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Bundle arguments = new Bundle();
				Intent i;
				arguments.putString(Constants.EVENT, evento);
				arguments.putString(Constants.RECORDID, zpInfante.getRecordId());
				switch (position) {
					case 0: ////ESTUDIOS DE IMAGENES
						i = new Intent(getApplicationContext(),
								NewZp07cInfantImageStudiesActivity.class);
						arguments.putString(Constants.EVENT, Constants.EXAM1);
						if (!zp07c.isEmpty()) {
							for (Zp07cInfantImageStudies zp: zp07c) {
								if (zp.getRedcapEventName().matches(Constants.EXAM1)){
									arguments.putSerializable(Constants.OBJECTO_ZP07C, zp);
									break;
								}
							}
						}
						i.putExtras(arguments);
						startActivity(i);
						break;
					case 1: ////ESTUDIOS DE IMAGENES
						i = new Intent(getApplicationContext(),
								NewZp07cInfantImageStudiesActivity.class);
						arguments.putString(Constants.EVENT, Constants.EXAM2);
						if (!zp07c.isEmpty()) {
							for (Zp07cInfantImageStudies zp: zp07c) {
								if (zp.getRedcapEventName().matches(Constants.EXAM2)){
									arguments.putSerializable(Constants.OBJECTO_ZP07C, zp);
									break;
								}
							}
						}
						i.putExtras(arguments);
						startActivity(i);
						break;
					case 2: ////ESTUDIOS DE IMAGENES
						i = new Intent(getApplicationContext(),
								NewZp07cInfantImageStudiesActivity.class);
						arguments.putString(Constants.EVENT, Constants.EXAM3);
						if (!zp07c.isEmpty()) {
							for (Zp07cInfantImageStudies zp: zp07c) {
								if (zp.getRedcapEventName().matches(Constants.EXAM3)){
									arguments.putSerializable(Constants.OBJECTO_ZP07C, zp);
									break;
								}
							}
						}
						i.putExtras(arguments);
						startActivity(i);
						break;
					case 3: ////ESTUDIOS DE IMAGENES
						i = new Intent(getApplicationContext(),
								NewZp07cInfantImageStudiesActivity.class);
						arguments.putString(Constants.EVENT, Constants.EXAM4);
						if (!zp07c.isEmpty()) {
							for (Zp07cInfantImageStudies zp: zp07c) {
								if (zp.getRedcapEventName().matches(Constants.EXAM4)){
									arguments.putSerializable(Constants.OBJECTO_ZP07C, zp);
									break;
								}
							}
						}
						i.putExtras(arguments);
						startActivity(i);
						break;
					case 4: ////ESTUDIOS DE IMAGENES
						i = new Intent(getApplicationContext(),
								NewZp07cInfantImageStudiesActivity.class);
						arguments.putString(Constants.EVENT, Constants.EXAM5);
						if (!zp07c.isEmpty()) {
							for (Zp07cInfantImageStudies zp: zp07c) {
								if (zp.getRedcapEventName().matches(Constants.EXAM5)){
									arguments.putSerializable(Constants.OBJECTO_ZP07C, zp);
									break;
								}
							}
						}
						i.putExtras(arguments);
						startActivity(i);
						break;
					case 5: ////ESTUDIOS DE IMAGENES
						i = new Intent(getApplicationContext(),
								NewZp07cInfantImageStudiesActivity.class);
						arguments.putString(Constants.EVENT, Constants.EXAM6);
						if (!zp07c.isEmpty()) {
							for (Zp07cInfantImageStudies zp: zp07c) {
								if (zp.getRedcapEventName().matches(Constants.EXAM6)){
									arguments.putSerializable(Constants.OBJECTO_ZP07C, zp);
									break;
								}
							}
						}
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
		new FetchUnshedVisitaInfanteTask().execute(evento);
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
		private class FetchUnshedVisitaInfanteTask extends AsyncTask<String, Void, String> {
			//private String eventoaFiltrar = null;
			private String filtro = null;
			@Override
			protected void onPreExecute() {
				// before the request begins, show a progress indicator
				showLoadingProgressDialog();
			}

			@Override
			protected String doInBackground(String... values) {
				//eventoaFiltrar = values[0];
				try {
					zipA.open();
					filtro = MainDBConstants.recordId + "='" + zpInfante.getRecordId() + "' and " + MainDBConstants.redcapEventName  + " like '" + "exam_%" +"'";
					zp07c = zipA.getZp07cInfantImageStudies(filtro, MainDBConstants.recordId);
					zipA.close();
				} catch (Exception e) {
					Log.e(TAG, e.getLocalizedMessage(), e);
					return "Error";
				}
				return "Exito";
			}

			protected void onPostExecute(String resultado) {
				// after the network request completes, hide the progress indicator
				gridView.setAdapter(new Infant07cVisitAdapter(getApplicationContext(), R.layout.menu_item_2, menu_infante_info,
						 zp07c));
				dismissProgressDialog();
			}

		}

}
	
