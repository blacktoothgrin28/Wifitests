package com.herenow.fase1.Activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.herenow.fase1.Cards.ChatCard;
import com.herenow.fase1.Cards.ScheduleCard;
import com.herenow.fase1.Cards.ScheduleCardTest;
import com.herenow.fase1.R;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.prototypes.LinearListView;
import it.gmariotti.cardslib.library.view.CardViewNative;
import util.dataExamples;
import util.myLog;

public class TestCardsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_cards);

        addCards();
    }

    private void addCards() {
        CardViewNative cv1 = (CardViewNative) findViewById(R.id.test_card1);
        CardViewNative cv2 = (CardViewNative) findViewById(R.id.test_card2);
//        CardViewNative cv3=(CardViewNative) findViewById(R.id.test_card3);
//
//        try {
//            LinearListView llv = (LinearListView) findViewById(R.id.card_inner_base_main_cardwithlist);
//            llv.setOrientation(LinearLayout.HORIZONTAL);
//        } catch (Exception e) {
//            myLog.add("problema horizontalizatnfo" + e.getLocalizedMessage());
//        }

        ChatCard chatCard = new ChatCard(this, R.layout.chat_card);

        CardHeader cardHeader = new CardHeader(this);
//        cardHeader.setTitle("Chat with Creapolis");
//        cardHeader.setButtonOverflowVisible(false);
        chatCard.addCardHeader(cardHeader);
//chatCard.setTitle("PUPA");
        cv1.setCard(chatCard);
//
//        ScheduleCardTest card2 = new ScheduleCardTest(this, R.layout.inner_base_main_cardwithlist_horizontal);
//        card2.setData(dataExamples.getExampleScheduleData());
//        card2.init();
//
//        cv2.setCard(card2);
//
//        ScheduleCardTest card1 = new ScheduleCardTest(this);
//        card1.setData(dataExamples.getExampleScheduleData());
//        card1.init();
//
//        cv1.setCard(card1);


    }


}
