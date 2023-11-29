package fr.istic.vv;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.visitor.VoidVisitorWithDefaults;
import com.github.javaparser.utils.Pair;


// This class visits a compilation unit and
// prints all public enum, classes or interfaces along with their public methods
public class PublicElementsPrinter extends VoidVisitorWithDefaults<Void> {

    
    private final String TAB = "  ";

    private List<String> requiredGetters;
    private List<String> fields;
    private int numberOfMethods;
    private Map<String, List<String>> methods;

    private void init(){
        System.out.println();
        methods = new HashMap<>();
        requiredGetters = new ArrayList<>();
        fields = new ArrayList<>();
        numberOfMethods = 0;
    }

    private void writeTCCReport() {
        System.out.println("\n" + TAB + "------TCC REPORT------");

        System.out.println(TAB + methods);

        Map<Pair<String, String>, List<String>> map = new HashMap<>();

        Object[] keys = methods.keySet().toArray();
        for(int i = 0; i<keys.length; i++){
            for(int j = 0; j<keys.length/2; j++){
                if(i != j){

                    List<String> commonFields = new ArrayList<>();
                    for(String field : methods.get(keys[i])){
                        if(methods.get(keys[j]).contains(field)){
                            commonFields.add(field);
                        }
                    }
                    map.put(new Pair<String,String>(keys[i].toString(), keys[j].toString()), commonFields);
                }
            }
        }

        System.out.println(TAB + map);

        long numberOfEdges = 0;

        for(Pair<String, String> edge : map.keySet()){
            if(!map.get(edge).isEmpty()){
                numberOfEdges++;
            }
        }

        long numberOfCouples = map.keySet().size();
        
        System.out.println(TAB + "TCC = " + numberOfEdges + "/" + numberOfCouples);
    }


    private void writeReport(){
        System.out.println("\n" + TAB + "------REPORT------");

        // Missing getters
        for(String getter : requiredGetters) {
            System.out.println(TAB + "Missing getter : " + getter);
        }


        // TCC
        writeTCCReport();
    }

    @Override
    public void visit(CompilationUnit unit, Void arg) {
        for(TypeDeclaration<?> type : unit.getTypes()) {
            type.accept(this, null);
        }
    }

    private String getterSignature(FieldDeclaration field){
        String name = field.getVariables().getFirst().get().toString(); // TODO : this is not name but whole decleration
    
        return "public " + field.getCommonType() + " get" + ((name.charAt(0) + "").toUpperCase()) + name.substring(1) + "()";
    }


    public void visitTypeDeclaration(TypeDeclaration<?> declaration, Void arg) {
        //if(!declaration.isPublic()) return;
        System.out.println(declaration.getFullyQualifiedName().orElse("[Anonymous]"));

        // Printing fields declaration
        for(FieldDeclaration field : declaration.getFields()) {
            if(!field.isPublic()){
                requiredGetters.add(getterSignature(field));
            }

            fields.add(field.getVariables().getFirst().get().toString());
        } 

        for(MethodDeclaration method : declaration.getMethods()) {
            List<String> usedFields = new ArrayList<>();

            // Find used fields;
            for(String string : method.getBody().get().toString().split(" |;")){
                if(fields.contains(string)){
                    usedFields.add(string);
                }
            }

            methods.put(method.getNameAsString(), usedFields);
            method.accept(this, arg);
        }

  
        // Printing nested types in the top level
        for(BodyDeclaration<?> member : declaration.getMembers()) {
            if (member instanceof TypeDeclaration)
                member.accept(this, arg);
        }

    }

    @Override
    public void visit(ClassOrInterfaceDeclaration declaration, Void arg) {
        init();
        visitTypeDeclaration(declaration, arg);
        writeReport();
    }

    @Override
    public void visit(EnumDeclaration declaration, Void arg) {
        init();
        visitTypeDeclaration(declaration, arg);
        writeReport();
    }

    @Override
    public void visit(MethodDeclaration declaration, Void arg) {
        numberOfMethods++;
        if(!declaration.isPublic()) return;
        String signature = declaration.getDeclarationAsString(true, true);
        requiredGetters.remove(signature);
        System.out.println("  " + declaration.getDeclarationAsString(true, true));
    }

}
