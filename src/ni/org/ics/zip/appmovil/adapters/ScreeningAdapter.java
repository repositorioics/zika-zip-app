package ni.org.ics.zip.appmovil.adapters;

import java.text.SimpleDateFormat;
import java.util.List;

import ni.org.ics.zip.appmovil.R;
import ni.org.ics.zip.appmovil.domain.Zp00Screening;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ScreeningAdapter extends ArrayAdapter<Zp00Screening> {

	private SimpleDateFormat mDateFormat = new SimpleDateFormat("MMM dd, yyyy");
	
	public ScreeningAdapter(Context context, int textViewResourceId,
			List<Zp00Screening> items) {
		super(context, textViewResourceId, items);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.complex_list_item, null);
		}
		Zp00Screening p = getItem(position);
		if (p != null) {

			TextView textView = (TextView) v.findViewById(R.id.name_text);
			if (textView != null) {
				textView.setText(this.getContext().getString(R.string.study_id) + ": " + p.getRecordId());
			}

			textView = (TextView) v.findViewById(R.id.identifier_text);
			if (textView != null) {
				textView.setText(this.getContext().getString(R.string.mat_fec) + ": " + mDateFormat.format(p.getScrVisitDate()));
			}
			
			textView = (TextView) v.findViewById(R.id.infoc_text);
			if (textView != null) {
				if(p.getStudyInm()=='0'){
					textView.setText(this.getContext().getString(R.string.noimn));
				}
				else if(p.getStudyInm()=='1'){
					textView.setText(this.getContext().getString(R.string.inm));
				}
				else if(p.getStudyInm()=='2'){
					textView.setText(this.getContext().getString(R.string.no_def));
				}
			}
			
			ImageView imageView = (ImageView) v.findViewById(R.id.image);
			if (imageView != null) {
				imageView.setImageResource(R.drawable.ic_pregnant);
			}
		}
		return v;
	}
}
