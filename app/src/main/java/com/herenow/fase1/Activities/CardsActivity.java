package com.herenow.fase1.Activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.herenow.fase1.CardData.CompanyData;
import com.herenow.fase1.Cards.AirportCard;
import com.herenow.fase1.R;
import com.parse.ParseObject;

import java.util.List;

import parse.ParseActions;
import util.myLog;

public class CardsActivity extends ActionBarActivity {

    private Bitmap wLogo;
    private String wName;
    private String wUrl;
    private String wCompanyDataObId;
    private CompanyData mCompanyData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);//Estaba al principio...

        try {
            Bundle b = getIntent().getExtras();
            wName = b.getString("wName");
            wUrl = b.getString("wUrl");
            wLogo = (Bitmap) b.get("wLogo");
            wCompanyDataObId = b.getString("wComapanyDataObId");

            setTitle(wName);

            String depOrArr = "";
            AirportCard.TypeOfCard mTypeAirportCard = AirportCard.TypeOfCard.departure;
            try {

                depOrArr = b.getString("typeOfAiportCard");
                myLog.add("CARDACTIVITY hemos recibido el typo de carta " + depOrArr);
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
                rf.setCardData(wCompanyDataObId, mTypeAirportCard);
            }


//            // Pass the  companydata ObId to fragment:
//            Bundle bundle = new Bundle();
//            bundle.putString("cardObId", wCompanyDataObId);
//            CardsActivityFragment fragInfo = new CardsActivityFragment();
//            fragInfo.setArguments(bundle);
//            FragmentTransaction transaction = null;
//            transaction.replace(R.id.cards_fragment, fragInfo);
//            transaction.commit();
        } catch (Exception e) {
            myLog.addError(this.getClass(), e);
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

        return super.onOptionsItemSelected(item);
    }
}
