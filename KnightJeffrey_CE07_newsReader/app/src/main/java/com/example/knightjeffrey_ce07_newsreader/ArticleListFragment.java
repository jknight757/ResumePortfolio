//Jeffrey Knight
// Java 2 1911
//CE07

package com.example.knightjeffrey_ce07_newsreader;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Objects;


public class ArticleListFragment extends ListFragment {

    private final ArrayList<Article> mArticles =  new ArrayList<>();
    public ArticleListFragment() {
        // Required empty public constructor
    }

    public static ArticleListFragment newInstance() {

        Bundle args = new Bundle();

        ArticleListFragment fragment = new ArticleListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_article_list, container, false);
    }

    @Override
    public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Article myArticle = mArticles.get(position);
        Intent webIntent = new Intent(Intent.ACTION_VIEW);
        webIntent.setData(Uri.parse(myArticle.getWebUrl()));
        startActivity(webIntent);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getContext() != null){
            ListView list = Objects.requireNonNull(getView()).findViewById(android.R.id.list);
            if(list != null){
                ArticleAdapter adapter = new ArticleAdapter(getContext());
                list.setAdapter(adapter);
            }
        }
    }

    // nested Adapter and View holder
    class ArticleAdapter extends BaseAdapter{

        private File[] articles;
        private final Context mContext;
        final File folderPath;

        ArticleAdapter(Context _context){

            mContext = _context;
            folderPath= Objects.requireNonNull(getContext()).getExternalFilesDir(SaveService.FOLDER);
            articles = Objects.requireNonNull(folderPath).listFiles();
        }
        @Override
        public int getCount() {
            if(articles != null){
                return articles.length;
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if(articles != null && position >= 0 && position <articles.length){
                return articles[position];
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
                convertView = LayoutInflater.from(mContext).inflate(R.layout.article_list,parent,false);
                vh = new ViewHolder(convertView);
                convertView.setTag(vh);
            }else{
                vh = (ViewHolder) convertView.getTag();
            }
            //File folderPath = getContext().getExternalFilesDir(SaveService.FOLDER);
            articles = folderPath.listFiles();
            Article article = null;
            byte[] byteImage;
            Bitmap image = null;
            if(articles != null){
                try{
                    FileInputStream fiz = new FileInputStream(articles[position].getAbsolutePath());
                    ObjectInputStream ois = new ObjectInputStream(fiz);
                    article = (Article) ois.readObject();
                    ois.close();
                    mArticles.add(article);

                    byteImage = article.getImage();
                    image = BitmapFactory.decodeByteArray(byteImage,0,byteImage.length);

                }catch (IOException |ClassNotFoundException e){
                    e.printStackTrace();
                }

                if(article != null){
                    vh.imageView.setImageBitmap(image);
                    vh.titleView.setText(article.getTitle());
                }

            }

            return convertView;
        }
    }
    static class ViewHolder{
        final ImageView imageView;
        final TextView titleView;
        ViewHolder(View _layout){
            imageView = _layout.findViewById(R.id.image_view);
            titleView = _layout.findViewById(R.id.title_view);
        }
    }

}
