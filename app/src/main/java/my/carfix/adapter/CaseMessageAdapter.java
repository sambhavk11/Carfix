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
import my.carfix.carfix.model.LogCaseMessage;

public class CaseMessageAdapter extends ArrayAdapter<LogCaseMessage>
{
    private List<LogCaseMessage> logCaseMessages;

    private Context context;

    public CaseMessageAdapter(Context context, int resource, List<LogCaseMessage> logCaseMessages)
    {
        super(context, resource, logCaseMessages);

        this.logCaseMessages = logCaseMessages;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LogCaseMessage message = logCaseMessages.get(position);

        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.case_message_row, parent, false);

            TextView messageTextView = (TextView)convertView.findViewById(R.id.text_view_message);

            messageTextView.setText(message.getMessage());

            Date messageDate = new Date(message.getMessageTime() * 1000);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            TextView messageTimeTextView = (TextView)convertView.findViewById(R.id.text_view_message_time);

            messageTimeTextView.setText(sdf.format(messageDate));
        }

        return convertView;
    }
}
