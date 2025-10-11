package hello;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class HelloServer implements Hello{

    @Override
    public String hello(String who) {
        String result = "Hello, " + who;
        System.out.println("result = " + result);
        return result;
    }

    @Override
    public int summ(int a, int b) {
        return a + b;
    }

    @Override
    public void method(Arg arg) throws RemoteException {
        System.out.println(arg.a + " " + arg.b);
    }

    public static void main(String[] args) throws Exception{
        HelloServer server = new HelloServer();
        Hello proxy = (Hello)UnicastRemoteObject.exportObject(server, 8080);
        Registry registry = LocateRegistry.createRegistry(8080);
        registry.bind("Hello", proxy);
        System.out.println("Server started");
    }
}
