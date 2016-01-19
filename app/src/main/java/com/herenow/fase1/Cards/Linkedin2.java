package com.herenow.fase1.Cards;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.herenow.fase1.R;
import com.squareup.picasso.Picasso;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.prototypes.CardWithList;
import it.gmariotti.cardslib.library.prototypes.LinearListView;
import util.myLog;

/**
 * Created by Milenko on 03/12/2015.
 */
public class Linkedin2 extends FetchingCard {
    public Linkedin2(Context context, String url, String title, @LayoutRes int innerLayout) {
        super(context, url, title, innerLayout);
    }

    @Override
    protected ArrayList<DefaultListObject> ExtraccionObjetos(String url) {
        ArrayList<DefaultListObject> employees = new ArrayList();
        try {
            Connection.Response response = Jsoup.connect(url)
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0 (Windows NT 6.3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.85 Safari/537.36")
                    .referrer("http://www.google.com")
                    .timeout(12000)
                    .followRedirects(true)
                    .execute();

            Document doc = response.parse();
//                Document doc = Jsoup.connect(strings[0]).get();
            Elements employeesRaw = doc.select("div [class=company-employees module]").first().select("ol").first().select("li");

            for (Element employee : employeesRaw) {
                employees.add(ProcessHtmlEmployee(employee));
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return employees;
    }

    @Override
    public View setupChildView(int childPosition, ListObject object, View convertView, ViewGroup parent) {

        try {
            TextView title = (TextView) convertView.findViewById(R.id.name);
            TextView content = (TextView) convertView.findViewById(R.id.position);

            ImageView ivImage = (ImageView) convertView.findViewById(R.id.news_image);

            //Retrieve the values from the object
            Employee emp = (Employee) object;
            title.setText(emp.name + " " + emp.lastName);
            content.setText(emp.position);

            if (emp.urlImage != null && !emp.urlImage.equals("")) {
                Picasso.with(mContext).load(emp.urlImage)//Todo si es que tiene url
                        .error(R.drawable.abc_ic_ab_back_mtrl_am_alpha)
                        .placeholder(R.mipmap.ic_launcher)
                        .into(ivImage);
            }

        } catch (Exception e) {
            myLog.add("errror item: " + e.getMessage());
        }

        return convertView;
    }

    private Employee ProcessHtmlEmployee(Element element) {
        Employee emp = new Employee(mParentCard);
        Element dt;
        try {
            dt = element.select("dt").first();
            emp.name = dt.select("span[class=given-name]").first().text();
            emp.lastName = dt.select("span[class=family-name]").first().text();
            emp.url = dt.select("a").attr("href");
            if (element.select("dd").size() > 0)
                emp.position = element.select("dd").first().text();
            emp.setObjectId(emp.url);
            emp.urlImage = element.select("img").attr("data-li-lazy-load-src");

        } catch (Exception e) {
            myLog.add("errror capturanfo employee " + e.getMessage());
        }
        return emp;
    }

    @Override
    public void OnError(Exception e) {

    }

    class Employee extends DefaultListObject {
        public String position = "";
        public String url;
        String name, lastName, urlImage;
        Bitmap picture;

        public Employee(Card parentCard) {
            super(parentCard);
            init();
        }

        private void init() {
            //OnClick Listener
            setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(LinearListView parent, View view, int position, ListObject object) {
                    Toast.makeText(getContext(), "Click on " + getObjectId(), Toast.LENGTH_SHORT).show();

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getObjectId()));
                    mContext.startActivity(browserIntent);
                }
            });

            //OnItemSwipeListener
            setOnItemSwipeListener(new OnItemSwipeListener() {
                @Override
                public void onItemSwipe(ListObject object, boolean dismissRight) {
                    Toast.makeText(getContext(), "Swipe on " + object.getObjectId(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
