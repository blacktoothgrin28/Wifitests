package com.herenow.fase1.Cards;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.herenow.fase1.Cards.Components.MessageAdapter;
import com.herenow.fase1.R;
import com.sinch.android.rtc.messaging.Message;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import it.gmariotti.cardslib.library.internal.Card;
import util.myLog;

import static com.google.android.gms.internal.zzhl.runOnUiThread;

/**
 * Created by Milenko on 02/11/2015.
 */
public class ChatCard extends Card {
    int iAns;
    EditText edt;
    private ListView list;
    private MessageAdapter mMessageAdapter;

    public ChatCard(Context context) {
        super(context);
    }

    public ChatCard(Context context, int innerLayout) {
        super(context, innerLayout);
        iAns = 0;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        super.setupInnerViewElements(parent, view);

        Button btn = (Button) view.findViewById(R.id.btnSend);
        edt = (EditText) view.findViewById(R.id.txtTextBody);
        list = (ListView) view.findViewById(R.id.lstMessages);

        mMessageAdapter = new MessageAdapter((Activity) mContext);
        list.setAdapter(mMessageAdapter);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                myLog.add("Snding own message");
                SendMessage(edt.getText().toString());
                edt.setText("");
            }
        });

    }

    private void SendMessage(String s) {
        try {
            //TODO send mesage
            Message msg = new MessageOut(s, "Yo");
            mMessageAdapter.addMessage(msg, MessageAdapter.DIRECTION_OUTGOING);


            //waits 3 secs and send answer
            final Timer t = new Timer();
            final ArrayList<String> answers = new ArrayList<>();
            answers.add("Give me a second, I'm gonna check...");
            answers.add("Goods news! Somebody returned a wallet to Reception.");

            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (iAns == answers.size() - 1) {
                        this.cancel();
                    }
                    Answering(answers);
                    myLog.add("ians=" + iAns + "|answersize=" + answers.size());
                }
            }, 4000, 4000);

        } catch (Exception e) {
            myLog.add("errer sending chat message");
        }
    }

    private void Answering(ArrayList<String> answers) {
        final String msg = answers.get(iAns);
        myLog.add("answering:" + msg + " " + iAns);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMessageAdapter.addMessage(new MessageOut(msg, "Creapolis"), MessageAdapter.DIRECTION_INCOMING);
                iAns++;
            }
        });

    }

    @Override
    protected void setupInnerLayout() {
        super.setupInnerLayout();
    }

    private class MessageOut implements Message {
        private String textBody, senderId;
        private Date timestamp;

        public MessageOut(String s, String senderId) {
            textBody = s;
            this.senderId = senderId;
            timestamp = new java.util.Date();

        }

        @Override
        public String getMessageId() {
            return "popo";
        }

        @Override
        public Map<String, String> getHeaders() {

            Map<String, String> map = new HashMap<>();
            map.put("ii", "oo");
            return null;
        }

        @Override
        public String getTextBody() {
            return textBody;
        }

        @Override
        public List<String> getRecipientIds() {
            List<String> res = new ArrayList<>();
            res.add("pipi");

            return res;
        }

        @Override
        public String getSenderId() {
            return senderId;
        }

        @Override
        public Date getTimestamp() {
            return timestamp;
        }
    }
}
