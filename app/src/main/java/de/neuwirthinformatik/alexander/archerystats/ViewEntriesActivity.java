package de.neuwirthinformatik.alexander.archerystats;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import alexander.neuwirthinformatik.de.archerystats.R;

public class ViewEntriesActivity extends AppCompatActivity {

    int[][] data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_entries);

        TableLayout tbl = (TableLayout) findViewById(R.id.table_layout);

        Object[] objectArray = (Object[])getIntent().getExtras().getSerializable("data");

        if(objectArray!=null && objectArray.length != 0){
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

        int full_sum=0;
        int full_shots = data[0].length*6;

        for(int j= 0; j < data[0].length;j++)
        {
            TableRow newRow = new TableRow(this);
            TextView tv = new TextView(this);
            String line = j+1 + ".  ";
            tv.setText(line);
            tv.setBackgroundColor(ContextCompat.getColor(this,R.color.colorRow));
            if(j%2==1)tv.setBackgroundColor(ContextCompat.getColor(this,R.color.colorSecondLine));
            newRow.addView(tv);
            int sum =0;

            for(int i= 0; i < data.length;i++)
            {
                sum += data[i][j];
                tv = new TextView(this);
                tv.setText(""+data[i][j]);
                if(j%2==1)tv.setBackgroundColor(ContextCompat.getColor(this,R.color.colorSecondLine));
                newRow.addView(tv);
            }
            full_sum += sum;
            tv = new TextView(this);
            tv.setText(""+sum);
            tv.setBackgroundColor(ContextCompat.getColor(this,R.color.colorSum));
            newRow.addView(tv);

            tv = new TextView(this);
            double avg = ((double)sum)/6 * 100D;
            avg = Math.round(avg)/100D;
            tv.setText(""+avg);
            tv.setBackgroundColor(ContextCompat.getColor(this,R.color.colorAvg));
            newRow.addView(tv);
            tbl.addView(newRow);
        }

        TableRow newRow = new TableRow(this);
        TextView tv = new TextView(this);
        tv.setText(getResources().getString(R.string.main_shots) + " " + full_shots);
        tv.setBackgroundColor(ContextCompat.getColor(this,R.color.colorRow));
        newRow.addView(tv);
        for(int i =0;i<6;i++)
        {
            tv = new TextView(this);
            tv.setText("-");
            tv.setBackgroundColor(ContextCompat.getColor(this,R.color.colorRow));
            newRow.addView(tv);
        }
        tv = new TextView(this);
        tv.setText(""+full_sum);
        tv.setBackgroundColor(ContextCompat.getColor(this,R.color.colorRow));
        newRow.addView(tv);

        tv = new TextView(this);
        double avg = ((double)full_sum)/full_shots * 100D;
        avg = Math.round(avg)/100D;
        tv.setText(""+avg);
        tv.setBackgroundColor(ContextCompat.getColor(this,R.color.colorRow));
        newRow.addView(tv);
        tbl.addView(newRow);

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
}
