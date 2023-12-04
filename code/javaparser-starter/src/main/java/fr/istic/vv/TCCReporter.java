package fr.istic.vv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.visitor.VoidVisitorWithDefaults;
import com.github.javaparser.utils.Pair;

public class TCCReporter extends VoidVisitorWithDefaults<Void>{

    private final String TAB = "  ";

    private List<String> variables;
    private Map<String, List<String>> methods;
    

    private void init(){
        System.out.println();
        variables = new ArrayList<>();
        methods = new HashMap<>();
    }

     /************* PARSE SECTION *************/

    // file
    @Override
    public void visit(CompilationUnit unit, Void arg) {
        init();
        for(TypeDeclaration<?> type : unit.getTypes()) {
            type.accept(this, null);
        }
        writeReport();
    }

    // class or interface
    @Override
    public void visit(ClassOrInterfaceDeclaration declaration, Void arg) {
        visitTypeDeclaration(declaration, arg);
    }

    // enum
    @Override
    public void visit(EnumDeclaration declaration, Void arg) {
        visitTypeDeclaration(declaration, arg);
    }


    // class
    public void visitTypeDeclaration(TypeDeclaration<?> declaration, Void arg) {
        System.out.println(declaration.getFullyQualifiedName().orElse("[Anonymous]"));

        for(FieldDeclaration field : declaration.getFields()) {
            field.accept(this, arg);
        }

        for(MethodDeclaration method : declaration.getMethods()) {
            method.accept(this, arg);
        }
        
    }

    // field
    @Override
    public void visit(FieldDeclaration declaration, Void arg){
        for(VariableDeclarator variableDeclarator : declaration.getVariables()){
            // add the variable to the list of variables
            variables.add(variableDeclarator.getNameAsString());
        }
    }

    // method
    @Override
    public void visit(MethodDeclaration declaration, Void arg) {
        
        // Find used variables;
        List<String> usedVariables = new ArrayList<>();
        if(declaration.getBody().isPresent()){
            for(String word : declaration.getBody().get().toString().split(" |;|this.|(|)")){
                if(!usedVariables.contains(word) && variables.contains(word)){
                    usedVariables.add(word);
                }
            }
        }
        methods.put(declaration.getNameAsString(), usedVariables);

        super.visit(declaration, arg);
    }

    /************* REPORT SECTION *************/

    private void writeReport() {
        System.out.println("\n" + TAB + "------REPORT------");

        // System.out.println(TAB + "Methods : " + methods);

        Map<Pair<String, String>, List<String>> cohesionMap = getCohesionMap();

        // System.out.println(TAB + "Cohesion Map : " + cohesionMap);

        long numberOfEdges = 0;

        for(Pair<String, String> edge : cohesionMap.keySet()){
            if(!cohesionMap.get(edge).isEmpty()){
                numberOfEdges++;
            }
        }

        System.out.println(TAB + "TCC = " + numberOfEdges + "/" + cohesionMap.keySet().size());
    }

    private Map<Pair<String, String>, List<String>> getCohesionMap(){
        Map<Pair<String, String>, List<String>> cohesionMap = new HashMap<>();

        Object[] keys = methods.keySet().toArray();
        for(int i = 0; i<keys.length; i++){
            for(int j = 0; j<keys.length/2; j++){
                if(i != j){

                    // find all fields used in both methods
                    List<String> commonFields = new ArrayList<>();
                    for(String field : methods.get(keys[i])){
                        if(methods.get(keys[j]).contains(field)){
                            commonFields.add(field);
                        }
                    }

                    cohesionMap.put(new Pair<String,String>(keys[i].toString(), keys[j].toString()), commonFields);
                }
            }
        }
        return cohesionMap;
    }
    
}
