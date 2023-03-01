import java.io.Serializable;

public class Massage implements Serializable {
    String writer;
    String massage;

    public Massage(String writer , String massage){
        this.writer = writer;
        this.massage = massage;
    }
}
