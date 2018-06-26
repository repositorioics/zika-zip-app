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
import ni.org.ics.zip.appmovil.domain.*;
import ni.org.ics.zip.appmovil.utils.Constants;

import java.util.List;

public class Infant07aVisitAdapter extends ArrayAdapter<String> {

	private final Context context;
	private final String[] values;
	private final List<Zp07aInfantOphtResults>  mZp07a;

	private boolean exa1 = false;
	private boolean exa2 = false;
	private boolean exa3 = false;
	private boolean exa4 = false;
	private boolean exa5 = false;
	private boolean exa6 = false;
	private boolean changed = false;

	public Infant07aVisitAdapter(Context context, int textViewResourceId,
								 String[] values, List<Zp07aInfantOphtResults> zp07a) {
		super(context, textViewResourceId, values);
		this.context = context;
		this.values = values;
		this.mZp07a = zp07a;
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

		if (!mZp07a.isEmpty()) {
			if (!changed){
				changed = true;
				for (Zp07aInfantOphtResults zp: mZp07a) {
					if (zp.getRedcapEventName().matches(Constants.EXAM1)){
						exa1 = true;
					}else if(zp.getRedcapEventName().matches(Constants.EXAM2)){
						exa2 = true;
					}else if(zp.getRedcapEventName().matches(Constants.EXAM3)){
						exa3 = true;
					}else if (zp.getRedcapEventName().matches(Constants.EXAM4)){
						exa4 = true;
					}else if (zp.getRedcapEventName().matches(Constants.EXAM5)){
						exa5 = true;
					}else if (zp.getRedcapEventName().matches(Constants.EXAM6)){
						exa6 = true;
					}
				}
			}

		}
		switch (position) {
			case 0:
				if (exa1) {
					textView.setText(textView.getText() + "\n" + context.getResources().getString(R.string.done));
				} else {
					textView.setTextColor(Color.RED);
					textView.setText(textView.getText() + "\n" + context.getResources().getString(R.string.pending));
				}
				img = getContext().getResources().getDrawable(R.drawable.ic_test);
				textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
				break;

			case 1:
				if (exa2) {
					textView.setText(textView.getText() + "\n" + context.getResources().getString(R.string.done));
				} else {
					textView.setTextColor(Color.RED);
					textView.setText(textView.getText() + "\n" + context.getResources().getString(R.string.pending));
				}
				img = getContext().getResources().getDrawable(R.drawable.ic_test);
				textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
				break;
			case 2:
				if (exa3) {
					textView.setText(textView.getText() + "\n" + context.getResources().getString(R.string.done));
				} else {
					textView.setTextColor(Color.RED);
					textView.setText(textView.getText() + "\n" + context.getResources().getString(R.string.pending));
				}
				img = getContext().getResources().getDrawable(R.drawable.ic_test);
				textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
				break;
			case 3:
				if (exa4) {
					textView.setText(textView.getText() + "\n" + context.getResources().getString(R.string.done));
				} else {
					textView.setTextColor(Color.RED);
					textView.setText(textView.getText() + "\n" + context.getResources().getString(R.string.pending));
				}
				img = getContext().getResources().getDrawable(R.drawable.ic_test);
				textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
				break;
			case 4:
				if (exa5) {
					textView.setText(textView.getText() + "\n" + context.getResources().getString(R.string.done));
				} else {
					textView.setTextColor(Color.RED);
					textView.setText(textView.getText() + "\n" + context.getResources().getString(R.string.pending));
				}
				img = getContext().getResources().getDrawable(R.drawable.ic_test);
				textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
				break;
			case 5:
				if (exa6) {
					textView.setText(textView.getText() + "\n" + context.getResources().getString(R.string.done));
				} else {
					textView.setTextColor(Color.RED);
					textView.setText(textView.getText() + "\n" + context.getResources().getString(R.string.pending));
				}
				img = getContext().getResources().getDrawable(R.drawable.ic_test);
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
