package ni.org.ics.zip.appmovil.adapters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import ni.org.ics.zip.appmovil.R;
import ni.org.ics.zip.appmovil.domain.Zp00Screening;
import ni.org.ics.zip.appmovil.domain.ZpEstadoEmbarazada;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MenuEmbarazadasAdapter extends ArrayAdapter<String> {

	private final String[] values;
	private final Zp00Screening mZp00;
	private final ZpEstadoEmbarazada mZp01;
	private final Calendar fechaIngreso;
	private final Context context;
	private Date fechaEvento;
	private Date todayDate;
	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
	
	public MenuEmbarazadasAdapter(Context context, int textViewResourceId,
			String[] values, Zp00Screening zp00, ZpEstadoEmbarazada zpEstado) {
		super(context, textViewResourceId, values);
		this.context = context;
		this.values = values;
		this.mZp00 = zp00;
		this.mZp01 = zpEstado;
		try {
			this.todayDate = formatter.parse(formatter.format(new Date()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.fechaIngreso = Calendar.getInstance();
		fechaIngreso.setTime(mZp00.getScrVisitDate());
	}
	
	@Override
    public boolean isEnabled(int position) {
        // Disable the first item of GridView
		boolean habilitado = true;
		long diff =0;
		switch (position){
		case 0:
        	fechaEvento = fechaIngreso.getTime();
        	diff = getDateDiff(fechaEvento,todayDate,TimeUnit.DAYS);
        	if(diff>1) habilitado = false;
        	break;
		case 1:
			fechaIngreso.add(Calendar.DATE, 28);fechaEvento = fechaIngreso.getTime();fechaIngreso.add(Calendar.DATE, -28);
        	diff = getDateDiff(fechaEvento,todayDate,TimeUnit.DAYS);
        	if(diff<-7||diff>7) habilitado = false;
        	break;
		case 2:
			fechaIngreso.add(Calendar.DATE, 56);fechaEvento = fechaIngreso.getTime();fechaIngreso.add(Calendar.DATE, -56);
        	diff = getDateDiff(fechaEvento,todayDate,TimeUnit.DAYS);
        	if(diff<-7||diff>7) habilitado = false;
        	break;
		case 3:
			fechaIngreso.add(Calendar.DATE, 84);fechaEvento = fechaIngreso.getTime();fechaIngreso.add(Calendar.DATE, -84);
        	diff = getDateDiff(fechaEvento,todayDate,TimeUnit.DAYS);
        	if(diff<-7||diff>7) habilitado = false;
        	break;
		case 4:
			fechaIngreso.add(Calendar.DATE, 112);fechaEvento = fechaIngreso.getTime();fechaIngreso.add(Calendar.DATE, -112);
        	diff = getDateDiff(fechaEvento,todayDate,TimeUnit.DAYS);
        	if(diff<-7||diff>7) habilitado = false;
        	break;
		case 5:
			fechaIngreso.add(Calendar.DATE, 140);fechaEvento = fechaIngreso.getTime();fechaIngreso.add(Calendar.DATE, -140);
        	diff = getDateDiff(fechaEvento,todayDate,TimeUnit.DAYS);
        	if(diff<-7||diff>7) habilitado = false;
        	break;
		case 6:
			fechaIngreso.add(Calendar.DATE, 168);fechaEvento = fechaIngreso.getTime();fechaIngreso.add(Calendar.DATE, -168);
        	diff = getDateDiff(fechaEvento,todayDate,TimeUnit.DAYS);
        	if(diff<-7||diff>7) habilitado = false;
        	break;
		case 7:
			fechaIngreso.add(Calendar.DATE, 196);fechaEvento = fechaIngreso.getTime();fechaIngreso.add(Calendar.DATE, -196);
        	diff = getDateDiff(fechaEvento,todayDate,TimeUnit.DAYS);
        	if(diff<-7||diff>7) habilitado = false;
        	break;	
		case 8:
			fechaIngreso.add(Calendar.DATE, 224);fechaEvento = fechaIngreso.getTime();fechaIngreso.add(Calendar.DATE, -224);
        	diff = getDateDiff(fechaEvento,todayDate,TimeUnit.DAYS);
        	if(diff<-7||diff>7) habilitado = false;
        	break;
		case 9:
			fechaIngreso.add(Calendar.DATE, 252);fechaEvento = fechaIngreso.getTime();fechaIngreso.add(Calendar.DATE, -252);
        	diff = getDateDiff(fechaEvento,todayDate,TimeUnit.DAYS);
        	if(diff<-7||diff>7) habilitado = false;
        	break;
		case 10:
			fechaIngreso.add(Calendar.DATE, 280);fechaEvento = fechaIngreso.getTime();fechaIngreso.add(Calendar.DATE, -280);
        	diff = getDateDiff(fechaEvento,todayDate,TimeUnit.DAYS);
        	if(diff<-7||diff>7) habilitado = false;
        	break;
		case 11:
			fechaIngreso.add(Calendar.DATE, 308);fechaEvento = fechaIngreso.getTime();fechaIngreso.add(Calendar.DATE, -308);
        	diff = getDateDiff(fechaEvento,todayDate,TimeUnit.DAYS);
        	if(diff<-28||diff>28) habilitado = false;
        	break;
		case 12:
			fechaIngreso.add(Calendar.DATE, 309);fechaEvento = fechaIngreso.getTime();fechaIngreso.add(Calendar.DATE, -309);
        	diff = getDateDiff(fechaEvento,todayDate,TimeUnit.DAYS);
        	if(diff<-28||diff>28) habilitado = false;
        	break;
		case 13:
			fechaIngreso.add(Calendar.DATE, 322);fechaEvento = fechaIngreso.getTime();fechaIngreso.add(Calendar.DATE, -322);
        	diff = getDateDiff(fechaEvento,todayDate,TimeUnit.DAYS);
        	if(diff<-7||diff>7) habilitado = false;
        	break;
		default:
			habilitado = true;
			break;
        }
        return habilitado;
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.menu_item_2, null);
		}
		TextView textView = (TextView) v.findViewById(R.id.label);
		textView.setTypeface(null, Typeface.BOLD);
		textView.setText(values[position]);
		// Change icon based on position
		Drawable img = null;
		switch (position){
		case 0:
			fechaEvento = fechaIngreso.getTime();
			if(String.valueOf(mZp01.getIngreso()).equals("0")){
				textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.pending));
				long dif = getDateDiff(fechaEvento,todayDate,TimeUnit.DAYS);
				if(dif>1){
					textView.setTextColor(Color.GRAY);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.notavailable));
				}
				else if(dif<1){
					textView.setTextColor(Color.BLUE);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.ontime));
				}
				else {
					textView.setTextColor(Color.RED);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.delayed));
				}
			}
			else{
				textView.setTextColor(Color.BLACK);
				textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.done));
			}
			img=getContext().getResources().getDrawable( R.drawable.ic_enroll);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			break;
		case 1: 
			fechaIngreso.add(Calendar.DATE, 28);
			fechaEvento = fechaIngreso.getTime();
			if(String.valueOf(mZp01.getSem4()).equals("0")){
				textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.pending));
				long dif = getDateDiff(fechaEvento,todayDate,TimeUnit.DAYS);
				if(dif<-7){
					textView.setTextColor(Color.GRAY);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.programmed)+": "+ formatter.format(fechaEvento));
				}
				else if(dif>7){
					textView.setTextColor(Color.GRAY);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.notavailable));
				}
				else if(dif<=0){
					textView.setTextColor(Color.BLUE);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.ontime));
				}
				else{
					textView.setTextColor(Color.RED);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.delayed));
				}
			}
			else{
				textView.setTextColor(Color.BLACK);
				textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.done));
			}
			img=getContext().getResources().getDrawable( R.drawable.ic_vis1);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			fechaIngreso.add(Calendar.DATE, -28);
			break;
		case 2: 
			fechaIngreso.add(Calendar.DATE, 56);
			fechaEvento = fechaIngreso.getTime();
			if(String.valueOf(mZp01.getSem8()).equals("0")){
				textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.pending));
				long dif = getDateDiff(fechaEvento,todayDate,TimeUnit.DAYS);
				if(dif<-7){
					textView.setTextColor(Color.GRAY);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.programmed)+": "+ formatter.format(fechaEvento));
				}
				else if(dif>7){
					textView.setTextColor(Color.GRAY);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.notavailable));
				}
				else if(dif<=0){
					textView.setTextColor(Color.BLUE);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.ontime));
				}
				else{
					textView.setTextColor(Color.RED);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.delayed));
				}
			}
			else{
				textView.setTextColor(Color.BLACK);
				textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.done));
			}
			img=getContext().getResources().getDrawable( R.drawable.ic_vis2);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			fechaIngreso.add(Calendar.DATE, -56);
			break;
		case 3:
			fechaIngreso.add(Calendar.DATE, 84);
			fechaEvento = fechaIngreso.getTime();
			if(String.valueOf(mZp01.getSem12()).equals("0")){
				textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.pending));
				long dif = getDateDiff(fechaEvento,todayDate,TimeUnit.DAYS);
				if(dif<-7){
					textView.setTextColor(Color.GRAY);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.programmed)+": "+ formatter.format(fechaEvento));
				}
				else if(dif>7){
					textView.setTextColor(Color.GRAY);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.notavailable));
				}
				else if(dif<=0){
					textView.setTextColor(Color.BLUE);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.ontime));
				}
				else{
					textView.setTextColor(Color.RED);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.delayed));
				}
			}
			else{
				textView.setTextColor(Color.BLACK);
				textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.done));
			}
			img=getContext().getResources().getDrawable( R.drawable.ic_vis3);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			fechaIngreso.add(Calendar.DATE, -84);
			break;
		case 4: 
			fechaIngreso.add(Calendar.DATE, 112);
			fechaEvento = fechaIngreso.getTime();
			if(String.valueOf(mZp01.getSem16()).equals("0")){
				textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.pending));
				long dif = getDateDiff(fechaEvento,todayDate,TimeUnit.DAYS);
				if(dif<-7){
					textView.setTextColor(Color.GRAY);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.programmed)+": "+ formatter.format(fechaEvento));
				}
				else if(dif>7){
					textView.setTextColor(Color.GRAY);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.notavailable));
				}
				else if(dif<=0){
					textView.setTextColor(Color.BLUE);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.ontime));
				}
				else{
					textView.setTextColor(Color.RED);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.delayed));
				}
			}
			else{
				textView.setTextColor(Color.BLACK);
				textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.done));
			}
			img=getContext().getResources().getDrawable( R.drawable.ic_vis4);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			fechaIngreso.add(Calendar.DATE, -112);
			break;
		case 5: 
			fechaIngreso.add(Calendar.DATE, 140);
			fechaEvento = fechaIngreso.getTime();
			if(String.valueOf(mZp01.getSem20()).equals("0")){
				textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.pending));
				long dif = getDateDiff(fechaEvento,todayDate,TimeUnit.DAYS);
				if(dif<-7){
					textView.setTextColor(Color.GRAY);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.programmed)+": "+ formatter.format(fechaEvento));
				}
				else if(dif>7){
					textView.setTextColor(Color.GRAY);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.notavailable));
				}
				else if(dif<=0){
					textView.setTextColor(Color.BLUE);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.ontime));
				}
				else{
					textView.setTextColor(Color.RED);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.delayed));
				}
			}
			else{
				textView.setTextColor(Color.BLACK);
				textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.done));
			}
			img=getContext().getResources().getDrawable( R.drawable.ic_vis5);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			fechaIngreso.add(Calendar.DATE, -140);
			break;
		case 6: 
			fechaIngreso.add(Calendar.DATE, 168);
			fechaEvento = fechaIngreso.getTime();
			if(String.valueOf(mZp01.getSem24()).equals("0")){
				textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.pending));
				long dif = getDateDiff(fechaEvento,todayDate,TimeUnit.DAYS);
				if(dif<-7){
					textView.setTextColor(Color.GRAY);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.programmed)+": "+ formatter.format(fechaEvento));
				}
				else if(dif>7){
					textView.setTextColor(Color.GRAY);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.notavailable));
				}
				else if(dif<=0){
					textView.setTextColor(Color.BLUE);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.ontime));
				}
				else{
					textView.setTextColor(Color.RED);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.delayed));
				}
			}
			else{
				textView.setTextColor(Color.BLACK);
				textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.done));
			}
			img=getContext().getResources().getDrawable( R.drawable.ic_vis6);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			fechaIngreso.add(Calendar.DATE, -168);
			break;
		case 7: 
			fechaIngreso.add(Calendar.DATE, 196);
			fechaEvento = fechaIngreso.getTime();
			if(String.valueOf(mZp01.getSem28()).equals("0")){
				textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.pending));
				long dif = getDateDiff(fechaEvento,todayDate,TimeUnit.DAYS);
				if(dif<-7){
					textView.setTextColor(Color.GRAY);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.programmed)+": "+ formatter.format(fechaEvento));
				}
				else if(dif>7){
					textView.setTextColor(Color.GRAY);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.notavailable));
				}
				else if(dif<=0){
					textView.setTextColor(Color.BLUE);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.ontime));
				}
				else{
					textView.setTextColor(Color.RED);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.delayed));
				}
			}
			else{
				textView.setTextColor(Color.BLACK);
				textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.done));
			}
			img=getContext().getResources().getDrawable( R.drawable.ic_vis7);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			fechaIngreso.add(Calendar.DATE, -196);
			break;
		case 8: 
			fechaIngreso.add(Calendar.DATE, 224);
			fechaEvento = fechaIngreso.getTime();
			if(String.valueOf(mZp01.getSem32()).equals("0")){
				textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.pending));
				long dif = getDateDiff(fechaEvento,todayDate,TimeUnit.DAYS);
				if(dif<-7){
					textView.setTextColor(Color.GRAY);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.programmed)+": "+ formatter.format(fechaEvento));
				}
				else if(dif>7){
					textView.setTextColor(Color.GRAY);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.notavailable));
				}
				else if(dif<=0){
					textView.setTextColor(Color.BLUE);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.ontime));
				}
				else{
					textView.setTextColor(Color.RED);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.delayed));
				}
			}
			else{
				textView.setTextColor(Color.BLACK);
				textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.done));
			}
			img=getContext().getResources().getDrawable( R.drawable.ic_vis8);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			fechaIngreso.add(Calendar.DATE, -224);
			break;
		case 9: 
			fechaIngreso.add(Calendar.DATE, 252);
			fechaEvento = fechaIngreso.getTime();
			if(String.valueOf(mZp01.getSem36()).equals("0")){
				textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.pending));
				long dif = getDateDiff(fechaEvento,todayDate,TimeUnit.DAYS);
				if(dif<-7){
					textView.setTextColor(Color.GRAY);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.programmed)+": "+ formatter.format(fechaEvento));
				}
				else if(dif>7){
					textView.setTextColor(Color.GRAY);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.notavailable));
				}
				else if(dif<=0){
					textView.setTextColor(Color.BLUE);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.ontime));
				}
				else{
					textView.setTextColor(Color.RED);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.delayed));
				}
			}
			else{
				textView.setTextColor(Color.BLACK);
				textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.done));
			}
			img=getContext().getResources().getDrawable( R.drawable.ic_vis9);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			fechaIngreso.add(Calendar.DATE, -252);
			break;
		case 10: 
			fechaIngreso.add(Calendar.DATE, 280);
			fechaEvento = fechaIngreso.getTime();
			if(String.valueOf(mZp01.getSem40()).equals("0")){
				textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.pending));
				long dif = getDateDiff(fechaEvento,todayDate,TimeUnit.DAYS);
				if(dif<-7){
					textView.setTextColor(Color.GRAY);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.programmed)+": "+ formatter.format(fechaEvento));
				}
				else if(dif>7){
					textView.setTextColor(Color.GRAY);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.notavailable));
				}
				else if(dif<=0){
					textView.setTextColor(Color.BLUE);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.ontime));
				}
				else{
					textView.setTextColor(Color.RED);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.delayed));
				}
			}
			else{
				textView.setTextColor(Color.BLACK);
				textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.done));
			}
			img=getContext().getResources().getDrawable( R.drawable.ic_vis10);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			fechaIngreso.add(Calendar.DATE, -280);
			break;
		case 11: 
			fechaIngreso.add(Calendar.DATE, 308);
			fechaEvento = fechaIngreso.getTime();
			if(String.valueOf(mZp01.getSem44()).equals("0")){
				textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.pending));
				long dif = getDateDiff(fechaEvento,todayDate,TimeUnit.DAYS);
				if(dif<-28){
					textView.setTextColor(Color.GRAY);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.programmed)+": "+ formatter.format(fechaEvento));
				}
				else if(dif>28){
					textView.setTextColor(Color.GRAY);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.notavailable));
				}
				else if(dif<=0){
					textView.setTextColor(Color.BLUE);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.ontime));
				}
				else{
					textView.setTextColor(Color.RED);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.delayed));
				}
			}
			else{
				textView.setTextColor(Color.BLACK);
				textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.done));
			}
			img=getContext().getResources().getDrawable( R.drawable.ic_vis11);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			fechaIngreso.add(Calendar.DATE, -308);
			break;
		case 12: 
			fechaIngreso.add(Calendar.DATE, 309);
			fechaEvento = fechaIngreso.getTime();
			if(String.valueOf(mZp01.getParto()).equals("0")){
				textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.pending));
				long dif = getDateDiff(fechaEvento,todayDate,TimeUnit.DAYS);
				if(dif<-28){
					textView.setTextColor(Color.GRAY);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.programmed)+": "+ formatter.format(fechaEvento));
				}
				else if(dif>28){
					textView.setTextColor(Color.GRAY);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.notavailable));
				}
				else if(dif<=0){
					textView.setTextColor(Color.BLUE);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.ontime));
				}
				else{
					textView.setTextColor(Color.RED);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.delayed));
				}
			}
			else{
				textView.setTextColor(Color.BLACK);
				textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.done));
			}
			img=getContext().getResources().getDrawable( R.drawable.ic_parto);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			fechaIngreso.add(Calendar.DATE, -309);
			break;
		case 13: 
			fechaIngreso.add(Calendar.DATE, 322);
			fechaEvento = fechaIngreso.getTime();
			if(String.valueOf(mZp01.getPosparto()).equals("0")){
				textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.pending));
				long dif = getDateDiff(fechaEvento,todayDate,TimeUnit.DAYS);
				if(dif<-7){
					textView.setTextColor(Color.GRAY);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.programmed)+": "+ formatter.format(fechaEvento));
				}
				else if(dif>7){
					textView.setTextColor(Color.GRAY);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.notavailable));
				}
				else if(dif<=0){
					textView.setTextColor(Color.BLUE);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.ontime));
				}
				else{
					textView.setTextColor(Color.RED);
					textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.delayed));
				}
			}
			else{
				textView.setTextColor(Color.BLACK);
				textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.done));
			}
			img=getContext().getResources().getDrawable( R.drawable.ic_post);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			fechaIngreso.add(Calendar.DATE, -322);
			break;
		case 14: 
			textView.setTextColor(Color.BLACK);
			textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.available)+"\n\n");
			img=getContext().getResources().getDrawable( R.drawable.ic_exit);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			break;
		case 15: 
			textView.setTextColor(Color.BLACK);
			textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.available)+"\n\n");
			img=getContext().getResources().getDrawable( R.drawable.ic_addvisit);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			break;
		case 16: 
			textView.setTextColor(Color.BLACK);
			textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.available)+"\n\n");
			img=getContext().getResources().getDrawable( R.drawable.ic_addvisit);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			break;
		case 17: 
			textView.setTextColor(Color.BLACK);
			textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.available)+"\n\n");
			img=getContext().getResources().getDrawable( R.drawable.ic_addvisit);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			break;
		case 18: 
			textView.setTextColor(Color.BLACK);
			textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.available)+"\n\n");
			img=getContext().getResources().getDrawable( R.drawable.ic_addvisit);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			break;
		case 19: 
			textView.setTextColor(Color.BLACK);
			textView.setText(textView.getText()+"\n"+ context.getResources().getString(R.string.available)+"\n\n");
			img=getContext().getResources().getDrawable( R.drawable.ic_addvisit);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			break;
		default:
			img=getContext().getResources().getDrawable( R.drawable.logo);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			break;
		}

		return v;
	}

	
	/**
	 * Get a diff between two dates
	 * @param date1 the oldest date
	 * @param date2 the newest date
	 * @param timeUnit the unit in which you want the diff
	 * @return the diff value, in the provided unit
	 */
	public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
	    long diffInMillies = date2.getTime() - date1.getTime();
	    return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
	}
}
