package gearworks;

import java.util.List;
import java.util.ArrayList;

enum Test {
    TEST;
}

/**
 * An abstract base class for implementing data formatting using the
 * FormatInterface.
 * Subclasses should override the appropriate methods to provide specific
 * formatting logic.
 */
public abstract class FileFormat implements FileFormatInterface {

    /**
     * Converts a formatted job data string into a Job object.
     *
     * <p>
     * Subclasses should provide an implementation for this method to parse a job
     * data
     * string specific to their format and create a corresponding Job object.
     *
     * @param jobLineString A formatted string representing job data.
     * @return A Job object parsed from the input jobLineString.
     * @throws UnsupportedOperationException if this method is not overridden by a
     *                                       subclass.
     */
    @Override
    public Job jobFromFormat(String jobLineString) {

        throw new UnsupportedOperationException("Subclasses must override jobFromFormat method.");

    }

    /**
     * Converts a Job object into a formatted job data string.
     *
     * <p>
     * Subclasses should provide an implementation for this method to convert a Job
     * object
     * into a formatted string specific to their format.
     *
     * @param job A Job object to be converted into a formatted string.
     * @return A formatted string representing the job data.
     * @throws UnsupportedOperationException if this method is not overridden by a
     *                                       subclass.
     */
    @Override
    public String jobToFormat(Job job) {

        throw new UnsupportedOperationException("Subclasses must override jobToFormat method.");
    }

    /**
     * Converts a list of job data strings into a list of Job objects.
     *
     * @param jobLineStrings A list of strings representing job data in a specific
     *                       format.
     * @return A list of Job objects parsed from the input jobLineStrings.
     */
    @Override
    public List<Job> jobsFromFormat(List<String> jobLineStrings) {

        ArrayList<Job> jobs = new ArrayList<Job>();
        for (String jobLineString : jobLineStrings) {

            jobs.add(jobFromFormat(jobLineString));
        }
        return jobs;
    }

    /**
     * Converts a list of Job objects into a list of job data strings.
     *
     * @param jobs A list of Job objects to be converted.
     * @return A list of strings representing job data in a specific format.
     */
    public List<String> jobsToFormat(List<Job> jobs) {

        ArrayList<String> jobLineStrings = new ArrayList<String>();
        for (Job job : jobs) {

            jobLineStrings.add(jobToFormat(job));
        }
        return jobLineStrings;
    }
}
