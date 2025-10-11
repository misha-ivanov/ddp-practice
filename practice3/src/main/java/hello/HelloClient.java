package hello;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class HelloClient {
    public static void main(String[] args) throws Exception{
        Registry registry = LocateRegistry.getRegistry("185.188.181.184", 8080);
        Hello proxy = (Hello)registry.lookup("Hello");
        System.out.println("proxy = " + proxy);
        String result = proxy.hello("Mike");
        System.out.println("result = " + result);
        System.out.println("proxy = " + proxy.summ(5, 10));
        proxy.method(new Arg(10, 13));
    }
}
