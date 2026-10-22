package martianrobots;

import java.util.List;

final class Robot {
    private Position position;
    private Orientation orientation;
    private boolean lost;

    Robot(Position position, Orientation orientation) {
        this.position = position;
        this.orientation = orientation;
    }

    void follow(List<Instruction> instructions, Grid grid) {
        for (var instruction : instructions) {
            if (lost) {
                return;
            }
            instruction.apply(this, grid);
        }
    }

    Position position() {
        return position;
    }

    Orientation orientation() {
        return orientation;
    }

    void turnLeft() {
        orientation = orientation.left();
    }

    void turnRight() {
        orientation = orientation.right();
    }

    void relocate(Position next) {
        position = next;
    }

    void markLost() {
        lost = true;
    }

    String report() {
        var line = position.x() + " " + position.y() + " " + orientation;
        if (lost) {
            return line + " LOST";
        }
        return line;
    }
}
