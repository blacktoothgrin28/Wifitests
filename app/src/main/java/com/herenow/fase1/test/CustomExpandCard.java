/*
 * ******************************************************************************
 *   Copyright (c) 2013-2014 Gabriele Mariotti.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *  *****************************************************************************
 */

package com.herenow.fase1.test;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.herenow.fase1.R;

import it.gmariotti.cardslib.library.internal.CardExpand;


public class CustomExpandCard extends CardExpand {

    int count;
    private String lemma;
    private String bullets;
    private String description;

    public CustomExpandCard(Context context) {
        super(context, R.layout.company_expand);
    }

    public CustomExpandCard(Context context, int i) {
        super(context, R.layout.company_expand);
        count = i;
    }

    //You can set you properties here (example buttons visibility)

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        if (view == null) return;

        //Retrieve TextView elements
        TextView txLemma = (TextView) view.findViewById(R.id.company_expand_lemma);
        TextView txBullets = (TextView) view.findViewById(R.id.company_expand_bullets);
        TextView txDescription = (TextView) view.findViewById(R.id.company_expand_description);
//        TextView tx4 = (TextView) view.findViewById(R.id.carddemo_expand_text4);

        //Set value in text views
        if (txLemma != null) {
                txLemma.setText(lemma);
        }

        if (txBullets != null) {
            txBullets.setText(bullets);
        }
        if (txDescription != null) {
            txDescription.setText(description);
        }
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public void setBullets(String bullets) {
        this.bullets = bullets;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
