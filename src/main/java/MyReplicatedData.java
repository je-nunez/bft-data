
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Deque;
import java.util.ArrayDeque;

// This is a sample class as an example, with the data to be replicated
// across the BFT replicas.
public class MyReplicatedData implements Serializable {

  private static final long serialVersionUID = 1234499876;

  // The fields in this example are public. In general, they should
  // contain access methods (lombok.*) or others with more complex
  // logic.
  public int value;
  public String name;
  public Deque<String> limitedLog;

  public MyReplicatedData() {
    limitedLog = new ArrayDeque<String>();
  }

  @Override
  public String toString() {
    return String.format("Value: " + value + "; Name: " + name
                         + "; First in Deque: " + limitedLog.getFirst());
  }

  public static byte[] toStream(MyReplicatedData obj) {
    byte[] result = null;
    try (ByteArrayOutputStream byteArrOutput = new ByteArrayOutputStream();
         ObjectOutputStream objectOutput =
                                    new ObjectOutputStream(byteArrOutput);
        ) {
      objectOutput.writeObject(obj);
      objectOutput.flush();
      result = byteArrOutput.toByteArray();
    } catch (IOException exc) {
      exc.printStackTrace();
    }
    return result;
  }

  public static MyReplicatedData fromStream(byte[] byteRepr) {
    MyReplicatedData result = null;
    try (ByteArrayInputStream byteArrInput =
                                    new ByteArrayInputStream(byteRepr);
         ObjectInputStream objectInput =
                                    new ObjectInputStream(byteArrInput);
        ) {
      result = (MyReplicatedData) objectInput.readObject();
    } catch (IOException | ClassNotFoundException exc) {
      exc.printStackTrace();
    }
    return result;
  }
}
