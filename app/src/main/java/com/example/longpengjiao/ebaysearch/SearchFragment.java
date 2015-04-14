package com.example.longpengjiao.ebaysearch;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SearchFragment extends Fragment {

    private String keywords;
    private String minPrice;
    private String maxPrice;
    private String sortBy;

    private float minPriceVal;
    private float maxPriceVal;

    private  EditText keywordTextField;
    private EditText minPriceTextField;
    private EditText maxPriceTextField;
    private TextView invalid_noResults;
    private TextView invalid_keyword_text;
    private TextView invalid_minPrice_text;
    private TextView invalid_maxPrice_text;
    private TextView invalid_min_max_text;
    private Pattern p = Pattern.compile("^\\d+(\\.\\d+)?$");

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        Spinner spinner = (Spinner) rootView.findViewById(R.id.sortBy_spinner);
    // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.sortBy_options_array, android.R.layout.simple_spinner_item);
    // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    // Inflate the layout for this fragment
        Button button = (Button) rootView.findViewById(R.id.searchButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkForm();
            }
        });



        invalid_noResults = (TextView)rootView.findViewById(R.id.invalid_noResults);
        invalid_noResults.setVisibility(View.GONE);

        invalid_keyword_text  =(TextView) rootView.findViewById(R.id.invalid_keywords);
        invalid_keyword_text.setVisibility(View.GONE);
        invalid_minPrice_text  =(TextView) rootView.findViewById(R.id.invalid_minPrice);
        invalid_minPrice_text.setVisibility(View.GONE);
        invalid_maxPrice_text  =(TextView) rootView.findViewById(R.id.invalid_maxPrice);
        invalid_maxPrice_text.setVisibility(View.GONE);
        invalid_min_max_text  =(TextView) rootView.findViewById(R.id.invalid_min_max);
        invalid_min_max_text.setVisibility(View.GONE);


        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
        invalid_noResults.setVisibility(View.GONE);
    }

    /*
        called when search button is pressed to check valid form fields
     */
    private void checkForm(){
        keywordTextField  =(EditText) getActivity().findViewById(R.id.keywords_field);
        keywords = keywordTextField.getText().toString();
       //debug toast Toast.makeText(getActivity(), keywords, Toast.LENGTH_SHORT).show();
        minPriceTextField  =(EditText) getActivity().findViewById(R.id.minPrice_field);
        minPrice = minPriceTextField.getText().toString();
        maxPriceTextField  =(EditText) getActivity().findViewById(R.id.maxPrice_field);
        maxPrice = maxPriceTextField.getText().toString();
        Spinner mySpinner= (Spinner) getActivity().findViewById(R.id.sortBy_spinner);
        sortBy = mySpinner.getSelectedItem().toString();
        //Toast.makeText(getActivity(), sortBy, Toast.LENGTH_SHORT).show();

        if(keywords.equals("")){
            invalid_keyword_text.setVisibility(View.VISIBLE);
            return;
        }else{
            invalid_keyword_text.setVisibility(View.GONE);
        }


        if(!minPrice.equals("")){
            Matcher m = p.matcher(minPrice);
            if(!m.matches()){
                invalid_minPrice_text.setVisibility(View.VISIBLE);
                return;
            }else{
                invalid_minPrice_text.setVisibility(View.GONE);
            }
        }

        if(!maxPrice.equals("")){
            Matcher m = p.matcher(maxPrice);
            if(!m.matches()){
                invalid_maxPrice_text.setVisibility(View.VISIBLE);
                return;
            }else{
                invalid_maxPrice_text.setVisibility(View.GONE);
            }
        }
        if(minPrice.length()!=0 && maxPrice.length()!=0) {
            minPriceVal = Float.parseFloat(minPrice);
            maxPriceVal = Float.parseFloat(maxPrice);

            if (maxPriceVal < minPriceVal) {
                invalid_min_max_text.setVisibility(View.VISIBLE);
                return;
            } else {
                invalid_min_max_text.setVisibility(View.GONE);
            }
        }

        makeCall();
    }

    private void makeCall(){
        FetchEbayResults ebayTask = new FetchEbayResults();
        ebayTask.execute();
    }


    public class FetchEbayResults extends AsyncTask<String, Void, String> {

        private final String LOG_TAG = FetchEbayResults.class.getSimpleName();

        @Override
        protected String doInBackground(String... params) {
            
            //HTTP request to HW9  index.php on amazon
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                urlConnection = null;
                reader = null;

                //raw JSON string as response
                String resultsJsonStr = null;

                //strings for building url
                final String EBAY_BASE_URL = "http://hw9ebaysearch-env.elasticbeanstalk.com/?";
                final String KEYWORDS_PARAM = "keywords";
                final String MINPRICE_PARAM = "min_price";
                final String MAXPRICE_PARAM = "max_price";
                final String SORTYBY_PARAM = "sortby";
                final String RESULTS_PER_PAGE_PARAM = "results_per_page";
                final String PAGE_NUM = "pageNum";

                //build URL to call PHP
                Uri builtUri = Uri.parse(EBAY_BASE_URL).buildUpon()
                        .appendQueryParameter(KEYWORDS_PARAM, keywords)
                        .appendQueryParameter(MINPRICE_PARAM, minPrice)
                        .appendQueryParameter(MAXPRICE_PARAM, maxPrice)
                        .appendQueryParameter(RESULTS_PER_PAGE_PARAM, "5")
                        .appendQueryParameter(PAGE_NUM, "1")
                        .appendQueryParameter(SORTYBY_PARAM, sortBy).build();


                URL url = new URL(builtUri.toString());
                //make URL connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();


                //Read Response
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                resultsJsonStr = buffer.toString();
                Log.v(LOG_TAG, "result json string: " + resultsJsonStr);

                return resultsJsonStr;

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error", e);
                return null;
            }  finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
        }


        @Override
        public void onPostExecute(String result) {
            try {
                JSONObject resultsJson = new JSONObject(result);

                String totItems = resultsJson.getString("resultCount");
                //check if there were any results and start activity, otherwise, show error dialog
                if (Integer.parseInt(totItems)!=0) {
                    //start new dislayResult activity
                    Intent resultIntent = new Intent(getActivity(), ResultActivity.class)
                            .putExtra(Intent.EXTRA_TEXT, result);
                    startActivity(resultIntent);
                }else{
                    getActivity().findViewById(R.id.invalid_noResults).setVisibility(View.VISIBLE);
                }

            }
            catch(JSONException e){
                Log.e("Error", e.getMessage());
            }
        }
    }


}
