import javax.activation.MimeType;
import java.io.Serializable;

public class Chunk implements Serializable
{
    int size;
    byte[] data;
    String FileId;
    public Chunk(int size,String FileId)
    {
        data=new byte[size];
        this.FileId=FileId;
    }
}
