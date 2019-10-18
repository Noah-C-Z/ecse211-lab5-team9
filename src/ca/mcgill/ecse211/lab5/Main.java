package ca.mcgill.ecse211.lab5;

import static ca.mcgill.ecse211.lab5.Resources.*;
import lejos.hardware.Button;

/**
 * The main driver class for the odometry lab.
 */
public class Main {

	/**
	 * The main entry point.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Thread a = new Thread(usPoller);
	    Thread b = new Thread(usLocalizer);
	    a.start();
	    b.start();
	    new Thread(new AlignmentDriverDisplay()).start();
	    Button.waitForAnyPress();
	    UltrasonicPoller.kill = true;
	  //Navigation.travelTo(TILE_SIZE/2, 22.0);
		new Thread (colorReader).start();
		new Thread (odometryCorrection).start();
		Button.waitForAnyPress();
		RobotDriver.getReadyToShoot(2, 6);
		LightLocalizer.Localize(); // Perform light localization
		Button.waitForAnyPress();
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