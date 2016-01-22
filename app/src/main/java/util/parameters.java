package util;

import com.herenow.fase1.CardData.ChefData;
import com.herenow.fase1.CardData.CompanyData;
import com.herenow.fase1.CardData.DayFoodMenu;
import com.herenow.fase1.CardData.FoodMenu;
import com.herenow.fase1.CardData.MenuSection;
import com.herenow.fase1.CardData.Schedule;
import com.herenow.fase1.R;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Milenko on 20/07/2015.
 */
public class parameters {
    final public static int defaultThreshold = -100; //For weacon detection
    public static final String pinWeacons = "Weacons";
    public static final String pinSapo = "SAPO2";
    //SAPO
    final static int LogFileSize = 100; //in kilobytes, after that, new is started
    public static Integer hitRepetitions = 20; //number of HitSapo in SAPO for considering a SSID important
    public static int minTimeForUpdates = 60 * 1; //in mins
    //WIFI
    public static int nHitsForLogIn = 3; //recommend 3. Number of hits for declaring the login in a spot
    public static long timeBetweenFlightQueries = (long) (2 * 60 * 1000);//in milliseconds, to verify if there are changes in gates, ets
    public static double radioSpotsQuery = 50; //Determines how many spots to load, (kilometers around user's position
    public static boolean isSapoActive = false;
    public static int spaceBetweenCards = 30; //in pixels

    //Default values to repetition of weacon detection
    public static int repeatedOffRemoveFromNotification = 5;
    public static int repeatedOffToDisappear = 10;
    public static int repeatedOffToChatOff = 4;
    public static int repeatedOnToChatOn = 3;


    public enum CardType {COMPANY, SCHEDULE, LINKEDIN, NEWS, FLIGHTS}

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

