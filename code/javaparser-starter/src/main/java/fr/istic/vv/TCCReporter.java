package fr.istic.vv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.utils.Pair;

public class TCCReporter extends FieldReporter {

    private Map<String, List<String>> methods;

    @Override
    protected void init(){
        super.init();
        methods = new HashMap<>();
    }

    @Override
    public void visit(MethodDeclaration declaration, Void arg) {
        
        // Find used variables;
        List<String> usedVariables = new ArrayList<>();
        for(String word : declaration.getBody().get().toString().split(" |;|this.|(|)")){
            if(!usedVariables.contains(word) && variables.contains(word)){
                usedVariables.add(word);
            }
        }
        methods.put(declaration.getNameAsString(), usedVariables);

        super.visit(declaration, arg);
    }

    private void writeTCCReport() {
        System.out.println("\n" + TAB + "------TCC REPORT------");

        System.out.println(TAB + "Methods : " + methods);

        Map<Pair<String, String>, List<String>> cohesionMap = getCohesionMap();

        System.out.println(TAB + "Cohesion Map : " + cohesionMap);

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

    @Override
    protected void writeReport(){
        super.writeReport();
        writeTCCReport();
    }
    
}
