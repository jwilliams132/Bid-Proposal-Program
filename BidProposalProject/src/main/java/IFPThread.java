import java.util.ArrayList;
import java.util.List;

public class IFPThread extends InputFileProcessor implements Runnable {

    List<Job> jobs = new ArrayList<Job>();
    String fileName;
    
    public IFPThread(String fileName) {
        this.fileName = fileName;
    }
    
    @Override
    public void run() {
        
        System.out.println(fileName);
    }
    
}
