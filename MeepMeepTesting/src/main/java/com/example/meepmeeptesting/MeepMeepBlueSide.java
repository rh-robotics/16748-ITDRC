package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.geometry.Pose2d;

import org.rowlandhall.meepmeep.MeepMeep;
import org.rowlandhall.meepmeep.roadrunner.DefaultBotBuilder;
import org.rowlandhall.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepBlueSide {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(800);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 15)
                .followTrajectorySequence(drive -> drive.trajectorySequenceBuilder(new Pose2d(35, 62.5, Math.toRadians(0)))
                        .forward(20)
                        .back(8)
                        .turn(Math.toRadians(-90))
                        .forward(30)
                        .back(30)
                        .turn(Math.toRadians(90))
                        .forward(8)
                        .back(8)
                        .strafeRight(37)
                        .forward(5)
                        .back(5)
                        .strafeLeft(37)
                        .forward(8)
                        .back(8)
                        .strafeRight(37)
                        .forward(12)
                        .back(12)
                        .strafeLeft(37)
                        .forward(6)
                        .back(110)

                        .build());


        meepMeep.setBackground(MeepMeep.Background.FIELD_INTOTHEDEEP_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}