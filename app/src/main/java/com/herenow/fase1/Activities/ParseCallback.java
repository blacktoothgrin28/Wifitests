package com.herenow.fase1.Activities;

import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Milenko on 26/09/2015.
 */
public interface  ParseCallback {
     void DatafromParseReceived(List<ParseObject> datos);
     void OnError(Exception e);
}
