
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import bftsmart.tom.ServiceProxy;

/**
 * Example client that updates a BFT replicated service (a JsonString).
 */
public class JsonStringClient {

    public static void main(String[] args) throws IOException {
        System.out.println("Testing JsonStringClient");
        if (args.length < 1) {
            System.out.println("Usage: java ... JsonStringClient <process id> <string-value>");
            System.out.println("       if <string-value> is empty, the request will be read-only");
            System.exit(-1);
        }

        ServiceProxy jsonStringProxy = new ServiceProxy(Integer.parseInt(args[0]));
        String jsonString = "";
        
        try {
            if (args.length >= 2) {
                jsonString = args[1];
            }
         
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            new DataOutputStream(out).writeUTF(jsonString);

            byte[] reply = (jsonString == "")?
                        jsonStringProxy.invokeUnordered(out.toByteArray()):
                	jsonStringProxy.invokeOrdered(out.toByteArray()); //magic happens here
                
            if (reply != null) {
                String newValue = new DataInputStream(new ByteArrayInputStream(reply)).readUTF();
                System.out.println(", returned value: " + newValue);
            } else {
                System.out.println(", ERROR! Exiting.");
            }
        } catch(IOException exc){
            jsonStringProxy.close();
        }
    }
}
