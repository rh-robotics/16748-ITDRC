package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.subsystems.HWC;

/**
 * TeleOp OpMode for simply driving with strafing wheels
 * Look at JAVA DOC!
 */

@TeleOp(name = "INIT TELEOP", group = "Iterative OpMode")

public class InitialTeleOp extends OpMode {
    private final ElapsedTime time = new ElapsedTime();
    HWC robot; // Declare the object for HWC, will allow us to access all the motors declared there!
    TeleOpStates state; // Creates object of states enum
    public enum MultiplierSelection {
        TURN_SPEED,
        DRIVE_SPEED,
        STRAFE_SPEED
    }
    private MultiplierSelection selection = MultiplierSelection.TURN_SPEED;
    private double turnSpeed = 0.5; // Speed multiplier for turning
    private double driveSpeed = 1; // Speed multiplier for driving
    private double strafeSpeed = 0.8; // Speed multiplier for strafing
    // init() Runs ONCE after the driver hits initialize
    @Override
    public void init() {
        // Tell the driver the Op is initializing
        telemetry.addData("Status", "Initializing");

        // Do all init stuff
        // TODO: ADD INITS THAT YOU NEED
        robot = new HWC(hardwareMap, telemetry);

        // Tell the driver the robot is ready
        telemetry.addData("Status", "Initialized");

        // Creates States
        state = TeleOpStates.START;
    }

    // init_loop() - Runs continuously until the driver hits play
    @Override
    public void init_loop() {
        robot.previousGamepad1.copy(robot.currentGamepad1);
        robot.currentGamepad1.copy(gamepad1);

        // ------ Speed Multiplier Selection ------ //
        if (robot.currentGamepad1.a && !robot.previousGamepad1.a) {
            selection = MultiplierSelection.TURN_SPEED;
        } else if (robot.currentGamepad1.b && !robot.previousGamepad1.b) {
            selection = MultiplierSelection.DRIVE_SPEED;
        } else if (robot.currentGamepad1.x && !robot.previousGamepad1.x) {
            selection = MultiplierSelection.STRAFE_SPEED;
        }

        // ------ Speed Multiplier Changes ------ //
        switch (selection) {
            case TURN_SPEED:
                if (robot.currentGamepad1.dpad_up && !robot.previousGamepad1.dpad_up) {
                    turnSpeed += 0.1;
                } else if (robot.currentGamepad1.dpad_down && !robot.previousGamepad1.dpad_down) {
                    turnSpeed -= 0.1;
                }
                break;
            case DRIVE_SPEED:
                if (robot.currentGamepad1.dpad_up && !robot.previousGamepad1.dpad_up) {
                    driveSpeed += 0.1;
                } else if (robot.currentGamepad1.dpad_down && !robot.previousGamepad1.dpad_down) {
                    driveSpeed -= 0.1;
                }
                break;
            case STRAFE_SPEED:
                if (robot.currentGamepad1.dpad_up && !robot.previousGamepad1.dpad_up) {
                    strafeSpeed += 0.1;
                } else if (robot.currentGamepad1.dpad_down && !robot.previousGamepad1.dpad_down) {
                    strafeSpeed -= 0.1;
                }
                break;
        }
        telemetry.addData("Press A to start changing turn speed", "");
        telemetry.addData("Press B to start changing drive speed", "");
        telemetry.addData("Press X to start changing strafe speed", "");
        telemetry.addLine();
        telemetry.addData("Modifying", selection);
        telemetry.addLine();
        telemetry.addData("Turn Speed", turnSpeed);
        telemetry.addData("Drive Speed", driveSpeed);
        telemetry.addData("Strafe Speed", strafeSpeed);
        telemetry.update();
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
        robot.previousGamepad1.copy(robot.currentGamepad1);
        robot.currentGamepad1.copy(gamepad1);


        // Calculate drive power
        double drive = -robot.currentGamepad1.left_stick_y * driveSpeed;
        double strafe = robot.currentGamepad1.left_stick_x * strafeSpeed;
        double turn = (robot.currentGamepad1.left_trigger - robot.currentGamepad1.right_trigger) * turnSpeed;

        double denominator = Math.max(Math.abs(drive) + Math.abs(strafe) + Math.abs(turn), 1);
        leftFPower = (turn - strafe - drive) / denominator;
        leftBPower = (turn + strafe - drive) / denominator;
        rightFPower = (turn - strafe + drive) / denominator;
        rightBPower = (turn + strafe + drive) / denominator;

        // Set power to values calculated above
        robot.leftFront.setPower(leftFPower);
        robot.leftRear.setPower(leftBPower);
        robot.rightFront.setPower(rightFPower);
        robot.rightRear.setPower(rightBPower);


        switch(state){

            case START:
                break;

            case DRIVE:
                break;

            case INTAKE_SAMPLE:
                break;

            case INTAKE_SPECIMEN:
                break;

            case DELIVER_SAMPLE:
                break;

            case DELIVER_SPECIMEN:
                break;

            case CLIMB_ONE:
                break;

            case CLIMB_TWO:
                break;

            case END:
                break;

            case UNKNOWN:
                break;

            default:
                state = TeleOpStates.UNKNOWN;
        }

        telemetry.addData("State", state);
        telemetry.addData("Right Front", rightFPower);
        telemetry.addData("Left Front", leftFPower);
        telemetry.addData("Right Back", rightBPower);
        telemetry.addData("Left Back", leftBPower);
        telemetry.addLine();
       // telemetry.addData("", driveSpeed);
        //telemetry.addData("Strafe Speed", strafeSpeed);
        telemetry.update();
        telemetry.update();
    }
}