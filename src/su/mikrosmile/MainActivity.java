package su.mikrosmile;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	Button b1;
	Button b2;
	Button b3;
	Button b4;
	Button b5;
	Button b6;
	private static String values = ""; /* Empty String to locate the fetched data */
	private static String bValues = ""; 
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String[] commands = {"adb shell", "echo 'boot-recovery ' > /cache/recovery/command",
			"adb shell", "echo '--update_package=SDCARD:update.zip'" + ">> /cache/recovery/command",
			"adb shell", "reboot recovery"};
		setContentView(R.layout.activity_main);
		findViewsById();
		b1.setOnClickListener(new OnClickListener(){
			
			/* Simple command to reboot to recovery without root access. 
			 * Also for reboot command you need to add permission for REBOOT
			 * You will get error in Eclipse after adding this permission
			 * just clean project and your are good
			 * Also for this you have to have reboot in system/bin 
			 * 
			 * Copyright mikrosmile (DarkSense Team)
			 * 
			 */

			@Override
			public void onClick(View v) {
				((PowerManager) getSystemService(Context.POWER_SERVICE)).reboot("recovery");
			}
			
		});
		b2.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				try {
				
					/*
					 * Here the method RunSu and the command array will be
					 * run together
					 */
					RunSu(commands);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				/*
				 * You still need to have reboot command to reboot to recovery
				 */
				((PowerManager) getSystemService(Context.POWER_SERVICE)).reboot("recovery");
				
			}
			
		});
		b3.setOnClickListener(new OnClickListener(){
			
			/* Simple method to request SU for your application
			 * Paste it to OnCreate method and su will be requested automatically 
			 * once you run it
			 * 
			 * Copyright mikrosmile (DarkSense Team)
			 * 
			 */
			
			

			@Override
			public void onClick(View v) {
				try {
					@SuppressWarnings("unused")
					Process process = Runtime.getRuntime().exec("su");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		});
		b4.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				/* In this method the su command will be executed as well as the command itself
				 * It will mount your system rw
				 * Always use try catch surrounding as in example 
				 * 
				 * Copyright mikrosmile (DarkSense Team)
				 * 
				 */
				
				try {
					Process process = Runtime.getRuntime().exec("su");
					DataOutputStream output = new DataOutputStream(process.getOutputStream());
					output.writeBytes("mount -o remount,rw /system\n");
					output.writeBytes("exit\n");
					output.flush();
					try {
						process.waitFor();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}
			
		});
		b5.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				/*
				 * first executing the method to get data
				 */
				BuildPropToString();
				/*
				 * Do with data what you want. I change name of Button
				 */
				b5.setText(values);
				
			}
			
		});
		b6.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				/*
				 * Here you will only read data from build.prop 
				 * you cannot store it as String
				 * It can help you to check something with if else statement
				 */
				bValues = getBuildProp("ro.product.version");
			 	Toast.makeText(getApplicationContext(),bValues , Toast.LENGTH_LONG).show();

				
			}
			
		});
	}

	private void findViewsById() {
		b1 = (Button) findViewById(R.id.button1);
		b2 = (Button) findViewById(R.id.button2);
		b3 = (Button) findViewById(R.id.button3);
		b4 = (Button) findViewById(R.id.button4);
		b5 = (Button) findViewById(R.id.button5);
		b6 = (Button) findViewById(R.id.button6);
		
	}
	public static void RunSu(String[] cmds) throws IOException{
		
		/* This method is universal method to execute different su commands without 
		 * writing the method itself again and again
		 * It will use tmpCmd as your string arrays (see at the top)
		 * Im using reboot to recovery commands with auto fetching the wile to open
		 * once it booted to recovery. 
		 * insite "" this you get your command
		 * by "," you separate each command to execute
		 * Here is my example executing in adb 
		 * 
		 * 	adb shell 
		 * 	echo 'boot-recovery ' > /cache/recovery/command
			adb shell
			echo '--update_package=SDCARD:update.zip' >> /cache/recovery/command
			adb shell
			reboot recovery
		 * once you have "," application will consider as run this command inside ""
		 * 
		 * Copyright mikrosmile (DarkSense Team)
		 * 
		 */
        Process process = Runtime.getRuntime().exec("su");
        DataOutputStream output = new DataOutputStream(process.getOutputStream());            
        for (String tmpCmd : cmds) {
        	output.writeBytes(tmpCmd+"\n");
        }           
        output.writeBytes("exit\n");  
        output.flush();
}
	public void BuildPropToString(){
		
		/* Here you can get prop values and convert it to string
		 * For example it can be good to store some non coded information in the app
		 * In my example you in changes the button name of Button 5 (b5)
		 * You will need to have empty static String and once you want to get data 
		 * run the whole method first (see example in b5) and when you get data do whatever you want
		 * if you insert this method to OnCreate so the data from build.prop will be fetched
		 *  once application run
		 * 
		 * Copyright mikrosmile (DarkSense Team)
		 */
		
		
    	Object obj;
		try {
			obj = Runtime.getRuntime().exec("getprop ro.product.version");
			Object obj1 = new BufferedReader(new InputStreamReader(((Process) (obj)).getInputStream()));
	        values = ((BufferedReader) (obj1)).readLine();
	        ((Process) (obj)).destroy();
	        ((BufferedReader) (obj1)).close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
	public static String getBuildProp(String string)
    {
		/* In this method you will get the whole build.prop in your cache
		*And once you want to get some data from build.prop use this method
		*  getBuildProp("ro.product.verison")
		*  in breakets the line you want to check and do whatever you want with the data
		*  See example in Button 6 (b6)
		*  
		*  Copyright mikrosmile (DarkSense Team)
		*
		*
		*
		*/
    	Object obj = new Properties();
    	try {
			((Properties) (obj)).load(new FileInputStream("/system/build.prop"));
			obj = ((Properties) (obj)).getProperty(string);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return ((String) (obj));
    }


}
