import java.nio.file.*;
import java.util.*;
import javax.tools.*;
import com.sun.source.util.JavacTask;
public class ParseJava {
    public static void main(String[] args) throws Exception {
        var compiler = ToolProvider.getSystemJavaCompiler();
        var diagnostics = new DiagnosticCollector<JavaFileObject>();
        try (var manager = compiler.getStandardFileManager(diagnostics, null, null);
             var paths = Files.walk(Path.of(args[0]))) {
            var files = paths.filter(p -> p.toString().endsWith(".java")).map(Path::toFile).toList();
            var task = (JavacTask)compiler.getTask(null, manager, diagnostics,
                    List.of("--release", "21", "-proc:none"), null, manager.getJavaFileObjectsFromFiles(files));
            task.parse();
            long errors = diagnostics.getDiagnostics().stream().filter(d -> d.getKind() == Diagnostic.Kind.ERROR).count();
            for (var d: diagnostics.getDiagnostics()) if (d.getKind() == Diagnostic.Kind.ERROR) System.out.println(d);
            System.out.println("Java syntax parse only: " + files.size() + " source files; " + errors + " errors. No type checking or Mixin validation.");
            if (errors != 0) System.exit(1);
        }
    }
}
