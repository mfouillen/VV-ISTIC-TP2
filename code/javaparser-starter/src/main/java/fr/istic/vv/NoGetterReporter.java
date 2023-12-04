package fr.istic.vv;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.visitor.VoidVisitorWithDefaults;

public class NoGetterReporter extends VoidVisitorWithDefaults<Void>{

    private final String TAB = "  ";

    private List<String> requiredGetters;
    private List<String> privateVariables;
    
    private void init(){
        System.out.println();
        requiredGetters = new ArrayList<>();
        privateVariables = new ArrayList<>();
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

        if(!declaration.isPublic()) return;

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
        System.out.println(TAB + declaration);
        for(VariableDeclarator variableDeclarator : declaration.getVariables()){

            // every private field should have a getter
            if(declaration.isPrivate()){

                // add the variable to the list of variables
                privateVariables.add(variableDeclarator.getNameAsString());
                requiredGetters.add(getterSignature(variableDeclarator));
            }
        }
    }

    // method
    @Override
    public void visit(MethodDeclaration declaration, Void arg) {        
        // check if method is a required getter
        String signature = declaration.getDeclarationAsString(true, true);
        requiredGetters.remove(signature);

    }

    /************* REPORT SECTION *************/

    private String getterSignature(VariableDeclarator variableDeclarator){
        String name = variableDeclarator.getNameAsString();
        return "public " + variableDeclarator.getTypeAsString() + " get" + ((name.charAt(0) + "").toUpperCase()) + name.substring(1) + "()";
    }

    private void writeReport(){
        System.out.println("\n" + TAB + "------REPORT------");
        
        // missing getters
        System.out.println(TAB + "Missing getters : " + requiredGetters);
    }
    
}
