package gasstationapproach2;

import java.util.Comparator;

/**
 *
 * @author RaghuNandan
 */
public class Node{
   private int name;
   private int weight;

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    
    Node(Integer name,Integer currentTraversed){
        this.name = name;
        this.weight = currentTraversed;
    }
    
    @Override
    public String toString(){
        return name+":"+weight;
    }
    
    @Override
    public int hashCode(){
        return name;
    }
    
     @Override
    public boolean equals(Object o){
        return o.hashCode()==this.hashCode();
    } 
   
}
