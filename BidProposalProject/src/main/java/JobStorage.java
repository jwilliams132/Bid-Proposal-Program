import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class JobStorage {
    
    private final File lettingFolder = new File(System.getProperty("user.home") + "/Desktop/Letting");
    private final String fileLookUpTargetName = "Program Output.txt";
    private final String fileLookUpTargetName2 = "Program Ouput.txt";
    
    private FileManager fileManager = new FileManager();
    private InputFileProcessor IFP = new InputFileProcessor();
    
    public List<String> filePaths = new ArrayList<>();
    public List<String> savedFilePaths = new ArrayList<>();
    private List<IFPThread> threads = new ArrayList<IFPThread>();
    private List<Job> jobs = new ArrayList<Job>(), jobs2 = new ArrayList<Job>();


    public JobStorage() {

        List<String> filePaths = findFilePaths(lettingFolder.getAbsolutePath()); // gets list of path strings

        
        ExecutorService executorService = Executors.newCachedThreadPool();
        long startTime = System.currentTimeMillis();
        for (String filePath : filePaths) {
            
            threads.add(new IFPThread(filePath));
            threads.forEach(thread -> executorService.execute(thread));
        }
        executorService.shutdown();
        try {
            
            boolean ifTasksEnded = executorService.awaitTermination(1, TimeUnit.MINUTES);
            if (ifTasksEnded) {
                
                threads.forEach(thread -> jobs.addAll(thread.getJobList()));
            }
        } catch (InterruptedException e) {
            
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("Time: " + elapsedTime + " ms.");
    Collections.sort(jobs, Comparator.comparing(Job::getCsj));
        
        IFPThread x;
        jobs2 = new ArrayList<Job>();
        startTime = System.currentTimeMillis();
        for (String filePath : filePaths) {
            
            x = new IFPThread(filePath);
            x.run();
            jobs2.addAll(x.getJobList());
        }
        endTime = System.currentTimeMillis();
        elapsedTime = endTime - startTime;
        System.out.println("Time: " + elapsedTime + " ms.");

        Collections.sort(jobs2, Comparator.comparing(Job::getCsj));

        for (int i = 0; i < jobs.size(); i++) {
            System.out.println(jobs.get(i).equals(jobs2.get(i)));
        }
        jobs.get(0).printJobInfo();
        jobs2.get(0).printJobInfo();
        // System.out.println();
        // System.out.println(jobs.equals(jobs2));
        // System.out.println(jobs.size() + " " + jobs2.size());

        // updateJobStorageFile();
    }

    /**
     * Updates the job storage file by collecting job data from multiple files in a
     * specified folder
     * and saving it in a consolidated format.
     */
    public void updateJobStorageFile() {

        List<String> jobStorageContents = new ArrayList<>();
        FormatInterface v2Output = new V2Format();

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

            if (file.isDirectory() && !file.getName().contains("Testing"))

                findFilePaths(file.getAbsolutePath());

            if (file.isFile() && (file.getName().equals(fileLookUpTargetName) || file.getName().equals(fileLookUpTargetName2)))

                filePaths.add(file.getAbsolutePath());
        }
        return filePaths;
    }
}
