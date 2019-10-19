package ca.mcgill.ecse211.lab5;

import static ca.mcgill.ecse211.lab5.Resources.colorReader;
import static ca.mcgill.ecse211.lab5.Resources.odometryCorrection;
import static ca.mcgill.ecse211.lab5.Resources.usLocalizer;
import static ca.mcgill.ecse211.lab5.Resources.usPoller;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * The main driver class for the odometry lab.
 */
public class Main {
  public static final int TARGETX = 0;
  public static final int TARGETY = 0;


  /**
   * The main entry point.
   * 
   * @param args
   */
  public static void main(String[] args) {
    // Phase 1
    Thread a = new Thread(usPoller);
    Thread b = new Thread(usLocalizer);
    Thread c = new Thread(new UltrasonicLocalizerDisplay());
    a.start();
    b.start();
    c.start();
    Button.waitForAnyPress();

    // Phase 2
    UltrasonicPoller.kill = true;
    UltrasonicLocalizerDisplay.kill = true;

    try {
      a.join(5000);
      b.join(5000);
      c.join(5000);
    } catch (InterruptedException e1) {
      e1.printStackTrace();
    }


    Thread d = new Thread(colorReader);
    Thread e = new Thread(odometryCorrection);
    d.start();
    e.start();

    // Navigate
    int[] destination = Navigation.findTarget(TARGETX, TARGETY);
    Navigation.moveForwardByTile(destination[1]);
    Navigation.turnRight();
    Navigation.moveForwardByTile(destination[0]);
    Navigation.turnTo(destination[2]);
    if (destination[2]%90 > 0) {
    	Navigation.moveForwardByTile(0.2);
    }
    Button.waitForAnyPress();

    // shoot
    final EV3LargeRegulatedMotor shooterMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
    
    shooterMotor.rotate(-190);
    Sound.twoBeeps();
    shooterMotor.rotate(190);
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
