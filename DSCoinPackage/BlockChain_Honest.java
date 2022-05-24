package DSCoinPackage;

import HelperClasses.CRF;

public class BlockChain_Honest {

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock lastBlock;

  public void InsertBlock_Honest (TransactionBlock newBlock) {
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
    lastBlock = newBlock;
  }
}
