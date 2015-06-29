package com.rocdev.android.piet.fooicalculator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;


public class MainActivity extends ActionBarActivity
        implements TextView.OnEditorActionListener,SeekBar.OnSeekBarChangeListener,
        RadioGroup.OnCheckedChangeListener, AdapterView.OnItemSelectedListener {

    //variabelen voor de widgets
    private EditText rekeningBedragEditText;
    private TextView bedragPerPersoonLabel,bedragPerPersoonTextView;
    private TextView fooiTextView,procentTextView, totaalTextView;
    private RadioGroup afrondRadioGroup;
    private RadioButton afrondGeenRadioButton;
    private RadioButton afrondFooiRadioButton;
    private RadioButton afrondTotaalRadioButton;
    private Spinner delenSpinner;
    private SeekBar procentSeekBar;

    //het SharedPreferences object
    private SharedPreferences prefs;



    //afrondings constanten
    private final int AFROND_GEEN = 0;
    private final int AFROND_FOOI = 1;
    private final int AFROND_TOTAAL = 2;

    //reken variabelen
    private String rekeningBedragString;
    private int fooiPercentageInt = 15;
    private int defaultFooiPercentageInt = 15;
    private double fooiPercentage = (double) fooiPercentageInt;
    private boolean bewaarFooiProcent = true;
    private int afronding = AFROND_GEEN;
    private int delen = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //maak referenties naar de widgets
        rekeningBedragEditText = (EditText) findViewById(R.id.rekeningBedragEditText);
        procentTextView = (TextView) findViewById(R.id.procentTextView);
        procentSeekBar = (SeekBar) findViewById(R.id.procentSeekBar);
        fooiTextView = (TextView) findViewById(R.id.fooiTextView);
        totaalTextView = (TextView) findViewById(R.id.totaalTextView);
        afrondRadioGroup = (RadioGroup) findViewById(R.id.afrondingRadioGroup);
        afrondGeenRadioButton = (RadioButton) findViewById(R.id.geenAfrondingRadioButton);
        afrondFooiRadioButton = (RadioButton) findViewById(R.id.fooiAfrondingRadioButton);
        afrondTotaalRadioButton = (RadioButton) findViewById(R.id.totaalAfrondingRadioButton);

        //geenAfrondingRadioButton = (RadioButton) findViewById(R.id.geenAfrondingRadioButton);
        delenSpinner = (Spinner) findViewById(R.id.delenSpinner);
        bedragPerPersoonLabel = (TextView) findViewById(R.id.perPersoonLabel);
        bedragPerPersoonTextView = (TextView) findViewById(R.id.bedragPerPersoonTextView);


        //array adapter voor de spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.delen_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        delenSpinner.setAdapter(adapter);

        //koppel de Listeners
        rekeningBedragEditText.setOnEditorActionListener(this);
        procentSeekBar.setOnSeekBarChangeListener(this);
        afrondRadioGroup.setOnCheckedChangeListener(this);
        delenSpinner.setOnItemSelectedListener(this);

        //set standaard waardes in de preferences file
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

       /*
        SharedPreferences bewaart waardes als methode onPause() wordt aangeroepen
        haalt waardes weer op als onResume() wordt aangeroepen
        @param "BewaardeWaardes = naam of sleutel
        @param Mode_private betekent dat het alleen binnen de app gebruikt mag worden
        */
        //prefs = getSharedPreferences("BewaardeWaardes", MODE_PRIVATE);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

    }

    @Override
    public void onPause() {
        // bewaar de variabelen
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("rekeningBedragString", rekeningBedragString);
        fooiPercentageInt = (int) fooiPercentage;
        editor.putInt("fooiPercentageInt", fooiPercentageInt);
        editor.commit();

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        bewaarFooiProcent = prefs.getBoolean("pref_bewaar_percentage", false);
        afronding = Integer.parseInt(prefs.getString("pref_afronding", "0"));
        if (bewaarFooiProcent) {
            fooiPercentageInt = prefs.getInt("fooiPercentageInt", 15);
        } else {
            fooiPercentageInt = Integer.parseInt(prefs.getString("pref_default_fooi", "15"));
        }
        rekeningBedragString = prefs.getString("rekeningBedragString", "");

        rekeningBedragEditText.setText(rekeningBedragString);
        fooiTextView.setText("" + fooiPercentageInt + "%");
        fooiPercentage = (double) fooiPercentageInt;

        // berekenEnToon
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
        if (afronding == AFROND_FOOI) {
            fooiBedrag = Math.round(fooiBedrag);
            afrondFooiRadioButton.setChecked(true);
        }
        double totaalBedrag = rekeningBedrag + fooiBedrag;
        if (afronding == AFROND_TOTAAL) {
            totaalBedrag = Math.round(totaalBedrag);
            //pas fooi aan aan de afronding
            fooiBedrag = totaalBedrag - rekeningBedrag;
            afrondTotaalRadioButton.setChecked(true);
        }
        if (afronding == AFROND_GEEN) {
            afrondGeenRadioButton.setChecked(true);
        }

        // toon geformatteerde bedragen
        DecimalFormat df = new DecimalFormat("€ ###,##0.00");
        fooiTextView.setText(df.format(fooiBedrag));
        totaalTextView.setText(df.format(totaalBedrag));
        //toon percentage
        procentTextView.setText("" + Math.round(fooiPercentage) + "%");
        //toon juiste progress in seekbar
        procentSeekBar.setProgress(fooiPercentageInt);



        //bedrag wordt eventueel gedeeld
        double bedragPerPersoon = 0;
        switch (delen) {
            case 1:  // geen deling, verberg per persoon views
                bedragPerPersoonLabel.setVisibility(View.GONE);
                bedragPerPersoonTextView.setVisibility(View.GONE);
                break;
            case 2:
            case 3:
            case 4:
                bedragPerPersoon = totaalBedrag / delen;
                bedragPerPersoonTextView.setText(df.format(bedragPerPersoon));
                bedragPerPersoonLabel.setVisibility(View.VISIBLE);
                bedragPerPersoonTextView.setVisibility(View.VISIBLE);
                break;
        }

    }

    /****************************************************************************
     Event handler voor de de EditText View
     ****************************************************************************/
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE ||
                actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
            berekenEnToon();
        }
        return false;
    }


    /****************************************************************************
     Event handlers voor de de SeekBar
     ****************************************************************************/
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        //geenAfrondingRadioButton.setChecked(true);
        fooiPercentageInt = i;
        fooiPercentage = (double) fooiPercentageInt;
        procentTextView.setText("" + fooiPercentageInt + "%");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //doe niets
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        berekenEnToon();
    }


    /****************************************************************************
     Event handler voor de de RadioGroup
     ****************************************************************************/
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.geenAfrondingRadioButton:
                afronding = AFROND_GEEN;
                berekenEnToon();
                break;
            case R.id.fooiAfrondingRadioButton:
                afronding = AFROND_FOOI;
                berekenEnToon();
                break;
            case R.id.totaalAfrondingRadioButton:
                afronding = AFROND_TOTAAL;
                berekenEnToon();
        }
    }

    /****************************************************************************
     Event handlers voor de de Spinner
     ****************************************************************************/
    public void onItemSelected(AdapterView<?> parent, View v, int positie, long id) {
        delen = positie + 1;
        berekenEnToon();
    }

    public void onNothingSelected(AdapterView<?> parent ) {
        //doe niets
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // bepaal welk menu-item is geselecteerd
        switch (item.getItemId() ) {
            case R.id.menu_instellingen:
                //Toast.makeText(this, "Instellingen", Toast.LENGTH_SHORT).show();
                Intent instellingen = new Intent(getApplicationContext(), InstellingenActivity.class);
                startActivity(instellingen);
                return true;
            case R.id.menu_info:
                startActivity(new Intent(getApplicationContext(), InfoActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }
}
