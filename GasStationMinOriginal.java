
package gasstationapproach2;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

/**
 *
 * @author RaghuNandan
 */
public class GasStationMinOriginal {
    //Map for storing the given input graph in adjList format
    Map<Integer,Set<Node>> graphMapAdjList;
    
    Map<Integer,Set<Integer>> graphCoverageSets;
    
    Set<Integer> finalMinSet = new HashSet<>();
    String inputFileName = "gasstation_graph.txt";
//    String inputFileName = "gasstation_small_sample.txt";

    public static void main(String[] args) {
        GasStationMinOriginal obj = new GasStationMinOriginal();
        int k=30;
        obj.readInputGraph(k);
        obj.printGraphMap();
        for(Integer vertex:obj.graphMapAdjList.keySet()){
             obj.graphCoverageSets.put(vertex,obj.computeCoverageSet(vertex, k));
        }
        for(Integer vertex:obj.graphCoverageSets.keySet()){
            System.out.println(vertex+" : "+obj.graphCoverageSets.get(vertex));
        }
        System.out.println(obj.computeMaxCoverageVertex());
        obj.computeMinimumVertexCover();
        obj.printToFile();
    }
    
    public void computeMinimumVertexCover(){
        
        int maxCoverVertex,currentNeighbour,neighNeigbour;
        Iterator setIterator,neighbourIterator,neighNeighIterator;
        while(!graphCoverageSets.isEmpty()){
            maxCoverVertex = computeMaxCoverageVertex();
            neighbourIterator = graphCoverageSets.get(maxCoverVertex).iterator();
            while(neighbourIterator.hasNext()){
                currentNeighbour = (Integer)neighbourIterator.next();
                neighNeighIterator = graphCoverageSets.get(currentNeighbour).iterator();
                while(neighNeighIterator.hasNext()){
                    neighNeigbour = (Integer)neighNeighIterator.next();
                    if(neighNeigbour==maxCoverVertex)continue;
                    setIterator = graphCoverageSets.get(neighNeigbour).iterator();
                    while(setIterator.hasNext()){
                        if((Integer)setIterator.next()==currentNeighbour){
                            setIterator.remove();
                            break;
                        }
                    }
                }    
            }       
            for(Integer n:graphCoverageSets.get(maxCoverVertex)){
                graphCoverageSets.remove(n);
            }
            finalMinSet.add(maxCoverVertex);
            System.out.println("adding...."+maxCoverVertex+"..."+finalMinSet.size());
            graphCoverageSets.remove(maxCoverVertex);
        }
        System.out.println(finalMinSet);
    }
    
    public int computeMaxCoverageVertex(){
        int maxCoverVertex=-1,maxCoverSize=-1;
        for(Integer vertex:graphCoverageSets.keySet()){
            if(graphCoverageSets.get(vertex).size()>maxCoverSize){
                maxCoverSize = graphCoverageSets.get(vertex).size();
                maxCoverVertex = vertex;
            }
        }
        return maxCoverVertex;
    }
    
