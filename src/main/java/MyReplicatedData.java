
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;

// This is a sample class as an example, with the data to be replicated
// across the BFT replicas.
public class MyReplicatedData implements Serializable {

  private static final long serialVersionUID = 1234499876;

  // which serialization to use to transmit the object to the BFT replicas,
  // Kryo or the Java default (true is to use Kryo, which is faster; false,
  // to use the Java default serialization):
  private static final boolean serializKryoOrJavaDefault = true;

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

  private static Kryo getKryoForClass() {
    Kryo kryo = new Kryo();
    kryo.register(java.util.ArrayDeque.class);
    kryo.register(MyReplicatedData.class);
    return kryo;
  }

  public static byte[] toStream(MyReplicatedData obj) {
    if (serializKryoOrJavaDefault) {
      return toStreamKryo(obj);
    } else {
      return toStreamJavaDefault(obj);
    }
  }

  public static byte[] toStreamJavaDefault(MyReplicatedData obj) {
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

  public static byte[] toStreamKryo(MyReplicatedData obj) {
    byte[] result = null;
    Kryo kryo = MyReplicatedData.getKryoForClass();
    try (ByteArrayOutputStream byteArrOutput = new ByteArrayOutputStream();
         Output objectOutput = new Output(byteArrOutput);
        ) {
      kryo.writeObjectOrNull(objectOutput, obj, MyReplicatedData.class);
      result = objectOutput.getBuffer();
    } catch (IOException exc) {
      exc.printStackTrace();
    }
    return result;
  }

  public static MyReplicatedData fromStream(byte[] byteRepr) {
    if (serializKryoOrJavaDefault) {
      return fromStreamKryo(byteRepr);
    } else {
      return fromStreamJavaDefault(byteRepr);
    }
  }

  public static MyReplicatedData fromStreamJavaDefault(byte[] byteRepr) {
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

  public static MyReplicatedData fromStreamKryo(byte[] byteRepr) {
    MyReplicatedData result = null;
    Kryo kryo = MyReplicatedData.getKryoForClass();
    try (ByteArrayInputStream byteArrInput =
                                    new ByteArrayInputStream(byteRepr);
         Input objectInput = new Input(byteArrInput);
        ) {
      result = kryo.readObjectOrNull(objectInput, MyReplicatedData.class);
    } catch (IOException exc) {
      exc.printStackTrace();
    }
    return result;
  }

}
