package ni.org.ics.zip.appmovil.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.List;

import ni.org.ics.zip.appmovil.R;
import ni.org.ics.zip.appmovil.domain.ZpAgendaEstudio;

/**
 * Created by A.L. on 13/11/2017.
 */

public class ZpAgendaEstudioAdapter extends ArrayAdapter<ZpAgendaEstudio> {

    private SimpleDateFormat mDateFormat = new SimpleDateFormat("hh:mm a");

    public ZpAgendaEstudioAdapter(Context context, int textViewResourceId,
                               List<ZpAgendaEstudio> items) {
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
        ZpAgendaEstudio p = getItem(position);
        if (p != null) {

            TextView textView = (TextView) v.findViewById(R.id.name_text);
            if (textView != null) {
                textView.setText(String.valueOf(position + 1) + ". " + this.getContext().getString(R.string.study_id) + ": " + p.getRecordId());
            }

            textView = (TextView) v.findViewById(R.id.identifier_text);
            if (textView != null && p.getAppointmentDateTime() != null) {
                textView.setText(this.getContext().getString(R.string._appointment_time) + ": " + mDateFormat.format(p.getAppointmentDateTime())
                            + "\n" + this.getContext().getString(R.string._especialidad) + ": " + p.getSpecialityType());
            }

            ImageView imageView = null;// (ImageView) v.findViewById(R.id.image);
            //if (imageView != null) {
          //      imageView.setImageResource(R.drawable.ic_baby);
            //}
        }
        return v;
    }

}
