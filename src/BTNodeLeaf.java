import java.util.ArrayList;

public class BTNodeLeaf extends BTNode
{
    public ArrayList<Integer> keyCounts;
    public BTNodeLeaf nextLeaf;

    public BTNodeLeaf(BPlusTree tree)
    {
        keys = new ArrayList<String>();
        keyCounts = new ArrayList<Integer>();
        nextLeaf = null;
        nodeID = tree.assignNodeID();
    }


    public void insert(String word, BPlusTree tree) {
        word = word.toLowerCase();
        int index = findInsertIndex(word);
        if (index == -2) {
            return;
        }

        // if the leaf is full, split the node
        if (index == -1) {
            // add the word in alphabetical order to the current leaf
            int keyIndex = -1;
            for (int i = 0; i < keys.size(); i++) {
                if (word.compareTo(keys.get(i)) < 0) {
                    keys.add(i, word);
                    keyCounts.add(i, 1);
                    keyIndex = i;
                    break;
                }
            }
            if (keyIndex == -1) {
                keys.add(word);
                keyCounts.add(1);
                keyIndex = keys.size() - 1;
            }

            // create a new leaf to split the current leaf data into
            BTNodeLeaf newLeaf = new BTNodeLeaf(tree);
            // create a new internal node if the leaf is the root
            BTNodeInternal internalNode;
            if (this.parent == null) {
                internalNode = new BTNodeInternal(tree);

                internalNode.children.add(this);
                this.parent = internalNode;

                internalNode.children.add(newLeaf);
                newLeaf.parent = internalNode;
            } else {
                internalNode = this.parent;
                internalNode.children.add(newLeaf);
                newLeaf.parent = internalNode;
            }

            // get ceiling of SIZE/2
            int mid = (SIZE + 1) / 2;
            // move the first half of the keys and keyCounts to the new leaf
            for (int i = 0; i < mid; i++) {
                newLeaf.keys.add(keys.get(0));
                newLeaf.keyCounts.add(keyCounts.get(0));
                keys.remove(0);
                keyCounts.remove(0);
            }

            internalNode.keys.add(keys.get(0));

            internalNode.insert(null, tree);

            // sequence the leaves
            newLeaf.nextLeaf = this;

            // if the leaf is the root, set the new internal node as the new root
            if (tree.root == this) {
                tree.root = internalNode;
            }
            return;
        }
        // end of splitting the leaf

        // add the word to the leaf
        if (index == keys.size()){
            keys.add(index, word);
            keyCounts.add(index, 1);
        } else if (!keys.get(index).equals(word)) {
            keys.add(index, word);
            keyCounts.add(index, 1);
        } else {
            keyCounts.set(index, keyCounts.get(index) + 1);
        }
        // end of adding the word to the leaf
    }

    public void printLeavesInSequence() {
        //print the leaves in sequence
        BTNodeLeaf current = this;
        while (current != null) {
            for (int i = 0; i < current.keys.size(); i++) {
                System.out.println("NodeID: " + current.nodeID + ", key: " + current.keys.get(i) + " " + current.keyCounts.get(i));
            }
            current = current.nextLeaf;
        }
    }

    public void printStructureWKeys(int indent) {
        //print the structure of the tree with the node id, keys, and proper indentation
        for (int i = 0; i < keys.size(); i++) {
            for (int j = 0; j < indent; j++) {
                System.out.print(" ");
            }
            System.out.println(nodeID + " " + keys.get(i) + " " + keyCounts.get(i));
        }
    }

    public int findInsertIndex(String word) {
        // check if word is already in keys
        for (int i = 0; i < keys.size(); i++) {
            if (keys.get(i).equals(word)) {
                keyCounts.set(i, keyCounts.get(i) + 1);
                return -2;
            }
        }
        // if word is not in keys, check if there is an empty key
        if (keys.size() < SIZE) {
            for (int i = 0; i < keys.size(); i++) {
                // get the alphabetical order of the word and the key
                if (word.compareTo(keys.get(i)) < 0) {
                    return i;
                }
            }
            return keys.size();
        }
        return -1;
    }

    public Boolean rangeSearch(String startWord, String endWord)
    {
        //range search for the word between startWord and endWord by searching through the leaf nodes
        boolean found = false;

        BTNodeLeaf current = this;
        while(current != null)
        {
            for(int i = 0; i < current.keys.size(); i++)
            {
                //Check if the word is within the range
                if(current.keys.get(i).compareTo(startWord) >= 0 && current.keys.get(i).compareTo(endWord) <= 0)
                {
                    System.out.println(current.keys.get(i) + " " + current.keyCounts.get(i));
                    found = true;
                }
            }
            current = current.nextLeaf;
        }

        return found;
    }

    public Boolean searchWord(String word){

        boolean found = false;
        //search for the word in the leaf node
        for(int i = 0; i < keys.size(); i++)
        {
            if(keys.get(i).equals(word))
            {
                System.out.println(keys.get(i) + " " + keyCounts.get(i));
                found = true;
            }
        }

        return found;
    }
}