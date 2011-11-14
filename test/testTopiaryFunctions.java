import junit.framework.*;
import topiaryexplorer.*;
import topiaryexplorer.TopiaryFunctions.*;

/**
 * testTopiaryFunctions.java
 *
 * @author megumi
 * @since 08/04/10
 */
public class testTopiaryFunctions extends TestCase {
    
    private String treeString = "(a:0.10000000149011612,b:0.20000000298023224,(c:0.30000001192092896,d:0.4000000059604645,((e:0.10000000149011612,f:0.20000000298023224,(g:0.30000001192092896,h:0.4000000059604645,i:0.10000000149011612):0.5):0.0):0.8999999761581421):0.5):0.0";
    private Node tree = TopiaryFunctions.createTreeFromNewickString(treeString);
    private double epsilon = 0.0000000001;
 
   public testTopiaryFunctions(String name) {
       super(name);
   }
   
   public void testCreateTreeFromNewickString() {
       // Create a tree based on a string
       Node testTree = TopiaryFunctions.createTreeFromNewickString(treeString);
       
       // Check root
       assertEquals("", testTree.getName());
       assertEquals(0, testTree.getBranchLength(), epsilon);
       assertEquals(3, testTree.nodes.size());
       assertSame(null, testTree.getParent());
       
       // Check 'a'
       assertEquals("a", testTree.nodes.get(0).getName());
       assertEquals(0.10000000149011612, testTree.nodes.get(0).getBranchLength(), epsilon);
       assertEquals(0, testTree.nodes.get(0).nodes.size());
       assertSame(testTree, testTree.nodes.get(0).getParent());
       
        // Check 'b'
       assertEquals("b", testTree.nodes.get(1).getName());
       assertEquals(0.20000000298023224, testTree.nodes.get(1).getBranchLength(), epsilon);
       assertEquals(0, testTree.nodes.get(1).nodes.size());
       assertSame(testTree, testTree.nodes.get(1).getParent());
       
        // Check 'c'
       assertEquals("c", testTree.nodes.get(2).nodes.get(0).getName());
       assertEquals(0.30000001192092896, testTree.nodes.get(2).nodes.get(0).getBranchLength(), epsilon);
       assertEquals(0, testTree.nodes.get(2).nodes.get(0).nodes.size());
       assertSame(testTree.nodes.get(2), testTree.nodes.get(2).nodes.get(0).getParent());
       
        // Check 'd'
       assertEquals("d", testTree.nodes.get(2).nodes.get(1).getName());
       assertEquals(0.4000000059604645, testTree.nodes.get(2).nodes.get(1).getBranchLength(), epsilon);
       assertEquals(0, testTree.nodes.get(2).nodes.get(1).nodes.size());
       assertSame(testTree.nodes.get(2), testTree.nodes.get(2).nodes.get(1).getParent());
       
        // Check 'e'
       assertEquals("e", testTree.nodes.get(2).nodes.get(2).nodes.get(0).nodes.get(0).getName());
       assertEquals(0.10000000149011612, testTree.nodes.get(2).nodes.get(2).nodes.get(0).nodes.get(0).getBranchLength(), epsilon);
       assertEquals(0, testTree.nodes.get(2).nodes.get(2).nodes.get(0).nodes.get(0).nodes.size());
       assertSame(testTree.nodes.get(2).nodes.get(2).nodes.get(0), testTree.nodes.get(2).nodes.get(2).nodes.get(0).nodes.get(0).getParent());
       
        // Check 'f'
       assertEquals("f", testTree.nodes.get(2).nodes.get(2).nodes.get(0).nodes.get(1).getName());
       assertEquals(0.20000000298023224, testTree.nodes.get(2).nodes.get(2).nodes.get(0).nodes.get(1).getBranchLength(), epsilon);
       assertEquals(0, testTree.nodes.get(2).nodes.get(2).nodes.get(0).nodes.get(1).nodes.size());
       assertSame(testTree.nodes.get(2).nodes.get(2).nodes.get(0), testTree.nodes.get(2).nodes.get(2).nodes.get(0).nodes.get(1).getParent());
       
        // Check 'g'
       assertEquals("g", testTree.nodes.get(2).nodes.get(2).nodes.get(0).nodes.get(2).nodes.get(0).getName());
       assertEquals(0.30000001192092896, testTree.nodes.get(2).nodes.get(2).nodes.get(0).nodes.get(2).nodes.get(0).getBranchLength(), epsilon);
       assertEquals(0, testTree.nodes.get(2).nodes.get(2).nodes.get(0).nodes.get(2).nodes.get(0).nodes.size());
       assertSame(testTree.nodes.get(2).nodes.get(2).nodes.get(0).nodes.get(2), 
       			testTree.nodes.get(2).nodes.get(2).nodes.get(0).nodes.get(2).nodes.get(0).getParent());
       
        // Check 'h'
       assertEquals("h", testTree.nodes.get(2).nodes.get(2).nodes.get(0).nodes.get(2).nodes.get(1).getName());
       assertEquals(0.4000000059604645, testTree.nodes.get(2).nodes.get(2).nodes.get(0).nodes.get(2).nodes.get(1).getBranchLength(), epsilon);
       assertEquals(0, testTree.nodes.get(2).nodes.get(2).nodes.get(0).nodes.get(2).nodes.get(1).nodes.size());
	   assertSame(testTree.nodes.get(2).nodes.get(2).nodes.get(0).nodes.get(2), 
	   			testTree.nodes.get(2).nodes.get(2).nodes.get(0).nodes.get(2).nodes.get(1).getParent());
       
        // Check 'i'
       assertEquals("i", testTree.nodes.get(2).nodes.get(2).nodes.get(0).nodes.get(2).nodes.get(2).getName());
       assertEquals(0.10000000149011612, testTree.nodes.get(2).nodes.get(2).nodes.get(0).nodes.get(2).nodes.get(2).getBranchLength(), epsilon);
       assertEquals(0, testTree.nodes.get(2).nodes.get(2).nodes.get(0).nodes.get(2).nodes.get(2).nodes.size());
	   assertSame(testTree.nodes.get(2).nodes.get(2).nodes.get(0).nodes.get(2), 
	   			testTree.nodes.get(2).nodes.get(2).nodes.get(0).nodes.get(2).nodes.get(2).getParent());
       
       
   }
   
   public void testCreateNewickStringFromTree() {
   		// We tested that creating a tree worked correctly in testCreateTreeFromNewickString(), so 
   		// now we make sure we can get back to the original string.
       String testTreeString = TopiaryFunctions.createNewickStringFromTree(tree);
       assertEquals(treeString, testTreeString);
   }
   
   public void testObjectify() {
   		// Test integer
   		assertTrue(TopiaryFunctions.objectify("5") instanceof Integer);
   		// Test floating point number
   		assertTrue(TopiaryFunctions.objectify("5.5") instanceof Double);
   		// Test other thing
   		assertTrue(TopiaryFunctions.objectify("qwq23zxadzf.asx") instanceof String);
   		
   }
   
}

