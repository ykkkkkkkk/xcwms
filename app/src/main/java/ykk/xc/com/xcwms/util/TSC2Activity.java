package ykk.xc.com.xcwms.util;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * 自定义的与蓝牙通讯的类
 * @author ykk
 */
public class TSC2Activity extends Activity {
    private static final String TAG = "THINBTCLIENT";
    private static final boolean D = true;
    private BluetoothAdapter mBluetoothAdapter = null;
//    private BluetoothSocket btSocket = null;
    public BluetoothSocket btSocket = null;
    private OutputStream OutStream = null;
    private InputStream InStream = null;
    private byte[] buffer = new byte[1024];
    private byte[] readBuf = new byte[1024];
    private String printerstatus = "";
    private TextView test1;
    private Button b1;
    private Button b2;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static String address = "00:19:0E:A0:04:E1";

    public TSC2Activity() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(2130903040);
        Log.e("THINBTCLIENT", "+++ ON CREATE +++");
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (this.mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available.", 1).show();
            this.finish();
        } else if (!this.mBluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "Please enable your BT and re-run this program.", 1).show();
            this.finish();
        } else {
            Log.e("THINBTCLIENT", "+++ DONE IN ON CREATE, GOT LOCAL BT ADAPTER +++");
        }
    }

    public void onStart() {
        super.onStart();
        Log.e("THINBTCLIENT", "++ ON START ++");
    }

    public void onResume() {
        super.onResume();
        Log.e("THINBTCLIENT", "+ ON RESUME +");
        Log.e("THINBTCLIENT", "+ ABOUT TO ATTEMPT CLIENT CONNECT +");
        this.test1 = (TextView)this.findViewById(2131165184);
        this.b1 = (Button)this.findViewById(2131165185);
        this.b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TSC2Activity.this.openport("00:19:0E:A0:04:E1");
                TSC2Activity.this.sendcommand("PRINT 10\n");
            }
        });
        this.b2 = (Button)this.findViewById(2131165186);
        this.b2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String QQ = TSC2Activity.this.batch();
                TSC2Activity.this.test1.setText(QQ);
            }
        });
    }

    public void onPause() {
        super.onPause();
        Log.e("THINBTCLIENT", "- ON PAUSE -");
        if (this.OutStream != null) {
            try {
                this.OutStream.flush();
            } catch (IOException var3) {
                Log.e("THINBTCLIENT", "ON PAUSE: Couldn't flush output stream.", var3);
            }
        }

        try {
            this.btSocket.close();
        } catch (IOException var2) {
            Log.e("THINBTCLIENT", "ON PAUSE: Unable to close socket.", var2);
        }

    }

    public void onStop() {
        super.onStop();
        Log.e("THINBTCLIENT", "-- ON STOP --");
    }

    public void onDestroy() {
        super.onDestroy();
        Log.e("THINBTCLIENT", "--- ON DESTROY ---");
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(2131099648, menu);
        return true;
    }

    public void openport(String address) {
        BluetoothDevice device = null;
        BluetoothAdapter mBluetoothAdapter = null;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        device = mBluetoothAdapter.getRemoteDevice(address);

        try {
            this.btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException var8) {
            ;
        }

        mBluetoothAdapter.cancelDiscovery();

        try {
            this.btSocket.connect();
            this.OutStream = this.btSocket.getOutputStream();
            this.InStream = this.btSocket.getInputStream();
        } catch (IOException var7) {
            try {
                this.btSocket.close();
            } catch (IOException var6) {
                ;
            }
        }

    }

    public void sendcommand(String message) {
        byte[] msgBuffer = message.getBytes();

        try {
            this.OutStream.write(msgBuffer);
        } catch (IOException var4) {
            Log.e("THINBTCLIENT", "ON RESUME: Exception during write.", var4);
        }

    }

    public void sendcommand(byte[] message) {
        try {
            this.OutStream.write(message);
        } catch (IOException var3) {
            Log.e("THINBTCLIENT", "ON RESUME: Exception during write.", var3);
        }

    }

    public String status() {
        byte[] message = new byte[]{27, 33, 83};

        try {
            this.OutStream.write(message);
        } catch (IOException var4) {
            Log.e("THINBTCLIENT", "ON RESUME: Exception during write.", var4);
        }

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException var3) {
            var3.printStackTrace();
        }

        int tim;
        try {
            while(this.InStream.available() > 0) {
                this.readBuf = new byte[1024];
                tim = this.InStream.read(this.readBuf);
            }
        } catch (IOException var5) {
            var5.printStackTrace();
        }

        if (this.readBuf[0] == 2 && this.readBuf[5] == 3) {
            for(tim = 0; tim <= 7; ++tim) {
                if (this.readBuf[tim] == 2 && this.readBuf[tim + 1] == 64 && this.readBuf[tim + 2] == 64 && this.readBuf[tim + 3] == 64 && this.readBuf[tim + 4] == 64 && this.readBuf[tim + 5] == 3) {
                    this.printerstatus = "Ready";
                    this.readBuf = new byte[1024];
                    break;
                }

                if (this.readBuf[tim] == 2 && this.readBuf[tim + 1] == 69 && this.readBuf[tim + 2] == 64 && this.readBuf[tim + 3] == 64 && this.readBuf[tim + 4] == 96 && this.readBuf[tim + 5] == 3) {
                    this.printerstatus = "Head Open";
                    this.readBuf = new byte[1024];
                    break;
                }

                if (this.readBuf[tim] == 2 && this.readBuf[tim + 1] == 64 && this.readBuf[tim + 2] == 64 && this.readBuf[tim + 3] == 64 && this.readBuf[tim + 4] == 96 && this.readBuf[tim + 5] == 3) {
                    this.printerstatus = "Head Open";
                    this.readBuf = new byte[1024];
                    break;
                }

                if (this.readBuf[tim] == 2 && this.readBuf[tim + 1] == 69 && this.readBuf[tim + 2] == 64 && this.readBuf[tim + 3] == 64 && this.readBuf[tim + 4] == 72 && this.readBuf[tim + 5] == 3) {
                    this.printerstatus = "Ribbon Jam";
                    this.readBuf = new byte[1024];
                    break;
                }

                if (this.readBuf[tim] == 2 && this.readBuf[tim + 1] == 69 && this.readBuf[tim + 2] == 64 && this.readBuf[tim + 3] == 64 && this.readBuf[tim + 4] == 68 && this.readBuf[tim + 5] == 3) {
                    this.printerstatus = "Ribbon Empty";
                    this.readBuf = new byte[1024];
                    break;
                }

                if (this.readBuf[tim] == 2 && this.readBuf[tim + 1] == 69 && this.readBuf[tim + 2] == 64 && this.readBuf[tim + 3] == 64 && this.readBuf[tim + 4] == 65 && this.readBuf[tim + 5] == 3) {
                    this.printerstatus = "No Paper";
                    this.readBuf = new byte[1024];
                    break;
                }

                if (this.readBuf[tim] == 2 && this.readBuf[tim + 1] == 69 && this.readBuf[tim + 2] == 64 && this.readBuf[tim + 3] == 64 && this.readBuf[tim + 4] == 66 && this.readBuf[tim + 5] == 3) {
                    this.printerstatus = "Paper Jam";
                    this.readBuf = new byte[1024];
                    break;
                }

                if (this.readBuf[tim] == 2 && this.readBuf[tim + 1] == 69 && this.readBuf[tim + 2] == 64 && this.readBuf[tim + 3] == 64 && this.readBuf[tim + 4] == 65 && this.readBuf[tim + 5] == 3) {
                    this.printerstatus = "Paper Empty";
                    this.readBuf = new byte[1024];
                    break;
                }

                if (this.readBuf[tim] == 2 && this.readBuf[tim + 1] == 67 && this.readBuf[tim + 2] == 64 && this.readBuf[tim + 3] == 64 && this.readBuf[tim + 4] == 64 && this.readBuf[tim + 5] == 3) {
                    this.printerstatus = "Cutting";
                    this.readBuf = new byte[1024];
                    break;
                }

                if (this.readBuf[tim] == 2 && this.readBuf[tim + 1] == 75 && this.readBuf[tim + 2] == 64 && this.readBuf[tim + 3] == 64 && this.readBuf[tim + 4] == 64 && this.readBuf[tim + 5] == 3) {
                    this.printerstatus = "Waiting to Press Print Key";
                    this.readBuf = new byte[1024];
                    break;
                }

                if (this.readBuf[tim] == 2 && this.readBuf[tim + 1] == 76 && this.readBuf[tim + 2] == 64 && this.readBuf[tim + 3] == 64 && this.readBuf[tim + 4] == 64 && this.readBuf[tim + 5] == 3) {
                    this.printerstatus = "Waiting to Take Label";
                    this.readBuf = new byte[1024];
                    break;
                }

                if (this.readBuf[tim] == 2 && this.readBuf[tim + 1] == 80 && this.readBuf[tim + 2] == 64 && this.readBuf[tim + 3] == 64 && this.readBuf[tim + 4] == 64 && this.readBuf[tim + 5] == 3) {
                    this.printerstatus = "Printing Batch";
                    this.readBuf = new byte[1024];
                    break;
                }

                if (this.readBuf[tim] == 2 && this.readBuf[tim + 1] == 96 && this.readBuf[tim + 2] == 64 && this.readBuf[tim + 3] == 64 && this.readBuf[tim + 4] == 64 && this.readBuf[tim + 5] == 3) {
                    this.printerstatus = "Pause";
                    this.readBuf = new byte[1024];
                    break;
                }

                if (this.readBuf[tim] == 2 && this.readBuf[tim + 1] == 69 && this.readBuf[tim + 2] == 64 && this.readBuf[tim + 3] == 64 && this.readBuf[tim + 4] == 64 && this.readBuf[tim + 5] == 3) {
                    this.printerstatus = "Pause";
                    this.readBuf = new byte[1024];
                    break;
                }
            }
        }

        return this.printerstatus;
    }

    public String batch() {
//        int printvalue = false;
        String printbatch = "";
        String stringbatch = "";
        String message = "~HS";
        byte[] batcharray = new byte[8];
        byte[] msgBuffer = message.getBytes();

        try {
            this.OutStream.write(msgBuffer);
        } catch (IOException var9) {
            Log.e("THINBTCLIENT", "ON RESUME: Exception during write.", var9);
        }

        try {
            Thread.sleep(100L);
        } catch (InterruptedException var8) {
            var8.printStackTrace();
        }

        try {
            while(this.InStream.available() > 0) {
                this.readBuf = new byte[1024];
                int var7 = this.InStream.read(this.readBuf);
            }
        } catch (IOException var10) {
            var10.printStackTrace();
        }

        if (this.readBuf[0] == 2) {
            System.arraycopy(this.readBuf, 55, batcharray, 0, 8);
            stringbatch = new String(batcharray);
            int printvalue = Integer.parseInt(stringbatch);
            printbatch = Integer.toString(printvalue);
        }

        return printbatch;
    }

    public void closeport() {
        try {
            this.btSocket.close();
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }

    public void setup(int width, int height, int speed, int density, int sensor, int sensor_distance, int sensor_offset) {
        String message = "";
        String size = "SIZE " + width + " mm" + ", " + height + " mm";
        String speed_value = "SPEED " + speed;
        String density_value = "DENSITY " + density;
        String sensor_value = "";
        if (sensor == 0) {
            sensor_value = "GAP " + sensor_distance + " mm" + ", " + sensor_offset + " mm";
        } else if (sensor == 1) {
            sensor_value = "BLINE " + sensor_distance + " mm" + ", " + sensor_offset + " mm";
        }

        message = size + "\n" + speed_value + "\n" + density_value + "\n" + sensor_value + "\n";
        byte[] msgBuffer = message.getBytes();

        try {
            this.OutStream.write(msgBuffer);
        } catch (IOException var15) {
            var15.printStackTrace();
        }

    }

    public void clearbuffer() {
        String message = "CLS\n";
        byte[] msgBuffer = message.getBytes();

        try {
            this.OutStream.write(msgBuffer);
        } catch (IOException var4) {
            var4.printStackTrace();
        }

    }

    public void barcode(int x, int y, String type, int height, int human_readable, int rotation, int narrow, int wide, String string) {
        String message = "";
        String barcode = "BARCODE ";
        String position = x + "," + y;
        String mode = "\"" + type + "\"";
        String height_value = "" + height;
        String human_value = "" + human_readable;
        String rota = "" + rotation;
        String narrow_value = "" + narrow;
        String wide_value = "" + wide;
        String string_value = "\"" + string + "\"";
        message = barcode + position + " ," + mode + " ," + height_value + " ," + human_value + " ," + rota + " ," + narrow_value + " ," + wide_value + " ," + string_value + "\n";
        byte[] msgBuffer = message.getBytes();

        try {
            this.OutStream.write(msgBuffer);
        } catch (IOException var22) {
            var22.printStackTrace();
        }

    }

    public void printerfont(int x, int y, String size, int rotation, int x_multiplication, int y_multiplication, String string) {
        String message = "";
        String text = "TEXT ";
        String position = x + "," + y;
        String size_value = "\"" + size + "\"";
        String rota = "" + rotation;
        String x_value = "" + x_multiplication;
        String y_value = "" + y_multiplication;
        String string_value = "\"" + string + "\"";
        message = text + position + " ," + size_value + " ," + rota + " ," + x_value + " ," + y_value + " ," + string_value + "\n";
        byte[] msgBuffer = message.getBytes();

        try {
            this.OutStream.write(msgBuffer);
        } catch (IOException var18) {
            var18.printStackTrace();
        }

    }

    public void printlabel(int quantity, int copy) {
        String message = "";
        message = "PRINT " + quantity + ", " + copy + "\n";
        byte[] msgBuffer = message.getBytes();

        try {
            this.OutStream.write(msgBuffer);
        } catch (IOException var6) {
            var6.printStackTrace();
        }

    }

    public void formfeed() {
        String message = "";
        message = "FORMFEED\n";
        byte[] msgBuffer = message.getBytes();

        try {
            this.OutStream.write(msgBuffer);
        } catch (IOException var4) {
            var4.printStackTrace();
        }

    }

    public void nobackfeed() {
        String message = "";
        message = "SET TEAR OFF\n";
        byte[] msgBuffer = message.getBytes();

        try {
            this.OutStream.write(msgBuffer);
        } catch (IOException var4) {
            var4.printStackTrace();
        }

    }

    public void sendfile(String filename) {
        try {
            FileInputStream fis = new FileInputStream("/sdcard/Download/" + filename);
            byte[] data = new byte[fis.available()];
            int[] var4 = new int[data.length];

            while(fis.read(data) != -1) {
                ;
            }

            this.OutStream.write(data);
            fis.close();
        } catch (Exception var5) {
            ;
        }

    }

    public void downloadpcx(String filename) {
        try {
            FileInputStream fis = new FileInputStream("/sdcard/Download/" + filename);
            byte[] data = new byte[fis.available()];
            int[] FF = new int[data.length];
            String download = "DOWNLOAD F,\"" + filename + "\"," + data.length + ",";
            byte[] download_head = download.getBytes();

            while(fis.read(data) != -1) {
                ;
            }

            this.OutStream.write(download_head);
            this.OutStream.write(data);
            fis.close();
        } catch (Exception var7) {
            ;
        }

    }

    public void downloadbmp(String filename) {
        try {
            FileInputStream fis = new FileInputStream("/sdcard/Download/" + filename);
            byte[] data = new byte[fis.available()];
            int[] FF = new int[data.length];
            String download = "DOWNLOAD F,\"" + filename + "\"," + data.length + ",";
            byte[] download_head = download.getBytes();

            while(fis.read(data) != -1) {
                ;
            }

            this.OutStream.write(download_head);
            this.OutStream.write(data);
            fis.close();
        } catch (Exception var7) {
            ;
        }

    }

    public void downloadttf(String filename) {
        try {
            FileInputStream fis = new FileInputStream("/sdcard/Download/" + filename);
            byte[] data = new byte[fis.available()];
            int[] FF = new int[data.length];
            String download = "DOWNLOAD F,\"" + filename + "\"," + data.length + ",";
            byte[] download_head = download.getBytes();

            while(fis.read(data) != -1) {
                ;
            }

            this.OutStream.write(download_head);
            this.OutStream.write(data);
            fis.close();
        } catch (Exception var7) {
            ;
        }

    }
}
