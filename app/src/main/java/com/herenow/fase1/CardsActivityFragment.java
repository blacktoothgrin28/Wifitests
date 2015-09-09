package com.herenow.fase1;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.herenow.fase1.CardData.CompanyData;
import com.herenow.fase1.Cards.AirportCard;
import com.herenow.fase1.Cards.CompanyCard;

import it.gmariotti.cardslib.library.view.CardViewNative;
import util.AppendLog;

//import com.herenow.fase1.Cards.MaterialLargeImageCard;

/**
 * A placeholder fragment containing a simple view.
 */
public class CardsActivityFragment extends Fragment {
    AirportCard airportCard;
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

            CompanyData companyData = new CompanyData("Aplicaciones en Informática Avanzada", R.drawable.im_aia_fondo_claro, R.drawable.im_aia_logo);
            companyData.setEmployeesNumber(55);
            companyData.setDescription("");
            companyData.setLemma("Algoritmos para un mundo mejor");
            companyData.setDirector("Regina Llopis");
            companyData.setFoundationYear("1990");
            companyData.setDescription("El objetivo principal de Grupo AIA es producir un beneficio económico real y cuantificable para nuestros clientes a través de la actividad innovadora usando la tecnología punta como una propuesta de negocio de alto valor.");
            companyData.setFounders(new String[]{"Regina Llopis", "Toni Trias", "Xavier Fustero"});
            companyData.setTypeOfBusiness("Software");
            companyData.setEmail("secretaria@aia.es");

            CompanyCard companyCardtest = CompanyCard.with(getActivity())
                    .setTitle("paropo")
                    .setData(companyData)
                    .build();

            CardViewNative cardViewCompany = (CardViewNative) getActivity().findViewById(R.id.carddemo_largeimage_test);
            cardViewCompany.setCard(companyCardtest);

            // Airport card
            airportCard = new AirportCard(getActivity());
            airportCard.init();

            CardViewNative cardViewAirport = (CardViewNative) getActivity().findViewById(R.id.carddemo_weathercard);
            cardViewAirport.setCard(airportCard);

        } catch (Exception e) {
            AppendLog.appendLog("---error init airportCard: " + e);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (airportCard != null)
            airportCard.unregisterDataSetObserver();//TODO, VER CoMO VA ESTE ROLLO DEL REGISTRO DE OBSERVER
    }
}
