package my.carfix.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import my.carfix.carfix.R;
import my.carfix.carfix.model.LogCase;

public class CaseAdapter extends ArrayAdapter<LogCase>
{
    private List<LogCase> logCases;

    private Context context;

    public CaseAdapter(Context context, int resource, List<LogCase> logCases)
    {
        super(context, resource, logCases);

        this.logCases = logCases;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LogCase logCase = logCases.get(position);

        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.case_row, parent, false);

            TextView caseNoTextView = (TextView)convertView.findViewById(R.id.text_view_caseno);

            caseNoTextView.setText("Case #" + logCase.getCaseNo());

            Date messageDate = new Date(logCase.getArrivedTime() * 1000);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            TextView arrivedTimeTextView = (TextView)convertView.findViewById(R.id.text_view_arrived_time);

            arrivedTimeTextView.setText("ETA : " + (logCase.getArrivedTime() > 0 ? sdf.format(messageDate) : "-"));
        }

        return convertView;
    }
}
