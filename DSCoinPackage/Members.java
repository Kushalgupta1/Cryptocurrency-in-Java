package DSCoinPackage;

import java.util.*;
import HelperClasses.*;

public class Members
 {

  public String UID;
  public List<Pair<String, TransactionBlock>> mycoins;
  public Transaction[] in_process_trans;

  public void initiateCoinsend(String destUID, DSCoin_Honest DSobj) {
    Transaction tobj = new Transaction();
    tobj.Source = this;
    Members destMember = null;
    for (int i = 0; i < DSobj.memberlist.length; i++) {
      if(DSobj.memberlist[i].UID.equals(destUID)) destMember = DSobj.memberlist[i];
    }
    tobj.Destination = destMember;
    tobj.coinID = mycoins.get(0).first;
    tobj.coinsrc_block = mycoins.get(0).second;
    mycoins.remove(0);
 
    for (int i = 0; true; i++) {
      if (in_process_trans[i] == null) {
        in_process_trans[i] = tobj;
        break;
      } // adding at first empty slot
    }
    DSobj.pendingTransactions.AddTransactions(tobj);
  }

  //same function for dscoin_malicious
  public void initiateCoinsend(String destUID, DSCoin_Malicious DSobj) {
    Transaction tobj = new Transaction();
    tobj.Source = this;
    Members destMember = null;
    for (int i = 0; i < DSobj.memberlist.length; i++) {
      if(DSobj.memberlist[i].UID.equals(destUID)) destMember = DSobj.memberlist[i];
    }
    tobj.Destination = destMember;
    tobj.coinID = mycoins.get(0).first;
    tobj.coinsrc_block = mycoins.get(0).second;
    mycoins.remove(0);
 
    for (int i = 0; true; i++) {
      if (in_process_trans[i] == null) {
        in_process_trans[i] = tobj;
        break;
      } // adding at first empty slot
    }
    DSobj.pendingTransactions.AddTransactions(tobj);
  }


  public Pair<List<Pair<String, String>>, List<Pair<String, String>>> finalizeCoinsend (Transaction tobj, DSCoin_Honest DSObj) throws MissingTransactionException {
    // finding transactionblock
    TransactionBlock tB = DSObj.bChain.lastBlock; // block containing this transaction
    int transactionIdx = -1;
    while (tB != null) {
      for (int i = 0; i < DSObj.bChain.tr_count; i++) {
        if (tobj.isEqual(tB.trarray[i]) ) {
          // transaction found in this block
          transactionIdx = i;
          break;
        }
      }
      if (transactionIdx != -1) break;
      tB = tB.previous;
    }
    if (transactionIdx == -1) throw new MissingTransactionException(); // transaction not found
    List<Pair<String, String>> pathToRoot = pathToRootofTree(transactionIdx, tB.Tree); // leaf index same as index in trarray
    List<Pair<String, String>> dgstList = new ArrayList<Pair<String, String>>();

    String prev_dgst;
    if(tB.previous == null) prev_dgst = BlockChain_Honest.start_string;
    else prev_dgst = tB.previous.dgst;
    dgstList.add(new Pair<String, String>(prev_dgst, null)); // as has been mentioned, tB.previous is not null
    dgstList.add(new Pair<String, String>(tB.dgst, prev_dgst + "#" + tB.trsummary + "#" + tB.nonce));
    TransactionBlock currBlock = DSObj.bChain.lastBlock;
    while (currBlock != tB) {
      dgstList.add(2, new Pair<String, String>(currBlock.dgst, currBlock.previous.dgst + "#" + currBlock.trsummary + "#" + currBlock.nonce));
      currBlock = currBlock.previous;
    }

    for (int i = 0; true; i++) {
      if (in_process_trans[i] != null && in_process_trans[i].isEqual(tobj)) {
        in_process_trans[i] = null;
        break;
      }
    }
    Pair<String, TransactionBlock> coin = new Pair<String, TransactionBlock>(tobj.coinID, tB);
     //binary search to find index for coin
    int l = -1, r = tobj.Destination.mycoins.size();
    while (r - l != 1) {
      if (tobj.Destination.mycoins.get((int) ((l + r) / 2)).first.compareTo(tobj.coinID) > 0)
        r = (int) ((l + r) / 2);
      else
        l = (int) ((l + r) / 2);
    }
    tobj.Destination.mycoins.add(r, coin);
    

    return new Pair<List<Pair<String, String>>, List<Pair<String, String>>>(pathToRoot, dgstList);
  }

	public List<Pair<String,String>> pathToRootofTree(int leaf_idx, MerkleTree tree){
		TreeNode docNode = getLeafbyIdx(leaf_idx, tree); //leafnode corresponding to tobj
		TreeNode tempNode = docNode.parent;
		List<Pair<String,String>> pathToNode = new ArrayList<Pair<String,String>>();

		while(tempNode != null){
			pathToNode.add(new Pair<String, String>(tempNode.left.val, tempNode.right.val));
			tempNode = tempNode.parent;
		}
		pathToNode.add(new Pair<String, String>(tree.rootnode.val, null));
		return pathToNode;
	}


  public TreeNode getLeafbyIdx(int leaf_idx, MerkleTree tree){
		// traversing down to leaf corresponding to student_id
		TreeNode currNode = tree.rootnode;
		int leftmostLeaf = 0;
		int rightmostLeaf = tree.numdocs - 1; // maintaining left most and right most index of leaf of subtree rooted at currnode
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

  public void MineCoin(DSCoin_Honest DSObj) {
    int trcount = DSObj.bChain.tr_count;
    Transaction[] tra = new Transaction[trcount];
    int numTra = 0; //number of valid transactions added to tra

    while(numTra < trcount - 1) {
      boolean invalid = false;
      try {
        tra[numTra] = DSObj.pendingTransactions.RemoveTransaction();
      } 
      catch (EmptyQueueException e) {
        e.printStackTrace();
      }
      //check if coin already in tra
      for (int j = 0; j < numTra; j++) {
        if(tra[j].coinID.equals(tra[numTra].coinID)){
          //this coin is already being used
          invalid = true;
          break;
        }
      }
      if(invalid) continue; //skip this transaction

      //check if transaction is valid 
      if(DSObj.bChain.lastBlock.checkTransaction(tra[numTra]) == false) continue;

      //valid transaction
      numTra++;
    }

    Transaction minerRewardTransaction = new Transaction();
    minerRewardTransaction.Source = null;
    minerRewardTransaction.coinsrc_block = null;
    DSObj.latestCoinID = String.valueOf(Integer.parseInt(DSObj.latestCoinID) + 1); //increment latestCoinID
    minerRewardTransaction.coinID = DSObj.latestCoinID;
    minerRewardTransaction.Destination = this;
    tra[trcount-1] = minerRewardTransaction;

    TransactionBlock tB = new TransactionBlock(tra);
    DSObj.bChain.InsertBlock_Honest(tB);

    Pair<String, TransactionBlock> coin = new Pair<String, TransactionBlock>( minerRewardTransaction.coinID, tB);
    // binary search to find index for coin
    int l = -1, r = this.mycoins.size();
    while (r - l != 1) {
      if (this.mycoins.get((int) ((l + r) / 2)).first.compareTo(minerRewardTransaction.coinID) > 0)
        r = (int) ((l + r) / 2);
      else
        l = (int) ((l + r) / 2);
    }
    this.mycoins.add(r, coin);
  }  

  public void MineCoin(DSCoin_Malicious DSObj) {
    int trcount = DSObj.bChain.tr_count;
    Transaction[] tra = new Transaction[trcount];
    int numTra = 0; //number of valid transactions added to tra
    TransactionBlock lastBlock = DSObj.bChain.FindLongestValidChain();

    while(numTra < trcount - 1){
      boolean invalid = false;
      try {
        tra[numTra] = DSObj.pendingTransactions.RemoveTransaction();
      } 
      catch (EmptyQueueException e) {
        e.printStackTrace();
      }
      //check if coin already in tra
      for (int j = 0; j < numTra; j++) {
        if(tra[j].coinID.equals(tra[numTra].coinID)){
          //this coin is already being used
          invalid = true;
          break;
        }
      }
      if(invalid) continue; //skip this transaction

      //check if transaction is valid 
      if(lastBlock.checkTransaction(tra[numTra]) == false) continue;


      //valid transaction
      numTra++;
    }

    Transaction minerRewardTransaction = new Transaction();
    minerRewardTransaction.Source = null;
    minerRewardTransaction.coinsrc_block = null;
    DSObj.latestCoinID = String.valueOf(Integer.parseInt(DSObj.latestCoinID) + 1); 
    minerRewardTransaction.coinID = DSObj.latestCoinID; //latestCoinID stores the largest used coin
    minerRewardTransaction.Destination = this;
    tra[trcount-1] = minerRewardTransaction;

    TransactionBlock tB = new TransactionBlock(tra);
    DSObj.bChain.InsertBlock_Malicious(tB);;

    Pair<String, TransactionBlock> coin = new Pair<String, TransactionBlock>( minerRewardTransaction.coinID, tB);
    // binary search to find index for coin
    int l = -1, r = this.mycoins.size();
    while (r - l != 1) {
      if (this.mycoins.get((int) ((l + r) / 2)).first.compareTo(minerRewardTransaction.coinID) > 0)
        r = (int) ((l + r) / 2);
      else
        l = (int) ((l + r) / 2);
    }
    this.mycoins.add(r, coin);
  }  
}
