package hello;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Hello extends Remote {

    String hello(String who) throws RemoteException;

    int summ (int a, int b) throws RemoteException;

    void method (Arg arg)throws RemoteException;


}
