package de.tum;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static CommandLine commandline;

    public static void main(String... args) {

        // Define command line options
        Options options = new Options();
        Option opt_help = new Option("help", "Print this help");
        Option opt_config = Option.builder("c")
                .required(false)
                .hasArg()
                .longOpt("config")
                .desc("Specify config file")
                .build();
        options.addOption(opt_help);
        options.addOption(opt_config);

        // Parse command line
        CommandLineParser parser = new DefaultParser();
        try {
            commandline = parser.parse(options, args);
        } catch (ParseException exp) {
            System.err.println("Parsing of command line failed.  Reason: " + exp.getMessage());

            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("rps", options);
            return;
        }

        SpringApplication.run(Application.class, args);
    }
}
