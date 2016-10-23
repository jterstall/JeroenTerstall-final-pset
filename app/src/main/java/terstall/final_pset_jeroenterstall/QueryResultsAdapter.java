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

class QueryResultsAdapter extends BaseAdapter implements ListAdapter
{
    private ArrayList<String> namelist;
    private ArrayList<String> artistlist;
    private ArrayList<String> urllist;
    private Context context;

    // Constructor
    QueryResultsAdapter(Context context, ArrayList<String> namelist, ArrayList<String> artistlist, ArrayList<String> urllist)
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
        View view = convertView;
        if(view == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_layout_item, null);
        }

        // Set name of object
        TextView queryName = (TextView) view.findViewById(R.id.name);
        queryName.setText(namelist.get(position));

        // Set artist name
        if(artistlist.size() != 0)
        {
            TextView queryArtist = (TextView) view.findViewById(R.id.artist);
            queryArtist.setText(artistlist.get(position));
        }

        // Set image
        ImageView queryImage = (ImageView) view.findViewById(R.id.image);
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

