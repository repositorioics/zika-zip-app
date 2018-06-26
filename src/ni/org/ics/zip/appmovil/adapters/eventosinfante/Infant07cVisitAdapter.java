package ni.org.ics.zip.appmovil.adapters.eventosinfante;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import ni.org.ics.zip.appmovil.R;
import ni.org.ics.zip.appmovil.domain.Zp07aInfantOphtResults;
import ni.org.ics.zip.appmovil.domain.Zp07bInfantAudioResults;
import ni.org.ics.zip.appmovil.domain.Zp07cInfantImageStudies;
import ni.org.ics.zip.appmovil.domain.Zp07dInfantBayleyScales;
import ni.org.ics.zip.appmovil.utils.Constants;

import java.util.List;

public class Infant07cVisitAdapter extends ArrayAdapter<String> {

	private final Context context;
	private final String[] values;
	private final List<Zp07cInfantImageStudies> mZp07c;

	private boolean ex1 = false;
	private boolean ex2 = false;
	private boolean ex3 = false;
	private boolean ex4 = false;
	private boolean ex5 = false;
	private boolean ex6 = false;
	private boolean changed = false;


	public Infant07cVisitAdapter(Context context, int textViewResourceId,
                                 String[] values, List<Zp07cInfantImageStudies> zp07c) {
		super(context, textViewResourceId, values);
		this.context = context;
		this.values = values;
		this.mZp07c = zp07c;

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

		// Change icon based on position
		Drawable img = null;

		if (!mZp07c.isEmpty()) {
			if (!changed){
				changed = true;
				for (Zp07cInfantImageStudies zp: mZp07c) {
					if (zp.getRedcapEventName().matches(Constants.EXAM1)){
						ex1 = true;
					}else if(zp.getRedcapEventName().matches(Constants.EXAM2)){
						ex2 = true;
					}else if(zp.getRedcapEventName().matches(Constants.EXAM3)){
						ex3 = true;
					}else if (zp.getRedcapEventName().matches(Constants.EXAM4)){
						ex4 = true;
					}else if (zp.getRedcapEventName().matches(Constants.EXAM5)){
						ex5 = true;
					}else if (zp.getRedcapEventName().matches(Constants.EXAM6)){
						ex6 = true;
					}
				}
			}

		}

		switch (position) {
			case 0:
				if (ex1) {
					textView.setText(textView.getText() + "\n" + context.getResources().getString(R.string.done));
				} else {
					textView.setTextColor(Color.RED);
					textView.setText(textView.getText() + "\n" + context.getResources().getString(R.string.pending));
				}
				img = getContext().getResources().getDrawable(R.drawable.ic_test);
				textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
				break;

			case 1:
				if (ex2) {
					textView.setText(textView.getText() + "\n" + context.getResources().getString(R.string.done));
				} else {
					textView.setTextColor(Color.RED);
					textView.setText(textView.getText() + "\n" + context.getResources().getString(R.string.pending));
				}
				img = getContext().getResources().getDrawable(R.drawable.ic_test);
				textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
				break;
			case 2:
				if (ex3) {
					textView.setText(textView.getText() + "\n" + context.getResources().getString(R.string.done));
				} else {
					textView.setTextColor(Color.RED);
					textView.setText(textView.getText() + "\n" + context.getResources().getString(R.string.pending));
				}
				img = getContext().getResources().getDrawable(R.drawable.ic_test);
				textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
				break;
			case 3:
				if (ex4) {
					textView.setText(textView.getText() + "\n" + context.getResources().getString(R.string.done));
				} else {
					textView.setTextColor(Color.RED);
					textView.setText(textView.getText() + "\n" + context.getResources().getString(R.string.pending));
				}
				img = getContext().getResources().getDrawable(R.drawable.ic_test);
				textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
				break;
			case 4:
				if (ex5) {
					textView.setText(textView.getText() + "\n" + context.getResources().getString(R.string.done));
				} else {
					textView.setTextColor(Color.RED);
					textView.setText(textView.getText() + "\n" + context.getResources().getString(R.string.pending));
				}
				img = getContext().getResources().getDrawable(R.drawable.ic_test);
				textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
				break;
			case 5:
				if (ex6) {
					textView.setText(textView.getText() + "\n" + context.getResources().getString(R.string.done));
				} else {
					textView.setTextColor(Color.RED);
					textView.setText(textView.getText() + "\n" + context.getResources().getString(R.string.pending));
				}
				img = getContext().getResources().getDrawable(R.drawable.ic_test);
				textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
				break;

			default:
				img = getContext().getResources().getDrawable(R.drawable.ic_launcher);
				textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
				break;
		}

		return v;
	}
}
