package my.carfix.carfix.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;

public class LogCaseMessage
{
    public static final String TABLE_NAME = "log_case_message";

    private String _id;

    private String caseNo;

    private String message;

    private long messageTime;

    public LogCaseMessage()
    {

    }

    public String get_id()
    {
        return _id;
    }

    public void set_id(String _id)
    {
        this._id = _id;
    }

    public String getCaseNo()
    {
        return caseNo;
    }

    public void setCaseNo(String caseNo)
    {
        this.caseNo = caseNo;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public long getMessageTime()
    {
        return messageTime;
    }

    public void setMessageTime(long messageTime)
    {
        this.messageTime = messageTime;
    }

    public void loadCursor(Cursor cursor)
    {
        set_id(cursor.getString(cursor.getColumnIndex("_id")));
        setCaseNo(cursor.getString(cursor.getColumnIndex("caseNo")));
        setMessage(cursor.getString(cursor.getColumnIndex("message")));
        setMessageTime(cursor.getLong(cursor.getColumnIndex("messageTime")));
    }

    public ContentValues toContentValues()
    {
        ContentValues contentValues = new ContentValues();

        contentValues.put("caseNo", caseNo);
        contentValues.put("message", message);
        contentValues.put("messageTime", messageTime);

        return contentValues;
    }

    @Override
    public String toString()
    {
        Date messageDate = new Date(messageTime * 1000);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        return String.format("%s %s", sdf.format(messageDate), message);
    }
}
