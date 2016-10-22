package terstall.final_pset_jeroenterstall;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

class TrackCollectionAdapter extends BaseAdapter implements ListAdapter
{
    private ArrayList<Track> mTracks;
    private Context context;

    TrackCollectionAdapter(Context context, ArrayList<Track> mTracks)
    {
        this.context = context;
        this.mTracks = mTracks;
    }

    @Override
    public int getCount()
    {
        return mTracks.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mTracks.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = convertView;
        if(view == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_layout_item, null);
        }
        Track mTrack = mTracks.get(position);

        TextView name = (TextView) view.findViewById(R.id.name);
        name.setText(mTrack.getName());

        TextView artist = (TextView) view.findViewById(R.id.artist);
        artist.setText(mTrack.getArtist());

        return view;
    }
}
