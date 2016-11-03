package ni.org.ics.zip.appmovil.activities.paginas;

import java.text.SimpleDateFormat;

import ni.org.ics.zip.appmovil.AbstractAsyncActivity;
import ni.org.ics.zip.appmovil.MainActivity;
import ni.org.ics.zip.appmovil.MyZipApplication;
import ni.org.ics.zip.appmovil.R;
import ni.org.ics.zip.appmovil.activities.paginas.eventosembarazo.IngresoActivity;
import ni.org.ics.zip.appmovil.adapters.MenuEmbarazadasAdapter;
import ni.org.ics.zip.appmovil.database.ZipAdapter;
import ni.org.ics.zip.appmovil.domain.Zp00Screening;
import ni.org.ics.zip.appmovil.domain.ZpEstadoEmbarazada;
import ni.org.ics.zip.appmovil.utils.Constants;
import ni.org.ics.zip.appmovil.utils.MainDBConstants;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

public class MenuEmbarazadasActivity extends AbstractAsyncActivity {

	private static Zp00Screening zp00 = new Zp00Screening();
	private static ZpEstadoEmbarazada zpEstado = new ZpEstadoEmbarazada();
	private GridView gridView;
	private TextView textView;
	private SimpleDateFormat mDateFormat = new SimpleDateFormat("MMM dd, yyyy");
	private ZipAdapter zipA;
	String[] menu_maternal_info;
	String filtro;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_maternal);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
		String mPass = ((MyZipApplication) this.getApplication()).getPassApp();
		zipA = new ZipAdapter(this.getApplicationContext(),mPass,false);
		zp00 = (Zp00Screening) getIntent().getExtras().getSerializable(Constants.OBJECTO_ZP00);
		filtro = MainDBConstants.recordId + "='" + zp00.getRecordId() + "'";
		new FetchDataEmbarazadaTask().execute(filtro);
		textView = (TextView) findViewById(R.id.label);
		textView.setTextColor(Color.BLUE);
		textView.setText(getString(R.string.maternal_events)+"\n"+
							getString(R.string.mat_id)+": "+zp00.getRecordId()+"\n"+
									getString(R.string.mat_fec)+": "+ mDateFormat.format(zp00.getScrVisitDate()));
		menu_maternal_info = getResources().getStringArray(R.array.menu_maternal_a);
		gridView = (GridView) findViewById(R.id.gridView1);
		
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Bundle arguments = new Bundle();
				Intent i;
				switch(position){
				
				case 0:
					i = new Intent(getApplicationContext(),
							IngresoActivity.class);
					/*Aca se pasa evento, tamizaje y estado*/
					arguments.putString(Constants.EVENT, Constants.ENTRY);
					if (zp00!=null) arguments.putSerializable(Constants.OBJECTO_ZP00 , zp00);
					if (zpEstado!=null) arguments.putSerializable(Constants.OBJECTO_ZPEST , zpEstado);
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
	}

	@Override
	protected void onResume() {
		new FetchDataEmbarazadaTask().execute(filtro);
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.MENU_BACK:
			finish();
			return true;
		case R.id.MENU_HOME:
			i = new Intent(getApplicationContext(),
					MainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	
	// ***************************************
	// Private classes
	// ***************************************
	private class FetchDataEmbarazadaTask extends AsyncTask<String, Void, String> {
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
				zpEstado = zipA.getZpEstadoEmbarazada(filtro, MainDBConstants.recordId);
				zipA.close();
			} catch (Exception e) {
				Log.e(TAG, e.getLocalizedMessage(), e);
				return "error";
			}
			return "exito";
		}

		protected void onPostExecute(String resultado) {
			// after the network request completes, hide the progress indicator
			gridView.setAdapter(new MenuEmbarazadasAdapter(getApplicationContext(), R.layout.menu_item_2, menu_maternal_info, zp00, zpEstado));
			dismissProgressDialog();
		}

	}
		
		
}
	
