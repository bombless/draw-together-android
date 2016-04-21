package com.example.myapplication;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    DrawCanvas dc;
    Point point;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        dc = new DrawCanvas(this, point);
        final int width = point.x;
        final int height = point.y;

        RelativeLayout ll = (RelativeLayout)findViewById(R.id.root);
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        dc.setLayoutParams(new ActionBar.LayoutParams(width, height));
        ll.addView(dc);
        dc.draw(canvas);
        //dc.addOperation(Operation.test());
        new JsonRequest().my(this);
    }

    public void show(String msg) {
        dc.invalidate();
    }

    public void draw(Operation op) {
        dc.addOperation(op);
    }

}
class DrawCanvas extends View {
    final int width;
    final int height;
    List<Operation> pendingOperations = new ArrayList();

    public void addOperation(Operation op) {
        pendingOperations.add(op);
    }

    public DrawCanvas(Context mContext, Point p) {
        super(mContext);
        width = p.x;
        height = p.y;
    }

    public void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        canvas.drawColor(Color.GREEN);
        for (Operation op: pendingOperations) {
            op.draw(paint, canvas);
        }
    }
}
class JsonRequest extends AsyncTask<URL, Void, String> {
    private MainActivity app;

    public void my(MainActivity app) {
        this.app = app;
        try {
            execute(new URL("http://45.56.96.222:6767/testList"));
        } catch (MalformedURLException e) {
            // nothing
        }

    }
    protected String doInBackground(URL... urls) {
        BufferedReader in;
        try {
            HttpURLConnection conn = (HttpURLConnection)urls[0].openConnection();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            in = new BufferedReader(new InputStreamReader(bis, "UTF-8"));
            return in.readLine();
        } catch (Exception e) {
            return e.toString();
        }
    }
    protected void onPostExecute(String result) {
        try {
            JSONArray obj = new JSONArray(result);
            for (int i = 0; i < obj.length(); ++i) {
                try {
                    JSONObject item = obj.getJSONObject(i);
                    app.draw(Operation.fromJson(item));
                } catch (Exception e) {
                    app.show(e.toString());
                }
            }
            app.show(result);
        } catch (Exception e) {
            app.show(e.toString());
        }
        new JsonRequest().my(app);
    }
}