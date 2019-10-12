package ca.mcgill.ecse211.lab5;

import static ca.mcgill.ecse211.lab5.Resources.*;

import lejos.hardware.Button;
import lejos.hardware.Sound;

public class UltrasonicLocalizer {
  
  /**
   * Constructor. Initializes all values.
   */
  public UltrasonicLocalizer() {
    prevUSDistance = usPoller.getDistance();
    filterControl = 0;
    leftWallAngle = -1;
    backWallAngle = -1;
    initialNoiseMarginAngle= -1;
    finalNoiseMarginAngle = -1;
    minUSDistance = MAX_US_DISTANCE;
  }
  
  /**
   * Distance from the robot to the wall as reported by the ultrasonic sensor.
   */
  private static int usDistance;
  
  /**
   * The previous distance reported by the ultrasonic sensor.
   */
  private static int prevUSDistance;
  
  /**
   * Used to help filter the distance returned by the ultrasonic sensor.
   */
  private static int filterControl;
  
  /**
   * The angle at which the robot found the left wall. 
   */
  private static double leftWallAngle;
  
  /**
   * The angle at which the robot found the back wall.
   */
  private static double backWallAngle;
  
  /**
   * The angle where the signal first crosses into the noise margin.
   */
  private static double initialNoiseMarginAngle;
  
  /**
   * The angle where the signal exits the noise margin.
   */
  private static double finalNoiseMarginAngle;
  
  /**
   * Holds whether or not the program is searching for the left wall or not.
   */
  private static boolean isSearchingForLeftWall;
  
  /**
   * The value of the smallest distance that the ultrasonic sensor reads.
   */
  private static int minUSDistance;
  
  /**
   * Localizes the robot using the ultrasonic sensor. The robot will rotate until it encounters a rising edge, then it
   * will stop, record the angle, then rotate the other direction until it encounters another rising edge. It will then
   * record that second angle, and correct the odometer theta.
   */
  public void localizeWithRisingEdge() {
    isSearchingForLeftWall = true;
    waitForStableUSDistance();
    leftMotor.setSpeed(ROTATE_SPEED);
    rightMotor.setSpeed(ROTATE_SPEED);
    leftMotor.forward();
    rightMotor.backward();
    while (true) {
      filter(usPoller.getDistance()); // Update the usDistance variable with the filtered distance value
      if (usDistance < minUSDistance) {
        minUSDistance = usDistance;
      }
      if (foundRisingEdge()) {
        recordAngle();
      }
      // If both angles have been set, then break
      if (leftWallAngle > 0 && backWallAngle > 0) {
        break;
      }
      // Run the loop at approximately 20 Hz
      try {
        Thread.sleep(50);
      } catch (Exception e) {
      }
    }
    leftMotor.stop(true);
    rightMotor.stop(false);
    // Fix the heading of the robot, then turn to 0 degrees. Wait for the TA to measure the angle, then adjust the
    // position of the robot so it can perform light localization.
    fixHeading();
    Navigation.turnTo(0);
    Button.waitForAnyPress();
    adjustPosition();
  }
  
  /**
   * Localizes the robot using the ultrasonic sensor. The robot will rotate until it encounters a falling edge, then it
   * will stop, record the angle, then rotate the other direction until it encounters another falling edge. It will then
   * record that second angle, and correct the odometer theta.
   */
  public void localizeWithFallingEdge() {
    isSearchingForLeftWall = false;
    waitForStableUSDistance();
    leftMotor.setSpeed(ROTATE_SPEED);
    rightMotor.setSpeed(ROTATE_SPEED);
    leftMotor.forward();
    rightMotor.backward();
    while (true) {
      filter(usPoller.getDistance()); // Update the usDistance variable with the filtered distance value
      if (usDistance < minUSDistance) {
        minUSDistance = usDistance;
      }
      if (foundFallingEdge()) {
        recordAngle();
      }
      // If both angles have been set, then break
      if (leftWallAngle > 0 && backWallAngle > 0) {
        break;
      }
      // Run the loop at approximately 20 Hz
      try {
        Thread.sleep(50);
      } catch (Exception e) {
      }
    }
    leftMotor.stop(true);
    rightMotor.stop(false);
    // Fix the heading of the robot, then turn to 0 degrees. Wait for the TA to measure the angle, then adjust the
    // position of the robot so it can perform light localization.
    fixHeading();
    Navigation.turnTo(0);
    Button.waitForAnyPress();
    adjustPosition();
  }
  
  /**
   * If the distance from the ultrasonic sensor is large enough, and the delta between the current and previous distance
   * is also large enough and positive, it will assume the robot has encountered a rising edge. It will record the
   * entrance angle, as well as the exit angle, and use them to approximate the angle at which the robot senses the
   * rising edge.
   * 
   * @return true if the signal has passed through the threshold plus noise margin
   */
  private static boolean foundRisingEdge() {
    // Once the signal exits the noise margin, capture the angle and let the caller know the edge was found
    if ((usDistance > SIGNAL_THRESHOLD + NOISE_MARGIN) && deltaIsLargeEnough()) {
      finalNoiseMarginAngle = odometer.getXYT()[2];
      if (initialNoiseMarginAngle < 0) {
        initialNoiseMarginAngle = finalNoiseMarginAngle;
      }
      Sound.beep();
      return true;
    }
    // Once the signal enters the noise margin, capture the angle, but let the caller know the edge was not found
    else if ((usDistance > SIGNAL_THRESHOLD - NOISE_MARGIN) && deltaIsLargeEnough()) {
      initialNoiseMarginAngle = odometer.getXYT()[2];
    }
    return false;
  }
  
