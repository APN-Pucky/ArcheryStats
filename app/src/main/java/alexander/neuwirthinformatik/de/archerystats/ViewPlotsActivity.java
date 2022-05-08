package alexander.neuwirthinformatik.de.archerystats;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Arrays;

public class ViewPlotsActivity extends AppCompatActivity {

    int[][] data;
    private XYPlot plot1,plot2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_plots);

        plot1 = (XYPlot) findViewById(R.id.plot1);
        plot2 = (XYPlot) findViewById(R.id.plot2);

        Object[] objectArray = (Object[])getIntent().getExtras().getSerializable("data");

        if(objectArray != null && objectArray.length != 0){
            data = new int[objectArray.length][];
            for(int i=0;i<objectArray.length;i++){
                data[i]=(int[]) objectArray[i];
            }
        }
        else
        {
            Log.e("ViewEntries", "No Data");
            return;
        }
        setUpPlot1();
        setUpPlot2();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    private void setUpPlot1()
    {
        Number[] series1Numbers = new Number[data[0].length*2];

        for(int j= 0; j < data[0].length;j++) {
            int sum=0;
            for (int i = 0; i < data.length; i++) {
                sum += data[i][j];
            }
            series1Numbers[2*j]=j;
            series1Numbers[2*j+1]=sum;
        }

        // create a couple arrays of y-values to plot:
        XYSeries series1 = new SimpleXYSeries(
                Arrays.asList(series1Numbers), SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, "Series1");

        LineAndPointFormatter series1Format =
                new LineAndPointFormatter(this, R.xml.plot_config);

        series1Format.setInterpolationParams(
                new CatmullRomInterpolator.Params(20, CatmullRomInterpolator.Type.Centripetal));

        // just for fun, add some smoothing to the lines:
        // see: http://androidplot.com/smooth-curves-and-androidplot/
        /*series1Format.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));*/

        plot1.setRangeBoundaries(0,60, BoundaryMode.FIXED);
        plot1.setRangeStep(StepMode.INCREMENT_BY_VAL,10);
        plot1.setDomainStep(StepMode.INCREMENT_BY_VAL,1);
        plot1.getLayoutManager().remove(plot1.getLegend());
        // add a new series' to the xyplot:
        plot1.addSeries(series1, series1Format);

        plot1.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                int i = Math.round(((Number) obj).floatValue());
                return toAppendTo.append(i+1);
            }
            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });
    }

    private void setUpPlot2()
    {
        int[] number_shots = new int[11];
        Number[] series1Numbers = new Number[11];

        for(int j= 0; j < data[0].length;j++) {
            for (int i = 0; i < data.length; i++) {
                number_shots[data[i][j]]++;
            }
        }
        for(int i =0; i  < number_shots.length;i++)
            series1Numbers[i] = number_shots[i];

        // create a couple arrays of y-values to plot:
        XYSeries series1 = new SimpleXYSeries(
                Arrays.asList(series1Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");

        LineAndPointFormatter series1Format =
                new LineAndPointFormatter(this, R.xml.plot_config);

        series1Format.setInterpolationParams(
                new CatmullRomInterpolator.Params(20, CatmullRomInterpolator.Type.Centripetal));
        plot2.setRangeStep(StepMode.INCREMENT_BY_VAL,5);
        plot2.setDomainStep(StepMode.INCREMENT_BY_VAL,1);
        plot2.getLayoutManager().remove(plot2.getLegend());

        // add a new series' to the xyplot:
        plot2.addSeries(series1, series1Format);

        plot2.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                int i = Math.round(((Number) obj).floatValue());
                return toAppendTo.append(i);
            }
            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });
    }
}
