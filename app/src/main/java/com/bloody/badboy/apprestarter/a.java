/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Arpan ßløødy ßadßøy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.bloody.badboy.apprestarter;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import java.io.*;


public class a extends Activity
{
	private Toast toast;
	private long lastBackPressTime;

	@Override
	private void SuperUser_Alert()
	{
        new AlertDialog.Builder(this)
			.setTitle("Fatel Error !!")
			.setMessage("Failed to get root access.\n\nAre you really rooted ?\n\nIf not then search in google how to Root your device.And then try again.")
			.setCancelable(false)
			.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id)
				{
					a.this.finish();
				}
			})
			.show();
    }

	@Override
	private void BatteryAlert()
	{
        new AlertDialog.Builder(this)
			.setTitle("Opps !!")
			.setMessage("\n/data/system/batterystats.bin file not found.\n\nFailed to calibrate your device battery.\n\nPlease reboot your device and then try again.")
			.setCancelable(true)
			.setPositiveButton("OK !!", null)
			.show();
    }

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restarter_button_layout);
		Button restart_systemui = (Button)findViewById(R.id.restartui);
		Button restart_settings = (Button)findViewById(R.id.restart_settings);
		Button soft_reboot = (Button)findViewById(R.id.soft_reboot);
		Button reboot = (Button)findViewById(R.id.reboot);
		Button reboot_recovery = (Button)findViewById(R.id.reboot_recovery);
		Button power_off= (Button)findViewById(R.id.power_off);
		Button battery_calibration= (Button)findViewById(R.id.battery_calibration);
////////////////////////////////////////////////////
//    For killing systemui using pkill command    //
////////////////////////////////////////////////////

		restart_systemui.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View view)
				{
					Toast.makeText(getApplicationContext(), "Restarting SystemUI....", Toast.LENGTH_LONG)
						.show();
					try
					{
						DataOutputStream dataOutputStream = new DataOutputStream(Runtime.getRuntime().exec("su").getOutputStream());
						dataOutputStream.writeBytes("busybox pkill com.android.systemui\n");
						dataOutputStream.writeBytes("exit\n");
						return;
					}
					catch (Exception e)
					{
						SuperUser_Alert();
						return;
					}
				}
			});

/////////////////////////////////////////////////////
//    For Killing settings using pkill command    //
///////////////////////////////////////////////////

		restart_settings.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View view)
				{
					Toast.makeText(getApplicationContext(), "Restarting Settings....", Toast.LENGTH_LONG)
						.show();
					try
					{
						DataOutputStream dataOutputStream = new DataOutputStream(Runtime.getRuntime().exec("su").getOutputStream());
						dataOutputStream.writeBytes("busybox pkill com.android.settings\n");
						dataOutputStream.writeBytes("exit\n");
						return;
					}
					catch (Exception e)
					{
						SuperUser_Alert();
						return;
					}
				}
			});

////////////////////////////////////////
//    For soft rebooting of device    //
///////////////////////////////////////
		soft_reboot.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View view)
				{
					try
					{
						DataOutputStream dataOutputStream = new DataOutputStream(Runtime.getRuntime().exec("su").getOutputStream());
						dataOutputStream.writeBytes("setprop ctl.restart surfaceflinger\n");
						dataOutputStream.writeBytes("setprop ctl.restart zygote\n");
						dataOutputStream.writeBytes("exit\n");
						return;
					}
					catch (Exception e)
					{
						SuperUser_Alert();
						return;
					}
				}
			});

////////////////////////////////////////
//    For hard rebooting of device    //
///////////////////////////////////////

		reboot.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View view)
				{
					try
					{
						DataOutputStream dataOutputStream = new DataOutputStream(Runtime.getRuntime().exec("su").getOutputStream());
						dataOutputStream.writeBytes("busybox reboot\n");
						dataOutputStream.writeBytes("reboot\n");
						dataOutputStream.writeBytes("exit\n");
						return;
					}
					catch (Exception e)
					{
						SuperUser_Alert();
						return;
					}
				}
			});

///////////////////////////////////////////////////
//    For Rebooting device into recovery mode    //    
//////////////////////////////////////////////////

		reboot_recovery.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View view)
				{
					try
					{
						DataOutputStream dataOutputStream = new DataOutputStream(Runtime.getRuntime().exec("su").getOutputStream());
						dataOutputStream.writeBytes("reboot recovery\n");
						dataOutputStream.writeBytes("exit\n");
						return;
					}
					catch (Exception e)
					{
						SuperUser_Alert();
						return;
					}
				}
			});

////////////////////////
//    For ShutDown    //
///////////////////////
		power_off.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View view)
				{
					try
					{
						DataOutputStream dataOutputStream = new DataOutputStream(Runtime.getRuntime().exec("su").getOutputStream());
						dataOutputStream.writeBytes("reboot -p\n");
						dataOutputStream.writeBytes("exit\n");
						return;
					}
					catch (Exception e)
					{
						SuperUser_Alert();
						return;
					}
				}
			});
/////////////////////////////////
//    Wipe batterystats.bin   //
////////////////////////////////
		battery_calibration.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View view)
				{
					if (new File("/data/system/batterystats.bin").exists())
					{
						try
						{
							DataOutputStream dataOutputStream = new DataOutputStream(Runtime.getRuntime().exec("su").getOutputStream());
							dataOutputStream.writeBytes("rm /data/system/batterystats.bin\n");
							dataOutputStream.writeBytes("exit\n");
							return;
						}
						catch (Exception e)
						{
							SuperUser_Alert();
							return;
						}
					}
					else
					{
						BatteryAlert();
					}
				}
			});
    }

////////////////////////////////////////////////
//    For closing app on back button click    //
///////////////////////////////////////////////

	@Override
	public void onBackPressed()
	{
		if (this.lastBackPressTime < System.currentTimeMillis() - 4000)
		{
			toast = Toast.makeText(this, "Press back once to exit.", 5000);
			toast.show();
			this.lastBackPressTime = System.currentTimeMillis();
		}
		else
		{
			if (toast != null)
			{
				toast.cancel();
			}
			super.onBackPressed();
		}
	}
}
