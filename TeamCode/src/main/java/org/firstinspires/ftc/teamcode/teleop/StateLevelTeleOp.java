package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.subsystems.DeliveryStates;
import org.firstinspires.ftc.teamcode.subsystems.GamePlayStates;
import org.firstinspires.ftc.teamcode.subsystems.HWC;

/**
 * TeleOp OpMode for simply driving with strafing wheels
 * Look at JAVA DOC!
 */

@TeleOp(name = "In-Progress Tele-Op", group = "Iterative OpMode")

public class StateLevelTeleOp extends OpMode {
    private final ElapsedTime time = new ElapsedTime();
    HWC robot; // Declare the object for HWC, will allow us to access all the motors declared there!
    GamePlayStates gameState; // Creates object of states enum
    DeliveryStates dState;
    public enum MultiplierSelection {
        TURN_SPEED,
        DRIVE_SPEED,
        STRAFE_SPEED
    }
    private MultiplierSelection selection = MultiplierSelection.TURN_SPEED;
    private double turnSpeed = 0.3; // Speed multiplier for turning
    private double driveSpeed = 0.8; // Speed multiplier for driving
    private double strafeSpeed = 0.6; // Speed multiplier for strafing


    // init() Runs ONCE after the driver hits initialize
    @Override
    public void init() {
        // Tell the driver the Op is initializing
        telemetry.addData("Status", "Initializing");

        // Do all init stuff
        // TODO: ADD INITS THAT YOU NEE
        robot = new HWC(hardwareMap, telemetry);

        // Tell the driver the robot is ready
        telemetry.addData("Status", "Initialized");

        // Creates States
        gameState = GamePlayStates.START;
        robot.armL.setPosition(HWC.armDefaultPos);
        robot.armR.setPosition(HWC.armDefaultPos);
        robot.jointR.setPosition(HWC.jointDefaultPos);
        robot.jointL.setPosition(HWC.jointDefaultPos);
        //Claw position is not set because we don't want to drop the preload if we don't score in auton
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
        double drive = robot.currentGamepad1.left_stick_y * driveSpeed;
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

        if (drive != 0 || strafe != 0 || turn != 0) gameState = GamePlayStates.DRIVE;


        //TODO: TEMPORARY SLIDE CONTROL
        robot.rightSlide.setPower(gamepad2.left_stick_y);
        robot.leftSlide.setPower(gamepad2.left_stick_y);


        //TODO: TEMPORARY CLAW CONTROL
        if (gamepad1.left_bumper){
            robot.claw.setPosition(robot.clawOpenPos);
        }
       if (gamepad2.right_bumper){
            robot.claw.setPosition(robot.clawClosePos);
        }

        if (gamepad2.dpad_left){
            robot.slideLComponent.setTarget(-3000);
            robot.slideLComponent.moveUsingPID();
            robot.slideRComponent.setTarget(-3000);
            robot.slideRComponent.moveUsingPID();
        }

        
        //TODO: TEMPORARY JOINT CONTROL
        if (gamepad2.a) {
            robot.jointR.setPosition(robot.jointR.getPosition() + 0.005);
            robot.jointL.setPosition(robot.jointL.getPosition() + 0.005);
        }
        if (gamepad2.b) {
            robot.jointL.setPosition(robot.jointL.getPosition() - 0.005);
            robot.jointR.setPosition(robot.jointR.getPosition() - 0.005);
        }

        //TODO: TEMPORARY ARM CONTROL
        if (gamepad1.dpad_left) {
            robot.armL.setPosition(robot.armL.getPosition() + 0.005);
            robot.armR.setPosition(robot.armR.getPosition() + 0.005);
        }
        if (gamepad1.dpad_right) {
            robot.armL.setPosition(robot.armL.getPosition() - 0.005);
            robot.armR.setPosition(robot.armR.getPosition() - 0.005);
        }
        if (gamepad1.dpad_up) {
            robot.armL.setPosition(HWC.armDefaultPos);
           robot.armR.setPosition(HWC.armDefaultPos);
        }
        if (gamepad1.a){
            robot.advancedMove(((robot.leftSlide.getCurrentPosition() + robot.rightSlide.getCurrentPosition())/2), HWC.armVertPos, HWC.jointScoringPos);
        }
        else if (gamepad1.b){
            robot.advancedMove(((robot.leftSlide.getCurrentPosition() + robot.rightSlide.getCurrentPosition())/2), HWC.armHorizPos, HWC.jointScoringPos);
        }
        else if (gamepad1.y){
            robot.advancedMove(HWC.highBasketPosSlides, HWC.armHorizPos,HWC.jointScoringPos);
        }
        else if (gamepad1.x){
            robot.advancedMove(HWC.slidesLoweredPos, HWC.armHorizPos, HWC.jointIntakePos);
        }

        //TODO: TEMP JOINT CONTROL
        if (gamepad1.left_bumper){
            robot.jointL.setPosition(robot.jointL.getPosition() - 0.005);
            robot.jointR.setPosition(robot.jointR.getPosition() - 0.005);
        }
        else if (gamepad1.right_bumper){
            robot.jointL.setPosition(robot.jointL.getPosition() + 0.005);
            robot.jointR.setPosition(robot.jointR.getPosition() + 0.005);
        }

        if (gamepad1.dpad_down){
            robot.jointL.setPosition(HWC.jointDefaultPos);
            robot.jointR.setPosition(HWC.jointDefaultPos);
        }


        //TODO: ADD RUMBLE METHODS
        if (gamepad1.left_stick_button) gamepad1.rumble(2000);

        //TODO: Better control?


        switch(gameState){

            case START:
                break;

            case DRIVE:
                if (robot.leftSlide.getPower() != 0 || robot.rightSlide.getPower() != 0) gameState = GamePlayStates.DELIVER;
                break;

            case INTAKE:
                break;

            case DELIVER:
                break;

            case CLIMB:
                if (gamepad1.left_stick_button){
                    //climb method
                }
                break;

            case END:
                break;

            case UNKNOWN:
                break;

            default:
                gameState = gameState.UNKNOWN;
        }
        telemetry.addData("State", gameState);
        telemetry.addData("Right Front Pow", robot.rightFront.getPower());
        telemetry.addData("Left Front Pow", robot.leftFront.getPower());
        telemetry.addData("Right Back Pow", robot.rightRear.getPower());
        telemetry.addData("Left Back Pow", robot.leftRear.getPower());
        telemetry.addLine();
        telemetry.addData("Right Slide Pos", robot.rightSlide.getCurrentPosition());
        telemetry.addData("Left Slide Pos", robot.leftSlide.getCurrentPosition());
        telemetry.addLine();
        telemetry.addData("Claw Position", robot.claw.getPosition());
        telemetry.addData("Arm L Position", robot.armL.getPosition());
        telemetry.addData("Arm R Position", robot.armR.getPosition());
        telemetry.addData("Joint R Position", robot.jointR.getPosition());
        telemetry.addData("Joint L Position", robot.jointL.getPosition());

        telemetry.update();
    }
}