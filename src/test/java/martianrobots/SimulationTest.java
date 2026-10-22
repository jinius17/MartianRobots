package martianrobots;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SimulationTest {

    @Test
    void sampleFromTheTask() {
        var input = """
                5 3
                1 1 E
                RFRFRFRF
                3 2 N
                FRRFLLFFRRFLL
                0 3 W
                LLFFFLFLFL
                """;
        var expected = """
                1 1 E
                3 3 N LOST
                2 3 S
                """;

        assertThat(Simulation.run(input)).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
            "5 3, 0 3 N, F, 0 3 N LOST",
            "5 3, 0 0 S, F, 0 0 S LOST",
            "5 3, 5 0 E, F, 5 0 E LOST",
            "5 3, 0 0 W, F, 0 0 W LOST",
            "50 50, 50 50 N, F, 50 50 N LOST"
    })
    void robotIsLostWhenItWalksOffAnEdge(String world, String position, String instructions, String expected) {
        assertThat(Simulation.run(script(world, position, instructions))).isEqualTo(expected + "\n");
    }

    @ParameterizedTest
    @CsvSource({
            "5 3, 0 0 E, F, 1 0 E",
            "5 3, 5 3 W, F, 4 3 W",
            "50 0, 50 0 W, F, 49 0 W"
    })
    void forwardMoveThatStaysOnTheGridIsApplied(String world, String position, String instructions, String expected) {
        assertThat(Simulation.run(script(world, position, instructions))).isEqualTo(expected + "\n");
    }

    @Test
    void laterRobotIgnoresAMoveOffAScentedCell() {
        var input = """
                5 3
                0 3 N
                F
                0 3 E
                LF
                """;
        var expected = """
                0 3 N LOST
                0 3 N
                """;

        assertThat(Simulation.run(input)).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
            "N, L, W",
            "N, R, E",
            "E, L, N",
            "E, R, S",
            "S, L, E",
            "S, R, W",
            "W, L, S",
            "W, R, N"
    })
    void leftAndRightTurns(String start, String turn, String facing) {
        assertThat(Simulation.run(script("5 3", "1 1 " + start, turn))).isEqualTo("1 1 " + facing + "\n");
    }

    @Test
    void ignoredFallStillRunsTheNextInstruction() {
        var input = """
                5 3
                0 3 N
                F
                0 3 N
                FR
                """;
        var expected = """
                0 3 N LOST
                0 3 E
                """;

        assertThat(Simulation.run(input)).isEqualTo(expected);
    }

    @Test
    void scentDoesNotSaveTheNeighbouringCell() {
        var input = """
                5 3
                0 3 N
                F
                1 3 N
                F
                """;
        var expected = """
                0 3 N LOST
                1 3 N LOST
                """;

        assertThat(Simulation.run(input)).isEqualTo(expected);
    }

    @Test
    void onlyCellInAZeroWorldIsLostOnTheFirstStep() {
        assertThat(Simulation.run(script("0 0", "0 0 N", "F"))).isEqualTo("0 0 N LOST\n");
    }

    @Test
    void worldWithNoRobotsProducesNoOutput() {
        assertThat(Simulation.run("5 3\n")).isEmpty();
    }

    @Test
    void scentStopsAFallOffADifferentEdgeOfTheSameCell() {
        var input = """
                5 3
                5 3 E
                F
                5 3 N
                F
                """;
        var expected = """
                5 3 E LOST
                5 3 N
                """;

        assertThat(Simulation.run(input)).isEqualTo(expected);
    }

    @Test
    void robotThatStartsOnAScentedCellIgnoresTheFall() {
        var input = """
                5 3
                0 3 N
                F
                0 3 N
                F
                """;
        var expected = """
                0 3 N LOST
                0 3 N
                """;

        assertThat(Simulation.run(input)).isEqualTo(expected);
    }

    @Test
    void turnsOnTheRimDoNotLeaveAScent() {
        var input = """
                5 3
                0 3 N
                RRRR
                0 3 N
                F
                """;
        var expected = """
                0 3 N
                0 3 N LOST
                """;

        assertThat(Simulation.run(input)).isEqualTo(expected);
    }

    @Test
    void instructionOfLength99IsAccepted() {
        assertThat(Simulation.run(script("5 3", "1 1 N", "R".repeat(99)))).isEqualTo("1 1 W\n");
    }

    @Test
    void scentDoesNotBlockAMoveThatStaysOnTheGrid() {
        var input = """
                5 3
                0 3 N
                F
                0 3 S
                F
                """;
        var expected = """
                0 3 N LOST
                0 2 S
                """;

        assertThat(Simulation.run(input)).isEqualTo(expected);
    }

    @Test
    void instructionsAfterTheRobotIsLostAreIgnored() {
        assertThat(Simulation.run(script("5 3", "0 2 N", "FFR"))).isEqualTo("0 3 N LOST\n");
    }

    @ParameterizedTest
    @MethodSource("invalidInput")
    void rejectsInvalidInput(String input, String message) {
        assertThatThrownBy(() -> Simulation.run(input))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage(message);
    }

    private static Stream<Arguments> invalidInput() {
        return Stream.of(
                Arguments.of("", "Input is empty"),
                Arguments.of("   \n", "Input is empty"),
                Arguments.of("51 3\n", "Upper-right coordinates must be two integers from 0 to 50"),
                Arguments.of("-1 3\n", "Upper-right coordinates must be two integers from 0 to 50"),
                Arguments.of("5 3\n-1 0 E\nF\n", "Robot position must be x y and an orientation N, E, S, or W"),
                Arguments.of("5\n", "Upper-right coordinates must be two integers from 0 to 50"),
                Arguments.of("5 3\n\n1 1 E\nF\n", "Unexpected blank line"),
                Arguments.of("5 3\n1 1 E\n", "Expected an instruction line"),
                Arguments.of("5 3\n1 1 Q\nF\n", "Robot position must be x y and an orientation N, E, S, or W"),
                Arguments.of("5 3\n51 0 E\nF\n", "Robot position must be x y and an orientation N, E, S, or W"),
                Arguments.of("5 3\n6 0 E\nF\n", "Robot starts outside the grid"),
                Arguments.of("5 3\n1 1 E\n" + "F".repeat(100) + "\n", "Instruction must be less than 100 characters and contain only L, R, and F"),
                Arguments.of("5 3\n1 1 E\nRFX\n", "Instruction must be less than 100 characters and contain only L, R, and F"),
                Arguments.of("5 3\n1 1 E\nf\n", "Instruction must be less than 100 characters and contain only L, R, and F"),
                Arguments.of("5 3\n1 1 E\nR F\n", "Instruction must be less than 100 characters and contain only L, R, and F")
        );
    }

    private static String script(String world, String position, String instructions) {
        return world + "\n" + position + "\n" + instructions + "\n";
    }
}
