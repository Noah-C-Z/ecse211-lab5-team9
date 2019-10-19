package ca.mcgill.ecse211.lab5;

import static ca.mcgill.ecse211.lab5.Resources.colorReader;
import static ca.mcgill.ecse211.lab5.Resources.odometryCorrection;
import static ca.mcgill.ecse211.lab5.Resources.usLocalizer;
import static ca.mcgill.ecse211.lab5.Resources.usPoller;

import javafx.scene.control.Button;

/**
 * The main driver class for the odometry lab.
 */
public class Main {
	public static final int TARGETX = 2;
	public static final int TARGETY = 6;
	

	/**
	 * The main entry point.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		//Phase 1
		Thread a = new Thread(usPoller);
	    Thread b = new Thread(usLocalizer);
	    Thread c = new Thread(new UltrasonicLocalizerDisplay());
	    a.start();
		b.start();
		c.start();
		Button.waitForAnyPress();
		
		//Phase 2
		UltrasonicPoller.kill = true;
		UltrasonicLocalizerDisplay.kill = true;

		a.join(5000);
		b.join(5000);
		c.join(5000);

		
		Thread d =new Thread (colorReader);
		Thread e = new Thread (odometryCorrection);
		d.start();
		e.start();

		// Navigate
		int [] destination = Navigation.findTarget(TARGETX, TARGETY);
		Navigation.moveForwardByTile(destination[1]);
		Navigation.turnRight();
		Navigation.moveForwardByTile(destination[0]);
		Navigation.turnTo(destination[2]);
		Button.waitForAnyPress();

		
		RobotDriver.getReadyToShoot(2, 6);
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