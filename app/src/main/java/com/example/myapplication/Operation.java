package com.example.myapplication;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import org.json.JSONException;
import org.json.JSONObject;

abstract public class Operation {
    public enum Types {
        Circle (1),
        Rectangle(2);
        private final int number;
        Types(int number) {
            this.number = number;
        }
        public static Types fromInt(int n) {
            switch (n) {
                case 1:
                    return Circle;
                case 2:
                    return Rectangle;
                default:
                    return null;
            }
        }
    }

    protected static float normalize(double val) {
        if (val > 1) {
            return 1;
        } else if (val < 0) {
            return 0;
        } else {
            return (float)val;
        }
    }

    static public class Circle extends Operation {

        private final float radius;
        private final float x;
        private final float y;

        Circle(double x, double y, double radius) {
            this.radius = normalize(radius);
            this.x = normalize(x);
            this.y = normalize(y);
        }

        @Override
        public void draw(Paint p, Canvas c) {
            p.setColor(Color.BLUE);
            final int width = c.getWidth();
            final int height = c.getHeight();
            c.drawCircle(x * width, y * height, radius * Math.min(width, height) / 2, p);
        }
    }

    static Operation fromJson(JSONObject json) {
        try {
            int type = json.getInt("type");
            if (Types.fromInt(type) == Types.Circle) {
                return new Circle(json.getDouble("x"), json.getDouble("y"), json.getDouble("radius"));
            }
        } catch (JSONException e) {

        }

        return null;
    }

    static Operation test() {
        return new Circle(.5, .5, 1);
    }

    abstract public void draw(Paint p, Canvas c);
}