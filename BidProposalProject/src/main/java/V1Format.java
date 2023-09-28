import java.io.File;
import java.util.ArrayList;

public class V1Format extends Format{

    public V1Format() {

    }

    public V1Format(Test test) {

        FileManager fileManager = new FileManager();
        File file = fileManager.chooseFile(null, null, FileManager.fileChooserOptions.OPEN, null);
        ArrayList<String> fileContents = fileManager.readFile(file);
        ArrayList<Job> jobs = jobsFromFormat(fileContents);
        jobs.forEach(job -> job.printJobInfo());
    }

    @Override
    public String jobToFormat(Job job) {

        return null;
    }

    @Override
    public Job jobFromFormat(String jobLineString) {

        return null;
    }
}
