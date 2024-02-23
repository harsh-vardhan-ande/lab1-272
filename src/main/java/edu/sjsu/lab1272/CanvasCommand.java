package edu.sjsu.lab1272;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.HashSet;
import java.util.Date;
import java.util.concurrent.Callable;

/*
    CanvasCommand contains 2 sub commands > list-courses and list-assignments.
    Additionally, it expects a mandatory --token argument as part of the jar invocation.
 */
@Component
@Command(name = "graphql", mixinStandardHelpOptions = true, subcommands = {CanvasCommand.ListCourses.class, CanvasCommand.ListAssignments.class})
public class CanvasCommand {
    static Logger logger = LoggerFactory.getLogger(CanvasCommand.class);
    @Option(names = "--token", description = "Bearer token of user for Canvas", required = true)
    private static String token;

    @Component
    @Command(name = "list-courses", mixinStandardHelpOptions = true, exitCodeOnExecutionException = 34)
    static class ListCourses implements Callable<Integer> {
        @Option(names = "--active", description = "Show active courses only", defaultValue = "false")
        private boolean active;

        @Option(names = "--no-active", description = "Show non active courses only", defaultValue = "false")
        private boolean nonActive;

        @Autowired
        private CanvasClient canvasClient;

        @Override
        public Integer call() {
            logger.debug("Listing courses");

            // List courses using Canvas Client
            List<Course> allCourses = this.canvasClient.ListCourses(CanvasCommand.token);
            if (allCourses==null){
                logger.debug("No courses found");
                return 33;
            }

            // Filter out active / non active courses based on given argument
            if (active || !nonActive) {
                allCourses = allCourses.stream().filter(
                        c -> Objects.equals(c.term.name, "Spring 2024")
                ).toList();
            } else {
                allCourses = allCourses.stream().filter(
                        c -> !Objects.equals(c.term.name, "Spring 2024")
                ).toList();
            }

            // Print the results
            for (int i = 0; i < allCourses.size(); i++) {
                System.out.println(allCourses.get(i).name);
            }
            return 33;
        }
    }

    @Component
    @Command(name = "list-assignments", mixinStandardHelpOptions = true, exitCodeOnExecutionException = 34)
    static class ListAssignments implements Callable<Integer> {
        @Option(names = "--active", description = "Show active assignments only", defaultValue = "false")
        private boolean active;

        @Option(names = "--no-active", description = "Show non active assignments only", defaultValue = "false")
        private boolean nonActive;

        @Parameters(description = "Course name for listing assignments", defaultValue = "")
        private String courseName;

        @Autowired
        private CanvasClient canvasClient;

        @Override
        public Integer call() {
            logger.debug("Listing assignments");

            // List courses using Canvas Client
            boolean listActiveOnly;
            listActiveOnly = active || (!nonActive);
            List<Course> allCourses = this.canvasClient.ListCourses(CanvasCommand.token);
            if (allCourses==null){
                logger.debug("No courses found");
                return 33;
            }

            // Filter out default term courses which do not count as credits
            allCourses = allCourses.stream().filter(
                    c -> !Objects.equals(c.term.name, "Default Term")
            ).toList();

            // Validate given course name, presence of atleast one match or more than one match for given course
            if (Objects.equals(this.courseName, "")) {
                logger.debug("No course name input given");
                System.out.println("Course name missing");
                return 33;
            }
            Set<Integer> indices = new HashSet<>();
            for (int i = 0; i < allCourses.size(); i++) {
                if (Objects.equals(allCourses.get(i).name, this.courseName) ||
                        allCourses.get(i).name.toLowerCase().contains(this.courseName.toLowerCase())) {
                    indices.add(i);
                }
            }
            if (indices.size() > 1) {
                logger.debug("Multiple matching courses found");
                System.out.println("Multiple matches found for the given course name -");
                List<Course> finalAllCourses = allCourses;
                indices.forEach(i -> System.out.println(finalAllCourses.get(i).name));
                return 33;
            } else if (indices.isEmpty()) {
                logger.debug("No matching courses found");
                System.out.println("No matches found for given course name");
                return 33;
            }

            // List assignments for matched course
            CourseAssignment courseAssignment = this.canvasClient.ListAssignments(CanvasCommand.token, allCourses.get(indices.iterator().next())._id);
            if(courseAssignment==null){
                return 33;
            }
            Date now = new Date();
            Set<Integer> dueAssignmentIndices = new HashSet<>();
            for (int i = 0; i < courseAssignment.assignmentsConnection.nodes.length; i++) {
                dueAssignmentIndices.add(i);
            }

            // Print active / non-active assignments based on input
            if (dueAssignmentIndices.isEmpty()) {
                logger.debug("No assignments found for - "+courseAssignment.name);
                System.out.printf("No assignments found for %s", courseAssignment.name);
            } else {
                for (int i = dueAssignmentIndices.size(); i > 0; i--) {
                    int assignmentIndex = dueAssignmentIndices.iterator().next();
                    dueAssignmentIndices.remove(assignmentIndex);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
                    String dateToShow;
                    if (courseAssignment.assignmentsConnection.nodes[assignmentIndex].dueAt != null) {
                        dateToShow = sdf.format(courseAssignment.assignmentsConnection.nodes[assignmentIndex].dueAt);
                    } else {
                        dateToShow = "NA";
                    }
                    if (listActiveOnly &&
                            courseAssignment.assignmentsConnection.nodes[assignmentIndex].dueAt != null &&
                            !now.before(courseAssignment.assignmentsConnection.nodes[assignmentIndex].dueAt)) {
                        continue;
                    } else if (!listActiveOnly &&
                            courseAssignment.assignmentsConnection.nodes[assignmentIndex].dueAt != null &&
                            now.before(courseAssignment.assignmentsConnection.nodes[assignmentIndex].dueAt)) {
                        continue;
                    }
                    System.out.printf("%s due at %s\n",
                            courseAssignment.assignmentsConnection.nodes[assignmentIndex].name,
                            dateToShow
                    );
                }
            }
            return 33;
        }
    }
}
