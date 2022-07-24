package alexander.neuwirthinformatik.de.archerystats;


import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class CSVExport {
    Uri file;
    MainActivity mainActivity;

    public CSVExport(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
    }

    public boolean isSessionActive()
    {
        return file != null;
    }

    public void closeSession()
    {
        file = null;
    }

    public void setSessionFile(Uri file)
    {
        this.file = file;
    }

    public void exportValues(int[] values)
    {
        if (file !=null)
        {
            try {
                ParcelFileDescriptor pfd = mainActivity.getContentResolver().
                openFileDescriptor(file, "wa");
                FileOutputStream fileOutputStream =
                new FileOutputStream(pfd.getFileDescriptor());

                OutputStreamWriter out = new OutputStreamWriter(fileOutputStream);

                for(int i : values)
                {
                    out.write(Integer.toString(i) + ";");
                }
                out.write('\n');
                out.close();
            } catch (IOException e) {
                Log.e("IO",""+e.toString());
            }
        }
    }

    public int[][] importValues()
    {
        int[][] data = new int[0][0];
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader((InputStream) mainActivity.getContentResolver().openInputStream(file)));
            String line;
            String file_data = "";
            int number_lines =0;
            do {
                line = br.readLine();
                file_data += line + "\n";
                number_lines++;

            } while (line != null);

            data = new int[6][number_lines-1];
            String[] lines = file_data.split("\\r?\\n");//split new line

            for(int i = 0; i < lines.length-1;i++)
            {

                String[] numbers = lines[i].split(";");
                for(int j = 0; j < numbers.length;j++)
                {
                    data[j][i] = Integer.parseInt(numbers[j]);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    /*public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }*/

}
