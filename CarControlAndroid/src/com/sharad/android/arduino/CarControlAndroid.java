/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sharad.android.arduino;

import java.io.IOException;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;

import com.flotype.bridge.Bridge;
import com.flotype.bridge.BridgeRemoteObject;

/**
 * This is an example of using the accelerometer to integrate the device's
 * acceleration to a position using the Verlet method. This is illustrated with
 * a very simple particle system comprised of a few iron balls freely moving on
 * an inclined wooden table. The inclination of the virtual table is controlled
 * by the device's accelerometer.
 * 
 * @see SensorManager
 * @see SensorEvent
 * @see Sensor
 */

public class CarControlAndroid extends Activity implements SensorEventListener {

	private SensorManager mSensorManager;
	private PowerManager mPowerManager;
	private WindowManager mWindowManager;
	private Display mDisplay;
	private float mSensorX, mSensorY;
	private long mSensorTimeStamp;
	private Sensor mAccelerometer;
	private WakeLock mWakeLock;
	private boolean leftOn = false, rightOn = false, acc = false, dec = false;

	interface Sharkad extends BridgeRemoteObject {
		public void accel();

		public void leftOn();

		public void leftOff();

		public void rightOn();

		public void rightOff();

		public void stop();

		public void decel();
	}

	Sharkad sharkad;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Get an instance of the SensorManager
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		// Get an instance of the PowerManager
		mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);

		// Get an instance of the WindowManager
		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		mDisplay = mWindowManager.getDefaultDisplay();

		// Create a bright wake lock
		mWakeLock = mPowerManager.newWakeLock(
				PowerManager.SCREEN_BRIGHT_WAKE_LOCK, getClass().getName());

		// mSimulationView = new SimulationView(this);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(this, mAccelerometer,
				SensorManager.SENSOR_DELAY_UI);
		setContentView(R.layout.main);

		Button accel = (Button) findViewById(R.id.accel);
		Button decel = (Button) findViewById(R.id.decel);
		accel.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (!acc) {
					sharkad.accel();
					acc = true;
				}
				return false;
			}

		});
		accel.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				sharkad.stop();
				acc = false;
			}

		});
		decel.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (!dec) {
					sharkad.decel();
					dec = true;
				}
				return false;
			}

		});
		decel.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				sharkad.stop();
				dec = false;
			}

		});
		Bridge bridge = new Bridge().setApiKey("abcdefgh");
		try {
			bridge.connect();
			sharkad = bridge.getService("sharkad", Sharkad.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		/*
		 * when the activity is resumed, we acquire a wake-lock so that the
		 * screen stays on, since the user will likely not be fiddling with the
		 * screen or buttons.
		 */

		// Start the simulation
	}

	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
			return;
		/*
		 * record the accelerometer data, the event's timestamp as well as the
		 * current time. The latter is needed so we can calculate the "present"
		 * time during rendering. In this application, we need to take into
		 * account how the screen is rotated with respect to the sensors (which
		 * always return data in a coordinate space aligned to with the screen
		 * in its native orientation).
		 */

		switch (mDisplay.getRotation()) {
		case Surface.ROTATION_0:
			mSensorX = event.values[0];
			mSensorY = event.values[1];

			break;
		case Surface.ROTATION_90:
			mSensorX = -event.values[1];
			mSensorY = event.values[0];

			break;
		case Surface.ROTATION_180:
			mSensorX = -event.values[0];
			mSensorY = -event.values[1];

			break;
		case Surface.ROTATION_270:
			mSensorX = event.values[1];
			mSensorY = -event.values[0];

			break;

		}
		if (mSensorX > 2) {
			if (!leftOn) {
				sharkad.leftOn();
				leftOn = true;
			}
		} else {
			if (leftOn) {
				sharkad.leftOff();
				leftOn = false;
			}
		}
		if (mSensorX < -2) {
			if (!rightOn) {
				rightOn = true;
				sharkad.rightOn();
			}
		} else {
			if (rightOn) {
				sharkad.rightOff();
				rightOn = false;
			}
		}
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		mSensorTimeStamp = event.timestamp;
	}

	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

}
