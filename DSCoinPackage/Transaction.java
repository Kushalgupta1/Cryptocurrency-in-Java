package DSCoinPackage;

public class Transaction {

  public String coinID;
  public Members Source;
  public Members Destination;
  public TransactionBlock coinsrc_block;
  public Transaction next; //gives the next transaction in transaction queue

  public boolean isEqual(Transaction t){
    if(t == null) return false;
    if(this.Source == t.Source && this.Destination == t.Destination && this.coinID.equals(t.coinID) && this.coinsrc_block == t.coinsrc_block) return true;
    return false;
  }
}
