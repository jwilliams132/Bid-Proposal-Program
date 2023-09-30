import java.util.List;

public interface FormatInterface {

    String jobToFormat(Job job);

    Job jobFromFormat(String jobLineString);

    List<String> jobsToFormat(List<Job> jobs);

    List<Job> jobsFromFormat(List<String> jobLineStrings);
}
