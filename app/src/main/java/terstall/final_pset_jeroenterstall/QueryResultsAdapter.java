package terstall.final_pset_jeroenterstall;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

// This class overwrites the basic adapter for a listview with images, text and a button

public class QueryResultsAdapter extends BaseAdapter implements ListAdapter
{
    private ArrayList<String> namelist;
    private ArrayList<String> artistlist;
    private ArrayList<String> urllist;
    private Context context;

    // Constructor
    public QueryResultsAdapter(Context context, ArrayList<String> namelist, ArrayList<String> artistlist, ArrayList<String> urllist)
    {
        this.context = context;
        this.namelist = namelist;
        this.artistlist = artistlist;
        this.urllist = urllist;
    }

    @Override
    public int getCount()
    {
        return namelist.size();
    }

    @Override
    public Object getItem(int position)
    {
        return position;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        // Create a listview with custom list items made in xml
        View view = convertView;
        if(view == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.query_list_item, null);
        }

        // Retrieve TextView object and change the text
        TextView queryName = (TextView) view.findViewById(R.id.query_name);
        queryName.setText(namelist.get(position));

        if(artistlist.size() != 0)
        {
            TextView queryArtist = (TextView) view.findViewById(R.id.query_artist);
            queryArtist.setText(artistlist.get(position));
        }
        // Retrieve ImageView and set it with the correct image
        ImageView queryImage = (ImageView) view.findViewById(R.id.query_image);
        String image_url = urllist.get(position);
        if(image_url.isEmpty())
        {
            queryImage.setImageResource(R.drawable.no_image);
        }
        else
        {
            Picasso.with(context).load(urllist.get(position)).into(queryImage);
        }
        return view;
    }
}
