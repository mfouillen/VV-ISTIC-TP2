package fr.istic.vv;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;

public class FieldReporter extends PublicElementsPrinter {

    protected final String TAB = "  ";

    private List<String> requiredGetters;
    protected List<String> variables;
    
    protected void init(){
        System.out.println();
        requiredGetters = new ArrayList<>();
        variables = new ArrayList<>();
    }
    
    @Override
    public void visit(FieldDeclaration declaration, Void arg){
        for(VariableDeclarator variableDeclarator : declaration.getVariables()){
            // add field to the list of fields
            variables.add(variableDeclarator.getNameAsString());

            // every non public field must have a getter
            if(!declaration.isPublic()){
                requiredGetters.add(getterSignature(variableDeclarator));
            }
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
        super.visit(declaration, arg);
        
        // check if method is a required getter
        String signature = declaration.getDeclarationAsString(true, true);
        requiredGetters.remove(signature);

    }

    public void visitTypeDeclaration(TypeDeclaration<?> declaration, Void arg) {
        for(FieldDeclaration field : declaration.getFields()) {
            field.accept(this, arg);
        }

        for(MethodDeclaration method : declaration.getMethods()) {
            method.accept(this, arg);
        }
        
        super.visitTypeDeclaration(declaration, arg);
    }


    private String getterSignature(VariableDeclarator variableDeclarator){
        String name = variableDeclarator.getNameAsString();
        return "public " + variableDeclarator.getTypeAsString() + " get" + ((name.charAt(0) + "").toUpperCase()) + name.substring(1) + "()";
    }


    protected void writeReport(){
        System.out.println("\n" + TAB + "------REPORT------");
        System.out.println(TAB + "Variables : " + variables);

        // Missing getters
        for(String getter : requiredGetters) {
            System.out.println(TAB + "Missing getter : " + getter);
        }
    }
    
}
