import java.util.ArrayList;

public interface FormatInterface {

    String jobToFormat(Job job);

    Job jobFromFormat(String jobLineString);

    ArrayList<String> jobsToFormat(ArrayList<Job> jobs);

    ArrayList<Job> jobsFromFormat(ArrayList<String> jobLineStrings);
}
