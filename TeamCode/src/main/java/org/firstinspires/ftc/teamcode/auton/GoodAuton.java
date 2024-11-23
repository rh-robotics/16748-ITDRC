package org.firstinspires.ftc.teamcode.auton;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.subsystems.HWC;

/*
 * This OpMode illustrates the concept of driving a path based on time.
 * The code is structured as a LinearOpMode
 *
 * The code assumes that you do NOT have encoders on the wheels,
 *   otherwise you would use: RobotAutoDriveByEncoder;
 *
 *   The desired path in this example is:
 *   - Drive forward for 3 seconds
 *   - Spin right for 1.3 seconds
 *   - Drive Backward for 1 Second
 *
 *  The code is written in a simple form with no optimizations.
 *  However, there are several ways that this type of sequence could be streamlined,
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this OpMode to the Driver Station OpMode list
 */

@Autonomous(name="Back Auton", group="Robot")

public class GoodAuton extends LinearOpMode {

    /* Declare OpMode members. */
  HWC robot;


    static final double     FORWARD_SPEED = 0.3;
   // static final double     TURN_SPEED    = 0.5;

    @Override
    public void runOpMode() {

        telemetry.addData("Status", "Initializing");

        // Do all init stuff
        // TODO: ADD INITS THAT YOU NEED
        robot = new HWC(hardwareMap, telemetry);
        ElapsedTime     runtime = new ElapsedTime();

        // Tell the driver the robot is ready
        telemetry.addData("Status", "Initialized");
        // Wait for the game to start (driver presses START)
        waitForStart();
        sleep(5000);
        // Step through each leg of the path, ensuring that the OpMode has not been stopped along the way.

        // Step 1:  Drive forward for 3 seconds
        robot.rightRear.setPower(FORWARD_SPEED);
        robot.rightFront.setPower(FORWARD_SPEED);
        robot.leftRear.setPower(-FORWARD_SPEED);
        robot.leftFront.setPower(-FORWARD_SPEED);

        runtime.reset();
        while (opModeIsActive() && (runtime.seconds() < 2)) {
            telemetry.addData("Path", "Leg 1: %4.1f S Elapsed", runtime.seconds());
            telemetry.update();
        }

       runtime.reset();

        // Step 4:  Stop
        robot.rightRear.setPower(0);
        robot.rightFront.setPower(0);
        robot.leftRear.setPower(0);
        robot.leftFront.setPower(0);

        telemetry.addData("Path", "Complete");
        telemetry.update();
        sleep(1000);
    }
}
