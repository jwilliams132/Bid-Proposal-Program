import java.io.File;

public class JobStorage {

    private FileManager fileManager = new FileManager();
    // private List<Job> jobs = new ArrayList<Job>();
    private File jobFile;
    private String jobFilePath = "";

    public JobStorage() {

        jobFile = fileManager.chooseFile(jobFilePath, null, FileManager.fileChooserOptions.OPEN, null);

        // ArrayList<String> fileContents = null;

        if (jobFile != null) {

            // fileContents = fileManager.readFile(jobFile);
        }
    }
}
