package ca.mcgill.ecse211.lab4;

import static ca.mcgill.ecse211.lab4.Resources.*;
import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;

public class Navigation {
  /**
   * Lets other methods know if the robot is currently navigating to a waypoint.
   */
  private static boolean isNavigating;
  
  /**
   * A filter for the ultrasonic sensor to ensure an object has actually been seen.
   */
  private int usFilter;
  
  /**
   * An array containing the current X, Y, and theta of the robot, as given by the odometer.
   */
  private static double [] position;
  /**
   * Vector in the x direction from the robots current position to the waypoint.
   */
  private static double vectorX;
  
  /**
   * Vector in the y direction from the robots current position to the waypoint.
   */
  private static double vectorY;
  
  /**
   * The angle the robot needs to go to so that it is a straight line to the waypoint.
   */
  private static double heading;
  
  /**
   * Constructor for the Navigation class.
   */
  public Navigation() {
    isNavigating = false;
    leftMotor.setAcceleration(ACCELERATION);
    rightMotor.setAcceleration(ACCELERATION);
  }
  
  /**
   * The main method used to travel to a waypoint. The method will loop at approximately 20 Hz and make sure the robot
   * is on the correct path towards the waypoint. It will call turnTo() if the robot needs to make a change in heading.
   * It will also call avoidObject() if the robot is about to run into an obstacle.
   * 
   * @param x the X coordinate of the waypoint
   * @param y the Y coordinate of the waypoint
   */
  public static void travelTo(double x, double y) {
    
    position = odometer.getXYT();
    vectorX = x - position[0];
    vectorY = y - position[1];

    while(distance(vectorX, vectorY) > WPOINT_RAD) {
      position = odometer.getXYT(); // Get position of the robot from the odometer
      // Update the vectors from the current position to the waypoint
      vectorX = x - position[0];
      vectorY = y - position[1];
      // Update the heading, and ensure it stays between 0 and 360 degrees
      heading = Math.toDegrees(Math.atan2(vectorX, vectorY));
      heading = (heading + 360) % 360;
      LCD.drawString("Heading: " + Double.toString(heading), 0, 3);
      // If the robot isn't too close to the waypoint, allow it to correct its heading by rotating
      if (distance(vectorX, vectorY) > (2*WPOINT_RAD)) {
        turnTo(heading);
      }

      leftMotor.setSpeed(FORWARD_SPEED);
      rightMotor.setSpeed(FORWARD_SPEED);
      leftMotor.forward();
      rightMotor.forward();
      try {
        Thread.sleep(50);
      } catch (Exception e) {
      }

    }
    leftMotor.stop(true);
    rightMotor.stop(false);
    Sound.twoBeeps(); // Beep when it has reached a waypoint
  }
  
  /**
   * Rotates the robot to an absolute angle theta. It also ensures the robot turns the minimal angle to
   * get to theta.
   * 
   * @param theta the absolute angle the robot should turn to in degrees
   */
  public static void turnTo(double theta) {
    double angleDiff = theta - odometer.getXYT()[2];
    // Don't correct the angle if it is within a certain threshold
    if (Math.abs(angleDiff) < 3.0 || Math.abs(angleDiff) > 357.0) {
      return;
    }
    leftMotor.setSpeed(ROTATE_SPEED);
    rightMotor.setSpeed(ROTATE_SPEED);
    // This ensures the robot uses the minimal angle when turning to theta
    if (Math.abs(angleDiff) > 180.0) {
      angleDiff = Math.signum(angleDiff) * 360.0 - angleDiff;
      leftMotor.rotate(convertAngle(-angleDiff), true);
      rightMotor.rotate(convertAngle(angleDiff), false);
    }
    else {
      leftMotor.rotate(convertAngle(angleDiff), true);
      rightMotor.rotate(convertAngle(-angleDiff), false);
    }
  }
  
  /**
   * Returns a boolean of whether or not the robot is currently navigating to a waypoint.
   * @return true if the robot is currently navigating to a waypoint.
   */
  public boolean isNavigating() {
    return isNavigating;
  }
  
  /**
   * Converts input distance to the total rotation of each wheel needed to cover that distance.
   * 
   * @param distance
   * @return the wheel rotations necessary to cover the distance
   */
  public static int convertDistance(double distance) {
    return (int) ((180 * distance) / (Math.PI * WHEEL_RAD));
  }
  
  /**
   * Converts input angle to the total rotation of each wheel needed to rotate the robot by that
   * angle.
   * 
   * @param angle
   * @return the wheel rotations necessary to rotate the robot by the angle
   */
  public static int convertAngle(double angle) {
    return convertDistance((Math.PI * TRACK * angle) / 360.0);
  }
  
  /**
   * Calculates the euclidian distance given an X and Y distance, in cm.
   * 
   * @param deltaX X distance
   * @param deltaY Y distance
   * @return Euclidean distance
   */
  private static double distance(double deltaX, double deltaY) {
    return Math.sqrt((Math.pow((deltaX), 2) + Math.pow((deltaY), 2)));
  }
  
}
