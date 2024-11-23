package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.subsystems.HWC;

/**
 * TeleOp OpMode for simply driving with strafing wheels
 * Look at JAVA DOC!
 */
@TeleOp(name = "Testing", group = "TeleOp")
public class Testing extends OpMode {
    // Enum to indicate what motor type (normal or servo) is selected
    enum MotorType {
        NORMAL,
        SERVO
    }

    private final ElapsedTime time = new ElapsedTime();
    HWC robot; // Declare the object for HWC, will allow us to access all the motors declared there!
    DcMotorEx[] motors; // Array of all normal motors on the robot
    int motorIndex; // Index to the selected normal motor in the motors array
    DcMotorEx selMotor; // Selected normal motor
    Servo[] servos; // Array of all servos on the robot
    int servoIndex; // Index to the selected servo in the servos array
    Servo selServo; // Selected servo
    MotorType selType; // What type of motor (normal or servo) we're testing; selected type
    double pwr; // Motor power
    double pos; // Servo position
    double incAmt; // Amount to increment (and decrement) power by for each button press
    boolean aPress, bPress; // if A was pressed last cycle, if B was pressed last cycle

    // init() Runs ONCE after the driver hits initialize
    @Override
    public void init() {
        // Tell the driver the Op is initializing
        telemetry.addData("Status", "Initializing");

        // Do all init stuff
        robot = new HWC(hardwareMap, telemetry);
        motors = new DcMotorEx[4];
        motors[0] = robot.leftFront;
        motors[1] = robot.rightFront;
        motors[2] = robot.leftRear;
        motors[3] = robot.rightRear;
        servos = new Servo[3]; // TODO: UPDATE LEN AND ADD ELEMENTS
        servos[0] = robot.claw;
        servos[1] = robot.joint;
        servos[2] = robot.arm;
        servoIndex = 0;
        selServo = servos[servoIndex];
        motorIndex = 0;
        selMotor = motors[motorIndex];
        selType =  MotorType.NORMAL; // Start with normal motors
        incAmt = 0.05;
        pwr = 0;
        pos = 0;
        aPress = false;
        bPress = false;

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
        if (gamepad1.right_bumper) {
            if (selType == MotorType.NORMAL){
                switchMotor(); // Switch to next motor in motors array
            } else if (selType == MotorType.SERVO) {
                switchServo(); // Switch to next servo in servos array
            }
        } else if (gamepad1.left_bumper) {
            switchType(); // Switch from current motor type to other motor type
        }

        // Increment/decrement pwr/pos depending on selected motor type
        if (gamepad1.a && !aPress) {
            if (selType == MotorType.NORMAL) {
                pwr += incAmt;
            }
            if (selType == MotorType.SERVO) {
                pos += incAmt;
            }
            aPress = true;
        } else if (gamepad1.b && !bPress) {
            if (selType == MotorType.NORMAL) {
                pwr -= incAmt;
            }
            if (selType == MotorType.SERVO) {
                pos -= incAmt;
            }
            bPress = true;
        }

        // Move selected motor at/to designated pwr/pos
        if (selType == MotorType.NORMAL) {
            selMotor.setPower(pwr);
        } else {
            selServo.setPosition(pos);
            telemetry.addData("Position", selMotor.getCurrentPosition());
            telemetry.update();
        }

        // If A/B button is no longer pressed, reset aPress/bPress
        if (!gamepad1.a) {
            aPress = false;
        }
        if (!gamepad1.b) {
            bPress = false;
        }

    }


    // Switch to next motor in array
    private void switchMotor() {
        selMotor.setPower(0); // Stop motor before switch
        pwr = 0; // Reset pwr before switching
        motorIndex++;
        if (motorIndex >= motors.length || motorIndex < 0) {
            motorIndex = 0; // Loop around if motor index too high
        }
        selMotor = motors[motorIndex];
    }

    // Switch to next servo in array
    private void switchServo() {
        pos = 0; // Reset pos before switching (new servo will go to pos=0)
        servoIndex++;
        if (servoIndex >= servos.length || servoIndex < 0) {
            servoIndex = 0; // Loop around
        }
        selServo = servos[servoIndex];
    }

    // Switch selected motor type
    private void switchType() {
        selMotor.setPower(0); // Stop motor before switch
        pwr = 0;
        pos = 0;
        if (selType == MotorType.NORMAL) {
            selType = MotorType.SERVO;
        } else {
            selType = MotorType.NORMAL;
        }
    }
}