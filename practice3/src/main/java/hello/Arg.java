package hello;

import java.io.Serializable;

public class Arg implements Serializable {
    int a;
    int b;

    public Arg(int a, int b){
        this.a = a;
        this.b = b;
    }
}
