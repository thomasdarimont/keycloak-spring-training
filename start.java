import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class start {

    public static void main(String[] args) throws Exception {

        System.out.println("### Starting Keycloak System...");

        if (!Files.exists(Path.of("keycloak/extensions/target/extensions.jar"))) {
            System.out.println("Keycloak extensions.jar not found, please run `mvn clean verify` first.");
            System.exit(-1);
        }

        var commandLine = new ArrayList<String>();
        commandLine.add("docker");
        commandLine.add("compose");
        commandLine.add("--file");
        commandLine.add("./docker-compose-infra.yml");
        commandLine.add("--file");
        commandLine.add("./docker-compose-keycloak.yml");
        commandLine.add("up");

        System.out.printf("Running command: %s%n", String.join(" ", commandLine));

        var pb = new ProcessBuilder(commandLine);
        pb.directory(new File("."));
        pb.inheritIO();
        var process = pb.start();
        var exitCode = process.waitFor();

        System.out.println("Started");

        System.exit(exitCode);
    }
}
