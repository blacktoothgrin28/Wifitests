package com.herenow.fase1.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.herenow.fase1.Cards.AirportCard;
import com.herenow.fase1.R;

import util.myLog;

public class CardsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);
//        overridePendingTransition(R.transition.trans_left_in, R.transition.trans_left_out);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            Bundle b = getIntent().getExtras();
//            Bitmap wLogo = (Bitmap) b.get("wLogo");
//            String wUrl = b.getString("wUrl");
            String wName = b.getString("wName");
            String wCompanyDataObId = b.getString("wComapanyDataObId");
            double wLat = b.getDouble("wLatitude");
            double wLon = b.getDouble("wLongitude");

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
                myLog.add("no hemos recibido en la card activiy el typo de carta:"+e.getLocalizedMessage());
            }

            //Pass some data to the fragment:
            CardsActivityFragment rf = (CardsActivityFragment) getSupportFragmentManager().findFragmentById(R.id.cards_fragment);
            if (rf != null) {
                rf.setCardsType(wCards);
                if (wCompanyDataObId != null) rf.setCardData(wCompanyDataObId, mTypeAirportCard);
                if (wLat != 0.0d) rf.launchBusCard(wLat, wLon);
            }
        } catch (Exception e) {
            myLog.add("elloooo" + e.getLocalizedMessage());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            // Make sure the request was successful
            myLog.add("-----on activity rsult");
            if (resultCode == RESULT_OK) {
                myLog.add("-----on activity rsult:ok");
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                // Do something with the contact here (bigger example below)
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.transition.trans_right_in, R.transition.trans_right_out);
    }

}
