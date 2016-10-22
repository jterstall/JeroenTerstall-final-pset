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

class AlbumCollectionAdapter extends BaseAdapter implements ListAdapter
{
    private ArrayList<Album> mAlbums;
    private Context context;

    AlbumCollectionAdapter(Context context, ArrayList<Album> mAlbums)
    {
        this.context = context;
        this.mAlbums = mAlbums;
    }

    @Override
    public int getCount()
    {
        return mAlbums.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mAlbums.get(position);
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
        Album mAlbum = mAlbums.get(position);

        ImageView image = (ImageView) view.findViewById(R.id.image);
        String image_url = mAlbum.getImage_url();
        if(image_url.trim().length() != 0)
        {
            Picasso.with(context).load(image_url).into(image);
        }

        TextView name = (TextView) view.findViewById(R.id.name);
        name.setText(mAlbum.getName());

        TextView artist = (TextView) view.findViewById(R.id.artist);
        artist.setText(mAlbum.getArtist());

        return view;
    }
}
