package org.firstinspires.ftc.teamcode.subsystems;

import android.graphics.Color;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.JavaUtil;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.subsystems.pid.RobotComponents;
import org.firstinspires.ftc.teamcode.subsystems.roadrunner.util.Encoder;

/**
 * Stores and Declares all hardware devices &amp; related methods
 */
public class HWC {
    // Declare empty variables for robot hardware
    public DcMotorEx leftFront, rightFront, leftRear, rightRear, rightSlide, leftSlide;
    public Servo jointL, jointR, armL, armR, claw;
    //not a servo
    public Servo lightLeft, lightRight;
    public Encoder leftEncoder, rightEncoder, frontEncoder;
    public RobotComponents slideLComponent, slideRComponent;

    // Position Variables
    public static double clawOpenPos = 0;
    public static double clawClosePos = 0;
    public static double clawTolerance = 0.002;
    public static double jointDefaultPos = 0;
    public static double jointIntakePos;
    public static double jointScoringPos = 0;
    public static double armDefaultPos = 0;
    public static double armVertPos = 0.355;
    public static double armHorizPos = 0.72;
    //public static double armPos3 = 0.75;
    public static int slidesIntakePos = 0;
    public static int slidesLoweredPos = 0;
    public static int lowBasketPosSlides = 0;
    public static int highBasketPosSlides = 0;
    public static int lowBarPosSlides = 0;
    public static int highBarPosSlides = 0;
    public static int climbOnePosSlides = 0;
    public static int climbTwoPosSlides = 0;
    public static double slidePPR = 751.8;
    public static boolean isArmBackwards;


    // Other Variables
    // ------ Declare Gamepads ------ //
    public Gamepad currentGamepad1 = new Gamepad();
    public Gamepad currentGamepad2 = new Gamepad();
    public Gamepad previousGamepad1 = new Gamepad();
    public Gamepad previousGamepad2 = new Gamepad();

    Telemetry telemetry;
    ElapsedTime time = new ElapsedTime();
    ElapsedTime sleepTimer = new ElapsedTime();

    /**
     * Constructor for HWC, declares all hardware components
     *
     * @param hardwareMap HardwareMap - Used to retrieve hardware devices
     * @param telemetry   Telemetry - Used to add telemetry to driver hub
     */
    public HWC(@NonNull HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;

        // Declare Driving motors
        leftFront = hardwareMap.get(DcMotorEx.class, "leftFront");
        rightFront = hardwareMap.get(DcMotorEx.class, "rightFront");
        leftRear = hardwareMap.get(DcMotorEx.class, "leftRear");
        rightRear = hardwareMap.get(DcMotorEx.class, "rightRear");

        //Declare Other Motors
        leftSlide = hardwareMap.get(DcMotorEx.class, "LSlide");
        rightSlide = hardwareMap.get(DcMotorEx.class, "RSlide");

        //Declare Servos
        claw = hardwareMap.get(Servo.class, "claw");
        armL = hardwareMap.get(Servo.class, "armL");
        armR = hardwareMap.get(Servo.class, "armR");
        jointL = hardwareMap.get(Servo.class, "jointL");
        jointR = hardwareMap.get(Servo.class, "jointR");

        //Declare Lights
        lightLeft = hardwareMap.get(Servo.class, "lightL");
        lightRight = hardwareMap.get(Servo.class, "lightR");


        //Declares OdoWheels
        leftEncoder = new Encoder(hardwareMap.get(DcMotorEx.class, "leftRear"));
        rightEncoder = new Encoder(hardwareMap.get(DcMotorEx.class, "rightRear"));
        frontEncoder = new Encoder(hardwareMap.get(DcMotorEx.class, "rightFront"));

        leftEncoder.setDirection(Encoder.Direction.REVERSE);

        // Set the direction of motors
        // TODO: UPDATE VALUES WITH NEW BOT
        leftFront.setDirection(DcMotorEx.Direction.FORWARD);
        rightFront.setDirection(DcMotorEx.Direction.FORWARD);
        leftRear.setDirection(DcMotorEx.Direction.FORWARD);
        rightRear.setDirection(DcMotorEx.Direction.FORWARD);

        leftSlide.setDirection(DcMotorEx.Direction.FORWARD);
        rightSlide.setDirection(DcMotorEx.Direction.REVERSE);


        // Set motors to break when power = 0
        // TODO: REMOVE IF THIS BEHAVIOUR IS NOT DESIRED ON NEW BOT
        leftFront.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        leftRear.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        rightRear.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        leftSlide.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        rightSlide.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

        // Set driving motors to run without encoders, we're using odometry instead
        leftFront.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        rightFront.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        leftRear.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        rightRear.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);

