package com.blogspot.teperi31.moneydiary;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class ChartTestActivity extends AppCompatActivity {
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.charttest);
		
		PieChart pieChart = (PieChart)findViewById(R.id.charttest_lineChart);
		
		pieChart.setUsePercentValues(true);
		pieChart.getDescription().setEnabled(false);
		pieChart.setExtraOffsets(5,10,5,5);
		
		pieChart.setDragDecelerationFrictionCoef(0.95f);
		
		pieChart.setDrawHoleEnabled(false);
		pieChart.setHoleColor(Color.WHITE);
		pieChart.setTransparentCircleRadius(61f);
		
		ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();
		
		yValues.add(new PieEntry(34f,"Japen"));
		yValues.add(new PieEntry(23f,"USA"));
		yValues.add(new PieEntry(14f,"UK"));
		yValues.add(new PieEntry(35f,"India"));
		yValues.add(new PieEntry(40f,"Russia"));
		yValues.add(new PieEntry(40f,"Korea"));
		
		Description description = new Description();
		description.setText("세계 국가"); //라벨
		description.setTextSize(15);
		pieChart.setDescription(description);
		
		pieChart.animateX(1000);
		
		PieDataSet dataSet = new PieDataSet(yValues,"Countries");
		dataSet.setSliceSpace(3f);
		dataSet.setSelectionShift(5f);
		dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
		
		PieData data = new PieData((dataSet));
		data.setValueTextSize(10f);
		data.setValueTextColor(Color.YELLOW);
		
		pieChart.setData(data);
	}
}
