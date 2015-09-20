package util;

import com.herenow.fase1.CardData.CompanyData;
import com.herenow.fase1.CardData.Schedule;
import com.herenow.fase1.R;

/**
 * Created by Milenko on 20/07/2015.
 */
public class parameters {
    final public static int defaultThreshold = -100; //For weacon detection
    public static final String pinWeacons = "Weacons";
    public static final String pinSapo = "SAPO2";
    final static int LogFileSize = 100; //in kilobytes, after that, new is started
    public static Integer hitRepetitions = 20; //number of HitSapo in SAPO for considering a SSID important
    public static int minTimeForUpdates = 60 * 1; //in mins
    public static int nHitsForLogIn = 3; //recommend 3. Number of hits for declaring the login in a spot


    public static CompanyData getExampleCompanyCard() {
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
        companyData.setLinkedinUrl("https://es.linkedin.com/company/aplicaciones-en-inform-tica-avanzada");

        return companyData;
    }

    public static Schedule getExampleScheduleData() {
        Schedule ex = new Schedule("Conference Sample");
        ex.addItem("Welcome", "", 9, 15, "Reception", "http://www.aia.es");
        ex.addItem("Challenges in near horizon", "Moe Szyslak", 9, 30, "Room A", "http://www.aia.es");
        ex.addItem("Beyond the Musgo", "José Mota", 10, 30, "Room A", "http://www.aia.es");
        ex.addItem("Power of Imagination", "Sponge Bob", 11, 30, "Creativity room", "http://www.aia.es");
        ex.addItem("Coffee break dance", "", 12, 15, "Reception", "http://www.aia.es");
        ex.addItem("Riding over your problems", "Little Pony", 12, 45, "Room B", "http://www.aia.es");
        ex.addItem("LUNCH", "", 12, 45, "Reception", "http://www.aia.es");
        ex.addItem("When Elsa met the Starks", "Princess Anna", 14, 0, "Refrigerator room", "http://www.aia.es");
        ex.addItem("Kill your problems gently", "Dexter", 15, 0, "Basement", "http://www.aia.es");
        ex.addItem("Aaarrggh Eaarmmm", "Zombie B", 16, 0, "Kitchen", "http://www.aia.es");
        ex.addItem("Next stupid events", "The Master", 17, 0, "Plenaty Room", "http://www.aia.es");

        return ex;
    }


    public enum typeOfWeacon {
        accounting, airport, amusement_park, aquarium, art_gallery, atm, bakery,
        bank, bar, beauty_salon, bicycle_store, book_store, bowling_alley, bus_station, cafe, campground,
        car_dealer, car_rental, car_repair, car_wash, casino, cemetery, church, city_hall, clothing_store,
        convenience_store, courthouse, dentist, department_store, doctor, electrician, electronics_store,
        embassy, establishment, finance, fire_station, florist, food, funeral_home, furniture_store,
        gas_station, general_contractor, grocery_or_supermarket, gym, hair_care, hardware_store, health,
        hindu_temple, home_goods_store, hospital, insurance_agency, jewelry_store, laundry, lawyer, library,
        liquor_store, local_government_office, locksmith, lodging, meal_delivery, meal_takeaway, mosque,
        movie_rental, movie_theater, moving_company, museum, night_club, painter, park, parking, pet_store,
        pharmacy, physiotherapist, place_of_worship, plumber, police, post_office, real_estate_agency, restaurant,
        roofing_contractor, rv_park, school, shoe_store, shopping_mall, spa, stadium, storage, store, subway_station,
        synagogue, taxi_stand, train_station, travel_agency, university, veterinary_care, zoo
    }
}

