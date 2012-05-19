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

public class BlimpControlAndroidActivity extends Activity implements
		SensorEventListener {

	private SensorManager mSensorManager;
	private PowerManager mPowerManager;
	private WindowManager mWindowManager;
	private Display mDisplay;
	private float mSensorX, mSensorY;
	private long mSensorTimeStamp;
	private Sensor mAccelerometer;
	private WakeLock mWakeLock;
	private boolean moving = false, left = false, straight = false,
			right = false, DOWN = false, UP = false;

	interface Sharkad extends BridgeRemoteObject {
		public void forward(int speed, int dur);

		public void left(int speed, int dur);

		public void right(int speed, int dur);

		public void noseUp();

		public void noseDown();

		public void stop();
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
		Button up = (Button) findViewById(R.id.up);
		Button down = (Button) findViewById(R.id.down);

		accel.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View arg0, MotionEvent arg1) {
				moving = true;
				System.out.println("MOVING");
				return false;
			}

		});
		accel.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				moving = false;
				sharkad.stop();
				left = false;
				right = false;
				straight = false;

			}

		});
		up.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (!UP) {
					sharkad.noseUp();
					UP = true;
				}
				return false;
			}

		});
		up.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				sharkad.stop();
				UP = false;
			}

		});
		down.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (!DOWN) {
					sharkad.noseDown();
					DOWN = true;
				}
				return false;
			}

		});
		down.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				sharkad.stop();
				DOWN = false;
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
		if (moving) {
			if (mSensorX < -2) {
				if (!left) {
					sharkad.stop();
					sharkad.left(1, -1);
					left = true;
					right = false;
					straight = false;
				}
			} else if (mSensorX > 2) {
				if (!right) {
					sharkad.stop();
					sharkad.right(1, -1);
					right = true;
					left = false;
					straight = false;
				}
			} else {
				if (!straight) {
					sharkad.stop();
					sharkad.forward(1, -1);
					straight = true;
					right = false;
					left = false;
				}

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
