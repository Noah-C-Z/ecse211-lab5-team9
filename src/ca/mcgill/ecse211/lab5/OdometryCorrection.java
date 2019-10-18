package ca.mcgill.ecse211.lab5;

import lejos.hardware.Sound;

public class OdometryCorrection implements Runnable{
	public enum WorkingState {
		HORIZONTAL, VERTICAL
	}
	public WorkingState state = WorkingState.VERTICAL;	
	
	public void run(){
		while (true) {
			switch (state) {
			case HORIZONTAL:
				if (ColorReader.detectBlackLine()) {
					Sound.beep();
					Odometer.getOdometer().setX(RobotDriver.x*Resources.TILE_SIZE+Resources.SENSOR_RADIUS);
					break;
				}
			case VERTICAL:
				if (ColorReader.detectBlackLine()) {
					Sound.beep();
					Odometer.getOdometer().setY(RobotDriver.y*Resources.TILE_SIZE+Resources.SENSOR_RADIUS);
					break;
				}
			}
		}
	}
}
