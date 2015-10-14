package com.herenow.fase1;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.herenow.fase1.Activities.CardsActivityFragment;
import com.herenow.fase1.Cards.AirportCard;

import util.myLog;

public class CardsActivityAer extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_cards_activity_aer);
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
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.transition.trans_right_in, R.transition.trans_right_out);
    }
}
