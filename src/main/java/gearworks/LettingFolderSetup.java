package gearworks;

import java.io.File;

public class LettingFolderSetup {

    // Method to create a single directory
    public boolean createDirectory(String path) {

        File directory = new File(path);
        if (!directory.exists()) {

            return directory.mkdir();
        }
        return false;
    }

    // Method to create nested directories
    public boolean createNestedDirectories(String path) {

        File directories = new File(path);
        if (!directories.exists()) {

            return directories.mkdirs();
        }
        return false;
    }

    // Method to create an entire directory structure
    public void createDirectoryStructure(String basePath) {

        String[] years = {"2024"};
        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        String[] subFolders = {"Jobs", "Plans", "Proposals - PDF", "Proposals - Excel"};

        for (String year : years) {

            String yearPath = basePath + File.separator + "Letting" + File.separator + year;
            createNestedDirectories(yearPath);
            for (String month : months) {

                String monthPath = yearPath + File.separator + month;
                createNestedDirectories(monthPath);
                for (String subFolder : subFolders) {
					
                    String subFolderPath = monthPath + File.separator + subFolder;
                    createNestedDirectories(subFolderPath);
                }
            }
        }
    }

    // Main method for testing purposes
    // public static void main(String[] args) {
    //     LettingFolderSetup ds = new LettingFolderSetup();
    //     ds.createDirectoryStructure("C:\\Users\\Jacob\\Desktop");  // Replace with your base path
	// 	// System.out.println(ds.createDirectory("C:\\Users\\Jacob\\Desktop\\Letting\\2024"));
    // }
}
