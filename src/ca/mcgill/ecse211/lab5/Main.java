package ca.mcgill.ecse211.lab5;
import static ca.mcgill.ecse211.lab5.Resources.*;

import ca.mcgill.ecse211.lab5.Display;
import lejos.hardware.Button;
import lejos.hardware.Sound;

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
//    UltrasonicLocalizer usLocalizer = new UltrasonicLocalizer(); // Create a new ultrasonic localizer
//    Display.showText("< left | right >",
//                     "       |        ",
//                     "Rising | Falling",
//                     " edge  |  edge  ",
//                     "       |        ",
//                     "   Center to    ",
//                     "     cancel     ");
//    int buttonID = Button.waitForAnyPress();
//    new Thread(new Display()).start(); // Start the display thread
//    new Thread(odometer).start(); // Start the odometer thread
//    new Thread(usPoller).start(); // Start the ultrasonic sensor thread
//    try {
//      odometer.join();
//    } catch (InterruptedException e) {
//      e.printStackTrace();
//    }
//    // Localize with the ultrasonic sensor using either a rising edge or a falling edge
//    if (buttonID == Button.ID_LEFT) {
//      usLocalizer.localizeWithRisingEdge();
//    }
//    else {
//      usLocalizer.localizeWithFallingEdge();
//    }
//    Sound.twoBeeps();
//    Button.waitForAnyPress();
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