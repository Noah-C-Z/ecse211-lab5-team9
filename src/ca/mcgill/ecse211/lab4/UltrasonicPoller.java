package ca.mcgill.ecse211.lab4;

import static ca.mcgill.ecse211.lab4.Resources.*;

/**
 * A poller for the ultrasonic sensor. It runs continuously in its own thread, polling the sensor about every 50 ms.
 * After getting a value from the sensor, it will convert the distance into centimeters and assign it to the distance
 * variable. This variable can be accessed by other classes by calling getDistance().
 */
public class UltrasonicPoller extends Thread {

  private float[] usData;
  private volatile int distance;

  public UltrasonicPoller() {
    usData = new float[US_SENSOR.sampleSize()];
    // controller = Main.selectedController;
  }

  /*
   * Sensors now return floats using a uniform protocol. Need to convert US result to an integer [0,255] (non-Javadoc)
   * 
   * @see java.lang.Thread#run()
   */
  public void run() {
    while (true) {
      US_SENSOR.getDistanceMode().fetchSample(usData, 0); // acquire distance data in meters
      distance = (int) (usData[0] * 100.0); // extract from buffer, convert to cm, cast to int, and update distance
      LCD.drawString("usDistance: " + Double.toString(distance), 0, 6);
      try {
        Thread.sleep(50);
      } catch (Exception e) {
      } // Poor man's timed sampling
    }
  }
  
  /**
   * @return distance sensed by the ultrasonic sensor.
   */
  public int getDistance() {
    return distance;
  }

}
