import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JobStorage {

    private FileManager fileManager = new FileManager();
    private InputFileProcessor IFP = new InputFileProcessor();
    private final File lettingFolder = new File(System.getProperty("user.home") + "/Desktop/Letting");
    public List<String> savedFilePaths = new ArrayList<>();
    private final String fileLookUpTargetName = "Program Output.txt";

    public List<String> filePaths = new ArrayList<>();

    public JobStorage() {
        String a = "aaaaa";
        String b = "bbbbb";
        String c = "ccccc";

        List<String> ah = new ArrayList<>();
        ah.add(a);
        ah.add(b);
        ah.add(c);

        for (String string : ah) {
            
            IFPThread thread = new IFPThread(string);
            Thread myThread = new Thread(thread);
            myThread.start();
        }
    }
    
    /**
     * Updates the job storage file by collecting job data from multiple files in a
     * specified folder
     * and saving it in a consolidated format.
     */
    public void updateJobStorageFile() {

        List<String> filePaths = findFilePaths(lettingFolder.getAbsolutePath()); // gets list of path strings
        List<Job> jobs = new ArrayList<Job>(); // creates new list of job lists
        List<String> jobStorageContents = new ArrayList<>();

        FormatInterface v2Output = new V2Format();

        for (String path : filePaths) {

            for (String savedPath : savedFilePaths) {

                if (path.equals(savedPath))

                    break;
                filePaths.add(path);
                jobs.addAll(IFP.parseFile(path));
            }
        }
        jobs.forEach(job -> jobStorageContents.add(v2Output.jobToFormat(job))); // adds each formatted output for each
                                                                                // job to jobStorageContents

        File jobStorageFile = fileManager.chooseFile(lettingFolder.getAbsolutePath() + "/Job Storage.txt", null,
                FileManager.fileChooserOptions.SAVE, null);

        fileManager.saveFile(jobStorageFile, jobStorageContents); // saves txt file of V2Format of all letting Program
                                                                  // Output.txt files
    }

    /**
     * Recursively searches for files with a specific target name in the specified
     * directory
     * and its subdirectories and returns a list of their absolute paths.
     *
     * @param directoryPath The path to the directory to start searching from.
     * @return A list of absolute file paths that match the specified target name.
     */
    public List<String> findFilePaths(String directoryPath) {

        File directory = new File(directoryPath);
        File[] directoryContents = directory.listFiles();

        if (directoryContents == null)
            return filePaths;

        for (File file : directoryContents) {

            if (file.isDirectory() && !file.getName().equals("Testing"))

                findFilePaths(file.getAbsolutePath());

            if (file.isFile() && file.getName().equals(fileLookUpTargetName))

                filePaths.add(file.getAbsolutePath());
        }
        return filePaths;
    }
}
