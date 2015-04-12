package com.example.longpengjiao.ebaysearch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SearchFragment extends Fragment {

    private String keywords;
    private String minPrice;
    private String maxPrice;
    private String sortBy;

    float minPriceVal;
    float maxPriceVal;

    EditText keywordTextField;
    EditText minPriceTextField;
    EditText maxPriceTextField;

    TextView invalid_keyword_text;
    TextView invalid_minPrice_text;
    TextView invalid_maxPrice_text;
    TextView invalid_min_max_text;
    Pattern p = Pattern.compile("^\\d+(\\.\\d+)?$");

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

        minPriceVal=Float.parseFloat(minPrice);
        maxPriceVal=Float.parseFloat(maxPrice);

        if(maxPriceVal<minPriceVal){
            invalid_min_max_text.setVisibility(View.VISIBLE);
            return;
        }else{
            invalid_min_max_text.setVisibility(View.GONE);
        }
    }

}
