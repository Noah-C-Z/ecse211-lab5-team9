package ca.mcgill.ecse211.lab5;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;

/**
 * This class is used to define static resources in one place for easy access and to avoid cluttering the rest of the
 * codebase. All resources can be imported at once like this:
 * 
 * <p>
 * {@code import static ca.mcgill.ecse211.lab3.Resources.*;}
 */
public class Resources {

  /**
   * The threshold for when the signal is considered to have triggered a rising or falling edge.
   */
  public static final int SIGNAL_THRESHOLD = 50;
  
  /**
   * The threshold in intensity delta for determining when a line has been detected
   */
  public static final double INTENSITY_THRESHOLD = 0.05;
  
  /**
   * The threshold for the delta between the current and previous value of the ultrasonic sensor. Used to detect rising
   * and falling edges.
   */
  public static final int DELTA_THRESHOLD = 7;
  
  /**
   * The margin around NOISE_THRESHOLD to account for any noise in the signal.
   */
  public static final int NOISE_MARGIN = 3;
  
  /**
   * Distance to waypoint threshold in centimeters
   */
  public static final double WPOINT_RAD = 1;
  
  /**
   * Distance to light sensor from center of rotation in centimeters
   */
  public static final double SENSOR_RADIUS = 12;
  
  /**
   * The wheel radius in centimeters.
   */
  public static final double WHEEL_RAD = 2.20;

  /**
   * The robot width in centimeters.
   */
  public static final double TRACK = 17.3;

  /**
   * The speed at which the robot moves forward in degrees per second.
   */
  public static final int FORWARD_SPEED = 120;

  /**
   * The speed at which the robot rotates in degrees per second.
   */
  public static final int ROTATE_SPEED = 60;

  /**
   * The motor acceleration in degrees per second squared.
   */
  public static final int ACCELERATION = 1000;

  /**
   * Timeout period in milliseconds.
   */
  public static final int TIMEOUT_PERIOD = 3000;

  /**
   * The tile size in centimeters.
   */
  public static final double TILE_SIZE = 30.48;

  /**
   * Filter for the distance reported by the ultrasonic sensor.
   */
  public static final double FILTER_OUT = 10;

  /**
   * The furthest the ultrasonic distance can effectively see.
   */
  public static final int MAX_US_DISTANCE = 100;
  
  /**
   * The distance from the front of the ultrasonic sensor to the wheelbase.
   */
  public static final int US_SENSOR_RADIUS = 5;
  
  /**
   * The left motor.
   */
  public static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));

  /**
   * The right motor.
   */
  public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));

  /**
   * The LCD.
   */
  public static final TextLCD LCD = LocalEV3.get().getTextLCD();

  /**
   * The odometer.
   */
  public static Odometer odometer = Odometer.getOdometer();

  /**
   * The ultrasonic sensor.
   */
  public static final EV3UltrasonicSensor US_SENSOR = new EV3UltrasonicSensor(LocalEV3.get().getPort("S2"));
  
  /**
   * The ultrasonic poller.
   */
  public static UltrasonicPoller usPoller = new UltrasonicPoller();
  
  /**
   * The color sensor.
   */
  public static final EV3ColorSensor colorSensor = new EV3ColorSensor(SensorPort.S1);
  
  /**
   * The dimensions sizes in squares
   */
  public static final int ARENA_X = 8;
  public static final int ARENA_Y = 8;
}