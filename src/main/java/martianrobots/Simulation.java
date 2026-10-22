package martianrobots;

import java.util.ArrayList;
import java.util.List;

final class Simulation {
    private static final String WORLD = "Upper-right coordinates must be two integers from 0 to 50";
    private static final String POSITION = "Robot position must be x y and an orientation N, E, S, or W";

    private Simulation() {
    }

    static String run(String input) {
        if (input.isBlank()) {
            throw new InvalidInputException("Input is empty");
        }
        var lines = contentLines(input);
        var grid = parseGrid(lines.getFirst());
        var deployments = parseDeployments(lines.subList(1, lines.size()), grid);
        var reports = new ArrayList<String>();
        for (var deployment : deployments) {
            deployment.robot().follow(deployment.instructions(), grid);
            reports.add(deployment.robot().report());
        }
        if (reports.isEmpty()) {
            return "";
        }
        return String.join("\n", reports) + "\n";
    }

    private static List<String> contentLines(String input) {
        var raw = input.lines().map(String::strip).toList();
        var end = raw.size();
        while (end > 0 && raw.get(end - 1).isEmpty()) {
            end--;
        }
        var lines = new ArrayList<String>();
        for (var index = 0; index < end; index++) {
            var line = raw.get(index);
            if (line.isEmpty()) {
                throw new InvalidInputException("Unexpected blank line");
            }
            lines.add(line);
        }
        if (lines.isEmpty()) {
            throw new InvalidInputException("Input is empty");
        }
        return List.copyOf(lines);
    }

    private static Grid parseGrid(String line) {
        var parts = line.split("\\s+");
        if (parts.length != 2) {
            throw new InvalidInputException(WORLD);
        }
        return new Grid(coordinate(parts[0]), coordinate(parts[1]));
    }

    private static int coordinate(String token) {
        try {
            var value = Integer.parseInt(token);
            if (value < 0 || value > 50) {
                throw new InvalidInputException(WORLD);
            }
            return value;
        } catch (NumberFormatException exception) {
            throw new InvalidInputException(WORLD);
        }
    }

    private static List<Deployment> parseDeployments(List<String> lines, Grid grid) {
        if (lines.size() % 2 != 0) {
            throw new InvalidInputException("Expected an instruction line");
        }
        var deployments = new ArrayList<Deployment>();
        for (var index = 0; index < lines.size(); index += 2) {
            deployments.add(new Deployment(parseRobot(lines.get(index), grid), Instruction.parse(lines.get(index + 1))));
        }
        return List.copyOf(deployments);
    }

    private static Robot parseRobot(String line, Grid grid) {
        var parts = line.split("\\s+");
        if (parts.length != 3) {
            throw new InvalidInputException(POSITION);
        }
        int x;
        int y;
        try {
            x = Integer.parseInt(parts[0]);
            y = Integer.parseInt(parts[1]);
        } catch (NumberFormatException exception) {
            throw new InvalidInputException(POSITION);
        }
        if (x < 0 || x > 50 || y < 0 || y > 50) {
            throw new InvalidInputException(POSITION);
        }
        var position = new Position(x, y);
        if (!grid.contains(position)) {
            throw new InvalidInputException("Robot starts outside the grid");
        }
        return new Robot(position, orientation(parts[2]));
    }

    private static Orientation orientation(String token) {
        return switch (token) {
            case "N" -> Orientation.N;
            case "E" -> Orientation.E;
            case "S" -> Orientation.S;
            case "W" -> Orientation.W;
            default -> throw new InvalidInputException(POSITION);
        };
    }

    private record Deployment(Robot robot, List<Instruction> instructions) {
    }
}
