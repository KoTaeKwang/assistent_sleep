package com.example.secmem_dy.sleep_assistant;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by SECMEM-DY on 2016-07-27.
 */
public class ShowDataActivity extends Activity {
    private int[] mColors = new int[]{
            ColorTemplate.VORDIPLOM_COLORS[0],
            ColorTemplate.VORDIPLOM_COLORS[1],
            ColorTemplate.VORDIPLOM_COLORS[2]
    };
    private AsyncHttpClient client;
    private String id;
    private LineChart mChart;

    private ArrayList<String> xData;
    private ArrayList<Double> yData;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visualize);
        Intent intent = getIntent();
        id = intent.getStringExtra("ID");
        client = HttpClient.getinstance();
        xData = new ArrayList<>();
        yData = new ArrayList<>();

        sendRequest();
        //create line chart
        mChart = (LineChart) findViewById(R.id.chart);
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<100;i++){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addEntry();
                        }
                    });
                    try {
                        Thread.sleep(600);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();*/
    }

    public void sendRequest() {
        StringEntity entity = null;
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put(HttpClient.JSON_ID, id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        entity = HttpClient.makeStringEntity(jsonParams);

        client.post(getApplicationContext(), HttpClient.getAbsoulteUrl(HttpClient.SHOW_VISUAL_DATA_URL), entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String ackData = null;
                Log.e("FROM_SERVER", "success");
                try {
                    ackData = new String(responseBody, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (ackData != null && ackData.equals(HttpClient.ACK_FAIL)) {//Login Success
                    Toast.makeText(getApplicationContext(), "이미지 URL 불러오기 실패", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("FROM_SERVER", ackData);
                    JSONArray jarray = null;
                    try {
                        jarray = new JSONArray(ackData);
                        for (int i = 0; i < jarray.length(); i++) {
                            JSONObject jObject = jarray.getJSONObject(i);
                            Log.e("FROM_SERVER", "x:" + jObject.getString("x") + "y" + jObject.getInt("y"));
                            xData.add(i,jObject.getString("x"));
                            yData.add(i, jObject.getDouble("y"));
                            setGrapth();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("FROM_SERVER", "fail");
                Toast.makeText(getApplicationContext(), "이미지 불러오기 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setGrapth() {
        //customize line chart
        mChart.setDescription("");
        mChart.setNoDataTextDescription("No data for the momet");

        //enable alue highlighting
        mChart.setHighlightPerDragEnabled(true);
        mChart.setHighlightPerTapEnabled(true);

        mChart.setTouchEnabled(true);

        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);

        //enable pinch zoom to to avoid scaling x and y axis separately
        mChart.setPinchZoom(true);

        //set bg color
        mChart.setBackgroundColor(Color.LTGRAY);

        //work on data
        /*LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        //add data to line chart
        mChart.setData(data);

        //get legent object;
        Legend l = mChart.getLegend();

        //customiaze legent
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        XAxis x1=mChart.getXAxis();
        x1.setTextColor(Color.WHITE);
        x1.setDrawGridLines(false);
        x1.setAvoidFirstLastClipping(true);

        YAxis y1=mChart.getAxisLeft();
        y1.setTextColor(Color.WHITE);
        y1.setAxisMaxValue(120f);
        y1.setDrawGridLines(true);

        YAxis y12 =mChart.getAxisRight();
        y12.setEnabled(false);
        addEntry();*/
        //get legent object;

        Legend l = mChart.getLegend();

        //customiaze legent
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        ArrayList<Entry> values = new ArrayList<Entry>();
        for (int yIdx = 0; yIdx < yData.size(); yIdx++) {
            double somefloatvalue = yData.get(yIdx);
            values.add(new Entry((float)somefloatvalue, yIdx));
        }
        LineDataSet set =createSet(values);
        dataSets.add(set);


        LineData data = new LineData(xData,dataSets);
        mChart.setData(data);
        mChart.notifyDataSetChanged();
        mChart.invalidate();
    }

    private void addnEtry() {

        LineData data = mChart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            /*if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }*/

            data.addEntry(new Entry(set.getEntryCount(), (int) ((float) (Math.random() * 40) + 30f)), 0);
            data.notifyDataChanged();

            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();

            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(120);
            mChart.invalidate();
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            //mChart.moveViewToX(data.getEntryCount());

            // this automatically refreshes the chart (calls invalidate())
            // mChart.moveViewTo(data.getXValCount()-7, 55f,
            // AxisDependency.LEFT);
        }
    }

    private LineDataSet createSet(ArrayList<Entry> values) {
        LineDataSet set = new LineDataSet(values, "Sleep Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(Color.WHITE);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }
}
