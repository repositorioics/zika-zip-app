package ni.org.ics.zip.appmovil.adapters.eventosembarazo;

import ni.org.ics.zip.appmovil.R;
import ni.org.ics.zip.appmovil.domain.Zp00Screening;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class IngresoAdapter extends ArrayAdapter<String> {

	private final String[] values;
	private final Zp00Screening mZp00;
	
	public IngresoAdapter(Context context, int textViewResourceId,
			String[] values, Zp00Screening zp00) {
		super(context, textViewResourceId, values);
		this.values = values;
		this.mZp00 = zp00;
	}
	
	@Override
    public boolean isEnabled(int position) {
        // Disable the first item of GridView
        if (position == 10) {
            return false;        
        } else {
        	return true;
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
		textView.setTextColor(Color.BLACK);
		textView.setText(values[position]);
		
		if (position == 0) {
			textView.setText(values[position]+"\n"+mZp00.getRecordId());
        }
		
		// Change icon based on position
		Drawable img = null;
		switch (position){
		case 0: 
			img=getContext().getResources().getDrawable( R.drawable.ic_demog);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			break;
		case 1: 
			img=getContext().getResources().getDrawable( R.drawable.ic_health);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			break;
		case 2: 
			img=getContext().getResources().getDrawable( R.drawable.ic_pregnancy);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			break;
		case 3: 
			img=getContext().getResources().getDrawable( R.drawable.ic_sample);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			break;
		case 4: 
			img=getContext().getResources().getDrawable( R.drawable.ic_briefcase);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			break;
		case 5: 
			img=getContext().getResources().getDrawable( R.drawable.ic_spray);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			break;
		case 6: 
			img=getContext().getResources().getDrawable( R.drawable.ic_pest);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			break;
		case 7: 
			img=getContext().getResources().getDrawable( R.drawable.ic_us);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			break;
		default:
			img=getContext().getResources().getDrawable( R.drawable.ic_launcher);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			break;
		}

		return v;
	}
}
