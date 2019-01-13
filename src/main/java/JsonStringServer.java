
import bftsmart.tom.MessageContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.defaultservices.DefaultSingleRecoverable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

/**
 * Example replica that implements a BFT replicated service (a JsonString).
 */

public final class JsonStringServer extends DefaultSingleRecoverable  {
    
    private String jsonString = "";
    private int iterations = 0;
    
    public JsonStringServer(int id) {
    	new ServiceReplica(id, this, this);
    }
            
    @Override
    public byte[] appExecuteUnordered(byte[] command, MessageContext msgCtx) {         
        iterations++;
        System.out.println("(" + iterations + ") JSON String value: " + jsonString);
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream(4);
            new DataOutputStream(out).writeUTF(jsonString);
            return out.toByteArray();
        } catch (IOException ex) {
            System.err.println("Invalid request received!");
            return new byte[0];
        }
    }
  
    @Override
    public byte[] appExecuteOrdered(byte[] command, MessageContext msgCtx) {
        iterations++;
        try {
            String newValue = new DataInputStream(new ByteArrayInputStream(command)).readUTF();
            jsonString = newValue;
            
            System.out.println("(" + iterations + ") New value was set. Current value = " + jsonString);
            
            ByteArrayOutputStream out = new ByteArrayOutputStream(4);
            new DataOutputStream(out).writeUTF(jsonString);
            return out.toByteArray();
        } catch (IOException ex) {
            System.err.println("Invalid request received!");
            return new byte[0];
        }
    }

    public static void main(String[] args){
        if(args.length < 1) {
            System.out.println("Use: java JsonStringServer <processId>");
            System.exit(-1);
        }      
        new JsonStringServer(Integer.parseInt(args[0]));
    }

    
    @SuppressWarnings("unchecked")
    @Override
    public void installSnapshot(byte[] state) {
        try (
              ByteArrayInputStream bis = new ByteArrayInputStream(state);
              ObjectInput in = new ObjectInputStream(bis);
            ) {
            jsonString = in.readUTF();
        } catch (IOException e) {
            System.err.println("ERROR: Deserializing state: " + e.getMessage());
        }
    }

    @Override
    public byte[] getSnapshot() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeUTF(jsonString);
            out.flush();
            bos.flush();
            out.close();
            bos.close();
            return bos.toByteArray();
        } catch (IOException ioe) {
            System.err.println("ERROR: serializing state: " + ioe.getMessage());
            return "ERROR".getBytes();
        }
    }
}
