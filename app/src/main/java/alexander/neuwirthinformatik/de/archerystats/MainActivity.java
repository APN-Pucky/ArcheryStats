package alexander.neuwirthinformatik.de.archerystats;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private CSVExport csvexport = new CSVExport(this);

    private int currentShot = 0;

    private TextView[] textViews = new TextView[6];
    private TextView textViewShotCount;
    ActionBar bar;
    private int[] values = {-1,-1,-1,-1,-1,-1};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        bar = getSupportActionBar();

        textViews[0] = (TextView) findViewById(R.id.textView1);
        textViews[1] = (TextView) findViewById(R.id.textView2);
        textViews[2] = (TextView) findViewById(R.id.textView3);
        textViews[3] = (TextView) findViewById(R.id.textView4);
        textViews[4] = (TextView) findViewById(R.id.textView5);
        textViews[5] = (TextView) findViewById(R.id.textView6);
        textViewShotCount =(TextView) findViewById(R.id.textViewShotCount);
        closeSession();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.new_session) {
            startNewSession();
        } else if (id == R.id.load_session) {
            loadSession();
        }else if (id == R.id.close_session) {
            closeSession();
        } else if (id == R.id.view_plots) {
            viewPlots();
        } else if (id == R.id.view_session_entries) {
            viewSessionEntries();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void input_button(int b)
    {
        if(!isSessionActive())
        {
            startNewSession();
        }
        else {
            values[currentShot] = b;
            updateCurrentTextView();
        }
    }

    private void setCurrentShot(int n)
    {
        textViews[currentShot].setBackgroundColor(Color.WHITE);
        currentShot = n;
        textViews[currentShot].setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimaryLight));
    }

    private void updateCurrentTextView(){
        if ( values[currentShot] >= 0) {
            textViews[currentShot].setText("" + values[currentShot]);
            setCurrentShot((currentShot + 1) % 6);
        }
        else {
            textViews[currentShot].setText(" - ");
        }
    }

    private void resetTextViews(){
        for( TextView tv : textViews)
        {
            tv.setText(" - ");
        }
        setCurrentShot(0);
    }

    private void updateShotCount()
    {
        int shots = 0;
        if(isSessionActive())
        {
            int[][] data = csvexport.importValues();
            if(data.length >0)shots = data[0].length*6;
        }
        textViewShotCount.setText(""+shots);
    }

    public void buttonSave(View view) {
        if(!isSessionActive())
        {
            startNewSession();
        }
        else
        {
            //push values to CSV
            for (int v : values)//ERROR TOAST DIALOG
            {
                if(v==-1)
                {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.dialog_empty_fields_header)
                            .setMessage(R.string.dialog_empty_fields_message)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    return;
                }
            }
            //sort values
            for(int i =0; i < 6;i++)
            {
                for(int j = 5 ;j >i;j--)
                {
                    if(values[j]>values[i]) {
                        //swap
                        values[j]^=values[i];
                        values[i]^=values[j];
                        values[j]^=values[i];
                    }
                }
            }
            csvexport.exportValues(values);
            values = new int[]{-1,-1,-1,-1,-1,-1};
            updateShotCount();
            resetTextViews();
        }
    }

    public boolean isSessionActive()
    {
        return csvexport.isSessionActive();
    }

    public void viewPlots()
    {
        if(!isSessionActive())
        {
            Toast.makeText(this,R.string.toast_no_session,Toast.LENGTH_SHORT).show();
        }
        else
        {
            Intent intent = new Intent(this, ViewPlotsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("data",csvexport.importValues());
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    public void viewSessionEntries()
    {
        if(!isSessionActive())
        {
            Toast.makeText(this,R.string.toast_no_session,Toast.LENGTH_SHORT).show();
        }
        else
        {
            Intent intent = new Intent(this, ViewEntriesActivity.class);
            Bundle bundle = new Bundle();
            //Log.e("data size",""+ csvexport.importValues().length);
            bundle.putSerializable("data",csvexport.importValues());
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    public void closeSession()
    {
        setTitle(R.string.toolbar_title_no_session);
        bar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this,R.color.colorAccent)));
        csvexport.closeSession();
        updateShotCount();
        resetTextViews();
    }

    public void updateSessionFile(Uri file)
    {
        setTitle(new File(file.getPath()).getName());
        bar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this,R.color.colorPrimary)));
        csvexport.setSessionFile(file);
        updateShotCount();
        resetTextViews();
    }

    private static final int CREATE_FILE = 1;

    public void createSession(String session) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        intent.putExtra(Intent.EXTRA_TITLE, session);

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when your app creates the document.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, "ArcheryStats");

        startActivityForResult(intent, CREATE_FILE);
    }

    private static final int PICK_FILE = 2;

    public void loadSession() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("file/*");
        String[] mimeTypes = { "text/csv", "text/comma-separated-values" ,"application/pdf","image/*"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        // Optionally, specify a URI for the file that should appear in the
        // system file picker when it loads.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, "ArcheryStats");

        startActivityForResult(intent, PICK_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
        Intent resultData) {
        if (requestCode == PICK_FILE
                && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                updateSessionFile(uri);
            }
        }
        if (requestCode == CREATE_FILE
                && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                updateSessionFile(uri);
            }
        }
    }

    public void startNewSession() {
        //NEW SESSION DIALOG
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_start_new_session_title)
                .setMessage(R.string.dialog_start_new_session_message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with new session
                        String dateString = new SimpleDateFormat("yyyy-MM-dd-HH;mm;ss", Locale.GERMAN).format(new Date());
                        createSession(dateString);
                        values = new int[]{-1,-1,-1,-1,-1,-1};
                        resetTextViews();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    public void buttonDel(View view) {
        input_button(-1);
    }
    public void button0(View view) {
        input_button(0);
    }
    public void button1(View view) {
        input_button(1);
    }
    public void button2(View view) {
        input_button(2);
    }
    public void button3(View view) {
        input_button(3);
    }
    public void button4(View view) {
        input_button(4);
    }
    public void button5(View view) {
        input_button(5);
    }
    public void button6(View view) {
        input_button(6);
    }
    public void button7(View view) {
        input_button(7);
    }
    public void button8(View view) {
        input_button(8);
    }
    public void button9(View view) {
        input_button(9);
    }
    public void button10(View view) {
        input_button(10);
    }
    public void textView1(View view) {
        setCurrentShot(0);
    }
    public void textView2(View view) {
        setCurrentShot(1);
    }
    public void textView3(View view) {
        setCurrentShot(2);
    }
    public void textView4(View view) {
        setCurrentShot(3);
    }
    public void textView5(View view) {
        setCurrentShot(4);
    }
    public void textView6(View view) {
        setCurrentShot(5);
    }
}
