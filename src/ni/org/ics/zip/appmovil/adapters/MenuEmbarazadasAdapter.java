package ni.org.ics.zip.appmovil.adapters;

import java.util.Calendar;
import java.util.Date;

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
	private final Date todayDate;
	private final Calendar fechaIngreso;
	
	public MenuEmbarazadasAdapter(Context context, int textViewResourceId,
			String[] values, Zp00Screening zp00, ZpEstadoEmbarazada zp01) {
		super(context, textViewResourceId, values);
		this.values = values;
		this.mZp00 = zp00;
		this.todayDate = new Date();
		this.fechaIngreso = Calendar.getInstance();
		fechaIngreso.setTime(mZp00.getScrVisitDate());
	}
	
	@Override
    public boolean isEnabled(int position) {
        // Disable the first item of GridView
        if (position == 0) {
            return true;
        } else {
        	return false;
        }
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
		
		// Change icon based on position
		Drawable img = null;
		switch (position){
		case 0:
			textView.setText(values[position]);
			if(todayDate.after(fechaIngreso.getTime())){
				textView.setTextColor(Color.RED);
				textView.setText(textView.getText()+"\n"+"Pendiente");
			}
			
			img=getContext().getResources().getDrawable( R.drawable.ic_enroll);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			break;
		case 1: 
			img=getContext().getResources().getDrawable( R.drawable.ic_vis1);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			break;
		case 2: 
			img=getContext().getResources().getDrawable( R.drawable.ic_vis2);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			break;
		case 3: 
			img=getContext().getResources().getDrawable( R.drawable.ic_vis3);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			break;
		case 4: 
			img=getContext().getResources().getDrawable( R.drawable.ic_vis4);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			break;
		case 5: 
			img=getContext().getResources().getDrawable( R.drawable.ic_vis5);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			break;
		case 6: 
			img=getContext().getResources().getDrawable( R.drawable.ic_vis6);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			break;
		case 7: 
			img=getContext().getResources().getDrawable( R.drawable.ic_vis7);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			break;
		case 8: 
			img=getContext().getResources().getDrawable( R.drawable.ic_vis8);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			break;
		case 9: 
			img=getContext().getResources().getDrawable( R.drawable.ic_vis9);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			break;
		case 10: 
			img=getContext().getResources().getDrawable( R.drawable.ic_vis10);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			break;
		case 11: 
			img=getContext().getResources().getDrawable( R.drawable.ic_vis11);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			break;
		case 12: 
			img=getContext().getResources().getDrawable( R.drawable.ic_parto);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			break;
		case 13: 
			img=getContext().getResources().getDrawable( R.drawable.ic_post);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			break;
		case 14: 
			img=getContext().getResources().getDrawable( R.drawable.ic_exit);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			break;
		default:
			img=getContext().getResources().getDrawable( R.drawable.ic_addvisit);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			break;
		}

		return v;
	}
}
