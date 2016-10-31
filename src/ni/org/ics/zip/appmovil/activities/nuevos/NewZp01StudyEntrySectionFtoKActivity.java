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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import ni.org.ics.zip.appmovil.AbstractAsyncActivity;
import ni.org.ics.zip.appmovil.MainActivity;
import ni.org.ics.zip.appmovil.MyZipApplication;
import ni.org.ics.zip.appmovil.R;
import ni.org.ics.zip.appmovil.database.ZipAdapter;
import ni.org.ics.zip.appmovil.domain.Zp00Screening;
import ni.org.ics.zip.appmovil.domain.Zp01StudyEntrySectionFtoK;
import ni.org.ics.zip.appmovil.parsers.Zp01StudyEntrySectionFtoKXml;
import ni.org.ics.zip.appmovil.preferences.PreferencesActivity;
import ni.org.ics.zip.appmovil.utils.Constants;
import ni.org.ics.zip.appmovil.utils.FileUtils;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.util.Date;


public class NewZp01StudyEntrySectionFtoKActivity extends AbstractAsyncActivity {

	protected static final String TAG = NewZp01StudyEntrySectionFtoKActivity.class.getSimpleName();
	
	private ZipAdapter zipA;
	private static Zp01StudyEntrySectionFtoK mIngreso = new Zp01StudyEntrySectionFtoK();
	
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
        Zp00Screening screening = (Zp00Screening) getIntent().getExtras().getSerializable(Constants.OBJECTO);
        mRecordId = screening.getRecordId();
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
		yes.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				dialogInit.dismiss();
				addZp01StudyEntryFtoK();
			}
		});

		Button no = (Button) dialogInit.findViewById(R.id.yesnoNo);
		no.setOnClickListener(new OnClickListener() {

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
					parseZp01StudyEntryFtoK(idInstancia, instanceFilePath);
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

	/**
	 * 
	 */
	private void addZp01StudyEntryFtoK() {
		try{
			//campos de proveedor de collect
			String[] projection = new String[] {
					"_id","jrFormId","displayName"};
			//cursor que busca el formulario
			Cursor c = getContentResolver().query(Constants.CONTENT_URI, projection,
					"jrFormId = 'zp01_study_entry_f_k' and displayName = 'Estudio ZIP Visita inicial en el estudio 3'", null, null);
			c.moveToFirst();
			//captura el id del formulario
			Integer id = Integer.parseInt(c.getString(0));
			//cierra el cursor
			if (c != null) {
				c.close();
			}
			//forma el uri para ODK Collect
			Uri formUri = ContentUris.withAppendedId(Constants.CONTENT_URI,id);
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
	
	private void parseZp01StudyEntryFtoK(Integer idInstancia, String instanceFilePath) {
		Serializer serializer = new Persister(); 
		File source = new File(instanceFilePath);
		try {
			Zp01StudyEntrySectionFtoKXml zp01Xml = new Zp01StudyEntrySectionFtoKXml();
			zp01Xml = serializer.read(Zp01StudyEntrySectionFtoKXml.class, source);
			mIngreso.setRecordId(mRecordId);
			mIngreso.setSeaPreg(zp01Xml.getSeaPreg());
            mIngreso.setSeaFirstPreg(zp01Xml.getSeaFirstPreg());
            mIngreso.setSeaAnemia(zp01Xml.getSeaAnemia());
            mIngreso.setSeaVaginal(zp01Xml.getSeaVaginal());
            mIngreso.setSeaUtiPrior(zp01Xml.getSeaUtiPrior());
            mIngreso.setSeaSexually(zp01Xml.getSeaSexually());
            mIngreso.setSeaPreterm(zp01Xml.getSeaPreterm());
            mIngreso.setSeaPreeclampsia(zp01Xml.getSeaPreeclampsia());
            mIngreso.setSeaEclampsia(zp01Xml.getSeaEclampsia());
            mIngreso.setSeaHeart(zp01Xml.getSeaHeart());
            mIngreso.setSeaNeuropathy(zp01Xml.getSeaNeuropathy());
            mIngreso.setSeaGestational(zp01Xml.getSeaGestational());
            mIngreso.setSeaTotalPreg(zp01Xml.getSeaTotalPreg());
            mIngreso.setSeaDeliveryDate1(zp01Xml.getSeaDeliveryDate1());
            mIngreso.setSeaGage1(zp01Xml.getSeaGage1());
            mIngreso.setSeaOutcome1(zp01Xml.getSeaOutcome1());
            mIngreso.setSeaBdefects1(zp01Xml.getSeaBdefects1());
            mIngreso.setSeaDeliveryDate2(zp01Xml.getSeaDeliveryDate2());
            mIngreso.setSeaGage2(zp01Xml.getSeaGage2());
            mIngreso.setSeaOutcome2(zp01Xml.getSeaOutcome2());
            mIngreso.setSeaBdefects2(zp01Xml.getSeaBdefects2());
            mIngreso.setSeaDeliveryDate3(zp01Xml.getSeaDeliveryDate3());
            mIngreso.setSeaGage3(zp01Xml.getSeaGage3());
            mIngreso.setSeaOutcome3(zp01Xml.getSeaOutcome3());
            mIngreso.setSeaBdefects3(zp01Xml.getSeaBdefects3());
            mIngreso.setSeaDeliveryDate4(zp01Xml.getSeaDeliveryDate4());
            mIngreso.setSeaGage4(zp01Xml.getSeaGage4());
            mIngreso.setSeaOutcome4(zp01Xml.getSeaOutcome4());
            mIngreso.setSeaBdefects4(zp01Xml.getSeaBdefects4());
            mIngreso.setSeaDeliveryDate5(zp01Xml.getSeaDeliveryDate5());
            mIngreso.setSeaGage5(zp01Xml.getSeaGage5());
            mIngreso.setSeaOutcome5(zp01Xml.getSeaOutcome5());
            mIngreso.setSeaBdefects5(zp01Xml.getSeaBdefects5());
            mIngreso.setSeaDeliveryDate6(zp01Xml.getSeaDeliveryDate6());
            mIngreso.setSeaGage6(zp01Xml.getSeaGage6());
            mIngreso.setSeaOutcome6(zp01Xml.getSeaOutcome6());
            mIngreso.setSeaBdefects6(zp01Xml.getSeaBdefects6());
            mIngreso.setSeaPersisHeadache(zp01Xml.getSeaPersisHeadache());
            mIngreso.setSeaDizziness(zp01Xml.getSeaDizziness());
            mIngreso.setSeaNausea(zp01Xml.getSeaNausea());
            mIngreso.setSeaVomiting(zp01Xml.getSeaVomiting());
            mIngreso.setSeaSeeingLights(zp01Xml.getSeaSeeingLights());
            mIngreso.setSeaSpecifyType(zp01Xml.getSeaSpecifyType());
            mIngreso.setSeaSwelling(zp01Xml.getSeaSwelling());
            mIngreso.setSeaFetalMov(zp01Xml.getSeaFetalMov());
            mIngreso.setSeaMovUsual(zp01Xml.getSeaMovUsual());
            mIngreso.setSeaMovDecreased(zp01Xml.getSeaMovDecreased());
            mIngreso.setSeaContraction(zp01Xml.getSeaContraction());
            mIngreso.setSeaFreqWeek(zp01Xml.getSeaFreqWeek());
            mIngreso.setSeaFreqDay(zp01Xml.getSeaFreqDay());
            mIngreso.setSeaFreqHour(zp01Xml.getSeaFreqHour());
            mIngreso.setSeaFreqMin(zp01Xml.getSeaFreqMin());
            mIngreso.setSeaVagiDischarge(zp01Xml.getSeaVagiDischarge());
            mIngreso.setSeaCharacterDisch(zp01Xml.getSeaCharacterDisch());//multiple
            mIngreso.setSeaBleeding(zp01Xml.getSeaBleeding());
            mIngreso.setSeaYesBleeding(zp01Xml.getSeaYesBleeding());
            mIngreso.setSeaUti(zp01Xml.getSeaUti());
            mIngreso.setSeaPrenatalCare(zp01Xml.getSeaDateCompleted());
            mIngreso.setSeaMutiv(zp01Xml.getSeaMutiv());
            mIngreso.setSeaIron(zp01Xml.getSeaIron());
            mIngreso.setSeaOften(zp01Xml.getSeaOften());
            mIngreso.setSeaProvideSym(zp01Xml.getSeaProvideSym());
            mIngreso.setSeaReminderPreg(zp01Xml.getSeaReminderPreg());
            mIngreso.setSeaReminderProvided(zp01Xml.getSeaReminderProvided());
            mIngreso.setSeaOneweekDate(zp01Xml.getSeaOneweekDate());
            mIngreso.setSeaOneweekTime(zp01Xml.getSeaOneweekTime());
            mIngreso.setSeaNextDate(zp01Xml.getSeaNextDate());
            mIngreso.setSeaNextTime(zp01Xml.getSeaNextTime());
            mIngreso.setSeaIdCompleting(username);
            mIngreso.setSeaDateCompleted(new Date());
            mIngreso.setSeaIdReviewer(username);
            mIngreso.setSeaDateReviewed(new Date());
            mIngreso.setSeaIdDataEntry(username);
            mIngreso.setSeaDateEntered(new Date());

			mIngreso.setRecordDate(new Date());
			mIngreso.setRecordUser(username);
			mIngreso.setIdInstancia(idInstancia);
			mIngreso.setInstancePath(instanceFilePath);
			mIngreso.setEstado(Constants.STATUS_NOT_SUBMITTED);
			mIngreso.setStart(zp01Xml.getStart());
			mIngreso.setEnd(zp01Xml.getEnd());
			mIngreso.setDeviceid(zp01Xml.getDeviceid());
			mIngreso.setSimserial(zp01Xml.getSimserial());
			mIngreso.setPhonenumber(zp01Xml.getPhonenumber());
			mIngreso.setToday(zp01Xml.getToday());

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
				zipA.crearZp01StudyEntrySectionFtoK(mIngreso);
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
