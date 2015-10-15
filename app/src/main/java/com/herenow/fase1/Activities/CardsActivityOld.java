package com.herenow.fase1.Activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.herenow.fase1.CardData.CompanyData;
import com.herenow.fase1.Cards.AirportCard;
import com.herenow.fase1.R;

import util.myLog;

public class CardsActivityOld extends ActionBarActivity {

    private CompanyData mCompanyData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        try {
            Bundle b = getIntent().getExtras();
//            Bitmap wLogo = (Bitmap) b.get("wLogo");
//            String wUrl = b.getString("wUrl");
            String wName = b.getString("wName");
            String wCompanyDataObId = b.getString("wComapanyDataObId");
            String[] wCards = b.getStringArray("wCards");

            setTitle(wName);

            String depOrArr = "";
            AirportCard.TypeOfCard mTypeAirportCard = AirportCard.TypeOfCard.departure;
            try {

                depOrArr = b.getString("typeOfAiportCard");
                if (depOrArr.equals("Arrivals")) {
                    mTypeAirportCard = AirportCard.TypeOfCard.arrival;
                } else {
                    mTypeAirportCard = AirportCard.TypeOfCard.departure;
                }
            } catch (Exception e) {
                myLog.add("no hemos recibido en la card activiy el typo de carta");
            }

            //Pass some data to the fragment:
            CardsActivityFragment rf = (CardsActivityFragment) getSupportFragmentManager().findFragmentById(R.id.cards_fragment);
            if (rf != null) {
                rf.setCardsType(wCards);
                rf.setCardData(wCompanyDataObId, mTypeAirportCard);
            }

        } catch (Exception e) {
            myLog.add("elloooo" + e.getLocalizedMessage());
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test_cards, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.browserMenu) {
            return true;
        }
        if (id == android.R.id.home) {
            finish();
            overridePendingTransition(R.transition.trans_right_in, R.transition.trans_right_out);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.transition.trans_right_in, R.transition.trans_right_out);
    }
}
