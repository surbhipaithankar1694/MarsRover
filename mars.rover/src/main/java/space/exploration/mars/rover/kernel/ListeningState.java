package space.exploration.mars.rover.kernel;

import com.google.protobuf.InvalidProtocolBufferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.exploration.mars.rover.InstructionPayloadOuterClass.InstructionPayload;
import space.exploration.mars.rover.InstructionPayloadOuterClass.InstructionPayload.TargetPackage;
import space.exploration.mars.rover.environment.EnvironmentUtils;
import space.exploration.mars.rover.kernel.ModuleDirectory.Module;
import space.exploration.mars.rover.robot.RobotPositionsOuterClass.RobotPositions;

public class ListeningState implements State {

    private Logger logger = LoggerFactory.getLogger(ListeningState.class);
    private Rover  rover  = null;

    public ListeningState(Rover rover) {
        this.rover = rover;
    }

    public void receiveMessage(byte[] message) {
        byte[] currentInstruction;

        /* Process instructions in FIFO */
        if (!rover.getInstructionQueue().isEmpty()) {
            currentInstruction = rover.getInstructionQueue().remove(0);
            rover.state = rover.transmittingState;
            rover.transmitMessage(rover.getLaggingAlertMsg());
        } else {
            currentInstruction = message;
        }

        InstructionPayload payload = null;
        try {
            System.out.println("This is the listening module");
            payload = InstructionPayload.parseFrom(currentInstruction);
            System.out.println(payload);
            logger.info(payload.toString());

            for (TargetPackage tp : payload.getTargetsList()) {
                if (!rover.getBattery().requestPower(tp.getEstimatedPowerUsage(), false)) {
                    rover.state = rover.hibernatingState;
                    rover.getMarsArchitect().getRobot().setColor(EnvironmentUtils.findColor("robotHibernate"));
                    rover.getMarsArchitect().getMarsSurface().repaint();
                    rover.getInstructionQueue().add(payload.toByteArray());
                    return;
                }

                rover.getMarsArchitect().getRobot().setColor(EnvironmentUtils.findColor(rover.getMarsConfig()
                        .getProperty(EnvironmentUtils.ROBOT_COLOR)));
                rover.getMarsArchitect().getMarsSurface().repaint();
                rover.getBattery().setPrimaryPowerUnits(rover.getBattery().getPrimaryPowerUnits() - tp
                        .getEstimatedPowerUsage());

                if (tp.getRoverModule() == Module.SENSOR_LIDAR.getValue()) {
                    System.out.println("Got lidar message");
                    rover.state = rover.sensingState;
                    rover.scanSurroundings();
                } else if (tp.getRoverModule() == Module.PROPULSION.getValue()) {
                    System.out.println("Rover " + Rover.ROVER_NAME + " is on the move!");
                    rover.state = rover.movingState;
                    rover.move(RobotPositions.parseFrom(tp.getAuxiliaryData().toByteArray()));
                } else if (tp.getRoverModule() == Module.SCIENCE.getValue()) {
                    System.out.println("Rover " + Rover.ROVER_NAME + " is on a scientific mission!");
                    rover.state = rover.exploringState;
                    rover.exploreArea();
                }
            }
        } catch (InvalidProtocolBufferException e) {
            logger.error("InvalidProtocolBufferException");
            logger.error(e.getMessage());
        }
    }

    public void transmitMessage(byte[] message) {
        logger.error("Can not transmit message while in the listening state");
    }

    public void exploreArea() {
        logger.error("Can not explore area while in the listening state");
    }

    public void performExperiments() {
        logger.error("Can not perform experiments while in the listening state");
    }

    public void move(RobotPositions positions) {
        logger.error("Can not move while in the listening state");
    }

    public void hibernate() {
    }

    public void rechargeBattery() {
    }

    public void scanSurroundings() {
    }

    public void performDiagnostics() {
    }

}
