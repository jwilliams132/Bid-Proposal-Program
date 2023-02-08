import java.io.File;
import java.util.List;

public class DocTest {

	public static void main(String[] args) {
		
		FileManager manager = new FileManager();
		File file1 = new File("C:\\Users\\Jacob\\Desktop\\Letting\\Test Letting\\Testtext.txt");
		File file2 = new File("C:\\Users\\Jacob\\Desktop\\Letting\\Test Letting\\Testtext2.txt");
		
		List<String> l1 = manager.readFile(file1);
		List<String> l2 = manager.readFile(file2);
		
		for (int i = 0 ; i < (l1.size() < l2.size() ? l2.size() : l1.size()); i++) {
			if(l1.get(i).equals(l2.get(i))) {
				System.out.println("match");
			} else {
				System.out.println();
				System.out.println(l1.get(i));
				System.out.println(l2.get(i));
				System.out.println();
			}
		}
	}
}
