import java.util.ArrayList;

public abstract class Format implements FormatInterface {

    public enum Test {
        TEST
    }

    @Override
    public Job jobFromFormat(String jobLineString) {
        
        throw new UnsupportedOperationException("Subclasses must override jobFromFormat method.");
    
    }

    @Override
    public String jobToFormat(Job job) {

        throw new UnsupportedOperationException("Subclasses must override jobToFormat method.");
    }

    @Override
    public ArrayList<Job> jobsFromFormat(ArrayList<String> jobLineStrings) {

        ArrayList<Job> jobs = new ArrayList<Job>();
        for (String jobLineString : jobLineStrings) {

            jobs.add(jobFromFormat(jobLineString));
        }
        return jobs;
    }

    public ArrayList<String> jobsToFormat(ArrayList<Job> jobs) {

        ArrayList<String> jobLineStrings = new ArrayList<String>();
        for (Job job : jobs) {

            jobLineStrings.add(jobToFormat(job));
        }
        return jobLineStrings;
    }
}