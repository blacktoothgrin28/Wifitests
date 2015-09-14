package com.herenow.fase1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.herenow.fase1.CardData.CompanyData;
import com.herenow.fase1.Cards.AirportCard;
import com.herenow.fase1.Cards.CompanyCard;
import com.herenow.fase1.Cards.NewsCard;

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
            //Just un exaple of retrieving news
            String siteUrl = "https://www.google.es/search?q=aviones&tbm=nws ";
//            (new ParseURL()).execute(new String[]{siteUrl});
//////test
//data:image/jpeg;base64,
            String coded = "/9j/4AAQSkZJRgABAQAAAQABAAD/2wCEAAkGBwgHBgkIBwgKCgkLDRYPDQwMDRsUFRAWIB0iIiAdHx8kKDQsJCYxJx8fLT0tMTU3Ojo6Iys/RD84QzQ5OjcBCgoKDQwNGg8PGjclHyU3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3N//AABEIAGQAZAMBIgACEQEDEQH/xAAbAAACAwEBAQAAAAAAAAAAAAAFBgADBAIBB//EADsQAAIBAwMCBAMFBgQHAAAAAAECAwAEEQUSIRMxBkFRYSJxgRQyQpGhByNSscHRFSSC4RYzQ2JyovD/xAAaAQACAwEBAAAAAAAAAAAAAAACAwABBAUG/8QAJhEAAgIBBAIBBAMAAAAAAAAAAAECEQMSITFBBBMUIlGBsTJScf/aAAwDAQACEQMRAD8AtMePKuTH7VvKH0rkx+1egs4xgMZ9K5ZVVGdyFVBkknGBnH9RRAoD5UO1SGVrXUJI5WTpwICB2fdIvB9v7ClZcmiNh4463RyNh24ZTuXcMHuK6Ke1KmoyC2sA67o5PhAMR2EdvMVZa6lcpH8F27DC5DENn5+dJXlrtDng+zGbp1OnQldYuIkkeWKOQID2O00cs2+12sc4QqHGdp5xWjHljPgTOEoclWz2r3p1sENeiH2pti7MfTroR+1bBCPOvejUslmPp1K3dH2qVVks3GH2rkw0SeJVVmYgBRkknGBVNs9tdwia0niniJwJInDKT6ZHFJ1oKjF0KE6tBcSWl/0n2x/ukZcffOc8fLFM7QYU0reJLu6tYLkxCIwm4jQqynOdrGs/kS2Q7AtxJ1u43WrI6/D1ONvft/tVMCpFLI5Gc4Hw+Va7mH7cjIVVfi3fzrhLUCSVgMqXyMDyrIajm9DGORVZgCpO31OR/vRnSPErJbvB/h7lLUYLhuG/OhkrRTZZXO3bgDB9a5vM2unxrLMAQC3bdwT59s8eXpml5M0sX8HuXpjJfUrGi08VaXJkXLtbE8r1FPI9cgcc1mvPF1ucrpsXVPbe/A/Lv/Kkqa7keKdyknkFAwq7QMj3rSs8UIJjgLAbm5woPw/39qv52Vqh3jeJik22r/Q4XGs3dvdBf3TqLeJ2Vhj4mQE8+XJq628TwOq9a1kUnGTGwYf3oPrQ2Xc6sOCixqfkij+lYrOMRsy/FwmMgZp8c+RRuzNPFBvgc31q1VscD/y4NSlCYuX+EkYHOcd/qM17SflZ32D8eA7+FtcNxHcQX1xbmKCMOkksmOc8KT/8aS5xDqdvrdtcyG2vWvlnhjmVikigCMndk5Jz654HqaWNKuZrTW7I6kG2Al2+HcQuDzgd8Yz9KbEvhrOpCNYRNp9qCIJI4SpkkJyNxwDkYOPT9aCUnN29tjoeN48XCr3bCP7HJr+Wy1a2vHP2e2dOnE45jY53Y9BwPrn3q/xhcMiyROi9NrsENnDHCsO3pzSvrurW+jTQyaVK6aqxfqnbtMXJPJ/Fn0PpXuopPrGjWs1oom1Dql5pncbicEMA7eQPOAatzehJgS8WsstLsttpss2yJmBPOPLmukMccbhomJc5AUZwPfBpf0q+ntpGE7SvDIBhl57eYruTVZ0eVI+pxxyMYHvQvJJOhXrQQh6f2b4RK7KuSiIzHGSScAeQq6+WNtJhX4mTIDOVyzDGfKvfCQa6hnlaYRyTQSRqqqQNpypOSccEfyrHBMszSW0CzXUQfaHK7d23OePL0oMqc1v0N9LUU12ZIbKS9lY6U8MccR3MJT3H5Hn+1brrStTeLIhixtZRscdyfmK58SLaaZr0q2ETAOqhVibeCMAnHryTXsupPFgdQHaoLLs5/P8AOqWmtwm54ZUgx4mdxqkgiIaJpDg47jPH8qHveNFOYhGpYjORxWWTVWuo5rlUDLbqjSlhyATgcef0rPbavZyXqySzJGeBuK9vXvTVLozuL5HK0s55LaJ/se7coYEPjuM1K2aZrdxcWUTafBFdRKu0yNn7w8htBHpUpntgtrF+rJ9gdomnRXS6hf6o7Nq15A6gNA6xW0ZGAoYjHHHP0+a2NQudIkjjsv8ALxAFtjSDqO5ABcgduPoPevr97Gi6fci5UCJomEh6pGFwe3FfGJEbVL+/jtCqGFQYwRw3PP5/1qY8fsTbNKzPG0uuzCVGs6/ZIWBa5uEjcgAZywB7fOiviIJpNve6fAm+FW6e6Qk7F74UeWSe/niqdO0931jQTDGBLPcxSbAPugNk/oDWzxrpepyahf3dxb7I4/jljjbO2MsVV+DwCF7fpS5wcZpS2HRyrdrcPaZoF2mjW1zBAxiaKMvxlhJsXfkHkfFkfSs8lpKzSN0oiWPO6NWHbGOafPDFwLLRtOju54I3e3Ry4wN7soZs89yxJz55ohe6ZYajkts6p/GmMmss4SvZiJK+D5lrNo/2OxtokWAJbDCwxbQclmJCjtnNUIi2On+G/sdkXWQ3D3G4nZISRt8/+wkD+9G/E2lnSr+xW4ugbZnll4iLbUWMlv5A4+dY1hP+AWc6ncFTcEY/F90eX+o1qtKEEuXyPw5JSyKMuEY9enKRQWtjcQNc3TpCygbVXcwAAYjt2yfevLfwtdT6xYWN7by2sUwjWeRzwqAZJyDgZBOD60DaUNrtiHIx1hgZHfcMfrT9eM9xYW6mV0uYiYwdoP7vuv5EmlZp+v6S/IlqnbBniHSrDQLyaw0vZNBcFRbyThXAbIBLHjIBPHyqzV/AelaFoMmrQ3Av5xEZRbkrhZAjEN7gEk7fPH0I+96iTRiWVpXCYV+mM7mYKvHbitDQ3Vvo96t1qgmVOnKDKioQFb4sEd/hJ/KrlPTpafItTjkqL6K9P1W6gtI0gmeFccqs20E9icA+1SgsxfqNsBABwcxE5P04qUx4ZtnS+VFbJjz431O507w3dzR6g4kICKNiAEn6Z7Z86F/8L2tpoVtdW+9LqK1BuAzYVxjceO2c5rjUZ4Nb8UQ2rC4lsdOAkmSJWPVmP3VI9BjPPvRnVr+d7CWK3gigaRdu6+kCKAe/Gc1IzcGmujkSjaaYE8MaeJfGXUuTtjsrJekpKnO8AKfP8JNG20P7VrXiNZQ/2e+s4VhcL93hwccdwefrSl4YEtn4hvVtLmLdHbxxNthecv7gL6YHJ45pnm02/v7o3IW7km2BRIYIo0GPLDliPotHmbc3bKglpVBy31LTLS2itCqxBYwvTXtHgdgO5x24zWe48WRWqYji1G4iAyrC3dAfYl9o+uaUrvT9SgO3WPFsNih/6YlOcfIbf5Vla38IxjNzrOp3kn4jBEMN9WH9aVpTCthHxL4jTUpbRWsXQqZEy15D910KtnazbTyDz3xiu7nV11fS7gz29lb3MEH7tLe7WV27DDgLgL59/KhcFxoZkkTSfDc2oN2V7pi2z5qMjHzxW/UL7XLHQLv7Xo+nWltLGVhjihU5c8A4BODTMcamiSlcWhJ0SzvtV1tEtkHWiTrDqIWGB+LHn3GKcrLTdebfLLeB0X7yRwID+Wc0b0DQ76HxHdahqN7FZ3UMcduUhRQJo9qkEF/w/DjjnIPaj+tXugou66ntBKuSWFwqsmPlkn5AGs+dOUiVqQmNbytIrG4LsR3Xb2z2PH86olKtLPZyXYkeSA4heNTkN8P4Rx58niqNV8SX19N09D09b9F4EiwEt+gBP5ChE8XiaDrXsywW007RxMnTBYDOBxzgfFmkxUoSTf4B0jZpem2UVtsm1OC3bdwjqc48vP6fSpQxrDUVSPqN12K8yrFwxyQeF7Yxj6Z86ldmGb6Vqy7/AOIzOMf6fsNQeE7fRY7u7fULlo9ueisrRqcZwWIOWPPmce1fNLx0aSSeZj8RzknJx9acfEujrZaY88l5NPJ2G8jGfrk/rSI7Mykbjg96xx4NT3Y2+C4LaWa41m41tdOEhMaqpQSFQRxls98DsM08u1rNGEt7C91AHgNcyMsZ+fUI4+Smkv8AZ3plq5kunjDypwu8ZC+4Hr719BUsGzk5oJyt2wktgWmlXmc9PS9OQ/htbYSuP9bAD/1NdWvhnSInaWa0+1TMcs9w27cfXHA/SjH/ADhzx59q7jRg3I4obZdHscaJD0URI4sY2IoUUC1ywlWBY4szRGVZdmfu7fiJ9ewx9R60xu0ESbppFUD+I1Ta+IvD9k7TygfvVcTXTqQsaKuQAfc+Xrmri2nZJcUBpJ5fFVxBd6XZw/5VWjjvr2PIQHltkfBbz5JAGTjvV0Pg7T2vUvNQaXULnj76BUGO3wKAPzzSHF4m1VpVtdG6jpH8K7V3O4HAJz/WnjR5dQYxvqmoyyoRh4IzsA+tC40i00NVs0UbdKJkQj8C4X9Kmoafbagg66AN/EBzW22gszAPs0aBSO6jmsrylX6bcn+L0pUooNOwBP4TSR9z/Z5OODImTj51KP8AxfxGpSdKC0o+T/tBkZbWFAeCRSIKlSujHgzvke/2cKGE+Se/an0qBgAVKlLlyEjUiKY9+Of9qA6zqNzaPGsDBSzY3YyRUqUPZbB18WMSzTM0zscZkOR+XamjS9Gs3gSedDO/GBIcgfIdqlSrlwUgokEMSMkUSIuOyqAKAatEkMzNENpJ5xUqUJZs024fYo45wDRHaFXeM5Jx3rypS5BxKmZs53GpUqUAZ//Z";
            Bitmap decodedByte;
            try {
                byte[] decodedString = Base64.decode(coded, Base64.DEFAULT);
                decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                AppendLog.appendLog(decodedByte.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }


            //Company Card

            CompanyData companyData = new CompanyData("Aplicaciones en Informática Avanzada S.L.", R.drawable.im_aia_fondo_claro, R.drawable.im_aia_logo);
            companyData.setEmployeesNumber(55);
            companyData.setLemma("Algoritmos para un mundo mejor");
            companyData.setDirector("Regina Llopis");
            companyData.setFoundationYear("1990");
            companyData.setDescription("El objetivo principal de Grupo AIA es producir un beneficio económico real y cuantificable para nuestros clientes a través de la actividad innovadora usando la tecnología punta como una propuesta de negocio de alto valor.");
            companyData.setFounders(new String[]{"Regina Llopis", "Toni Trias", "Xavier Fustero"});
            companyData.setTypeOfBusiness("Software");
            companyData.setEmail("secretaria@aia.es");
            companyData.setPhone("+34935044900");
            companyData.setWebsite("www.aia.es");

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
//            newsCard.Pon(cardViewNews);
//            cardViewNews.setCard(newsCard);

            // Airport card
            airportCard = new AirportCard(getActivity());
            airportCard.init();

            CardViewNative cardViewAirport = (CardViewNative) getActivity().findViewById(R.id.card_view_airport);
            cardViewAirport.setCard(airportCard);

        } catch (Exception e) {
            AppendLog.appendLog("---error init cards: " + e);
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
