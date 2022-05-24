package DSCoinPackage;

import HelperClasses.MerkleTree;
import HelperClasses.CRF;

public class TransactionBlock {

  public Transaction[] trarray;
  public TransactionBlock previous;
  public MerkleTree Tree;
  public String trsummary;
  public String nonce;
  public String dgst;

  TransactionBlock(Transaction[] t) {
    int trcount = t.length;
    trarray = new Transaction[trcount];
    for (int i = 0; i < trcount; i++) {
      trarray[i] = t[i]; 
    }
    Tree = new MerkleTree();
    trsummary = Tree.Build(trarray);
    previous = null; //previous, nonce and dgst computed while adding to blockchain
    dgst = nonce = null;
  }

  public boolean checkTransaction (Transaction t) {
    if (t.coinsrc_block == null) return true; //assumed valid as mentioned in the project
    //condition1 finding the coinid in coinsrc_block
    boolean condition1 = false;
    for (int i = 0; i < t.coinsrc_block.trarray.length; i++) {
      Transaction tra = t.coinsrc_block.trarray[i];
      if (tra.coinID.equals(t.coinID) && tra.Destination.UID.equals(t.Source.UID)) {
        condition1 = true;
        break;
      }
    }
    if(condition1 == false) return false;
    //condition2 checking for double spending
    //TransactionBlock currBlock = this.previous;
    TransactionBlock currBlock = this;
    while(currBlock != t.coinsrc_block){
      for (int i = 0; i < currBlock.trarray.length; i++) {
        Transaction tra = currBlock.trarray[i];
        if (tra.coinID.equals(t.coinID)) {
          return false;
        } // double spending found
      }
      currBlock = currBlock.previous;
    }
    return true;
  }
}
