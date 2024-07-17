package gearworks;

import java.util.List;

/**
 * An interface for defining data formatting methods.
 */
public interface FileFormatInterface {

    /**
     * Converts a single Job object into a formatted string representation.
     *
     * @param job The Job object to be converted.
     * @return A formatted string representing the Job object.
     */
    String jobToFormat(Job job);

    /**
     * Parses a formatted string representation of a Job and creates a Job object.
     *
     * @param jobLineString The formatted string representation of the Job.
     * @return A Job object created from the formatted string.
     */
    Job jobFromFormat(String jobLineString);

    /**
     * Converts a list of Job objects into a list of formatted string
     * representations.
     *
     * @param jobs The list of Job objects to be converted.
     * @return A list of formatted strings representing the Job objects.
     */
    List<String> jobsToFormat(List<Job> jobs);

    /**
     * Parses a list of formatted string representations of Jobs and creates a list
     * of Job objects.
     *
     * @param jobLineStrings The list of formatted strings representing Jobs.
     * @return A list of Job objects created from the formatted strings.
     */
    List<Job> jobsFromFormat(List<String> jobLineStrings);
}
