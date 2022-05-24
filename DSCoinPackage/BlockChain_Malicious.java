package DSCoinPackage;

import HelperClasses.CRF;
import HelperClasses.MerkleTree;

import java.util.*;

public class BlockChain_Malicious {

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock[] lastBlocksList;

  public static boolean checkTransactionBlock (TransactionBlock tB) {
    String prev_dgst;
    CRF obj = new CRF(64);
    if(tB.previous == null) prev_dgst = start_string;
    else prev_dgst = tB.previous.dgst;

    if(tB.dgst.substring(0, 4).equals("0000") == false || tB.dgst.equals(obj.Fn(prev_dgst + "#" + tB.trsummary + "#" + tB.nonce) ) == false ) return false; //condition1

    //condition2 checking trsummary, finding rootnode val using queue (like done in build tree)
    List<String> q = new ArrayList<String>();
    MerkleTree temp = new MerkleTree();
    for (int i = 0; i < tB.trarray.length; i++) {
      Transaction t = tB.trarray[i];
      String leaf = temp.get_str(t); //computes leaf value appropriately
      q.add(leaf);
    }
    String l, r;
    while (q.size() > 1) {
      l = q.get(0);
      q.remove(0);
      r = q.get(0);
      q.remove(0);
      String data = obj.Fn(l + "#" + r);
      q.add(data);
    }
    String rootval = q.get(0);
    if(rootval.equals(tB.trsummary) == false) return false;

    //condition 3
    for (int i = 0; i < tB.trarray.length; i++) {
      Transaction t = tB.trarray[i];
      if(tB.previous.checkTransaction(t) == false ) return false;
    }    

    return true;
  }

  public TransactionBlock FindLongestValidChain () {
    int maxLength = 0; // length of max valid chain
    TransactionBlock tb = null; // required block to be returned i.e. last block of max valid chain

    for (int i = 0; i < lastBlocksList.length; i++) {
      if (lastBlocksList[i] == null)
        break; // end of list

      // find the length of the longest valid chain if we start from each endblock
      TransactionBlock currBlock = lastBlocksList[i];
      int currLength = 0; // length of max valid chain if we start from this currBlock
      TransactionBlock currTB = lastBlocksList[i]; // last block of max valid chain if we start from this currBlock
      while (currBlock.previous != null) { //first block is always valid as created by moderator
        if (checkTransactionBlock(currBlock))
          currLength++;
        else {
          // found an invalid block, hence currTB must be previous to this block
          currLength = 0;
          currTB = currBlock.previous;
        }
        currBlock = currBlock.previous;
      }
      if (currLength > maxLength) {
        // found a larger valid chain
        maxLength = currLength;
        tb = currTB;
      }
    }
    return tb;
  }

  public void InsertBlock_Malicious (TransactionBlock newBlock) {
    //if(lastBlocksList == null) {
      //empty chain
      //lastBlocksList = new TransactionBlock[100];
    //}
    //lastblockslist assumed to contain atleast one block which is inserted by the moderator
    TransactionBlock lastBlock = FindLongestValidChain(); //assumed to be not null as moderator is honest, newblock inserted after this lastblock
    CRF obj = new CRF(64);
    //finding nonce
    String prev_dgst;
    if(lastBlock == null) prev_dgst = start_string;
    else prev_dgst = lastBlock.dgst;
    int nonce_int = 1000000001;
    while(true){
      newBlock.dgst = obj.Fn(prev_dgst + "#" + newBlock.trsummary + "#" + nonce_int);
      if (newBlock.dgst.substring(0, 4).equals("0000")) {
        newBlock.nonce = String.valueOf(nonce_int);
        break;
      }
      nonce_int++;
    }//here it is assumed that a nonce can be found
    newBlock.previous = lastBlock;

    //only thing left is to update lastblockslist

    for (int i = 0; true; i++) { //assumed lastblockslist size is large enough 
      if(lastBlocksList[i] == null || lastBlocksList[i] == lastBlock) {
        // lastBlock not in the list, if present, lastblock replaced by newblock
        lastBlocksList[i] = newBlock; 
        return; 
      }
    }

  }
  public void InsertBlockbyModerator (TransactionBlock newBlock) {
    if (lastBlocksList == null) {
      // empty chain
      lastBlocksList = new TransactionBlock[100];
    }
    CRF obj = new CRF(64);
    //finding nonce
    String prev_dgst;
    if(lastBlocksList[0] == null) prev_dgst = start_string;
    else prev_dgst = lastBlocksList[0].dgst;
    int nonce_int = 1000000001;
    while(true){
      newBlock.dgst = obj.Fn(prev_dgst + "#" + newBlock.trsummary + "#" + nonce_int);
      if (newBlock.dgst.substring(0, 4).equals("0000")) {
        newBlock.nonce = String.valueOf(nonce_int);
        break;
      }
      nonce_int++;
    }//here it is assumed that a nonce can be found
    newBlock.previous = lastBlocksList[0];
    lastBlocksList[0] = newBlock;

  }
}
