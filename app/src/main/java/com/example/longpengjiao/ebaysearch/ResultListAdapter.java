package com.example.longpengjiao.ebaysearch;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by longpengjiao on 4/12/15.
 * Custom Array Adapter to populate results list of ebay search
 */
public class ResultListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final ArrayList<String> itemTitlesList;
    private final ArrayList<String> itemPricesList;
    private final ArrayList<String> itemImagesURLList;


    public ResultListAdapter(Activity context, ArrayList<String> titles, ArrayList<String> imageURLs, ArrayList<String> prices) {
        super(context, R.layout.results_row, titles);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.itemTitlesList=titles;
        this.itemImagesURLList=imageURLs;
        this.itemPricesList = prices;
    }

    public View getView(int position,View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.results_row, null,true);

        TextView title = (TextView) rowView.findViewById(R.id.title);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.item_image);
        TextView price = (TextView) rowView.findViewById(R.id.price);

        title.setText(itemTitlesList.get(position));
        String imgURL = itemImagesURLList.get(position);

        new DownloadImageTask(imageView).execute(imgURL);

        //imageView.setImageDrawable(itemImagesList.get(position));
        price.setText(itemPricesList.get(position));
        return rowView;

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        private final String LOG_TAG = DownloadImageTask.class.getSimpleName();
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
