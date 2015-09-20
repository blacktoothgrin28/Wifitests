package com.herenow.fase1;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.herenow.fase1.CardData.CompanyData;
import com.herenow.fase1.Cards.AirportCard;
import com.herenow.fase1.Cards.CompanyCard;
import com.herenow.fase1.Cards.LinkedinCard;
import com.herenow.fase1.Cards.NewsCard;
import com.herenow.fase1.Cards.ScheduleCard;

import it.gmariotti.cardslib.library.view.CardViewNative;
import util.AppendLog;
import util.parameters;

//import com.herenow.fase1.Cards.MaterialLargeImageCard;

/**
 * A placeholder fragment containing a simple view.
 */
public class CardsActivityFragment extends Fragment {
    AirportCard airportCard;
    ScheduleCard scheduleCard;
//    CompanyCard card2;

    public CardsActivityFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initCards();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.demo_fragment_cardwithlist_card, container, false);
    }

    private void initCards() {

        try {
            //Company Card
            CompanyData companyData = parameters.getExampleCompanyCard();
            CompanyCard companyCardtest = CompanyCard.with(getActivity())
                    .setData(companyData)
                    .build();

            CardViewNative cardViewCompany = (CardViewNative) getActivity().findViewById(R.id.card_view_company);
            cardViewCompany.setCard(companyCardtest);

            // News Card
            NewsCard newsCard = new NewsCard(getActivity(), companyData.getNameClean());
            CardViewNative cardViewNews = (CardViewNative) getActivity().findViewById(R.id.card_view_news);
            newsCard.setView(cardViewNews);
            newsCard.init();


            //Linkedin card
            // todo change format of linkedin card
            LinkedinCard linkedinCard = new LinkedinCard(getActivity(), companyData.getLinkedinUrl());
            CardViewNative cvLinkedin = (CardViewNative) getActivity().findViewById(R.id.card_view_linkedin);
            linkedinCard.setView(cvLinkedin);
            linkedinCard.init();


            //Schedule card
            scheduleCard = new ScheduleCard(getActivity());
            scheduleCard.setData(parameters.getExampleScheduleData());//it has 11 items
            scheduleCard.init();

            CardViewNative cardViewSchedule = (CardViewNative) getActivity().findViewById(R.id.card_view_schedule);
            cardViewSchedule.setCard(scheduleCard);


            // Airport card
            airportCard = new AirportCard(getActivity());
            airportCard.init();

            CardViewNative cardViewAirport = (CardViewNative) getActivity().findViewById(R.id.card_view_airport);
            cardViewAirport.setCard(airportCard);

        } catch (Exception e) {
            AppendLog.appendLog("---error init cards: " + e.getMessage());
        }

    }

    @Override
    public void onPause() {

        try {
            super.onPause();
            if (airportCard != null)
                airportCard.unregisterDataSetObserver();//TODO, VER CoMO VA ESTE ROLLO DEL REGISTRO DE OBSERVER
        } catch (Exception e) {
            AppendLog.appendLog("--error on Pause cards" + e.getMessage());
        }
    }
}
