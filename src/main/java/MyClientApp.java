
import bftsmart.tom.ServiceProxy;

/**
 * Example client that updates a BFT replicated service (object type is of
 * custom class MyReplicatedData).
 */
public class MyClientApp {

  static MyReplicatedData loadObjectInClient(ServiceProxy clientProxy) {
    byte[] reply = clientProxy.invokeUnordered(null);
    if (reply != null) {
      return MyReplicatedData.fromStream(reply);
    } else {
      return null;
    }
  }

  public static void main(String[] args) {
    System.out.println("Testing MyClientApp");
    if (args.length < 1) {
      System.out.println(
            "Usage: java ... MyClientApp <process id> <string-value>" + "\n"
            + "       if <string-value> is empty, then do a read-only retrieve"
      );
      System.exit(-1);
    }

    ServiceProxy bftClientProxy = new ServiceProxy(Integer.parseInt(args[0]));

    MyReplicatedData remoteMyReplicatedData =
                                           loadObjectInClient(bftClientProxy);

    if (args.length < 2) {
      System.out.println("Retrieved value: " + remoteMyReplicatedData);
    } else {
      // we want to set the value of the remote replicated data
      // (this is an example)
      if (remoteMyReplicatedData == null) {
        remoteMyReplicatedData = new MyReplicatedData();  // initialize
      }

      remoteMyReplicatedData.limitedLog.add(args[1]);
      remoteMyReplicatedData.name = args[1];
      remoteMyReplicatedData.value =
                                remoteMyReplicatedData.limitedLog.size();

      byte[] reply = bftClientProxy.invokeOrdered(
                        MyReplicatedData.toStream(remoteMyReplicatedData)
                     );

      if (reply != null) {
        MyReplicatedData newRemoteValue = MyReplicatedData.fromStream(reply);
        System.out.println("Returned value: " + newRemoteValue);
      } else {
        System.out.println("ERROR!");
      }
    }

    bftClientProxy.close();
  }
}
