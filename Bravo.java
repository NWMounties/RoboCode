/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.cscore.UsbCamera;
//import java.util.ArrayList;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
//import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.SerialPort;
//import sun.awt.image.ImageFetchable;
//import edu.wpi.first.wpilibj.util.Color;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Bravo extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  //private final I2C.Port i2cPort = I2C.Port.kOnboard;
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();
  boolean isCamera = false;
  //private SPILink spi;
  int state=-1;
  private SerialPort arduino;
private Timer timer;
  
  Joystick stick0;
  Joystick stick1;
  XboxController controller;

  Ultrasonic ultrasonic1 = new Ultrasonic(0, 1); // ping, echo
  Ultrasonic ultrasonic2 = new Ultrasonic(2, 3); // ping, echo
  //0=ping 1=echo

  Spark lights;
  Spark Claw;
  Spark RightMotor;
  Spark LeftMotor;
  Spark Turning;

  int directButton;

  ADXRS450_Gyro gyro1;
  double angle_from_start;
  

  
  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {try{
    arduino = new SerialPort(9600, SerialPort.Port.kUSB);
    System.out.println("Connected on kUSB");
  } catch (Exception e) {
    System.out.println("Failed to connect on kUSB, trying kUSB 1");

    try {
      arduino = new SerialPort(9600, SerialPort.Port.kUSB1);
      System.out.println("Connected to kUSB1");
    } catch (Exception e1) {
      System.out.println("Failed to connect to kUSB1, trying kUSB2");

      try {
        arduino = new SerialPort(9600, SerialPort.Port.kUSB2);
        System.out.println("Connected to kUSB2");
      } catch (Exception e2) {
        System.out.println("Failed to connect to kUSB2, all connection attempts failed");
      }
    }
  }

  timer = new Timer();
  timer.start();
    
    Shuffleboard.getTab("Test").add(ultrasonic1);
    Shuffleboard.getTab("Test").add(ultrasonic2);
    
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    

    UsbCamera cam = CameraServer.getInstance().startAutomaticCapture();
    cam.setResolution(160, 120);
    cam.setFPS(30);

    gyro1 = new ADXRS450_Gyro(SPI.Port.kOnboardCS0);
    gyro1.reset();
    SmartDashboard.putData("Gyro", gyro1);

    stick0 = new Joystick(0);
    stick1 = new Joystick(1);

    lights = new Spark(9);

    controller = new XboxController(2);

    //Claw = new Spark(0);

    LeftMotor = new Spark(1);
    RightMotor = new Spark(0);
    Turning = new Spark(7);

    directButton = 1;

    ultrasonic1.setAutomaticMode(true);
    ultrasonic2.setAutomaticMode(true);

    lights.set(0.71);
    
    
  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    if (ultrasonic1.getRangeInches() < 20) {
      System.out.println("Wrote to Arduino");
      arduino.write(new byte[] {0x12}, 1);
      timer.reset();
    }
    else{
      arduino.write(new byte[] {0x13}, 1); 
    }

    if (arduino.getBytesReceived() >0) {
      //System.out.println("Hello World");
      System.out.println(arduino.readString());
    }
  
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to
   * the switch structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);

    Timer.delay(5);

    double distance = 0;

    double time = 20.5;

    while (distance < time * 10) {
      motorPowers(0.3, 0.3);
      if (ultrasonic1.getRangeInches() < 20) {
        motorPowers(0, 0);
        while (ultrasonic1.getRangeInches() < 20) {
          Timer.delay(0.1);
        }
        Timer.delay(2);
        distance -= 5;
      }
      Timer.delay(0.1);
      distance++;
    }
    motorPowers(0, 0);
    
  }
 

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    /*
      if (ultrasonic1.getRangeInches() > 20) {
        motorPowers(0.3, 0.3);
      } else {
        motorPowers(0, 0);
      }
      */
  }

    /*Timer.delay(1);
    RightMotor.set(-0.5);
    LeftMotor.set(-0.5);
    
    Timer.delay(3);
    RightMotor.set(0);
    LeftMotor.set(0);
    Timer.delay(3);/*


    /*if (ultrasonic.getRangeInches() > 12) {
      RightMotor.set(-stick0.getThrottle());
      LeftMotor.set(-stick0.getThrottle());
    } else {
      RightMotor.set(0);
      LeftMotor.set(0);
    }*/
    
    //watch(stick0, stick1);
    //watchX(controller);
  //}

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    gyro1.reset();
  }
  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {

    /*double angle = 0;
    if (gyro1.getAngle() > angle + 1) {
      Turning.set(-0.2);
    } else if (gyro1.getAngle() < angle - 1) {
      Turning.set(0.2);
    } else {
      Turning.set(0);
    }

    motorPowers(0.4, 0.4);*/
  }

  private void motorPowers(double motor1, double motor2) {
    RightMotor.set(-motor1);
    LeftMotor.set(-motor2);

  }
  //private void lift() {

    //wheel.set(ControlMode.PercentOutput, controller.getY(Hand.kLeft) * 0.5);
    
  //}
    /*
  private boolean turn(double direction) {
    double rotation = gyro1.getAngle();
    double rotationSpeed = 0.5;
    while ((rotation - direction) < -0.1) {

      rotation = gyro1.getAngle();
      frontRight.set(rotationSpeed);
      rearRight.set(rotationSpeed);
      System.out.println("hi");
      return true;
    }
    while ((rotation - direction) < 0.1) {

      rotation = gyro1.getAngle();
      frontLeft.set(rotationSpeed);
      System.out.println(rotation);
      return true;
    }

    return true;
  }*/
  

    
}
