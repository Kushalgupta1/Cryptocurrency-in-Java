package HelperClasses;

import DSCoinPackage.Transaction;
import java.util.*;

public class MerkleTree {

  // Check the TreeNode.java file for more details
  public TreeNode rootnode;
  public int numdocs;

  void nodeinit(TreeNode node, TreeNode l, TreeNode r, TreeNode p, String val, boolean isLeaf) {
    node.left = l;
    node.right = r;
    node.parent = p;
    node.val = val;
    node.isLeaf = isLeaf;
  }

  public String get_str(Transaction tr) {
    CRF obj = new CRF(64);
    String val = tr.coinID;
    if (tr.Source == null)
      val = val + "#" + "Genesis"; 
    else
      val = val + "#" + tr.Source.UID;

    val = val + "#" + tr.Destination.UID;

    if (tr.coinsrc_block == null)
      val = val + "#" + "Genesis";
    else
      val = val + "#" + tr.coinsrc_block.dgst;

    return obj.Fn(val);
  }

  public String Build(Transaction[] tr) {
    CRF obj = new CRF(64);
    int num_trans = tr.length;
    numdocs = num_trans;
    List<TreeNode> q = new ArrayList<TreeNode>();
    for (int i = 0; i < num_trans; i++) {
      TreeNode nd = new TreeNode();
      String val = get_str(tr[i]);
      nodeinit(nd, null, null, null, val, true);
      q.add(nd);
    }
    TreeNode l, r;
    while (q.size() > 1) {
      l = q.get(0);
      q.remove(0);
      r = q.get(0);
      q.remove(0);
      TreeNode nd = new TreeNode();
      String l_val = l.val;
      String r_val = r.val;
      String data = obj.Fn(l_val + "#" + r_val);
      nodeinit(nd, l, r, null, data, false);
      l.parent = nd;
      r.parent = nd;
      q.add(nd);
    }
    rootnode = q.get(0);

    return rootnode.val;
  }	
  /*
	public List<Pair<String,String>> pathToRootofTree(Transaction tobj, Transaction[] trarray){
		// Implement Code here
    int leaf_idx = 0;
    for (int i = 0; i < trarray.length; i++) {
      if(trarray[i].isEqual(tobj) ) {
        leaf_idx = i;
        break;
      }
    }
		TreeNode docNode = getLeafbyIdx(leaf_idx); //leafnode corresponding to tobj
		TreeNode tempNode = docNode.parent;
		List<Pair<String,String>> pathToNode = new ArrayList<Pair<String,String>>();

		while(tempNode != null){
			pathToNode.add(new Pair<String, String>(tempNode.left.val, tempNode.right.val));
			tempNode = tempNode.parent;
		}
		pathToNode.add(new Pair<String, String>(rootnode.val, null));
		return pathToNode;
	}


  public TreeNode getLeafbyIdx(int leaf_idx){
		// traversing down to leaf corresponding to student_id
		TreeNode currNode = rootnode;
		int leftmostLeaf = 0;
		int rightmostLeaf = numdocs - 1; // maintaining left most and right most index of leaf of subtree rooted at currnode

		while (!currNode.isLeaf) { // stop if a leaf is reached
			// deciding whether to go to left subtree or right subtree
			if (leaf_idx <= (leftmostLeaf + rightmostLeaf - 1) / 2) {
				// go to left subtree
				currNode = currNode.left;
				rightmostLeaf = (leftmostLeaf + rightmostLeaf - 1) / 2;
			} else {// go to right subtree
				currNode = currNode.right;
				leftmostLeaf = (leftmostLeaf + rightmostLeaf - 1) / 2 + 1;
			}
		}
		return currNode;
	}
  */
}
