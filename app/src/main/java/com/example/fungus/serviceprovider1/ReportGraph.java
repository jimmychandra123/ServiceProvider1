package com.example.fungus.serviceprovider1;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.fungus.serviceprovider1.model.Booking;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

public class ReportGraph extends AppCompatActivity {
    String TAG = "ReportGraph";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_graph);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
            getSupportFragmentManager().beginTransaction().add(R.id.container2, new PlaceholderFragment2()).commit();
            getSupportFragmentManager().beginTransaction().add(R.id.container3, new PlaceholderFragment3()).commit();

        }
    }

    public static class PlaceholderFragment extends Fragment{
        private int max=0;
        private int position;
        private String TAG = "ReportGraph";
        private ArrayList<Booking> bookings;
        private LineChartView chart;
        private LineChartData data;
        private int numberOfLines = 1;
        private int maxNumberOfLines = 1;
        private int numberOfPoints = 12;

        int[][] pointValues = new int[maxNumberOfLines][numberOfPoints];
        private boolean hasAxes = true;
        private boolean hasAxesNames = true;
        private boolean hasLines = true;
        private boolean hasPoints = true;
        private ValueShape shape = ValueShape.CIRCLE;
        private boolean isFilled = false;
        private boolean hasLabels = false;
        private boolean isCubic = true;
        private boolean hasLabelForSelected = false;
        private boolean pointsHaveDifferentColor;
        private boolean hasGradientToTransparent = false;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            setHasOptionsMenu(true);
            View rootView = inflater.inflate(R.layout.fragment_line_chart, container, false);

            chart = (LineChartView) rootView.findViewById(R.id.chart);
            chart.setOnValueTouchListener(new ReportGraph.PlaceholderFragment.ValueTouchListener());

            // Generate some random values.
            generateValues();

            // Disable viewport recalculations, see toggleCubic() method for more info.
            chart.setViewportCalculationEnabled(false);


            return rootView;
        }

        private void generateValues(){
//            for (int i = 0; i < maxNumberOfLines; ++i) {
//                for (int j = 0; j < numberOfPoints; ++j) {
//                    randomNumbersTab[i][j] = (float) Math.random() * 100f;
//                }
//            }
            //firebase
            DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            String id = firebaseAuth.getCurrentUser().getUid();
            Query query = firebaseDatabase.child("Booking").orderByChild("sp_id").startAt(id).endAt(id);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    bookings = new ArrayList<>();
                    for(DataSnapshot next:dataSnapshot.getChildren()){
                        Booking booking = next.getValue(Booking.class);
                        //getting value
//                    Booking booking = new Booking(next.child("b_id").getValue().toString(), next.child("date").getValue().toString(), next.child("time").getValue().toString(), next.child("s_id").getValue().toString(), next.child("sp_id").getValue().toString(), next.child("u_id").getValue().toString(),next.child(""));
                        if(booking!=null) {
                            Log.e(TAG, booking.getB_id());
                            bookings.add(booking);
                        }
                    }

                    if(bookings.size()!=0){
                        pointValues = new int[maxNumberOfLines][numberOfPoints];
                        for(int i=0;i<bookings.size();i++){
                            String date = bookings.get(i).getDate();
                            String[] spitDate = date.split("/");
                            position = Integer.valueOf(spitDate[1]);
                            position--;
                            pointValues[0][position]++;
                            Log.e("e",String.valueOf(pointValues[0][4]));
                        }
                    }
                    generateData();
                    resetViewport();
//                    toggleCubic();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        private void resetViewport() {
            // Reset viewport height range to (0,100)
            final Viewport v = new Viewport(chart.getMaximumViewport());
            for (int i = 0; i < numberOfPoints; i++) {
                if (max < pointValues[0][i])
                    max = pointValues[0][i];
            }

            v.bottom = 0-3;
            v.top = max+5;
            v.left = 0;
            v.right = numberOfPoints - 1;
            chart.setMaximumViewport(v);
            chart.setCurrentViewport(v);
        }

        private void generateData() {
            List<Line> lines = new ArrayList<Line>();
            for (int i = 0; i < numberOfLines; ++i) {
                List<PointValue> values = new ArrayList<PointValue>();
                for (int j = 0; j < numberOfPoints; ++j) {
                    values.add(new PointValue(j, pointValues[i][j]));
                }

                Line line = new Line(values);
                line.setColor(ChartUtils.COLORS[i]);
                line.setShape(shape);
                line.setCubic(isCubic);
                line.setFilled(isFilled);
                line.setHasLabels(hasLabels);
                line.setHasLabelsOnlyForSelected(hasLabelForSelected);
                line.setHasLines(hasLines);
                line.setHasPoints(hasPoints);

//                line.setHasGradientToTransparent(hasGradientToTransparent);
                if (pointsHaveDifferentColor){
                    line.setPointColor(ChartUtils.COLORS[(i + 1) % ChartUtils.COLORS.length]);
                }
                lines.add(line);
            }

            data = new LineChartData(lines);

            if (hasAxes) {
                String[] axisData = {"Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sept",
                        "Oct", "Nov", "Dec"};
                List axisValues = new ArrayList();
                for(int i = 0; i < axisData.length; i++){
                    axisValues.add(i, new AxisValue(i).setLabel(axisData[i]));
                }
                Axis axisX = new Axis(axisValues);
                Axis axisY = new Axis().setHasLines(true);
                if (hasAxesNames) {
                    axisX.setName("Month");
                    axisY.setName("Booking");
                }
//                axisX.setValues()
                data.setAxisXBottom(axisX);
                data.setAxisYLeft(axisY);
            } else {
                data.setAxisXBottom(null);
                data.setAxisYLeft(null);
            }

            data.setBaseValue(Float.NEGATIVE_INFINITY);
            chart.setLineChartData(data);

        }

        private void toggleCubic() {
            generateData();
            // It is good idea to manually set a little higher max viewport for cubic lines because sometimes line
            // go above or below max/min. To do that use Viewport.inest() method and pass negative value as dy
            // parameter or just set top and bottom values manually.
            // In this example I know that Y values are within (0,100) range so I set viewport height range manually
            // to (-5, 105).
            // To make this works during animations you should use Chart.setViewportCalculationEnabled(false) before
            // modifying viewport.
            // Remember to set viewport after you call setLineChartData().
            final Viewport v = new Viewport(chart.getMaximumViewport());
            v.bottom = -3;
            v.top = max+5;
            // You have to set max and current viewports separately.
            chart.setMaximumViewport(v);
            // I changing current viewport with animation in this case.
            chart.setCurrentViewportWithAnimation(v);
        }

        private class ValueTouchListener implements LineChartOnValueSelectListener {

            @Override
            public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
                Toast.makeText(getActivity(), "Selected: " + value, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onValueDeselected() {
                // TODO Auto-generated method stub

            }

        }

    }
    public static class PlaceholderFragment2 extends Fragment {
        private int max = 0;
        private int position;
        private String TAG = "ReportGraph";
        private ArrayList<Booking> bookings;
        private LineChartView chart;
        private LineChartData data;
        private int numberOfLines = 1;
        private int maxNumberOfLines = 1;
        private int numberOfPoints = 12;

        int[][] pointValues = new int[maxNumberOfLines][numberOfPoints];
        private boolean hasAxes = true;
        private boolean hasAxesNames = true;
        private boolean hasLines = true;
        private boolean hasPoints = true;
        private ValueShape shape = ValueShape.CIRCLE;
        private boolean isFilled = false;
        private boolean hasLabels = false;
        private boolean isCubic = true;
        private boolean hasLabelForSelected = false;
        private boolean pointsHaveDifferentColor;
        private boolean hasGradientToTransparent = false;

        public PlaceholderFragment2() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            setHasOptionsMenu(true);
            View rootView = inflater.inflate(R.layout.fragment_line_chart, container, false);

            chart = (LineChartView) rootView.findViewById(R.id.chart);
            chart.setOnValueTouchListener(new ReportGraph.PlaceholderFragment2.ValueTouchListener());

            // Generate some random values.
            generateValues();

            // Disable viewport recalculations, see toggleCubic() method for more info.
            chart.setViewportCalculationEnabled(false);


            return rootView;
        }

        private void generateValues() {
//            for (int i = 0; i < maxNumberOfLines; ++i) {
//                for (int j = 0; j < numberOfPoints; ++j) {
//                    randomNumbersTab[i][j] = (float) Math.random() * 100f;
//                }
//            }
            //firebase
            DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            String id = firebaseAuth.getCurrentUser().getUid();
            Query query = firebaseDatabase.child("Booking").orderByChild("sp_id").startAt(id).endAt(id);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    bookings = new ArrayList<>();
                    for (DataSnapshot next : dataSnapshot.getChildren()) {
                        Booking booking = next.getValue(Booking.class);
                        //getting value
//                    Booking booking = new Booking(next.child("b_id").getValue().toString(), next.child("date").getValue().toString(), next.child("time").getValue().toString(), next.child("s_id").getValue().toString(), next.child("sp_id").getValue().toString(), next.child("u_id").getValue().toString(),next.child(""));
                        if (booking != null) {
                            Log.e(TAG, booking.getB_id());
                            bookings.add(booking);
                        }
                    }

                    if (bookings.size() != 0) {
                        pointValues = new int[maxNumberOfLines][numberOfPoints];
                        for (int i = 0; i < bookings.size(); i++) {
                            if(bookings.get(i).getStatus().equals("Confirmed")) {
                                String date = bookings.get(i).getDate();
                                String[] spitDate = date.split("/");
                                position = Integer.valueOf(spitDate[1]);
                                position--;
                                pointValues[0][position]++;
                                Log.e("f", String.valueOf(pointValues[0][4]));
                            }
                        }
                    }
                    generateData();
                    resetViewport();
//                    toggleCubic();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        private void resetViewport() {
            // Reset viewport height range to (0,100)
            final Viewport v = new Viewport(chart.getMaximumViewport());
            for (int i = 0; i < numberOfPoints; i++) {
                if (max < pointValues[0][i])
                    max = pointValues[0][i];
            }

            v.bottom = 0 - 3;
            v.top = max + 5;
            v.left = 0;
            v.right = numberOfPoints - 1;
            chart.setMaximumViewport(v);
            chart.setCurrentViewport(v);
        }

        private void generateData() {
            List<Line> lines = new ArrayList<Line>();
            for (int i = 0; i < numberOfLines; ++i) {
                List<PointValue> values = new ArrayList<PointValue>();
                for (int j = 0; j < numberOfPoints; ++j) {
                    values.add(new PointValue(j, pointValues[i][j]));
                }

                Line line = new Line(values);
                line.setColor(ChartUtils.COLORS[i]);
                line.setShape(shape);
                line.setCubic(isCubic);
                line.setFilled(isFilled);
                line.setHasLabels(hasLabels);
                line.setHasLabelsOnlyForSelected(hasLabelForSelected);
                line.setHasLines(hasLines);
                line.setHasPoints(hasPoints);

//                line.setHasGradientToTransparent(hasGradientToTransparent);
                if (pointsHaveDifferentColor) {
                    line.setPointColor(ChartUtils.COLORS[(i + 1) % ChartUtils.COLORS.length]);
                }
                lines.add(line);
            }

            data = new LineChartData(lines);

            if (hasAxes) {
                String[] axisData = {"Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sept",
                        "Oct", "Nov", "Dec"};
                List axisValues = new ArrayList();
                for (int i = 0; i < axisData.length; i++) {
                    axisValues.add(i, new AxisValue(i).setLabel(axisData[i]));
                }
                Axis axisX = new Axis(axisValues);
                Axis axisY = new Axis().setHasLines(true);
                if (hasAxesNames) {
                    axisX.setName("Month");
                    axisY.setName("Booking");
                }
//                axisX.setValues()
                data.setAxisXBottom(axisX);
                data.setAxisYLeft(axisY);
            } else {
                data.setAxisXBottom(null);
                data.setAxisYLeft(null);
            }

            data.setBaseValue(Float.NEGATIVE_INFINITY);
            chart.setLineChartData(data);

        }

        private void toggleCubic() {
            generateData();
            // It is good idea to manually set a little higher max viewport for cubic lines because sometimes line
            // go above or below max/min. To do that use Viewport.inest() method and pass negative value as dy
            // parameter or just set top and bottom values manually.
            // In this example I know that Y values are within (0,100) range so I set viewport height range manually
            // to (-5, 105).
            // To make this works during animations you should use Chart.setViewportCalculationEnabled(false) before
            // modifying viewport.
            // Remember to set viewport after you call setLineChartData().
            final Viewport v = new Viewport(chart.getMaximumViewport());
            v.bottom = -3;
            v.top = max + 5;
            // You have to set max and current viewports separately.
            chart.setMaximumViewport(v);
            // I changing current viewport with animation in this case.
            chart.setCurrentViewportWithAnimation(v);
        }

        private class ValueTouchListener implements LineChartOnValueSelectListener {

            @Override
            public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
                Toast.makeText(getActivity(), "Selected: " + value, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onValueDeselected() {
                // TODO Auto-generated method stub

            }

        }
    }

    public static class PlaceholderFragment3 extends Fragment {
        private int max = 0;
        private int position;
        private String TAG = "ReportGraph";
        private ArrayList<Booking> bookings;
        private LineChartView chart;
        private LineChartData data;
        private int numberOfLines = 1;
        private int maxNumberOfLines = 1;
        private int numberOfPoints = 12;

        int[][] pointValues = new int[maxNumberOfLines][numberOfPoints];
        private boolean hasAxes = true;
        private boolean hasAxesNames = true;
        private boolean hasLines = true;
        private boolean hasPoints = true;
        private ValueShape shape = ValueShape.CIRCLE;
        private boolean isFilled = false;
        private boolean hasLabels = false;
        private boolean isCubic = true;
        private boolean hasLabelForSelected = false;
        private boolean pointsHaveDifferentColor;
        private boolean hasGradientToTransparent = false;

        public PlaceholderFragment3() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            setHasOptionsMenu(true);
            View rootView = inflater.inflate(R.layout.fragment_line_chart, container, false);

            chart = (LineChartView) rootView.findViewById(R.id.chart);
            chart.setOnValueTouchListener(new ReportGraph.PlaceholderFragment3.ValueTouchListener());

            // Generate some random values.
            generateValues();

            // Disable viewport recalculations, see toggleCubic() method for more info.
            chart.setViewportCalculationEnabled(false);


            return rootView;
        }

        private void generateValues() {
//            for (int i = 0; i < maxNumberOfLines; ++i) {
//                for (int j = 0; j < numberOfPoints; ++j) {
//                    randomNumbersTab[i][j] = (float) Math.random() * 100f;
//                }
//            }
            //firebase
            DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            String id = firebaseAuth.getCurrentUser().getUid();
            Query query = firebaseDatabase.child("Booking").orderByChild("sp_id").startAt(id).endAt(id);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    bookings = new ArrayList<>();
                    for (DataSnapshot next : dataSnapshot.getChildren()) {
                        Booking booking = next.getValue(Booking.class);
                        //getting value
//                    Booking booking = new Booking(next.child("b_id").getValue().toString(), next.child("date").getValue().toString(), next.child("time").getValue().toString(), next.child("s_id").getValue().toString(), next.child("sp_id").getValue().toString(), next.child("u_id").getValue().toString(),next.child(""));
                        if (booking != null) {
                            Log.e(TAG, booking.getB_id());
                            bookings.add(booking);
                        }
                    }

                    if (bookings.size() != 0) {
                        pointValues = new int[maxNumberOfLines][numberOfPoints];
                        for (int i = 0; i < bookings.size(); i++) {
                            if(bookings.get(i).getStatus().equals("Canceled")) {
                                String date = bookings.get(i).getDate();
                                String[] spitDate = date.split("/");
                                position = Integer.valueOf(spitDate[1]);
                                position--;
                                pointValues[0][position]++;
                                Log.e("g", String.valueOf(pointValues[0][4]));
                            }
                        }
                    }
                    generateData();
                    resetViewport();
//                    toggleCubic();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        private void resetViewport() {
            // Reset viewport height range to (0,100)
            final Viewport v = new Viewport(chart.getMaximumViewport());
            for (int i = 0; i < numberOfPoints; i++) {
                if (max < pointValues[0][i])
                    max = pointValues[0][i];
            }

            v.bottom = 0 - 3;
            v.top = max + 5;
            v.left = 0;
            v.right = numberOfPoints - 1;
            chart.setMaximumViewport(v);
            chart.setCurrentViewport(v);
        }

        private void generateData() {
            List<Line> lines = new ArrayList<Line>();
            for (int i = 0; i < numberOfLines; ++i) {
                List<PointValue> values = new ArrayList<PointValue>();
                for (int j = 0; j < numberOfPoints; ++j) {
                    values.add(new PointValue(j, pointValues[i][j]));
                }

                Line line = new Line(values);
                line.setColor(ChartUtils.COLORS[i]);
                line.setShape(shape);
                line.setCubic(isCubic);
                line.setFilled(isFilled);
                line.setHasLabels(hasLabels);
                line.setHasLabelsOnlyForSelected(hasLabelForSelected);
                line.setHasLines(hasLines);
                line.setHasPoints(hasPoints);

//                line.setHasGradientToTransparent(hasGradientToTransparent);
                if (pointsHaveDifferentColor) {
                    line.setPointColor(ChartUtils.COLORS[(i + 1) % ChartUtils.COLORS.length]);
                }
                lines.add(line);
            }

            data = new LineChartData(lines);

            if (hasAxes) {
                String[] axisData = {"Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sept",
                        "Oct", "Nov", "Dec"};
                List axisValues = new ArrayList();
                for (int i = 0; i < axisData.length; i++) {
                    axisValues.add(i, new AxisValue(i).setLabel(axisData[i]));
                }
                Axis axisX = new Axis(axisValues);
                Axis axisY = new Axis().setHasLines(true);
                if (hasAxesNames) {
                    axisX.setName("Month");
                    axisY.setName("Booking");
                }
//                axisX.setValues()
                data.setAxisXBottom(axisX);
                data.setAxisYLeft(axisY);
            } else {
                data.setAxisXBottom(null);
                data.setAxisYLeft(null);
            }

            data.setBaseValue(Float.NEGATIVE_INFINITY);
            chart.setLineChartData(data);

        }

        private void toggleCubic() {
            generateData();
            // It is good idea to manually set a little higher max viewport for cubic lines because sometimes line
            // go above or below max/min. To do that use Viewport.inest() method and pass negative value as dy
            // parameter or just set top and bottom values manually.
            // In this example I know that Y values are within (0,100) range so I set viewport height range manually
            // to (-5, 105).
            // To make this works during animations you should use Chart.setViewportCalculationEnabled(false) before
            // modifying viewport.
            // Remember to set viewport after you call setLineChartData().
            final Viewport v = new Viewport(chart.getMaximumViewport());
            v.bottom = -3;
            v.top = max + 5;
            // You have to set max and current viewports separately.
            chart.setMaximumViewport(v);
            // I changing current viewport with animation in this case.
            chart.setCurrentViewportWithAnimation(v);
        }

        private class ValueTouchListener implements LineChartOnValueSelectListener {

            @Override
            public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
                Toast.makeText(getActivity(), "Selected: " + value, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onValueDeselected() {
                // TODO Auto-generated method stub

            }

        }
    }
}
