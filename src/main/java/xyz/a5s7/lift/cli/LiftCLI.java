package xyz.a5s7.lift.cli;

import org.apache.commons.cli.*;
import xyz.a5s7.lift.Lift;
import xyz.a5s7.lift.Settings;

import java.util.Scanner;

public class LiftCLI {

    public static final char FLOORS_ARG = 'n';
    public static final char HEIGHT_ARG = 'd';
    public static final char VELOCITY_ARG = 'v';
    public static final char DOOR_TIME_ARG = 't';
    public static final String HOW_TO = "To call a lift type: c [floor] + enter" + System.lineSeparator() +
            "To go to floor type: g [floor] + enter" + System.lineSeparator() +
            "Press q to exit" + System.lineSeparator();

    public static void main(String[] args) {
        CommandLineParser parser = new PosixParser();
        Options options = new Options();
        //using options for clarity
        options.addOption(
                OptionBuilder
                        .isRequired()
                        .withLongOpt("floors")
                        .withDescription("Number of floors from 5 to 20")
                        .hasArg()
                        .withArgName("floors")
                        .create(FLOORS_ARG)
        );
        options.addOption(
                OptionBuilder
                        .isRequired()
                        .withLongOpt("height")
                        .withDescription("Floor height (m)")
                        .hasArg()
                        .withArgName("height")
                        .create(HEIGHT_ARG)
        );
        options.addOption(
                OptionBuilder
                        .isRequired()
                        .withLongOpt("velocity")
                        .withDescription("Lift velocity (m/sec)")
                        .hasArg()
                        .withArgName("velocity")
                        .create(VELOCITY_ARG)
        );
        options.addOption(
                OptionBuilder
                        .isRequired()
                        .withLongOpt("door_time")
                        .withDescription("Time to close lift doors (milliseconds)")
                        .hasArg()
                        .withArgName("door_time")
                        .create(DOOR_TIME_ARG)
        );
        options.addOption(new Option("h", "help", false, "Print this message" ));

        HelpFormatter formatter = new HelpFormatter();
        try (Scanner scanner = new Scanner(System.in)) {
            CommandLine parsedArgs = parser.parse(options, args);

            if (parsedArgs.hasOption("h")) {
                printHelp(options, formatter);
                return;
            }

            String floors = parsedArgs.getOptionValue(FLOORS_ARG);
            String velocity = parsedArgs.getOptionValue(VELOCITY_ARG);
            String height = parsedArgs.getOptionValue(HEIGHT_ARG);
            String doorTime = parsedArgs.getOptionValue(DOOR_TIME_ARG);

            Settings settings = Settings.create(
                    Integer.valueOf(height),
                    Integer.valueOf(floors),
                    Integer.valueOf(doorTime),
                    Float.valueOf(velocity)
            );
            Lift lift = new Lift(settings);
            lift.addEventListener(System.out::println);

            System.out.println(HOW_TO);
            while (true) {
                String input = scanner.nextLine();
                if ("q".equalsIgnoreCase(input)) {
                    break;
                }

                Request request = Request.parse(input);

                try {
                    switch (request.getType()) {
                        case GO:
                            lift.go(request.getFloor());
                            break;
                        case CALL:
                            lift.call(request.getFloor());
                            break;
                        default:
                            System.err.println("Unknown request: " + input);
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getMessage());
                }

            }
            lift.stop(true);
        } catch (IllegalArgumentException e) {
            System.out.println("Incorrect arguments: " + e.getMessage());
            die(options, formatter);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            System.out.println("Couldn't parse command line arguments: " + e.getMessage());
            die(options, formatter);
        }
    }

    private static void die(Options options, HelpFormatter formatter) {
        printHelp(options, formatter);
        System.exit(1);
    }

    private static void printHelp(Options options, HelpFormatter formatter) {
        formatter.printHelp("lift [ arguments ]" + System.lineSeparator(),
                "Simulate a lift. Prints events while going a floor, opening/closing doors." + System.lineSeparator(),
                options,
                System.lineSeparator() + "Example: --height 3 --floors 10 -v 1.3 -t 1" +
                        System.lineSeparator() + HOW_TO
        );
    }
}
