package org.firstinspires.ftc.teamcode.subsystems;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * Stores and Declares all hardware devices &amp; related methods
 */
public class HWC {
    // Declare empty variables for robot hardware
    public DcMotorEx leftFront, rightFront, leftRear, rightRear, rightSlide, leftSlide;
    //TODO: CHANGE TO NORMAL SERVO
    public Servo claw, joint, arm;


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
        arm = hardwareMap.get(Servo.class,"arm");
        joint = hardwareMap.get(Servo.class, "joint");




        // Set the direction of motors
        // TODO: UPDATE VALUES WITH NEW BOT
        leftFront.setDirection(DcMotorEx.Direction.FORWARD);
        rightFront.setDirection(DcMotorEx.Direction.FORWARD);
        leftRear.setDirection(DcMotorEx.Direction.FORWARD);
        rightRear.setDirection(DcMotorEx.Direction.FORWARD);

        //Set direction of other motors
       // leftSlide.setDirection(DcMotorEx.Direction.FORWARD);
        //rightSlide.setDirection(DcMotorEx.Direction.FORWARD);

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
    }
    // TODO: ADD ANY HARDWARE RELATED FUNCTIONS BELOW

    public static void betterSleep(double secs){
        
    }
}