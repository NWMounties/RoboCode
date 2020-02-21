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
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
//import sun.awt.image.ImageFetchable;
//import edu.wpi.first.wpilibj.util.Color;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Alpha extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  //private final I2C.Port i2cPort = I2C.Port.kOnboard;
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();
  boolean isCamera = false;
  //private SPILink spi;
  int state=-1;
  
  Joystick stick0;
  Joystick stick1;
  XboxController controller;

  Ultrasonic ultrasonic = new Ultrasonic(0, 1);

  Spark frontLeft;
  Spark rearLeft;
  Spark frontRight;
  Spark rearRight;

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
  public void robotInit() {
    Shuffleboard.getTab("Test").add(ultrasonic);
    
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    

    UsbCamera cam = CameraServer.getInstance().startAutomaticCapture();
    cam.setResolution(160, 120);
    cam.setFPS(30);

    gyro1 = new ADXRS450_Gyro(SPI.Port.kOnboardCS0);
    gyro1.reset();
    SmartDashboard.putData("Gyro", gyro1);
    //angle_


    stick0 = new Joystick(0);
    stick1 = new Joystick(1);

    frontLeft = new Spark(6);
    rearLeft = new Spark(5);
    frontRight = new Spark(4);
    rearRight = new Spark(3);

    lights = new Spark(9);

    controller = new XboxController(2);

    //Claw = new Spark(0);

    LeftMotor = new Spark(1);
    RightMotor = new Spark(0);
    Turning = new Spark(7);

    directButton = 1;

    ultrasonic.setAutomaticMode(true);

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

    Timer.delay(2);
    //motorPowers(0.2, 0.2);
    
    //Turning.set(0.3); // allign wheels to center
    //Timer.delay(0.95);
    //Turning.set(0);
    
    motorPowers(0, 0);
    Turning.set(0);
    Timer.delay(3);

    //Turning.set(-0.2);
    motorPowers(0.5, 0.5); // drive to corner
    Timer.delay(2.9);

    motorPowers(0, 0); // stop the motors
    Timer.delay(2);

    Turning.set(0.3); // turn the wheels to the right
    Timer.delay(1.2);
    Turning.set(0);

    motorPowers(0.5, 0.5); // move around the corner
    Timer.delay(3.5);

    Turning.set(-0.2);// straighten the wheels
    Timer.delay(1);
    motorPowers(0, 0); // stop motors
    Timer.delay(0.6);
    Turning.set(0);
    
    //motorPowers(0, 0); // stop at the end
    //Timer.delay(5);
    
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {

    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        //turn(90);
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
        
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
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    watch(stick0, stick1);
    watchX(controller);

  }
  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
    watch(stick0, stick1);
    watchX(controller);
    //turn(90);
  }

  private void watch(Joystick stickA , Joystick stickB) {
    directButton = changeDirection(stickB.getRawButton(4));
    double yspeedA = (stickA.getY() * -1) * directButton;
    double yspeedB = stickB.getY() * directButton;
    // power is the control for motor power
    // change this value
    double power = ((-1*stickA.getThrottle() * 0.5) + 0.5 );
    double power2 = ((-1*stickB.getThrottle() * 0.5) + 0.5 );
    

    // strafe left
    // Paste this line after each set when switching back to Talons: ControlMode.PercentOutput, 
    if (stickA.getTrigger()){
      //System.out.println("I am strafing left !" + power2);
      rearRight.set(power2);
      frontRight.set(-power2);
      rearLeft.set(power2);
      frontLeft.set(-power2);
    } else if (stickB.getTrigger()) {
        //System.out.println("I am strafing right!" + power2);
        rearRight.set(-power2);
        frontRight.set(power2);
        rearLeft.set(-power2);
        frontLeft.set(power2);
    } else {
        //System.out.println("Driving Mode: "+ power);
        if (Math.abs(yspeedA) - 0.2 <= 0) {
          frontLeft.set(0);
          rearLeft.set(0);
          frontRight.set(yspeedB * power);
          rearRight.set(yspeedB * power);
        }
        else if (Math.abs(yspeedB) - 0.3 <= 0) {
          frontRight.set(0);
          rearRight.set(0);
          frontLeft.set(yspeedA * power);
          rearLeft.set(yspeedA * power);
        }
        else if ((Math.abs(yspeedA) - 0.2 <= 0) && (Math.abs(yspeedB) - 0.3 <= 0)) {
          frontRight.set(0);
          rearRight.set(0);
          frontLeft.set(0);
          rearLeft.set(0);
        } else {
          frontRight.set(yspeedB * power);
          rearRight.set(yspeedB * power);
          frontLeft.set(yspeedA * power);
          rearLeft.set(yspeedA * power);
        }
    }
  }
  private int changeDirection(boolean button) {
    if (button) {
      directButton = directButton * -1;
    }
    return directButton;
  }
  private void watchX(XboxController controller) {
    if (Math.abs(controller.getY(Hand.kLeft)) > 0.1) {
      //System.out.println("Left-hand stick is moving.");
      longArmrotator();
    } else if (controller.getYButton()) {
      //System.out.println("Y Button pressed.");
      shortArmRotator(1);
    } else if (controller.getAButton()) {
      //System.out.println("A Button pressed.");
      shortArmRotator(-1);
    } else if (controller.getXButton()) {
      //System.out.println("X Button pressed.");
      openClaw();
    } else if (controller.getBButton()) {
      //System.out.println("B Button pressed.");
      closeClaw();
    }
    else{
      //shortRotator.set(ControlMode.PercentOutput,0.1);
      Claw.set(0);
    }
    //lights.set(0.73 + (controller.getY(Hand.kRight) / 5));// 0.57 -- 0.89
  }
  private void longArmrotator() {
    //System.out.println("Rotating long arm.");
  }
  private void shortArmRotator(double power) {
    //System.out.println("Rotating short arm.");
    //shortRotator.set(ControlMode.PercentOutput, power);
  }
  private void openClaw() {
    //System.out.println("I am openning the claw.");
    Claw.set(0.5);    
  }
  
  private void closeClaw() {
    //System.out.println("I am closing the claw.");
    Claw.set(-0.5);
  }
  private void motorPowers(double motor1, double motor2) {
    RightMotor.set(-motor1);
    LeftMotor.set(-motor2);

  }
  //private void lift() {

    //wheel.set(ControlMode.PercentOutput, controller.getY(Hand.kLeft) * 0.5);
    
  //}

  /*private boolean turn(double direction) {
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
