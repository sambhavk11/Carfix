package my.carfix.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import my.carfix.carfix.R;
import my.carfix.carfix.model.DrawerItem;

public class DrawerItemAdapter extends ArrayAdapter<DrawerItem>
{
    private List<DrawerItem> drawerItems;

    private Context context;

    public DrawerItemAdapter(Context context, int resource, List<DrawerItem> drawerItems)
    {
        super(context, resource, drawerItems);

        this.drawerItems = drawerItems;

        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        DrawerItem drawerItem = drawerItems.get(position);

        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.drawer_item_row, parent, false);

            ImageView imageView = (ImageView)convertView.findViewById(R.id.image_view);

            imageView.setImageDrawable(convertView.getResources().getDrawable(drawerItem.getResourceId()));

            TextView nameTextView = (TextView)convertView.findViewById(R.id.text_view_name);

            nameTextView.setText(drawerItem.getName());
        }

        return convertView;
    }
}
