package execution;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Project {

	public static void main(String[] args) throws IOException {
		String path = args[0];
		Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		System.out.println("Current relative path is: " + s);
		
		if (!Files.exists(Paths.get(path)) || !Files.isRegularFile(Paths.get(path))) {
			throw new IllegalArgumentException("Input file not available: " + Paths.get(path) + "!");
		}
		
		Parser parser = new Parser(path);
		Scheduler scheduler = parser.parseInput();
		scheduler.search();
		
	}
	
}