        // Set slide motors to use encoders
        rightSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


        armL.setDirection(Servo.Direction.FORWARD);
        armR.setDirection(Servo.Direction.REVERSE);
        jointL.setDirection(Servo.Direction.FORWARD);
        jointR.setDirection(Servo.Direction.REVERSE);
        //TODO: TUNE THESE VALUES
        slideLComponent = new RobotComponents(leftSlide, slidePPR, 0, 0, 0, 0);
        slideRComponent = new RobotComponents(rightSlide, slidePPR, 0, 0, 0, 0);

    }
    // TODO: ADD ANY HARDWARE RELATED FUNCTIONS BELOW

    public static void betterSleep(double secs) {
        ElapsedTime sleepTimer = new ElapsedTime();
        sleepTimer.reset();
        while (sleepTimer.seconds() < secs) {

        }

    }

    public void moveSlides(int position) {
        slideLComponent.setTarget(position);
        slideRComponent.setTarget(position);
        slideRComponent.moveUsingPID();
        slideLComponent.moveUsingPID();
    }

    public void advancedMove(int slidePosition, double armPosition, double jointPosition) {
        moveSlides(slidePosition);
        armL.setPosition(armPosition);
        armR.setPosition(armPosition);
        jointL.setPosition(jointPosition);
        jointR.setPosition(jointPosition);
    }

    public void climb(boolean firstClimb) {
        if (firstClimb) {
            moveSlides(climbOnePosSlides);
        } else {
            moveSlides(climbTwoPosSlides);
        }
    }

    public void toggleClaw() {
        if (claw.getPosition() == clawOpenPos) {
            claw.setPosition(clawClosePos);
        } else if (claw.getPosition() == clawClosePos) {
            claw.setPosition(clawOpenPos);
        }
    }

    public void lightLights(Lights color, char side) {
        double c = 0;
        if (color == Lights.BLUE) c = .611;
        if (color == Lights.RED) c = .279;
        if (color == Lights.GREEN) c = .5;
        if (color == Lights.PURPLE) c = .722;
        if (color == Lights.WHITE) c = 1;
        if (color == Lights.YELLOW) c = .388;

        if (side == 'L') {
            lightLeft.setPosition(c);

        } else if (side == 'R') {
            lightRight.setPosition(c);
        } else {
            lightRight.setPosition(c);
            lightLeft.setPosition(c);
        }
    }
    //TODO: ADD AURORAS VISION CODE
    /*

    public void alignWithPixel(color){
    int centerX1 = 0;
    int centerX2 = 0;
    int centerY1 = 0
    int centerY2 = 0
    boolean aligned = false;
    while (!aligned){
    if (vision.first x coord > centerx2 || vision.second x coord > centerx){
    strafe right(math on ((coord1+cord2)/2 -centerx)
    else if ((vision.first x coord < centerx1 plus or statement){
    strafe right(math on ((coord1+cord2)/2 -centerx}
   else if (vision.first y coord > centery2 || vision.second y coord > centery){
    forward (math on ((coord1+cord2)/2 -centerx)
    }
    else if ((vision.first y coord < centery1 plus or statement){
    back (math on ((coordy+cord2)/2 -centery)
    }
    else{
    aligned = true;}
    }
    }

         */

}


