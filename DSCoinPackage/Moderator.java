package DSCoinPackage;

import HelperClasses.Pair;

public class Moderator
 {

  public void initializeDSCoin(DSCoin_Honest DSObj, int coinCount) {
    TransactionBlock tB = null;
    int numBlocks = (int)(coinCount / DSObj.bChain.tr_count);
    int currCoin = 100000; //coin not yet used
    Members mod = new Members();
    mod.UID = "Moderator"; //other attributes of mod not used
    //will initialise DSObj.latestCoinID, DSObj.bChain.lastBlock appropriately
    //DSObj.bChain, DSObj.pendingTransactions, DSObj.memberlist, DSObj.bChain.tr_count should be initialised 
    for (int i = 0; i < numBlocks; i++) {
      //create and insert the block
      Transaction[] tra = new Transaction[DSObj.bChain.tr_count];
      for (int j = 0; j < tra.length; j++) {
        //create a transaction and store in the array
        Transaction t = new Transaction();
        t.coinsrc_block = null;
        t.Source = mod;
        t.Destination = DSObj.memberlist[(currCoin - 100000) % DSObj.memberlist.length]; //dest calculated based on the coinid
        t.coinID = String.valueOf(currCoin);
        tra[j] = t;
        currCoin++;
      }
      tB= new TransactionBlock(tra);
      DSObj.bChain.InsertBlock_Honest(tB);
    }
    //update mycoins of all
    while(tB!= null){
      for (int i = DSObj.bChain.tr_count - 1; i >=0 ; i--) {
        Pair<String, TransactionBlock> coin = new Pair<String, TransactionBlock>(tB.trarray[i].coinID , tB);
        tB.trarray[i].Destination.mycoins.add(0, coin); //coin added at the start as we proceed from last to first block
      }
      tB = tB.previous;
    }
    DSObj.latestCoinID = String.valueOf(currCoin - 1); //currCoin - 1 is the last coinid alloted
  }
    
  public void initializeDSCoin(DSCoin_Malicious DSObj, int coinCount) {
    TransactionBlock tB = null;
    int numBlocks = (int)(coinCount / DSObj.bChain.tr_count);
    int currCoin = 100000; //coin not yet used
    Members mod = new Members();
    mod.UID = "Moderator"; //other attributes of mod not used
    //will initialise DSObj.latestCoinID, DSObj.bChain.lastBlocksList appropriately
    //DSObj.bChain, DSObj.pendingTransactions, DSObj.memberlist, DSObj.bChain.tr_count should be initialised 
    for (int i = 0; i < numBlocks; i++) {
      //create and insert the block
      Transaction[] tra = new Transaction[DSObj.bChain.tr_count];
      for (int j = 0; j < tra.length; j++) {
        //create a transaction and store in the array
        Transaction t = new Transaction();
        t.coinsrc_block = null;
        t.Source = mod;
        t.Destination = DSObj.memberlist[(currCoin - 100000) % DSObj.memberlist.length]; //dest calculated based on the coinid
        t.coinID = String.valueOf(currCoin);
        tra[j] = t;
        currCoin++;
      }
      tB= new TransactionBlock(tra);
      DSObj.bChain.InsertBlockbyModerator(tB); // here use InsertBlockbyModerator which assumes moderator is honest, so no need to check blocks before adding
    }
    //update mycoins of all
    while(tB!= null){
      for (int i = DSObj.bChain.tr_count - 1; i >=0 ; i--) {
        Pair<String, TransactionBlock> coin = new Pair<String, TransactionBlock>(tB.trarray[i].coinID , tB);
        tB.trarray[i].Destination.mycoins.add(0, coin); //coin added at the start as we proceed from last to first block
      }
      tB = tB.previous;
    }
    DSObj.latestCoinID = String.valueOf(currCoin - 1); //currCoin - 1 is the last coinid alloted
  }
}
