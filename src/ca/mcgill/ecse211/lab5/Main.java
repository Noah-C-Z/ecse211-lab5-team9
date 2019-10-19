package ca.mcgill.ecse211.lab5;

import static ca.mcgill.ecse211.lab5.Resources.usLocalizer;
import static ca.mcgill.ecse211.lab5.Resources.usPoller;
import static ca.mcgill.ecse211.lab5.Resources.shooterMotor;
import lejos.hardware.Button;
import lejos.hardware.Sound;

/**
 * The main driver class for the odometry lab.
 */
public class Main {
	public static final int TARGETX = 1;
	public static final int TARGETY = 1;

	/**
	 * The main entry point.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// Phase 1: localize using US sensor and rammer
		Thread pollerThread = new Thread(usPoller);
		Thread localizerThread = new Thread(usLocalizer);
		Thread localizerDisplayThread = new Thread(new UltrasonicLocalizerDisplay());
		pollerThread.start();
		localizerThread.start();
		localizerDisplayThread.start();
		Button.waitForAnyPress();
		// Phase 2: Navigate to position and take aim
		UltrasonicPoller.kill = true;
		UltrasonicLocalizerDisplay.kill = true;

		try {
			pollerThread.join(5000);
			localizerThread.join(5000);
			localizerDisplayThread.join(5000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		// Navigate
		int[] destination = Navigation.findTarget(TARGETX, TARGETY);
		Navigation.moveForwardByTile(destination[1]);
		Navigation.turnRight();
		Navigation.moveForwardByTile(destination[0]);
		Navigation.turnTo(destination[2]);
		if (destination[2] % 90 > 0) {
			Navigation.moveForwardByTile(0.4);
		}

		// Phase 3: shoot the ball
		shooterMotor.setSpeed(150);
		int shots = 0;
		while (shots < 5) {
			shooterMotor.rotate(-190);
			Sound.twoBeeps();
			shooterMotor.rotate(225);
			Button.waitForAnyPress();
			shots++;
		}

		System.exit(0);
	}

	public static void sleepFor(long duration) {
		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			// There is nothing to be done here
		}
	}
}
