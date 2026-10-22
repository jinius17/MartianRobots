package martianrobots;

import java.util.HashSet;
import java.util.Set;

final class Grid {
    private final int maxX;
    private final int maxY;
    private final Set<Position> scents = new HashSet<>();

    Grid(int maxX, int maxY) {
        this.maxX = maxX;
        this.maxY = maxY;
    }

    boolean contains(Position position) {
        return position.x() >= 0 && position.x() <= maxX && position.y() >= 0 && position.y() <= maxY;
    }

    boolean hasScent(Position position) {
        return scents.contains(position);
    }

    void leaveScent(Position position) {
        scents.add(position);
    }
}
