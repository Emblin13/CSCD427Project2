import java.util.ArrayList;

public class BTNodeInternal extends BTNode
{
   public ArrayList<BTNode> children;

   public BTNodeInternal(BPlusTree tree)
   {
      keys = new ArrayList<String>();
      children = new ArrayList<BTNode>();
      nodeID = tree.assignNodeID();
   }



   public void insert(String key, BPlusTree tree) {
      // Add a new key to the node from a leaf node
      //if (key != null && key.equals("information")) {
      //   System.out.println("information");
      //}
      if (key == null) {
         // Sort the keys
         for (int i = 0; i < keys.size(); i++) {
            for (int j = i + 1; j < keys.size(); j++) {
               if (keys.get(i).compareTo(keys.get(j)) > 0) {
                  String temp = keys.get(i);
                  keys.set(i, keys.get(j));
                  keys.set(j, temp);
               }
            }
         }
         // Sort the children
         for (int i = 0; i < children.size(); i++) {
            for (int j = i + 1; j < children.size(); j++) {
               if (children.get(i).keys.get(0).compareTo(children.get(j).keys.get(0)) > 0) {
                  BTNode temp = children.get(i);
                  children.set(i, children.get(j));
                  children.set(j, temp);
               }
            }
         }

         // Set the next leaf for the leaf nodes
         for (int i = 0; i < children.size(); i++) {
            if (children.get(i) instanceof BTNodeLeaf) {
               if (i + 1 < children.size()) {
                  ((BTNodeLeaf) children.get(i)).nextLeaf = (BTNodeLeaf) children.get(i + 1);
               }
            }
         }

         // Check if node is full, if so split
         if (keys.size() > SIZE) {
            // Create a new internal node
            BTNodeInternal newInternal = new BTNodeInternal(tree);
            // Get the middle index
            int mid = (SIZE + 1) / 2;
            // Move the first half of the keys to the new internal node
            for (int i = 0; i < mid; i++) {
               newInternal.keys.add(keys.get(0));
               keys.remove(0);
            }
            // Move the first half of the children to the new internal node
            for (int i = 0; i < mid+1; i++) {
               newInternal.children.add(children.get(0));
               children.remove(0);
            }
            // Add the new internal node to the parent
            if (parent == null) {
               BTNodeInternal newRoot = new BTNodeInternal(tree);
               newRoot.children.add(this);
               newRoot.children.add(newInternal);
               this.parent = newRoot;
               newInternal.parent = newRoot;
               tree.root = newRoot;
            } else {
               parent.children.add(newInternal);
               newInternal.parent = parent;
            }
            // Add the middle key to the parent
            parent.keys.add(keys.get(0));
            keys.remove(0);
            // Sort the parent keys
            parent.insert(null, tree);
         }
         return;
      }

      int index = findInsertIndex(key);
      children.get(index).parent = this;
      children.get(index).insert(key, tree);

   }

   public void printLeavesInSequence() {
      //print the leftmost child node
        children.get(0).printLeavesInSequence();
   }

   public void printStructureWKeys(int indent) {
      //print the tree structure in the console with node id, keys, and proper indentation
        for (int i = 0; i < keys.size(); i++) {
             for (int j = 0; j < indent; j++) {
                System.out.print(" ");
             }
             System.out.println(nodeID + " Key: " + keys.get(i));
             children.get(i).printStructureWKeys(indent + 1);
        }
        children.get(keys.size()).printStructureWKeys(indent + 1);
   }

   public int findInsertIndex(String word) {
      // find the index of the child to follow
      for (int i = 0; i < keys.size(); i++) {
         if (word.compareTo(keys.get(i)) < 0) {
            return i;
         }
      }
      return keys.size();
   }

   public Boolean rangeSearch(String startWord, String endWord)
   {
      boolean found = false;

      // Find the start index
      int startIndex = findInsertIndex(startWord);

      // Iterate over the child nodes that could contain the words within the range
      for (int i = startIndex; i < keys.size(); i++) {
         if (endWord.compareTo(keys.get(i)) < 0) {
            break;
         }
         found = children.get(i).rangeSearch(startWord, endWord) || found;
      }

      // Check the last child node if it hasn't been checked yet
      if (!found && endWord.compareTo(keys.get(keys.size() - 1)) >= 0) {
         found = children.get(children.size() - 1).rangeSearch(startWord, endWord);
      }

      return found;
   }

   public Boolean searchWord(String word)
   {
        //find the index of the child to follow
        int index = findInsertIndex(word);
        //search through the children nodes
        return children.get(index).searchWord(word);

   }






}
