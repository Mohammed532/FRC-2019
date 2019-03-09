/*---------------------------------------------------------------------------------------*/                                                                            
/*            66666666       222222222222222         1111111       333333333333333       */
/*           6::::::6       2:::::::::::::::22      1::::::1      3:::::::::::::::33     */
/*          6::::::6        2::::::222222:::::2    1:::::::1      3::::::33333::::::3    */
/*         6::::::6         2222222     2:::::2    111:::::1      3333333     3:::::3    */
/*        6::::::6                      2:::::2       1::::1                  3:::::3    */
/*       6::::::6                       2:::::2       1::::1                  3:::::3    */
/*      6::::::6                     2222::::2        1::::1          33333333:::::3     */
/*     6::::::::66666           22222::::::22         1::::l          3:::::::::::3      */
/*    6::::::::::::::66       22::::::::222           1::::l          33333333:::::3     */
/*    6::::::66666:::::6     2:::::22222              1::::l                  3:::::3    */
/*    6:::::6     6:::::6   2:::::2                   1::::l                  3:::::3    */
/*    6:::::6     6:::::6   2:::::2                   1::::l                  3:::::3    */
/*    6::::::66666::::::6   2:::::2       222222   111::::::111   3333333     3:::::3    */
/*     66:::::::::::::66    2::::::2222222:::::2   1::::::::::1   3::::::33333::::::3    */
/*       66:::::::::66      2::::::::::::::::::2   1::::::::::1   3:::::::::::::::33     */
/*         666666666        22222222222222222222   111111111111    333333333333333       */
/*                                                                                    ©  */
/*---------------------------------------------------------------------------------------*/

package frc.robot;

////////////////////////Custom imports////////////////////////////////
import frc.pneumatics.Pneumatics;
import frc.autonomous.Auto;

/////////////////////////WPILib imports///////////////////////////////
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.MotorSafety;
import edu.wpi.first.wpilibj.PWMSpeedController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  //Variables
  double speedMod;
  double speed;
  double rTrigger;
  double lTrigger;
  boolean rBumper;
  boolean lBumper;
  double YAnalog;
  double XAnalog;
  double eControl;
  boolean bSPressed;
  boolean tSPressed;
  boolean bStart;
  boolean bBack;
  final double eSpeed = 0.5;
  final int IMG_HEIGHT = 340;
  final int IMG_WIDTH = 340;

  //Robot Objects
  private final DifferentialDrive robotDrive
      = new DifferentialDrive(new Spark(0), new Spark(1));
  private final XboxController m_Xbox = new XboxController(0);
  private final VictorSP elev = new VictorSP(3);

  private final DifferentialDrive intake = new DifferentialDrive(new Spark(4), new Spark(5));
  private final DoubleSolenoid hatchSol = new DoubleSolenoid(6, 7);

  private final Timer timer = new Timer();
  private static final String kDefaultAuto = "Default";
  private static final String kSandstorm = "Sandstorm";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();
  private final DigitalInput topElevSwitch = new DigitalInput(1);
  // private final DigitalInput bottomElevatorSwitch = new DigitalInput(0);

  //Custom Objects
  private final Auto auto = new Auto();
  


  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kSandstorm);
    m_chooser.addOption("My Auto", kDefaultAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

    UsbCamera cam = CameraServer.getInstance().startAutomaticCapture();
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

  @Override
  public void disabledInit(){

  }

  @Override
  public void disabledPeriodic(){

  }

  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kSandstorm:
        teleopInit();
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }
  }

  /**
   * This function is called periodically during operator control.
   */


  @Override
  public void teleopInit(){
    rTrigger = m_Xbox.getRawAxis(3);
    // lTrigger = m_Xbox.getRawAxis(2);
    rBumper = m_Xbox.getBumper(Hand.kRight);
    lBumper = m_Xbox.getBumper(Hand.kLeft);
    // rBumper = m_Xbox.getRawButton(5);
    // lBumper = m_Xbox.getRawButton(4);
    YAnalog = m_Xbox.getRawAxis(0);
    XAnalog = m_Xbox.getRawAxis(1);
    // eControl = m_Xbox.getY();
    // bBack = m_Xbox.getRawButton(6);
    // bStart = m_Xbox.getRawButton(7);
  }

  @Override
  public void teleopPeriodic() {
    speedMod = getSpeed(m_Xbox);
    YAnalog = m_Xbox.getRawAxis(0);

    //Drive code
    robotDrive.arcadeDrive(m_Xbox.getY() * speedMod, m_Xbox.getX() * speedMod);

    //Intake code
    if(m_Xbox.getBumper(Hand.kRight)){
      intake.arcadeDrive(0.75, 0.0); //Outtake
    }else if(m_Xbox.getBumper(Hand.kLeft)){
      intake.arcadeDrive(-0.75, 0.0); //Intake
    }

    if(m_Xbox.getRawAxis(0) > 0.5){
      hatchSol.set(DoubleSolenoid.Value.kForward);
    }else if(m_Xbox.getRawAxis(0) < 0.5){
    hatchSol.set(DoubleSolenoid.Value.kReverse);
    }

    //Elevator code
    if(m_Xbox.getRawAxis(2) != 0.25){ //lTrigg
      elev.set(eSpeed);
      if(topElevSwitch.get()){
        elev.stopMotor();
        System.out.println();
      }
    }else if(m_Xbox.getRawAxis(3) != 0.25){//rTrigg
      elev.set(-1 * (eSpeed));
    }

  }

  @Override
  public void testInit(){
  
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
    
  }

  ///////////////////Custom Methods///////////////////////////

  public double getSpeed(XboxController m_Xbox){
    boolean aButton = m_Xbox.getAButton();
    boolean bButton = m_Xbox.getBButton();
    boolean yButton = m_Xbox.getYButton();
    boolean xButton = m_Xbox.getXButton();

    //Controls speed
    if(aButton) {
      speed = 0.5;
      System.out.println("A is pushed");
    }
    if(bButton) {
      speed = 0.75;
      System.out.println("B is pushed");
    } 
    if(yButton) {
      speed = 1.0;
      System.out.println("Y is pushed");
    }
    if(xButton){
      speed = 0.0;
      System.out.println("X is pushed");
    }

    return speed;
  }
  
}
