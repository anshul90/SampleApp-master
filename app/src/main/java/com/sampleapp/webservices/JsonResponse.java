package com.sampleapp.webservices;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.sampleapp.model.WorldPojo;
import com.sampleapp.sqlite.Database;
import com.sampleapp.utils.CallbackListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class JsonResponse extends AsyncTask<Void, Void, String> {
    CallbackListener callbackListener;
    Database dataBase;
    Context con;

    public JsonResponse(Context con) {
        this.con = con;
        this.dataBase = new Database(con);
    }

    @Override
    protected String doInBackground(Void... params) {
        return getString();
    }

    private String getString() {
        // TODO Auto-generated method stub
        URL obj = null;
        HttpURLConnection con = null;
        try {
            obj = new URL("http://www.androidbegin.com/tutorial/jsonparsetutorial.txt");
            con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { //success
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                Log.i("TAG", response.toString());
                return response.toString();


            } else {
                Log.i("TAG", "POST request did not work.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);

        ArrayList<WorldPojo> catListDao;
        if (result != null) {
            JSONObject jsonObject;
            try {
                catListDao = new ArrayList<WorldPojo>();
                jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("worldpopulation");

                if (jsonArray.length() > 0) {
                    if (dataBase.numberOfRows() > 0)
                        dataBase.deleteData();
                    for (int ii = 0; ii < jsonArray.length(); ii++) {
                        WorldPojo dataObject = new WorldPojo();
                        JSONObject jsonObject11 = jsonArray.getJSONObject(ii);
                        dataObject.setFlag_image(jsonObject11.getString("flag"));
                        dataObject.setRank(jsonObject11.getString("rank"));
                        dataObject.setCountry(jsonObject11.getString("country"));
                        dataObject.setPopulation(jsonObject11.getString("population"));
                        catListDao.add(dataObject);
                        boolean issuccess = dataBase.addData(jsonObject11.getString("rank"), jsonObject11.getString("population")
                                , jsonObject11.getString("country"), jsonObject11.getString("flag"));
                    }
                    callbackListener.CatList(catListDao);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    public void setListener(CallbackListener callbackResponse) {
        this.callbackListener = callbackResponse;
    }
}