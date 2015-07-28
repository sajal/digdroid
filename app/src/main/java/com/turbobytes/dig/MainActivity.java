package com.turbobytes.dig;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ListPopupWindow;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import go.digdroid.Digdroid;


public class MainActivity extends Activity implements AdapterView.OnItemClickListener {
    EditText serverview;
    ListPopupWindow listPopupWindow;
    ArrayList<String> servers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        servers = new ArrayList<String>();
        Method method = null;
        Class<?> SystemProperties = null;
        try {
            SystemProperties = Class.forName("android.os.SystemProperties");
            method = SystemProperties.getMethod("get", new Class[]{String.class});
            for (String name : new String[] { "net.dns1", "net.dns2", "net.dns3", "net.dns4", }) {
                String value = (String) method.invoke(null, name);
                if (value != null && !"".equals(value) && !servers.contains(value))
                    servers.add(value);
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        Log.d("dns", servers.toString());
        servers.add("8.8.8.8");
        servers.add("208.67.222.222");
        serverview = (EditText)findViewById(R.id.server);
        listPopupWindow = new ListPopupWindow(
                MainActivity.this);
        listPopupWindow.setAdapter(new ArrayAdapter(
                MainActivity.this,
                R.layout.list_item, servers));
        listPopupWindow.setAnchorView(serverview);
        listPopupWindow.setModal(true);
        serverview.setText(servers.get(0));
        listPopupWindow.setOnItemClickListener(      MainActivity.this);
        serverview.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                listPopupWindow.show();
            }
        });

        //Typeface face= Typeface.createFromAsset(getAssets(), "courier.tif");
        //TextView results = (TextView)findViewById(R.id.results);
        //results.setTypeface(face);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id) {
        serverview.setText(servers.get(position));
        listPopupWindow.dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void runQuery(View x){
        hideSoftKeyboard(MainActivity.this);
        TextView results = (TextView)findViewById(R.id.results);
        TextView errors = (TextView)findViewById(R.id.errors);
        TextView rtt = (TextView)findViewById(R.id.rtt);
        Spinner qtype_spinner = (Spinner) findViewById(R.id.qtype_spinner );

        results.setText("");
        errors.setText("");
        rtt.setText("");
        EditText questionview = (EditText)findViewById(R.id.dnsquery);
        EditText serverview = (EditText)findViewById(R.id.server);

        String question = questionview.getText().toString();
        String server = serverview.getText().toString();
        if (!server.equals("") && !server.endsWith(":53")) {
            server = server + ":53";
        }
        if (!question.endsWith(".")) {
            question = question + ".";
        }

        Digdroid.DNSResult result = Digdroid.RunDNS(question, server, qtype_spinner.getSelectedItem().toString(), false);
        String output = result.getOutput();
        String err = result.getErr();
        if (output != null){
            //Dont know how to get tab behaviour in Java...
            results.setText(output.replace("\t", "   "));
        }
        errors.setText(err);
        rtt.setText(result.getRtt());
    }
}
