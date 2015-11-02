package util;

import com.herenow.fase1.CardData.ChefData;
import com.herenow.fase1.CardData.CompanyData;
import com.herenow.fase1.CardData.DayFoodMenu;
import com.herenow.fase1.CardData.FoodMenu;
import com.herenow.fase1.CardData.JobData;
import com.herenow.fase1.CardData.MenuSection;
import com.herenow.fase1.CardData.ProductItem;
import com.herenow.fase1.CardData.ProductItemBuilder;
import com.herenow.fase1.CardData.ProductsData;
import com.herenow.fase1.CardData.Schedule;
import com.herenow.fase1.R;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by halatm on 22/10/2015.
 */
public class dataExamples {
    public static CompanyData getExampleCompanyCard() {
        CompanyData companyData = new CompanyData("Aplicaciones en Informática Avanzada S.L.", R.drawable.im_aia_fondo_claro, R.drawable.im_aia_logom);
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

    public static FoodMenu getSampleFoodMenu() {
        FoodMenu foodMenu = new FoodMenu("1st November 2015");
        foodMenu.addSection(getSecAperitiv());
        foodMenu.addSection(getSecSalad());
        foodMenu.addSection(getSecMar());
        foodMenu.addSection(getSecGluten());

        return foodMenu;
    }

    public static DayFoodMenu getSampleDayFoodMenu() {
        DayFoodMenu dayMenu = new DayFoodMenu("3th November, until 15:30", 10.5);
        dayMenu.addSection(getSecAperitiv());
        dayMenu.addSection(getSecSalad());
        dayMenu.addSection(getSecMar());
        return dayMenu;
    }

    private static MenuSection getSecAperitiv() {
        return new MenuSection("Aperitivos")
                .addFoodItem("Paniza", "Pan de coca con tomate, sal y aceite de oliva virgen", new String[]{"Pan",
                        "tomate", "sal", "Aceite"}, 6)
                .addFoodItem("Delicia de Rovira", "Sobrasada de Can Rovira con miel y pan de coca con tomate", new String[]{"Pan",
                        "miel", "tomate"}, 9)
                .addFoodItem("Andrés Calamaro", "Calamares de potera enharinados", new String[]{"Pan",
                        "miel", "tomate"}, 4.5)
                .addFoodItem("Buñuelos de bacalao ", "Precio por unidad", new String[]{"Bacalao"
                }, 3);
    }

    private static MenuSection getSecSalad() {
        return new MenuSection("Ensaladas")
                .addFoodItem("Oye cerdo", "Ensalada de escarola, oreja de cerdo crujiente y vinagreta de piñones", new String[]{
                        "oreja", "piñones", "Aceite"}, 12)
                .addFoodItem("Escalera al cielo", "Láminas de bacalao con piparras, aceitunas y anchoas de La Escala", new String[]{
                        "Escalones", "olivas"}, 19)
                .addFoodItem("Espárragos blancos", "Espárragos blancos gratinados con almendras", new String[]{"Espárragos",
                        "almendras"}, 19)
                .addFoodItem("Verduras", "Verduras escalibadas con anchoas y romesco ", new String[]{"Anchoas", "romesco"
                }, 3);
    }

    private static MenuSection getSecMar() {
        return new MenuSection("Del Mar")
                .addFoodItem("Pulpo suave", "Pulpo a la brasa con cremoso de patata y pimentón ", new String[]{
                        "pulpo", "patatas", "pimentón"}, 19)
                .addFoodItem("Rape nomás", "Rape al horno de leña con patatas, jamón ibérico y refrito de ajos ", new String[]{
                        "Rape", "patatas", "jamón ibérico"}, 24)
                .addFoodItem("Sepias matizadas", "Sepietas de la Barceloneta con sofrito de cebolla y tomate ", new String[]{
                        "Sepia", "tomate", "cebolla"}, 22);
    }

    private static MenuSection getSecGluten() {
        return new MenuSection("Gluten Free")
                .addFoodItem("Buñuelos de bacalao ", "Precio por unidad", new String[]{"Bacalao"
                }, 3)
                .addFoodItem("Rape nomás", "Rape al horno de leña con patatas, jamón ibérico y refrito de ajos ", new String[]{
                        "Rape", "patatas", "jamón ibérico"}, 24)
                .addFoodItem("Delicia de Rovira", "Sobrasada de Can Rovira con miel y pan de coca con tomate", new String[]{"Pan",
                        "miel", "tomate"}, 9);
    }

    public static Schedule getExampleScheduleData() {
        Schedule ex = new Schedule("Interesting Conference");
        ex.subtitle = "Once a year, the most wise people met";
        Calendar cal = new GregorianCalendar();
        cal.set(2015, 11, 3);
        ex.setDate(cal);
//        ex.setEndOfMeeting(date in milli);
        //todo posibilidad de ver de días siguiente
        //todo add all schedule to calendar
        ex.addItem("Welcome", "", 9, 15, "Reception", "http://www.aia.es");
        ex.addItem("Challenges in near horizon", "Moe Szyslak", 9, 30, "Room A", "http://www.aia.es");
        ex.addItem("Beyond the Musgo", "José Mota", 10, 30, "Room A", "http://www.aia.es");
        ex.addItem("Power of Imagination", "Sponge Bob", 11, 30, "Creativity room",
                "http://www.aia.es", "http://www.robotictechnologyinc.com/images/upload/file/Presentation%20Technology%20Innovation.pdf");
        ex.addItem("Coffee break dance", "", 12, 15, "Reception", "http://www.aia.es");
        ex.addItem("Riding over your problems", "Little Pony", 12, 45, "Room B", "http://www.aia.es");
        ex.addItem("LUNCH", "", 12, 45, "Reception", "http://www.aia.es");
        ex.addItem("When Elsa met the Starks", "Princess Anna", 14, 0, "Refrigerator room", "http://www.aia.es");
        ex.addItem("Kill your problems gently", "Dexter", 15, 0, "Basement", "http://www.aia.es");
        ex.addItem("Aaarrggh Eaarmmm", "Zombie B", 16, 0, "Kitchen", "http://www.aia.es");
        ex.addItem("Next amazin events", "The Master", 17, 0, "Plenary Room", "http://www.aia.es");

        return ex;
    }

    public static ProductsData getExampleProducts() {
        ProductsData productsData = new ProductsData("AIA");
        ProductItem it = new ProductItemBuilder()
                .setName("HELM-Flow")
                .setDesription("HELM-Flow is a simulation and analysis tool for transmission and distribution that delivers fast, accurate workflow")
                .setKeyWords("Software, Power Systems, Simulation.")
                .setImageUrl("http://gridquant.com/assets/HELM_Flow-300x180.jpg")
                .createProductItem();

        ProductItem it2 = new ProductItemBuilder()
                .setName("iTesla")
                .setDesription("The iTesla project aims at improving network operations with a new security assessment tool able to cope with increasingly uncertain operating conditions and take advantage of the growing flexibility of the grid. ")
                .setKeyWords("EU project, FP7, Power Grids.")
                .setImageUrl("http://www.energyville.be/sites/default/files/styles/2mpact_detail/public/itesla_1.jpg")
                .createProductItem();

        ProductItem it3 = new ProductItemBuilder()
                .setName("Big Data Banking")
                .setDesription("The value of Big Data to the retail banking industry is estimated at more than £6 billion over the next five years. Immediate cost-reduction opportunities lie in fraud and sanctions management, while account management can be enhanced by enhanced customer insight.")
                .setKeyWords("Consultancy, Big Data, Banking.")
                .setImageUrl("http://41e67ca818dba1c3d3c5-369a671ebb934b49b239e372822005c5.r33.cf1.rackcdn.com/big-data-analytics-context-aware-security-landingPageImage-7-w-419.jpg")
                .createProductItem();


        productsData.addProduct(it);
        productsData.addProduct(it2);
        productsData.addProduct(it3);

        return productsData;
    }

    public static JobData getExampleJobOffers() {
        JobData jd = new JobData("Job Opportunities", "23th October 2015", "AIA");

        JobData.JobOffer j1 = new JobData.JobOfferBuilder().setTitle("Senior Data Scientist ")
                .setDescription("Your role will be extract value to data through innovative Big Data/Data Science approaches in order to support our business processes and decision making.")
                .setLanguages(new String[]{"English", "Spanish"})
                .setSkills(new String[]{"Statistics", "Big Data", "R", "H2O"})
                .setUrl("https://www.linkedin.com/jobs2/view/78904182?trk=biz-overview-job-post")
                .createJobOffer();

        JobData.JobOffer j2 = new JobData.JobOfferBuilder().setTitle("Java Developer")
                .setDescription("Analista Programador en JAVA/J2EE Oracle-Pl/Sql para trabajar en proyectos innovadores para clientes de referencia en el mercado.")
                .setLanguages(new String[]{"Spanish", "Catalan"})
                .setSkills(new String[]{"Java", "xml", "Html", "Javascript", "Pl/Sql"})
                .setUrl("https://www.linkedin.com/jobs2/view/71821162?trk=biz-overview-job-post")
                .createJobOffer();

        jd.add(j1);
        jd.add(j2);

        return jd;
    }

    public static ChefData getExampleChef() {
        ChefData chef = new ChefData("Gnocchi Vermell", "http://d3rsl50p8hwbdu.cloudfront.net/square_374_1541.jpg", 22.);
        chef.setDescription("This braised rabbit is a refreshing take on a classic Italian cacciatore dish. " +
                "The addition of chanterelle mushrooms and green olives lends brighter notes to the dish.");
        return chef;
    }
}
