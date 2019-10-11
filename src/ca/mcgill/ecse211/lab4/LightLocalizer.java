package ca.mcgill.ecse211.lab4;

import static ca.mcgill.ecse211.lab4.Resources.*;
import ca.mcgill.ecse211.lab4.Main;
import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;

public class LightLocalizer {
  
  private static final long CORRECTION_PERIOD = 50;

  /**
   * The current intensity value. It gets updated every time the color sensor poller gets queried.
   */
  private static float currIntensity;
  
  /**
   * The intensity value of the previous cycle.
   */
  private static float prevIntensity;
  private static float midIntensity;
  
  /**
   * A filter for detecting the lines. When a difference is detected, this number is incremented. It then prevents the
   * robot from detecting another line for another 20 cycles.
   */
  private static int colorFilter = 0;
  
  /**
   * The array used to hold the theta values of the odometer.
   * These are used in calculating the position and angle of the robot
   */
  private static double[] angles = {0,0,0,0,0};
  
  /**
   * A counter for the number of lines detected. Begins at -1 to account for the initialization of the sensor.
   */
  private static int numLinesDetected = -1;
  
  /**
   * Class variables used to store the values of the corrected position and angle of the robot.
   */
  private static double localizedX, localizedY, localizedTheta, thetaError;
  
  /**
   * Stores the value of the current odometer readings.
   */
  private static double[] currXYT = odometer.getXYT(); // Update current position and orientation
  
  /**
   * When called upon, this method rotates the robot until five lines are detected, then calculates the position and
   * angle of the robot relative to 0 degrees.
   * Once this is complete, this method drives the robot to 1,1 and turns it towards 0 degrees.
   */
  public static void Localize() {
    LCD.clear();
    long correctionStart, correctionEnd; // Used for ensuring the light sensor is polled once per time interval
    SampleProvider colorSensorStatus = colorSensor.getRedMode(); // Creates sample provider for the color sensor
    float[] sampleColor = new float[colorSensorStatus.sampleSize()]; // Allocates a buffer for the poller
    try{
      Thread.sleep((long) 1000.0);
    }
    catch (InterruptedException e) {
    }
    //Spin the robot
    rightMotor.setSpeed(ROTATE_SPEED);
    leftMotor.setSpeed(ROTATE_SPEED);
    leftMotor.backward();
    rightMotor.forward();
    
    // Rotates the robot until 5 lines are detected, while recording the angle of the robot at each line
    while (numLinesDetected < 5) {
      correctionStart = System.currentTimeMillis();
      currXYT = odometer.getXYT(); // Update current position and orientation
      colorSensorStatus.fetchSample(sampleColor, 0); // Grab the current intensity from the sensor
      currIntensity = sampleColor[0];
      
      // Display the current values
      LCD.drawString("Now: " + Float.toString(currIntensity), 0, 3);
      LCD.drawString("Prev " + Float.toString(prevIntensity), 0, 4);
      LCD.drawString("Num Lines: " + numLinesDetected, 0, 5);

      // Case for the initialization of the robot. Ensures that this measurement is discarded,
      // as it does not correspond to a line
      if (numLinesDetected == -1 && currIntensity - prevIntensity != 0 && colorFilter == 0) {
        numLinesDetected++;
        colorFilter++;
      }
      
      // If the difference in intensity is big enough, and the filter is 0, then a line has been detected
      if (currIntensity - prevIntensity >= INTENSITY_THRESHOLD && colorFilter == 0) {
        angles[numLinesDetected] = currXYT[2];
        numLinesDetected++;
        colorFilter++;
        Sound.beep();

      }
      // Logic to properly increment and reset the filter
      else {
        if (colorFilter > 0) {
          colorFilter++;
          if (colorFilter > 10) {
            colorFilter = 0;
          }
        }
      }
      
      prevIntensity = midIntensity; // Keep track of the last intensity value
      midIntensity = currIntensity; // Keep track of the last intensity value
      
      
      // This ensures the odometry correction occurs only once every period
      correctionEnd = System.currentTimeMillis();
      if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
        Main.sleepFor(CORRECTION_PERIOD - (correctionEnd - correctionStart));
      }
    }
    
    // Stop motors as we have detected all 5 lines
    leftMotor.stop(true);
    rightMotor.stop(false);
    
    // Calculations of X and Y and theta values. X and Y are calculated by the method used in the localization tutorial
    localizedX = -SENSOR_RADIUS * Math.cos(Math.toRadians(Math.abs((angles[2]-angles[0])/2)));
    localizedY = -SENSOR_RADIUS * Math.cos(Math.toRadians(Math.abs((angles[1]-angles[3])/2)));
    // The difference between the robot's current angle and 0 degrees is given by the inverse sin
    // of the x position divided by the distance from the centre of rotation to the sensor, this value is negative
    thetaError = Math.toDegrees(Math.asin(localizedX / SENSOR_RADIUS));
    localizedTheta = (thetaError + 360); // Add 360 to this value to get the current heading of the robot
    
    localizedX += TILE_SIZE; // Correct the values from relative to 1,1 to be relative to 0,0 for movement to 1,1
    localizedY += TILE_SIZE;
    
    odometer.setX(localizedX);
    odometer.setY(localizedY);
    odometer.setTheta(localizedTheta);
    
    // Drive the robot to the destination and turn it to 0 degrees.
    Navigation.travelTo(TILE_SIZE,TILE_SIZE);
    Navigation.turnTo(0);
  }
}