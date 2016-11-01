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
import ni.org.ics.zip.appmovil.domain.Zp01StudyEntrySectionE;
import ni.org.ics.zip.appmovil.parsers.Zp01StudyEntrySectionEXml;
import ni.org.ics.zip.appmovil.preferences.PreferencesActivity;
import ni.org.ics.zip.appmovil.utils.Constants;
import ni.org.ics.zip.appmovil.utils.FileUtils;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.util.Date;


public class NewZp01StudyEntrySectionEActivity extends AbstractAsyncActivity {

	protected static final String TAG = NewZp01StudyEntrySectionEActivity.class.getSimpleName();
	
	private ZipAdapter zipA;
	private static Zp01StudyEntrySectionE mIngreso = new Zp01StudyEntrySectionE();
	
	public static final int ADD_TAMIZAJE_ODK = 1;
	public static final int BARCODE_CAPTURE_TAM = 2;

	Dialog dialogInit;
	private SharedPreferences settings;
	private String username;
	private String mRecordId = "";

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
		mIngreso = (Zp01StudyEntrySectionE) getIntent().getExtras().getSerializable(Constants.OBJECTO_ZP01E);
        mRecordId = getIntent().getExtras().getString(Constants.RECORDID);
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
		if (mIngreso!=null){
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
				addStudyEntryE();
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
					parseStudyEntryE(idInstancia,instanceFilePath);
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
	private void addStudyEntryE() {
		try{
			//campos de proveedor de collect
			String[] projection = new String[] {
					"_id","jrFormId","displayName"};
			//cursor que busca el formulario
			Cursor c = getContentResolver().query(Constants.CONTENT_URI, projection,
					"jrFormId = 'zp01_study_entry_e' and displayName = 'Estudio ZIP Visita inicial en el estudio 2'", null, null);
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
	
	private void parseStudyEntryE(Integer idInstancia, String instanceFilePath) {
		Serializer serializer = new Persister(); 
		File source = new File(instanceFilePath);
		try {
			Zp01StudyEntrySectionEXml zp01Xml = new Zp01StudyEntrySectionEXml();
			zp01Xml = serializer.read(Zp01StudyEntrySectionEXml.class, source);
			mIngreso.setRecordId(mRecordId);
            mIngreso.setSeaDiseases(zp01Xml.getSeaDiseases());
            mIngreso.setSeaOtherSpecify(zp01Xml.getSeaOtherSpecify());
            mIngreso.setSeaHepatitis(zp01Xml.getSeaHepatitis());
            mIngreso.setSeaHepatitisDate1(zp01Xml.getSeaHepatitisDate1());
            mIngreso.setSeaHepatitisDate2(zp01Xml.getSeaHepatitisDate2());
            mIngreso.setSeaHepatitisDate3(zp01Xml.getSeaHepatitisDate3());
            mIngreso.setSeaMeasles(zp01Xml.getSeaMeasles());
            mIngreso.setSeaMeaslesDate1(zp01Xml.getSeaMeaslesDate1());
            mIngreso.setSeaMeaslesDate2(zp01Xml.getSeaMeaslesDate2());
            mIngreso.setSeaMeaslesDate3(zp01Xml.getSeaMeaslesDate3());
            mIngreso.setSeaChickenpox(zp01Xml.getSeaChickenpox());
            mIngreso.setSeaChickenpoxDate1(zp01Xml.getSeaChickenpoxDate1());
            mIngreso.setSeaChickenpoxDate2(zp01Xml.getSeaChickenpoxDate2());
            mIngreso.setSeaChickenpoxDate3(zp01Xml.getSeaChickenpoxDate3());
            mIngreso.setSeaInfluenza(zp01Xml.getSeaInfluenza());
            mIngreso.setSeaInfluenzaDate1(zp01Xml.getSeaInfluenzaDate1());
            mIngreso.setSeaInfluenzaDate2(zp01Xml.getSeaInfluenzaDate2());
            mIngreso.setSeaInfluenzaDate3(zp01Xml.getSeaInfluenzaDate3());
            mIngreso.setSeaYellow(zp01Xml.getSeaYellow());
            mIngreso.setSeaYellowDate1(zp01Xml.getSeaYellowDate1());
            mIngreso.setSeaYellowDate2(zp01Xml.getSeaYellowDate2());
            mIngreso.setSeaYellowDate3(zp01Xml.getSeaYellowDate3());
            mIngreso.setSeaDengue(zp01Xml.getSeaDengue());
            mIngreso.setSeaDengueDate1(zp01Xml.getSeaDengueDate1());
            mIngreso.setSeaDengueDate2(zp01Xml.getSeaDengueDate2());
            mIngreso.setSeaDengueDate3(zp01Xml.getSeaDengueDate3());
            mIngreso.setSeaVacOther(zp01Xml.getSeaVacOther());
            mIngreso.setSeaVacotherSpecify(zp01Xml.getSeaVacotherSpecify());
            mIngreso.setSeaOtherDate1(zp01Xml.getSeaOtherDate1());
            mIngreso.setSeaOtherDate2(zp01Xml.getSeaOtherDate2());
            mIngreso.setSeaOtherDate3(zp01Xml.getSeaOtherDate3());
            mIngreso.setSeaRubella(zp01Xml.getSeaRubella());
            mIngreso.setSeaRubellaDate1(zp01Xml.getSeaRubellaDate1());
            mIngreso.setSeaRubellaDate2(zp01Xml.getSeaRubellaDate2());
            mIngreso.setSeaRubellaDate3(zp01Xml.getSeaRubellaDate3());
            mIngreso.setSeaRubellaDoc(zp01Xml.getSeaRubellaDoc());
            mIngreso.setSeaCmv(zp01Xml.getSeaCmv());
            mIngreso.setSeaCmvDate1(zp01Xml.getSeaCmvDate1());
            mIngreso.setSeaCmvDate2(zp01Xml.getSeaCmvDate2());
            mIngreso.setSeaCmvDate3(zp01Xml.getSeaCmvDate3());
            mIngreso.setSeaCmvDoc(zp01Xml.getSeaCmvDoc());
            mIngreso.setSeaHerpes(zp01Xml.getSeaHerpes());
            mIngreso.setSeaHerpesDate1(zp01Xml.getSeaHerpesDate1());
            mIngreso.setSeaHerpesDate2(zp01Xml.getSeaHerpesDate2());
            mIngreso.setSeaHerpesDate3(zp01Xml.getSeaHerpesDate3());
            mIngreso.setSeaHerpesDoc(zp01Xml.getSeaHerpesDoc());
            mIngreso.setSeaParvovirus(zp01Xml.getSeaParvovirus());
            mIngreso.setSeaParvovirusDate1(zp01Xml.getSeaParvovirusDate1());
            mIngreso.setSeaParvovirusDate2(zp01Xml.getSeaParvovirusDate2());
            mIngreso.setSeaParvovirusDate3(zp01Xml.getSeaParvovirusDate3());
            mIngreso.setSeaParvovirusDoc(zp01Xml.getSeaParvovirusDoc());
            mIngreso.setSeaToxoplasmosis(zp01Xml.getSeaToxoplasmosis());
            mIngreso.setSeaToxoplasmosisDate1(zp01Xml.getSeaToxoplasmosisDate1());
            mIngreso.setSeaToxoplasmosisDate2(zp01Xml.getSeaToxoplasmosisDate2());
            mIngreso.setSeaToxoplasmosisDate3(zp01Xml.getSeaToxoplasmosisDate3());
            mIngreso.setSeaToxoplasmosisDoc(zp01Xml.getSeaToxoplasmosisDoc());
            mIngreso.setSeaSyphillis(zp01Xml.getSeaSyphillis());
            mIngreso.setSeaSyphillisDate1(zp01Xml.getSeaSyphillisDate1());
            mIngreso.setSeaSyphillisDate2(zp01Xml.getSeaSyphillisDate2());
            mIngreso.setSeaSyphillisDate3(zp01Xml.getSeaSyphillisDate3());
            mIngreso.setSeaSyphillisDoc(zp01Xml.getSeaSyphillisDoc());
            mIngreso.setSeaHiv(zp01Xml.getSeaHiv());
            mIngreso.setSeaHivDate1(zp01Xml.getSeaHivDate1());
            mIngreso.setSeaHivDate2(zp01Xml.getSeaHivDate2());
            mIngreso.setSeaHivDate3(zp01Xml.getSeaHivDate3());
            mIngreso.setSeaHivDoc(zp01Xml.getSeaHivDoc());
            mIngreso.setSeaZika(zp01Xml.getSeaZika());
            mIngreso.setSeaZikaDate1(zp01Xml.getSeaZikaDate1());
            mIngreso.setSeaZikaDate2(zp01Xml.getSeaZikaDate2());
            mIngreso.setSeaZikaDate3(zp01Xml.getSeaZikaDate3());
            mIngreso.setSeaZikaDoc(zp01Xml.getSeaZikaDoc());
            mIngreso.setSeaChikung(zp01Xml.getSeaChikung());
            mIngreso.setSeaChikungDate1(zp01Xml.getSeaChikungDate1());
            mIngreso.setSeaChikungDate2(zp01Xml.getSeaChikungDate2());
            mIngreso.setSeaChikungDate3(zp01Xml.getSeaChikungDate3());
            mIngreso.setSeaChikungDoc(zp01Xml.getSeaChikungDoc());
            mIngreso.setSeaInfluInfect(zp01Xml.getSeaInfluInfect());
            mIngreso.setSeaInflueInfectDate1(zp01Xml.getSeaInflueInfectDate1());
            mIngreso.setSeaInfluInfectDate2(zp01Xml.getSeaInfluInfectDate2());
            mIngreso.setSeaInfluInfectDate3(zp01Xml.getSeaInfluInfectDate3());
            mIngreso.setSeaInfluInfectDoc(zp01Xml.getSeaInfluInfectDoc());
            mIngreso.setSeaDengueInfect(zp01Xml.getSeaDengueInfect());
            mIngreso.setSeaDengueInfectDate1(zp01Xml.getSeaDengueInfectDate1());
            mIngreso.setSeaDengueInfectDate2(zp01Xml.getSeaDengueInfectDate2());
            mIngreso.setSeaDengueInfectDate3(zp01Xml.getSeaDengueInfectDate3());
            mIngreso.setSeaDengueInfectDoc(zp01Xml.getSeaDengueInfectDoc());
            mIngreso.setSeaFeverSymptom(zp01Xml.getSeaFeverSymptom());
            mIngreso.setSeaRash(zp01Xml.getSeaRash());
            mIngreso.setSeaItch(zp01Xml.getSeaItch());
            mIngreso.setSeaRashFirst(zp01Xml.getSeaRashFirst());
            mIngreso.setSeaRashDay(zp01Xml.getSeaRashDay());
            mIngreso.setSeaRashMonth(zp01Xml.getSeaRashMonth());
            mIngreso.setSeaRashYear(zp01Xml.getSeaRashYear());
            mIngreso.setSeaRashDuration(zp01Xml.getSeaRashDuration());
            mIngreso.setSeaRashSpread(zp01Xml.getSeaRashSpread());
            mIngreso.setSeaSpreadPart(zp01Xml.getSeaSpreadPart());
            mIngreso.setSeaFeverExperience(zp01Xml.getSeaFeverExperience());
            mIngreso.setSeaTempMeasure(zp01Xml.getSeaTempMeasure());
            mIngreso.setSeaHighTemp(zp01Xml.getSeaHighTemp());
            mIngreso.setSeaHightemUnit(zp01Xml.getSeaHightemUnit());
            mIngreso.setSeaTempunknown(zp01Xml.getSeaTempunknown());
            mIngreso.setSeaFeverDay(zp01Xml.getSeaFeverDay());
            mIngreso.setSeaFeverMonth(zp01Xml.getSeaFeverMonth());
            mIngreso.setSeaFeverYear(zp01Xml.getSeaFeverYear());
            mIngreso.setSeaFeverDuration(zp01Xml.getSeaFeverDuration());
            mIngreso.setSeaRedeyes(zp01Xml.getSeaRedeyes());
            mIngreso.setSeaRedeyesDay(zp01Xml.getSeaRedeyesDay());
            mIngreso.setSeaRedeyesMonth(zp01Xml.getSeaRedeyesMonth());
            mIngreso.setSeaRedeyesYear(zp01Xml.getSeaRedeyesYear());
            mIngreso.setSeaRedeyesDuration(zp01Xml.getSeaRedeyesDuration());
            mIngreso.setSeaOccurSame(zp01Xml.getSeaOccurSame());
            mIngreso.setSeaSameSymptom(zp01Xml.getSeaSameSymptom());
            mIngreso.setSeaJoint(zp01Xml.getSeaJoint());
            mIngreso.setSeaJointDay(zp01Xml.getSeaJointDay());
            mIngreso.setSeaJointMonth(zp01Xml.getSeaJointMonth());
            mIngreso.setSeaJointYear(zp01Xml.getSeaJointYear());
            mIngreso.setSeaJointDuration(zp01Xml.getSeaJointDuration());
            mIngreso.setSeaHeadache(zp01Xml.getSeaHeadache());
            mIngreso.setSeaHeadacheDay(zp01Xml.getSeaHeadacheDay());
            mIngreso.setSeaHeadacheMonth(zp01Xml.getSeaHeadacheMonth());
            mIngreso.setSeaHeadacheYear(zp01Xml.getSeaHeadacheYear());
            mIngreso.setSeaHeadacheDuration(zp01Xml.getSeaHeadacheDuration());
            mIngreso.setSeaSymptomOther(zp01Xml.getSeaSymptomOther());
            mIngreso.setSeaSpecifySymptom(zp01Xml.getSeaSpecifySymptom());
            mIngreso.setSeaOtherSymptom(zp01Xml.getSeaOtherSymptom());
            mIngreso.setSeaMedicare(zp01Xml.getSeaMedicare());
            mIngreso.setSeaCareDay(zp01Xml.getSeaCareDay());
            mIngreso.setSeaCareMonth(zp01Xml.getSeaCareMonth());
            mIngreso.setSeaCareYear(zp01Xml.getSeaCareYear());
            mIngreso.setSeaCareFacility(zp01Xml.getSeaCareFacility());
            mIngreso.setSeaHospitalized(zp01Xml.getSeaHospitalized());
            mIngreso.setSeaHospital(zp01Xml.getSeaHospital());
            mIngreso.setSeaDiagRubella(zp01Xml.getSeaDiagRubella());
            mIngreso.setSeaDiagDengue(zp01Xml.getSeaDiagDengue());
            mIngreso.setSeaDiagChikung(zp01Xml.getSeaDiagChikung());
            mIngreso.setSeaDiagZika(zp01Xml.getSeaDiagZika());
            mIngreso.setSeaDiagCytome(zp01Xml.getSeaDiagCytome());
            mIngreso.setSeaMedicine(zp01Xml.getSeaMedicine());
            mIngreso.setSeaMedcineName(zp01Xml.getSeaMedcineName());
            mIngreso.setSeaGuillainbarre(zp01Xml.getSeaGuillainbarre());
            mIngreso.setSeaTingling(zp01Xml.getSeaTingling());
            mIngreso.setSeaTinglingArm(zp01Xml.getSeaTinglingArm());
            mIngreso.setSeaTinglingLeg(zp01Xml.getSeaTinglingLeg());
            mIngreso.setSeaTinglingHand(zp01Xml.getSeaTinglingHand());
            mIngreso.setSeaTinglingFoot(zp01Xml.getSeaTinglingFoot());
            mIngreso.setSeaTinglingFace(zp01Xml.getSeaTinglingFace());
            mIngreso.setSeaTinglingOther(zp01Xml.getSeaTinglingOther());
            mIngreso.setSeaSensationMin(zp01Xml.getSeaSensationMin());
            mIngreso.setSeaSensationHr(zp01Xml.getSeaSensationHr());
            mIngreso.setSeaSenstaionDay(zp01Xml.getSeaSenstaionDay());
            mIngreso.setSeaInjury(zp01Xml.getSeaInjury());
            mIngreso.setSeaTinglingDay(zp01Xml.getSeaTinglingDay());
            mIngreso.setSeaTinglingMonth(zp01Xml.getSeaTinglingMonth());
            mIngreso.setSeaTinglingYear(zp01Xml.getSeaTinglingYear());
            mIngreso.setSeaTinglingDuration(zp01Xml.getSeaTinglingDuration());
            mIngreso.setSeaNumbness(zp01Xml.getSeaNumbness());
            mIngreso.setSeaNumbArm(zp01Xml.getSeaNumbArm());
            mIngreso.setSeaNumbLeg(zp01Xml.getSeaNumbLeg());
            mIngreso.setSeaNumbHand(zp01Xml.getSeaNumbHand());
            mIngreso.setSeaNumbFoot(zp01Xml.getSeaNumbFoot());
            mIngreso.setSeaNumbFace(zp01Xml.getSeaNumbFace());
            mIngreso.setSeaNumbOther(zp01Xml.getSeaNumbOther());
            mIngreso.setSeaNumbDay(zp01Xml.getSeaNumbDay());
            mIngreso.setSeaNumbMonth(zp01Xml.getSeaNumbMonth());
            mIngreso.setSeaNumbYear(zp01Xml.getSeaNumbYear());
            mIngreso.setSeaNumbDuration(zp01Xml.getSeaNumbDuration());
            mIngreso.setSeaParalysis(zp01Xml.getSeaParalysis());
            mIngreso.setSeaParaArm(zp01Xml.getSeaParaArm());
            mIngreso.setSeaParaLeg(zp01Xml.getSeaParaLeg());
            mIngreso.setSeaParaHand(zp01Xml.getSeaParaHand());
            mIngreso.setSeaParaFoot(zp01Xml.getSeaParaFoot());
            mIngreso.setSeaParaFace(zp01Xml.getSeaParaFace());
            mIngreso.setSeaParaOther(zp01Xml.getSeaParaOther());
            mIngreso.setSeaParaDay(zp01Xml.getSeaParaDay());
            mIngreso.setSeaParaMonth(zp01Xml.getSeaParaMonth());
            mIngreso.setSeaParaYear(zp01Xml.getSeaParaYear());
            mIngreso.setSeaParaDuration(zp01Xml.getSeaParaDuration());
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
				zipA.crearZp01StudyEntrySectionE(mIngreso);
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
