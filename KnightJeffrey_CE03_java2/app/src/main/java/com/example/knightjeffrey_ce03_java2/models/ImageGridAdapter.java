package com.example.knightjeffrey_ce03_java2.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.example.knightjeffrey_ce03_java2.R;
import java.io.File;


public class ImageGridAdapter extends BaseAdapter {

    private Context mContext;
    private final File[] mFileNames;

    public ImageGridAdapter(Context _context, File[] _fileNames){
        mContext = _context;
        mFileNames = _fileNames;
    }
    @Override
    public int getCount() {
        if(mFileNames != null){
            return mFileNames.length;
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if(mFileNames != null && position >= 0 && position < mFileNames.length){
            return mFileNames[position];
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;


        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.my_grid_layout,parent,false);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder)convertView.getTag();
        }

        if(mFileNames[position] != null){
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inSampleSize = 4;
            Bitmap bmp = BitmapFactory.decodeFile(mFileNames[position].getAbsolutePath(), opt);
            Log.i("Path", "getView: " + mFileNames[position].getAbsolutePath());
            vh.imageView.setImageBitmap(bmp);

        }

        return convertView;
    }

    static class ViewHolder{
        final ImageView imageView;

        ViewHolder(View _layout){
            imageView = _layout.findViewById(R.id.img_item);

        }
    }
}
