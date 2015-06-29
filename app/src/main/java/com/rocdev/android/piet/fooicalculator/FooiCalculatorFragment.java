package com.rocdev.android.piet.fooicalculator;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;



public class FooiCalculatorFragment extends Fragment
        implements TextView.OnEditorActionListener, View.OnClickListener {

    //variabelen voor de widgets
    private EditText rekeningBedragEditText;
    private TextView procentTextView;
    private Button procentPlusButton;
    private Button procentMinButton;
    private TextView fooiTextView;
    private TextView totaalTextView;

    //variabelen die moeten worden bewaard
    private String rekeningBedragString = "";
    private int fooiPercentage = 15;

    //afrondings constanten
    private final int AFRONDING_GEEN = 0;
    private final int AFRONDING_FOOI = 1;
    private final int AFRONDING_TOTAAL = 2;

    //maak preferences
    private SharedPreferences prefs;
    private boolean bewaarFooiPercentage = true;
    private int afronding = AFRONDING_GEEN;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // standaard waardes voor de preferences
        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);

        // maak het default SharedPreferences object aan
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // geef aan dat er een opties menu is
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate de layout voor dit fragment
        View view = inflater.inflate(R.layout.fragment_fooi_calculator, container, false);

        // haal de widgets op
        rekeningBedragEditText = (EditText) view.findViewById(R.id.rekeningBedragEditText);
        procentTextView = (TextView) view.findViewById(R.id.procentTextView);
        procentPlusButton = (Button) view.findViewById(R.id.plus_knop);
        procentMinButton = (Button) view.findViewById(R.id.min_knop);
        fooiTextView = (TextView) view.findViewById(R.id.fooiTextView);
        totaalTextView = (TextView) view.findViewById(R.id.totaalTextView);

        // koppel de listeners
        rekeningBedragEditText.setOnEditorActionListener(this);
        procentPlusButton.setOnClickListener(this);
        procentMinButton.setOnClickListener(this);

        // return View
        return view;
    }

    @Override
    public void onPause() {
        // bewaar de variabelen
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("rekeningBedragString", rekeningBedragString);
        editor.putFloat("fooiPercentage", fooiPercentage);
        editor.commit();

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        // haal preferences op
        bewaarFooiPercentage = prefs.getBoolean("pref_remember_percent", true);
        afronding = Integer.parseInt(prefs.getString("pref_rounding", "0"));

        // haal de bewaarde variabelen op
        rekeningBedragString = prefs.getString("billAmountString", "");
        if (bewaarFooiPercentage) {
            fooiPercentage = prefs.getInt("fooiPercentage", 15);
        } else {
            fooiPercentage = 15;
        }

        // vertoon rekening bedrag in widget
        rekeningBedragEditText.setText(rekeningBedragString);

        // bereken en toon
        berekenEnToon();
    }

    private void berekenEnToon() {

        // haal rekening bedrag op
        rekeningBedragString = rekeningBedragEditText.getText().toString();
        double rekeningBedrag;
        if (rekeningBedragString.equals("")) {
            rekeningBedrag = 0.0;
        } else {
            rekeningBedrag = Double.parseDouble(rekeningBedragString);
        }

        // bereken fooi en totaal
        double fooiBedrag = rekeningBedrag * (fooiPercentage/100);
        if (afronding == AFRONDING_FOOI) {
            fooiBedrag = Math.round(fooiBedrag);
        }
        double totaalBedrag = rekeningBedrag + fooiBedrag;

        if (afronding == AFRONDING_TOTAAL) {
            totaalBedrag = Math.round(totaalBedrag);
            //pas de fooi aan aan de afronding
            fooiBedrag = totaalBedrag - rekeningBedrag;

        }

        // toon geformatteerde bedragen
        DecimalFormat df = new DecimalFormat("€ ###,##0.00");
        fooiTextView.setText(df.format(fooiBedrag));
        totaalTextView.setText(df.format(totaalBedrag));
        //toon percentage
        procentTextView.setText("" + Math.round(fooiPercentage) + "%");

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        int keyCode = -1;
        if (event != null) {
            keyCode = event.getKeyCode();
        }
        if (actionId == EditorInfo.IME_ACTION_DONE ||
                actionId == EditorInfo.IME_ACTION_NEXT ||
                actionId == EditorInfo.IME_ACTION_UNSPECIFIED ||
                keyCode == KeyEvent.KEYCODE_DPAD_CENTER ||
                keyCode == KeyEvent.KEYCODE_ENTER) {
            berekenEnToon();
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.min_knop:
                fooiPercentage = fooiPercentage - 1;
                berekenEnToon();
                break;
            case R.id.plus_knop:
                fooiPercentage = fooiPercentage + 1;
                berekenEnToon();
                break;
        }
    }








}