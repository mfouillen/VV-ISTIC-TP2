package fr.istic.vv;

import com.github.javaparser.Problem;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.ast.visitor.VoidVisitorWithDefaults;
import com.github.javaparser.utils.SourceRoot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws IOException {
        if(args.length == 0) {
            System.err.println("Should provide the path to the source code");
            System.exit(1);
        }

        File file = new File(args[0]);
        if(!file.exists() || !file.isDirectory() || !file.canRead()) {
            System.err.println("Provide a path to an existing readable directory");
            System.exit(2);
        }

        SourceRoot root = new SourceRoot(file.toPath());

        
        if(args.length >= 2){
            if(args[1].equals("noGetter")){
                NoGetterReporter printer = new NoGetterReporter();
                root.parse("", (localPath, absolutePath, result) -> {
                    result.ifSuccessful(unit -> unit.accept(printer, null));
                    return SourceRoot.Callback.Result.DONT_SAVE;
                });
            }
            else if(args[1].equals("TCC")){
                TCCReporter printer = new TCCReporter();
                root.parse("", (localPath, absolutePath, result) -> {
                    result.ifSuccessful(unit -> unit.accept(printer, null));
                    return SourceRoot.Callback.Result.DONT_SAVE;
                });
            }
        }
        else {
            PublicElementsPrinter printer = new PublicElementsPrinter();
            root.parse("", (localPath, absolutePath, result) -> {
                result.ifSuccessful(unit -> unit.accept(printer, null));
                return SourceRoot.Callback.Result.DONT_SAVE;
            });
        }

        
    }
}
