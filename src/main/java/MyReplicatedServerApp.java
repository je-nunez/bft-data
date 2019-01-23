
import bftsmart.tom.MessageContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.defaultservices.DefaultSingleRecoverable;

/**
 * Example replica that implements a BFT replicated service (a JsonString).
 */

public final class MyReplicatedServerApp extends DefaultSingleRecoverable  {

  private String jsonString = "";
  private MyReplicatedData replicatedData;
  private int iterations = 0;

  public MyReplicatedServerApp(int id) {
    new ServiceReplica(id, this, this);
  }

  @Override
  public byte[] appExecuteUnordered(byte[] command, MessageContext msgCtx) {
    iterations++;
    System.out.println("(" + iterations + ") "
                       + "Replicated Data: " + replicatedData);

    return MyReplicatedData.toStream(replicatedData);
  }

  @Override
  public byte[] appExecuteOrdered(byte[] command, MessageContext msgCtx) {
    iterations++;

    MyReplicatedData newValue = MyReplicatedData.fromStream(command);
    // some checks for validity here in real-life (not in an example)
    replicatedData = newValue;

    System.out.println("(" + iterations + ") "
                       + "New value was set = " + replicatedData);

    return MyReplicatedData.toStream(replicatedData);
  }

  public static void main(String[] args) {
    if (args.length < 1) {
      System.out.println("Use: java MyReplicatedServerApp <processId>");
      System.exit(-1);
    }
    new MyReplicatedServerApp(Integer.parseInt(args[0]));
  }


  @SuppressWarnings("unchecked")
  @Override
  public void installSnapshot(byte[] state) {
    replicatedData = MyReplicatedData.fromStream(state);
  }

  @Override
  public byte[] getSnapshot() {
    return MyReplicatedData.toStream(replicatedData);
  }
}
