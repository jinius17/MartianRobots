package martianrobots;

enum Orientation {
    N(0, 1),
    E(1, 0),
    S(0, -1),
    W(-1, 0);

    private final int dx;
    private final int dy;

    Orientation(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    Orientation left() {
        return switch (this) {
            case N -> W;
            case W -> S;
            case S -> E;
            case E -> N;
        };
    }

    Orientation right() {
        return switch (this) {
            case N -> E;
            case E -> S;
            case S -> W;
            case W -> N;
        };
    }

    Position step(Position position) {
        return new Position(position.x() + dx, position.y() + dy);
    }
}
