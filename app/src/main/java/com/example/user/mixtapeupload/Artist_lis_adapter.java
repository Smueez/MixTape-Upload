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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class Artist_lis_adapter extends ArrayAdapter<Artist_class> {
    private Activity context;
    private List<Artist_class> list;
    String list_img;
    ImageView imageView;

    public Artist_lis_adapter(Activity context,List<Artist_class>list){
        super(context,R.layout.artist_adapter,list);

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
        View mylistview = inflater.inflate(R.layout.artist_adapter,null,true);

        StorageReference storageReference1 = FirebaseStorage.getInstance().getReference().child("image");
        TextView artistnm = mylistview.findViewById(R.id.textView);

        TextView likes_count = mylistview.findViewById(R.id.textView21);
        TextView song_count = mylistview.findViewById(R.id.textView20);
        TextView view_count = mylistview.findViewById(R.id.textView213);


        imageView = mylistview.findViewById(R.id.imageView9);
        Artist_class song1 = list.get(position);

        artistnm.setText(song1.getArtist_name());
        likes_count.setText(song1.getTotal_likes());
        song_count.setText(String.valueOf(song1.getSong_count()));
        view_count.setText(song1.getView_count());
        list_img = song1.getImg_url();

        if (list_img != "empty"){
            storageReference1.child(list_img).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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
