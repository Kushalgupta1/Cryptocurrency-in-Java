package DSCoinPackage;

public class TransactionQueue {

  public Transaction firstTransaction;
  public Transaction lastTransaction;
  public int numTransactions;

  public void AddTransactions (Transaction transaction) {
    if(firstTransaction == null) firstTransaction = lastTransaction = transaction;
    else {
      lastTransaction.next = transaction;
      lastTransaction = transaction;
    }
    numTransactions++;
  }
  
  public Transaction RemoveTransaction () throws EmptyQueueException {
    if(numTransactions <=0 ) throw new EmptyQueueException();
    Transaction deleted = firstTransaction;
    firstTransaction = firstTransaction.next;
    numTransactions--;
    return deleted;
  }

  public int size() {
    return numTransactions;
  }
}
