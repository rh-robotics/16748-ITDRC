package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.subsystems.HWC;

/**
 * TeleOp OpMode for simply driving with strafing wheels
 * Look at JAVA DOC!
 */
@TeleOp(name = "Basic Strafe Drive", group = "Iterative OpMode")
public class StrafeDrive extends OpMode {
    private final ElapsedTime time = new ElapsedTime();
    public DcMotorEx leftFront, rightFront, leftRear, rightRear; // Declare the object for HWC, will allow us to access all the motors declared there!

    // init() Runs ONCE after the driver hits initialize
    @Override
    public void init() {
        // Tell the driver the Op is initializing
        telemetry.addData("Status", "Initializing");

        // Do all init stuff
        // TODO: ADD INITS THAT YOU NEED
        leftFront = hardwareMap.get(DcMotorEx.class, "leftFront");
        rightFront = hardwareMap.get(DcMotorEx.class, "rightFront");
        leftRear = hardwareMap.get(DcMotorEx.class, "leftRear");
        rightRear = hardwareMap.get(DcMotorEx.class, "rightRear");
        leftFront.setDirection(DcMotorEx.Direction.FORWARD);
        rightFront.setDirection(DcMotorEx.Direction.FORWARD);
        leftRear.setDirection(DcMotorEx.Direction.FORWARD);
        rightRear.setDirection(DcMotorEx.Direction.FORWARD);
        leftFront.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        leftRear.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        rightRear.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        leftFront.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        rightFront.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        leftRear.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        rightRear.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);

        // Tell the driver the robot is ready
        telemetry.addData("Status", "Initialized");
    }

    // init_loop() - Runs continuously until the driver hits play
    @Override
    public void init_loop() {

    }

    // Start() - Runs ONCE when the driver presses play
    @Override
    public void start() {
        time.reset();
    }

    // loop() - Runs continuously while the OpMode is active
    @Override
    public void loop() {

        double leftFPower;
        double rightFPower;
        double leftBPower;
        double rightBPower;
        double drive = gamepad1.left_stick_y * 0.8;
        double strafe = -gamepad1.right_stick_x * 0.8;

        // Calculate drive power
        double turn = (gamepad1.left_trigger - gamepad1.right_trigger) * -0.6;

        double denominator = Math.max(Math.abs(drive) + Math.abs(strafe) + Math.abs(turn), 1);
        leftFPower = (turn - strafe - drive) / denominator;
        leftBPower = (turn + strafe - drive) / denominator;
        rightFPower = (turn - strafe + drive) / denominator;
        rightBPower = (turn + strafe + drive) / denominator;


        // Set power to values calculated above
        leftFront.setPower(leftFPower);
        leftRear.setPower(leftBPower);
        rightFront.setPower(rightFPower);
        rightRear.setPower(rightBPower);


    }
}