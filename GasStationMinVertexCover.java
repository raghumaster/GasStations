package gasstationapproach2;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

/**
 *
 * @author RaghuNandan
 */
public class GasStationMinVertexCover {
    //Map for storing the given input graph in adjList format
    Map<Integer,List<Node>> graphMapAdjList;
    //Map for storing the coverage vertex set for each node
    Map<Integer,Set<Integer>> graphCoverageSets;
    //Set for storing the final minimum vertex where the gas stations need to be placed
    Set<Integer> finalMinSet = new HashSet<>();
    
    //Input file names
//    String inputFileName = "gasstation_graph.txt";
  String inputFileName = "gasstationinput3.txt";
//  String inputFileName = "gasstation_small_sample.txt";
    
    //Helper Set used by computeCoverageSet() method (add as instance variables for optimizing the memory)
    //Set for storing the current node set
    Set<Node> coveredNodeSet = new LinkedHashSet<>();
    //Create a new helper set to store the nodes to be added
    TreeSet<Node> helperNodeSet = new TreeSet<>(new NodeComparator());
    Set<Integer> helperVertexNameSet = new HashSet<>();

    public static void main(String[] args) {
        GasStationMinVertexCover obj = new GasStationMinVertexCover();
        int k=50;
        obj.readInputGraph(k);
        obj.printGraphMap();
        //Compute the coverage set for each vertex and store them in the global hashMap
        for(Integer vertex:obj.graphMapAdjList.keySet()){
            obj.graphCoverageSets.put(vertex,obj.computeCoverageSet(vertex, k));
        }
        //Print the coverage sets
        for(Integer vertex:obj.graphCoverageSets.keySet()){
            System.out.println(vertex+" : "+obj.graphCoverageSets.get(vertex));
        }
        //Compute the min vertex cover using the coveragesets map
        obj.computeMinimumVertexCover();
        //Print the output to file
        obj.printToFile();
    }
    
    //Method that computes the min vertex cover from the graphCoverageSets
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
        
        //Set for storing final vertices which are within k miles from the given "vertex"
        Set<Integer> coverageSet = new HashSet<>();
        //Clear the global helper sets
        coveredNodeSet.clear();
        helperNodeSet.clear();
        helperVertexNameSet.clear();
        //Checking for memory consumption
//        System.out.println("Total Memory : "+Runtime.getRuntime().totalMemory()+"...free..."+Runtime.getRuntime().freeMemory());
//        System.out.println("Computing coverage set "+vertex);
        
        //Add the given vertex to the covered Set
        coveredNodeSet.add(new Node(vertex,0));
        
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
//            System.out.println("Helper Node Set Size :"+helperNodeSet.size()+"----"+graphMapAdjList.keySet().size());
            if(helperNodeSet.size()>=graphMapAdjList.keySet().size()-2){
                coveredNodeSet.addAll(helperNodeSet);
                break;
            }
        }
        //Extract name from the covered node set
        for(Node n:coveredNodeSet){
            coverageSet.add(n.getName());
        }
        coverageSet.remove(vertex);
        System.out.println("Coverage Set Size :"+coverageSet.size());
        return coverageSet;
    }
    
    public GasStationMinVertexCover() {
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
            List<Node> adjWeightList;
            Set<Integer> uniqueEdgeWeightSet = new HashSet<>();
            while((str=br.readLine())!=null){
                System.out.println("Readin...");
                StringTokenizer vertexTokenizer = new StringTokenizer(str,"x");
                StringTokenizer edgeTokenizer;
                while(vertexTokenizer.hasMoreTokens()){
                    edgeTokenizer = new StringTokenizer(vertexTokenizer.nextToken(),",");
                    adjWeightList = new ArrayList<>();
                    edgeCount=0;
                    System.out.println("parsing...."+vertexCount);
                    while(edgeTokenizer.hasMoreElements()){
                        
                        edgeWeight = Integer.parseInt(edgeTokenizer.nextToken());                       
                        //Add the edges to the graph which are less than or equal to k
                        if(edgeWeight>0 && edgeWeight<=k){
                            adjWeightList.add(new Node(edgeCount,edgeWeight));
                            //uniqueEdgeWeightSet.add(edgeWeight);
                        }
                        edgeCount++;
                    }
                    graphMapAdjList.put(vertexCount, adjWeightList);
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
       PrintStream fileWriter1 = new PrintStream(new FileOutputStream("gasstationoutput3.txt")); 
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
