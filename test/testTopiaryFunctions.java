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
 
   public testTopiaryFunctions(String name) {
       super(name);
   }
 
   public void test() {
       boolean test = true;
       assertEquals(true, test);
/*       System.out.println(treeString);
       System.out.println(tree);*/
   }
   
   public void testCreateTreeFromNewickString() {
       Node testTree = TopiaryFunctions.createTreeFromNewickString(treeString);
       assertEquals(tree, testTree);
   }
   
   public void testCreateNewickStringFromTree() {
       String testTreeString = TopiaryFunctions.createNewickStringFromTree(tree);
       assertEquals(treeString, testTreeString);
   }
}

