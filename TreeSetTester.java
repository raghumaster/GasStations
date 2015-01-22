package gasstationapproach2;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author RaghuNandan
 */
public class TreeSetTester {
    public static void main(String[] args) {
        TreeSet<Node> sampleSet = new TreeSet<>(new NodeComparator());
        sampleSet.add(new Node(8,2));
        sampleSet.add(new Node(1,5));
        sampleSet.add(new Node(2,9));
        sampleSet.add(new Node(6,2));
        sampleSet.add(new Node(3,1));
        sampleSet.add(new Node(4,7));
        sampleSet.add(new Node(5,2));
        sampleSet.add(new Node(7,2));
        System.out.println(sampleSet);
        
        Iterator itr = sampleSet.iterator();
        Node updateNode = new Node(7,3);
        Node tempNode; boolean updateRequired=false;
        while(itr.hasNext()){
            tempNode = (Node)itr.next();
            if(Objects.equals(tempNode,updateNode)){
                System.out.println("Update Node found ..");
                if(tempNode.getWeight()>updateNode.getWeight()){
                    System.out.println("Condition is true");
                    tempNode.setWeight(updateNode.getWeight());
                    itr.remove();
                    updateRequired=true;
                    break;
                }
            }
        }       
        if(updateRequired){
            sampleSet.add(updateNode);
        }
        System.out.println(sampleSet);
        
        Node updateNode1 = new Node(7,1);
        if(sampleSet.contains(updateNode1)){
            System.out.println("Yes it contains ...");
        }
        sampleSet.add(updateNode1);
        System.out.println(sampleSet);
    }
}
