package net.sourceforge.retroweaver.tests;

import java.rmi.Remote;
import java.rmi.RemoteException;

interface RmicEnum extends Remote {
    Object callColor(Color c) throws RemoteException;
}

public class RmicEnumTest implements RmicEnum {

    public Object callColor(Color c) throws RemoteException {
        return null;
    }
}