    public Set<Integer> computeCoverageSet(Integer vertex,int k){
        System.out.println("Computing coverage set "+vertex);
        //Set for storing final vertices which are within k miles from the given "vertex"
        Set<Integer> coverageSet = new HashSet<>();
        //Set for storing the current 
        Set<Node> coveredNodeSet = new LinkedHashSet<>();
        //Add the given vertex to the covered Set
        coveredNodeSet.add(new Node(vertex,0));
        //Create a new helper set to store the nodes to be added
        TreeSet<Node> helperNodeSet = new TreeSet<>(new NodeComparator());
        Set<Integer> helperVertexNameSet = new HashSet<>();
        //Add the all the current neighbours of the given vertex to the helperNodeSet
        //The elements in this set are always sorted based on distance (minimum distance from given vertex)
        helperNodeSet.addAll(graphMapAdjList.get(vertex));
        for(Node n:helperNodeSet){helperVertexNameSet.add(n.getName());}
        //Helper variables
        Node currentNode, tempNode=null, updateNode;
        int traveresedDistance=0;
        Iterator helperSetIterator; boolean isUpdateRequired=false;
        //Traverse each node of the helperNodeSet untill it is empty
        while(!helperNodeSet.isEmpty()){
            //Remove the first element of the helperNodeSet
            currentNode = helperNodeSet.pollFirst();
            helperVertexNameSet.remove(currentNode.getName());
            //****Check this
            //Add this to the coveredNodeSet
            coveredNodeSet.add(currentNode);
//            System.out.println("Now processing "+currentNode);
            //For every neighbour of the the current neighbour process each once
            for(Node neighbour:graphMapAdjList.get(currentNode.getName())){
                //Reset update Required to false
                isUpdateRequired=false;
                //Recompute the total traversed distance (neighbours weight + parents weight)
                traveresedDistance = neighbour.getWeight()+currentNode.getWeight();
                //if the node is already present in the coveredNodeSet ignore
                if(coveredNodeSet.contains(neighbour)){
                    
//                    System.out.println("Already present "+neighbour);
                }
                //Else if the traveresed distance is less than k process the node
                else if(traveresedDistance<=k)
                {    
//                    System.out.println("helper node set at start of iinner if : "+helperNodeSet+"   "+helperNodeSet.contains(neighbour)+" ...."+neighbour);
                    if(helperVertexNameSet.contains(neighbour.getName())){
//                        System.out.println("--------------------------------------------------------------------");
                        helperSetIterator = helperNodeSet.iterator();
                        while(helperSetIterator.hasNext()){
                            tempNode = (Node)helperSetIterator.next();
                            if(Objects.equals(tempNode,neighbour)){
//                                System.out.println("**************Update Node found .."+tempNode);
                                if(tempNode.getWeight()>traveresedDistance){
//                                    System.out.println("**************Condition is true");
                                    tempNode.setWeight(traveresedDistance);
                                    helperSetIterator.remove();
                                    isUpdateRequired=true;
                                    break;
                                }
                            }
                        }
                        if(isUpdateRequired && tempNode!=null){
                            helperNodeSet.add(tempNode);
                            helperVertexNameSet.add(tempNode.getName());
                        }
                    }else{
//                        System.out.println("last else adding to helper node set "+neighbour.getName()+":"+traveresedDistance);
                        //Add the neighbour to the tree set
                        helperNodeSet.add(new Node(neighbour.getName(),traveresedDistance));
                        helperVertexNameSet.add(neighbour.getName());
                    }
                }
            }
//            System.out.println("Coverage :"+coveredNodeSet);
//            System.out.println("Helper :"+helperNodeSet);
//            System.out.println("Helper Vertex Set :"+helperVertexNameSet);
            if(helperNodeSet.size()>=graphMapAdjList.keySet().size()){
                coveredNodeSet.addAll(helperNodeSet);
                break;
            }
        }
        //Extract name from the covered node set
        for(Node n:coveredNodeSet){
            coverageSet.add(n.getName());
        }
        coverageSet.remove(vertex);
        return coverageSet;
    }
    
    public GasStationMinOriginal() {
        graphMapAdjList = new HashMap<>();
        graphCoverageSets = new HashMap<>();
    }
    
    public void printGraphMap(){
        for(Integer key:graphMapAdjList.keySet()){
            System.out.println(key+" : "+graphMapAdjList.get(key));
        }
    }
    
    public void readInputGraph(int k){
        try {
            BufferedReader br = new BufferedReader(new FileReader(inputFileName));
            String str;
            int vertexCount=0,edgeCount=0,edgeWeight;
            Set<Node> adjWeightSet;
            Set<Integer> uniqueEdgeWeightSet = new HashSet<>();
            while((str=br.readLine())!=null){
                StringTokenizer vertexTokenizer = new StringTokenizer(str,"x");
                StringTokenizer edgeTokenizer;
                while(vertexTokenizer.hasMoreTokens()){
                    System.out.println("Readin..."+vertexCount);
                    edgeTokenizer = new StringTokenizer(vertexTokenizer.nextToken(),",");
                    adjWeightSet = new HashSet<>();
                    edgeCount=0;
                    while(edgeTokenizer.hasMoreElements()){

                        edgeWeight = Integer.parseInt(edgeTokenizer.nextToken());                       
                        //Add the edges to the graph which are less than or equal to k
                        if(edgeWeight>0 && edgeWeight<=k){
                            adjWeightSet.add(new Node(edgeCount,edgeWeight));
                            uniqueEdgeWeightSet.add(edgeWeight);
                        }
                        edgeCount++;
                    }
                    graphMapAdjList.put(vertexCount, adjWeightSet);
                    vertexCount++;
                }
            }
            System.out.println("Graph Initialized successfully...Vertices : "+vertexCount);
            System.out.println("Unique Edge Weight Count :"+uniqueEdgeWeightSet.size());
            System.out.println(uniqueEdgeWeightSet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void printToFile(){
        try {
        int countForme=0;
       PrintStream fileWriter1 = new PrintStream(new FileOutputStream("gasstationoutput1.txt")); 
       for(Integer i:finalMinSet){
           countForme++;
           fileWriter1.print(i+"x");
       }
       System.out.println("Final Count ..."+countForme);
       fileWriter1.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
    }
    
}
