package topiarytool;


import java.io.*;
import java.util.*;
import javax.swing.*;
import java.text.*;

public class TopiaryFunctions {

    public static ArrayList<String> tokenizeTree(String tree) {
        ArrayList<String> goodTokens = new ArrayList<String>();
        goodTokens.add("(");
        goodTokens.add(":");
        goodTokens.add(")");
        goodTokens.add(",");
        goodTokens.add(";");
    
        ArrayList<String> result = new ArrayList<String>();
        String saved = "";
        boolean inQuotes = false;
        CharacterIterator it = new StringCharacterIterator(tree);
        for (char ch=it.first(); ch!=CharacterIterator.DONE; ch=it.next()) {
            if (Character.toString(ch).equals("'")) {
                inQuotes = !inQuotes;
            }
            if (goodTokens.contains(Character.toString(ch)) && !inQuotes) {
                String curr = saved;
                while (curr.length() > 0 && curr.charAt(0) == ' ') curr = curr.substring(1);
                while (curr.length() > 0 && curr.charAt(curr.length()-1) == ' ') curr = curr.substring(0, curr.length()-1);
                if (curr.length() > 0) {
                    result.add(curr);
                }
                result.add(Character.toString(ch));
                saved = "";
            } else {
                saved += Character.toString(ch);
            }
                
        }
        return result;
    }
    
    /*
     * Return the head of a tree which is created from the given Newick string.  The algorithm is iterative
     * so should work for large trees.  It is adapted from the DndParser() in PyCogent.
     *
     * @param  data  the string to parse
     */
    public static Node createTreeFromNewickString(String data) {
        int left_count = data.replaceAll("[^(]","").length();
        int right_count = data.replaceAll("[^)]","").length();
        if (left_count != right_count) {
          throw new RuntimeException("Number of open and close parentheses are not the same in tree string.");
        }
        
        //remove whitespace
        data = data.replaceAll("[ |\n|\r]", "");
        
        ArrayList<String> tokens = tokenizeTree(data);
        Node curr_node = null;
        String state = "PreColon";
        String state1 = "PreClosed";
        for (int i = 0; i < tokens.size(); i++) {
          String t = tokens.get(i);
          if (t.equals(":")) {  //expecting branch length
              state = "PostColon";
              //prevent state reset
              continue;
          }
          if (t.equals(")")) {  //closing the current node
              curr_node = curr_node.getParent();
              state1 = "PostClosed";
              continue;
          }
          if (t.equals("(")) {    //opening a new node
              Node temp_node = new Node();
              if (curr_node != null) {
                curr_node.addChild(temp_node);
              }
              curr_node = temp_node;
          }
          else if (t.equals(";")) {  //end of data
              break;
          }
          else if (t.equals(",")) {  //separator: next node adds to this node's parent
              curr_node = curr_node.getParent();
          }
          else if (state.equals("PreColon") && state1.equals("PreClosed")) {   //data for the current node
              Node temp_node = new Node();
              if (curr_node != null) {
                curr_node.addChild(temp_node);
              }
              curr_node = temp_node;
              //remove surrounding quotes from label
              while (t.charAt(0) == '\'' || t.charAt(0) == '"') t = t.substring(1);
              while (t.charAt(t.length()-1) == '\'' || t.charAt(t.length()-1) == '"') t = t.substring(0,t.length()-1);
              curr_node.setLabel(t.replace("'", "").replace("\"", ""));
          }
          else if (state.equals("PreColon") && state1.equals("PostClosed")) {
              curr_node.setLabel(t);
          }
          else if (state.equals("PostColon")) {  //length data for the current node
              float bl = Float.valueOf(t).floatValue();
              //make sure there are no negative branch lengths
              if (bl < 0) { bl = 0; }
              curr_node.setBranchLength(bl);
          }
          else {   //can't think of a reason to get here
             throw new RuntimeException("Incorrect PhyloNode state.");
          }
          state = "PreColon";  //get here for any non-colon token
          state1 = "PreClosed";
        }

        if (curr_node != null && curr_node.getParent() != null){
          throw new RuntimeException("Didn't get back to root of tree.");
        }

        if (curr_node == null) {       //no data -- return empty node
            return new Node();
        }
        return curr_node;    //this should be the root of the tree
    }


    /**
     * Load a Newick-formatted tree from the given file and returns the head node of the tree.
     */
    public static Node createTreeFromNewickFile(File file) {
      String newickString = "";
      try {
        Scanner scanner = new Scanner(file);
        scanner.useDelimiter("\\Z");
        newickString = scanner.next();
        scanner.close();
      }
      catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Unable to load " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      }
      return createTreeFromNewickString(newickString);
    }

    public static Object objectify (String str) {
        try {
                int i = Integer.parseInt(str);
				return i;
			} catch (NumberFormatException nfx) {
                try {
                    double f = Double.parseDouble(str);
                    return f;
                } catch (NumberFormatException nfx2) {
                    if (str.equals("NA")) {
                        return null;
                    } else {
                        return str;
                    }
                }
			}
	}
    
}
