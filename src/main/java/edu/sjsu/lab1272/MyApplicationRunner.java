package edu.sjsu.lab1272;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.IFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.stereotype.Component;

@Component
public class MyApplicationRunner implements CommandLineRunner, ExitCodeGenerator {
    static Logger logger = LoggerFactory.getLogger(MyApplicationRunner.class);
    private final CanvasCommand canvasCommand;

    private final IFactory factory;

    private int exitCode;

    public MyApplicationRunner(CanvasCommand canvasCommand, IFactory factory) {
        this.canvasCommand = canvasCommand;
        this.factory = factory;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.debug("Running the given command object - "+canvasCommand.toString());
        exitCode = new CommandLine(canvasCommand, factory).execute(args);
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }
}