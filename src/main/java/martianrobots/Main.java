package martianrobots;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        var code = execute(args, System.in, System.out, System.err);
        if (code != 0) {
            System.exit(code);
        }
    }

    static int execute(String[] args, InputStream input, PrintStream out, PrintStream err) {
        try {
            out.print(Simulation.run(read(args, input)));
            return 0;
        } catch (InvalidInputException exception) {
            err.println(exception.getMessage());
            return 1;
        } catch (IOException exception) {
            err.println("Could not read input");
            return 1;
        }
    }

    private static String read(String[] args, InputStream input) throws IOException {
        if (args.length > 1) {
            throw new InvalidInputException("Pass one input file, or no arguments to read standard input");
        }
        if (args.length == 1) {
            return Files.readString(Path.of(args[0]));
        }
        return new String(input.readAllBytes(), UTF_8);
    }
}
