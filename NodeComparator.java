package gasstationapproach2;

import java.util.Comparator;

/**
 *
 * @author RaghuNandan
 */
public class NodeComparator implements Comparator<Node>{
    
    @Override
    public int compare(Node n1,Node n2) {
        if(n1.getWeight()>n2.getWeight()){
            return 1;
        }else if(n1.getWeight()==n2.getWeight()){
            if(n1.getName()>n2.getName()){
                return 1;
            }else if(n1.getName()<n2.getName()){
                return -1;
            }else{
                return 0;
            }
        }
        else{
            return -1;
        }
    }
}
