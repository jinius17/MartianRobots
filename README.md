# Martian Robots

Robots move on a rectangular grid from instructions read on standard input. A robot that walks off the grid is lost and leaves a scent. A later robot does not fall from that same cell.

JDK 21 or newer. Gradle is not installed separately. The wrapper downloads Gradle 9.3.1 on the first run, then the test libraries from Maven Central. That first command needs a network connection. On Windows, use `gradlew.bat` instead of `./gradlew`.

## Build and run

Build:

```shell
./gradlew assemble
```

Run the sample from standard input:

```shell
./gradlew run --quiet < sample.txt
```

Or pass the file:

```shell
./gradlew run --quiet --args="sample.txt"
```

The report goes to standard output. Invalid input prints one line on standard error and exits non-zero. No robot output is printed in that case.

Sample input:

```
5 3
1 1 E
RFRFRFRF
3 2 N
FRRFLLFFRRFLL
0 3 W
LLFFFLFLFL
```

Sample output:

```
1 1 E
3 3 N LOST
2 3 S
```

## Tests

```shell
./gradlew test
```

`./gradlew build` compiles the program and runs the tests.

## Decisions and trade-offs

Most of the behaviour is fixed by the task: turns, forward moves, `LOST`, stopping a lost robot, and the coordinate limits. These are the choices that were open.

- **A scent sticks to the square.** The rules say the scent is left at the last grid position, and that a move off the world from a grid point where a robot was already lost is ignored. The testing guidance says "same edge", which can be read as the direction of the fall: a later robot could still leave a corner by another side. The sample never tries that second side. The rules name the grid point and do not mention orientation, so every fall from that square is ignored. A scent of position plus facing would follow the guidance phrase and contradict the rule.
- **An unknown letter fails the run.** The other option was to skip letters that are not `L`, `R`, or `F`. Failing means a new command has to be added in code.
- **Commands are an enum.** `L`, `R`, and `F` are constants on `Instruction`, and each one defines `apply`. A new command is one new constant in that file. Separate classes would be one file per command plus a parser change, with the same behaviour. The enum is enough while each command is a short rule. A class is the better home if a later command grows its own fields or helpers.
- **Bad input produces no robot output.** The file is checked before any robot moves. The other option was to print the robots that were already valid and then stop. One bad line fails the whole run. A blank line is rejected for the same reason: it would shift every later robot. An empty instruction line therefore cannot occur.
