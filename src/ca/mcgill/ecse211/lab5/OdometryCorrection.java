package ca.mcgill.ecse211.lab5;

import ca.mcgill.ecse211.lab5.ColorReader;
import lejos.hardware.Sound;

public class OdometryCorrection implements Runnable{
	public enum WorkingState {
		HORIZONTAL, VERTICAL
	}
	public WorkingState state = WorkingState.VERTICAL;	
	private static final double ODOMETRY_CORRECTION = 4.5;
	
	public void run(){
		while (true) {
			switch (state) {
			case HORIZONTAL:
				if (ColorReader.detectBlackLine()) {
					Sound.beep();
					Odometer.getOdometer().setX(RobotDriver.x*Resources.TILE_SIZE+ODOMETRY_CORRECTION);
					break;
				}
			case VERTICAL:
				if (ColorReader.detectBlackLine()) {
					Sound.beep();
					Odometer.getOdometer().setY(RobotDriver.y*Resources.TILE_SIZE+ODOMETRY_CORRECTION);
					break;
				}
			}
		}
	}
}
