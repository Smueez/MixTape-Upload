package com.example.user.mixtapeupload;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.support.constraint.Constraints.TAG;

public class Listadapter extends ArrayAdapter<Song> {
    private Activity context;
    private List<Song> list;
    String list_img;

    public Listadapter(Activity context,List<Song>list){
        super(context,R.layout.list_song,list);

        this.context = context;
        this.list = list;
    }
    public static class DownLoadImageTask extends AsyncTask<String,Void,Bitmap> {
        ImageView listimage;


        public DownLoadImageTask(ImageView listimage){
            this.listimage = listimage;
        }

        /*
            doInBackground(Params... params)
                Override this method to perform a computation on a background thread.
         */
        protected Bitmap doInBackground(String...urls){
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try{
                InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
                logo = BitmapFactory.decodeStream(is);
            }catch(Exception e){ // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }

        /*
            onPostExecute(Result result)
                Runs on the UI thread after doInBackground(Params...).
         */
        protected void onPostExecute(Bitmap result){
            listimage.setImageBitmap(result);
        }
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View mylistview = inflater.inflate(R.layout.list_song,null,true);

        StorageReference storageReference1 = FirebaseStorage.getInstance().getReference();

        TextView songName = mylistview.findViewById(R.id.song);
        TextView artist = mylistview.findViewById(R.id.artist);

        TextView likes = mylistview.findViewById(R.id.likes);
        TextView views = mylistview.findViewById(R.id.views);
        final ImageView imageView = mylistview.findViewById(R.id.imageView3);
        Song song1 = list.get(position);
        songName.setText(song1.getSname());
        artist.setText(song1.getArtist());
        likes.setText(song1.getLikes());
        views.setText(song1.getViews());
        list_img = song1.getImageurl();

        if (list_img != "empty"){
            storageReference1.child("image").child(song1.getSname()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Log.d(TAG, "onSuccess: got url of image!");
                    new Listadapter.DownLoadImageTask(imageView).execute(uri.toString());
                    Log.d(TAG, "onSuccess: "+uri.toString());
                    // progressBar1.setVisibility(View.GONE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: "+e.getMessage());
                }
            });
        }

        return mylistview;
    }
}
