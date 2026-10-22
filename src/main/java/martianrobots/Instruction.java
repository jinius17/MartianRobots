package martianrobots;

import java.util.ArrayList;
import java.util.List;

enum Instruction {
    L {
        @Override
        void apply(Robot robot, Grid grid) {
            robot.turnLeft();
        }
    },
    R {
        @Override
        void apply(Robot robot, Grid grid) {
            robot.turnRight();
        }
    },
    F {
        @Override
        void apply(Robot robot, Grid grid) {
            var next = robot.orientation().step(robot.position());
            if (grid.contains(next)) {
                robot.relocate(next);
                return;
            }
            if (grid.hasScent(robot.position())) {
                return;
            }
            grid.leaveScent(robot.position());
            robot.markLost();
        }
    };

    private static final String MESSAGE = "Instruction must be less than 100 characters and contain only L, R, and F";

    abstract void apply(Robot robot, Grid grid);

    static List<Instruction> parse(String raw) {
        if (raw.length() >= 100) {
            throw new InvalidInputException(MESSAGE);
        }
        var instructions = new ArrayList<Instruction>();
        for (var index = 0; index < raw.length(); index++) {
            instructions.add(from(raw.charAt(index)));
        }
        return List.copyOf(instructions);
    }

    private static Instruction from(char command) {
        try {
            return valueOf(String.valueOf(command));
        } catch (IllegalArgumentException exception) {
            throw new InvalidInputException(MESSAGE);
        }
    }
}
