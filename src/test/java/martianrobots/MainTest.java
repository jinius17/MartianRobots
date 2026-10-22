package martianrobots;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

class MainTest {

    @Test
    void readsStandardInputAndWritesTheReport() {
        var input = """
                5 3
                1 1 E
                RFRFRFRF
                """;
        var out = new ByteArrayOutputStream();
        var err = new ByteArrayOutputStream();

        var code = Main.execute(new String[0], new ByteArrayInputStream(input.getBytes(UTF_8)), print(out), print(err));

        assertThat(code).isZero();
        assertThat(out.toString(UTF_8)).isEqualTo("1 1 E\n");
        assertThat(err.toString(UTF_8)).isEmpty();
    }

    @Test
    void readsAFileArgument(@TempDir Path directory) throws Exception {
        var file = directory.resolve("input.txt");
        Files.writeString(file, """
                5 3
                1 1 E
                RFRFRFRF
                """);
        var out = new ByteArrayOutputStream();
        var err = new ByteArrayOutputStream();

        var code = Main.execute(new String[]{file.toString()}, InputStream.nullInputStream(), print(out), print(err));

        assertThat(code).isZero();
        assertThat(out.toString(UTF_8)).isEqualTo("1 1 E\n");
    }

    @Test
    void invalidInputExitsWithTheMessage() {
        var out = new ByteArrayOutputStream();
        var err = new ByteArrayOutputStream();

        var code = Main.execute(new String[0], new ByteArrayInputStream(new byte[0]), print(out), print(err));

        assertThat(code).isEqualTo(1);
        assertThat(err.toString(UTF_8)).isEqualTo("Input is empty\n");
        assertThat(out.toString(UTF_8)).isEmpty();
    }

    @Test
    void twoFileArgumentsAreRejected() {
        var out = new ByteArrayOutputStream();
        var err = new ByteArrayOutputStream();

        var code = Main.execute(new String[]{"one.txt", "two.txt"}, InputStream.nullInputStream(), print(out), print(err));

        assertThat(code).isEqualTo(1);
        assertThat(err.toString(UTF_8)).isEqualTo("Pass one input file, or no arguments to read standard input\n");
        assertThat(out.toString(UTF_8)).isEmpty();
    }

    @Test
    void missingFileReportsAReadError() {
        var out = new ByteArrayOutputStream();
        var err = new ByteArrayOutputStream();

        var code = Main.execute(new String[]{"missing-input.txt"}, InputStream.nullInputStream(), print(out), print(err));

        assertThat(code).isEqualTo(1);
        assertThat(err.toString(UTF_8)).isEqualTo("Could not read input\n");
    }

    private static PrintStream print(ByteArrayOutputStream buffer) {
        return new PrintStream(buffer, true, UTF_8);
    }
}