  /**
   * If the distance from the ultrasonic sensor is small enough, and the delta between the current and previous distance
   * is also large enough and negative, it will assume the robot has encountered a falling edge. It will record the
   * entrance angle, as well as the exit angle, and use them to approximate the angle at which the robot senses the
   * falling edge.
   * 
   * @return true if the signal has passed through the threshold plus noise margin
   */
  private static boolean foundFallingEdge() {
    // Once the signal exits the noise margin, capture the angle and let the caller know the edge was found
    if ((usDistance < SIGNAL_THRESHOLD - NOISE_MARGIN) && deltaIsLargeEnough()) {
      finalNoiseMarginAngle = odometer.getXYT()[2];
      if (initialNoiseMarginAngle < 0) {
        initialNoiseMarginAngle = finalNoiseMarginAngle;
      }
      Sound.beep();
      return true;
    }
    // Once the signal enters the noise margin, capture the angle, but let the caller know the edge was not found
    else if ((usDistance < SIGNAL_THRESHOLD + NOISE_MARGIN) && deltaIsLargeEnough()) {
      initialNoiseMarginAngle = odometer.getXYT()[2];
    }
    return false;
  }
  
  /**
   * Using the threshold for the magnitude of the delta, it lets the caller know if the delta is big enough to count as
   * a rising or falling edge.
   * 
   * @return true if the magnitude of the difference between the current and previous sensor distance is bigger than the
   * threshold
   */
  private static boolean deltaIsLargeEnough() {
    return Math.abs(usDistance - prevUSDistance) >= DELTA_THRESHOLD;
  }
  
  /**
   * Calculates the angle at which the rising or falling edge was detected, and sets it to the appropriate variable.
   */
  private static void recordAngle() {
    leftMotor.stop(true);
    rightMotor.stop(false);
    double avgAngle = (finalNoiseMarginAngle + initialNoiseMarginAngle) / 2.0;
    // Depending on which wall was found, update the corresponding variable and change the isSearchingForLeftWall
    // boolean value.
    if (isSearchingForLeftWall) {
      leftWallAngle = avgAngle;
      isSearchingForLeftWall = false;
    }
    else {
      backWallAngle = avgAngle;
      isSearchingForLeftWall = true;
    }
    // Clear values and make the robot turn the other way
    initialNoiseMarginAngle = -1;
    finalNoiseMarginAngle = -1;
    leftMotor.backward();
    rightMotor.forward();
  }
  
  /**
   * Using the left and back angles, updates the odometer with the corrected theta.
   */
  private static void fixHeading() {
    double deltaTheta;
    if (backWallAngle < leftWallAngle) {
      deltaTheta = 45 - (backWallAngle + leftWallAngle) / 2.0;
    }
    else {
      deltaTheta = 225 - (backWallAngle + leftWallAngle) / 2.0;
    }
    deltaTheta = (deltaTheta + 360) % 360;
    odometer.setTheta(((odometer.getXYT()[2] + deltaTheta - 180) + 360) % 360);
  }
  
  /**
   * Moves the robot to approximately the (0.8,0.8) point so that it can perform the light localization successfully.
   */
  private static void adjustPosition() {
    odometer.setX(minUSDistance + US_SENSOR_RADIUS);
    odometer.setY(minUSDistance + US_SENSOR_RADIUS);
    Navigation.travelTo(0.85 * TILE_SIZE, 0.85 * TILE_SIZE);
    Navigation.turnTo(0);
  }
  
  /**
   * Loops endlessly until the poller begins reporting values other than zero. Without this function, a false positive
   * can be detected.
   */
  private static void waitForStableUSDistance() {
    while (true) {
      filter(usPoller.getDistance());
      if (usDistance > 0) {
        break;
      }
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
  
  /**
   * Filters the distance reported by the ultrasonic sensor. It doesn't allow the distance to surpass MAX_US_DISTANCE,
   * since the maximum range of the sensor is not needed for the localization process.
   * 
   * @param newDistance the distance in cm to be filtered
   */
  private static void filter(int newDistance) {
    if (newDistance >= MAX_US_DISTANCE && filterControl < FILTER_OUT) {
      // bad value, do not set the distance var, however do increment the filter value
      filterControl++; 
    }
    else if (newDistance >= MAX_US_DISTANCE) {
      // Repeated large values, so there is nothing there: set distance to the max distance needed for this lab
      prevUSDistance = usDistance;
      usDistance = MAX_US_DISTANCE;
    }
    else {
      // distance went below the max distance: reset filter and leave distance alone.
      filterControl = 0;
      prevUSDistance = usDistance;
      usDistance = newDistance;
    }
  }
}
