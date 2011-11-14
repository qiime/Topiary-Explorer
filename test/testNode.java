import junit.framework.TestCase;
import topiaryexplorer.*;
import java.awt.*;
import java.util.*;

/**
 * testNode
 *
 * @author megumi
 * @since 08/04/10
 */
public class testNode extends TestCase {
 
    private String treeString = "(a:0.10000000149011612,b:0.20000000298023224,(c:0.30000001192092896,d:0.4000000059604645,((e:0.10000000149011612,f:0.20000000298023224,(g:0.30000001192092896,h:0.4000000059604645,i:0.10000000149011612):0.5):0.0):0.8999999761581421):0.5):0.0";
    private Node tree = TopiaryFunctions.createTreeFromNewickString(treeString);
    private ArrayList<String> leaves = new ArrayList<String>(Arrays.asList("a","b","c","d","e","f","g","h","i"));
    private ArrayList<String> nodes = new ArrayList<String>(Arrays.asList("a","b","c","d","e","f","g","h","i", "","","","",""));
    private ArrayList<String> lineages = new ArrayList<String>(Arrays.asList(
    "Bacteria; Actinobacteria; Actinobacteridae; Propionibacterineae; Propionibacterium",
    "Bacteria; Actinobacteria; Actinobacteridae; Gordoniaceae; Corynebacteriaceae",
    "Bacteria; Actinobacteria; Actinobacteridae",
    "Bacteria; Actinobacteria; Actinobacteridae; Gordoniaceae",
    "Bacteria; Proteobacteria; Gammaproteobacteria; Enterobacteriales",
    "Bacteria; Proteobacteria; Gammaproteobacteria; Pseudomonadaceae",
    "Bacteria; Proteobacteria; Gammaproteobacteria; Moraxellaceae",
    "Bacteria; Firmicutes; Alicyclobacillaceae; Alicyclobacillus; Bacillales",
    "Bacteria; Actinobacteria; Actinobacteridae"));
    private String consensusLineage100 = "Actinobacteria[100%];Actinobacteridae[100%];";
   
   public testNode(String name) {
       super(name);
   }
 
   public void testGetConsensusLineage100() {
       for(Node n: tree.nodes)
            n.setLineage(lineages.remove(0));
        for(Node n: tree.nodes)
            n.setConsensusLineage(n.getConsensusLineageF(1));
        tree.setConsensusLineage(tree.getConsensusLineageF(1));
       String test = tree.getConsensusLineage();
       assertEquals(consensusLineage100,test);
   }
   
   public void testGetNumberOfLeaves() {
       for(Node n: tree.getNodes())
            n.setNumberOfLeaves(n.getNumberOfLeavesF());
       tree.setNumberOfLeaves(tree.getNumberOfLeavesF());
       int test = tree.getNumberOfLeaves();
       assertEquals(leaves.size(), test);
   }
   
   public void testGetLeaves() {
       ArrayList<Node> testNodes = tree.getLeaves();
       ArrayList<String> testNames = new ArrayList<String>();
       for(Node n: testNodes)
        testNames.add(n.getName());
       assertEquals(leaves,testNames);
   }
   
   public void testGetNodes() {
       ArrayList<Node> testNodes = tree.getNodes();
       ArrayList<String> testNames = new ArrayList<String>();
       for(Node n: testNodes)
        testNames.add(n.getName());
       assertEquals(nodes,testNames);
   }
   
   public void testDepth() {
       for(Node n: tree.getNodes())
            n.setDepth(n.depthF());
        tree.setDepth(tree.depthF());
       double test = tree.depth();
       assertEquals(2.2999999821186066, test);
   }
   
   public void testShortestRootToTipDistance() {
       double test = tree.shortestRootToTipDistance();
       assertEquals(0.10000000149011612,test);
   }
   
   public void testLongestRootToTipDistance() {
       double test = tree.longestRootToTipDistance();
       assertEquals(2.2999999821186066,test);
   }
   
   public void testGetLongestLabel() {
       String test = tree.getLongestLabel();
       assertEquals("a",test);
   }
}