package edu.colorado.phet.circuitconstructionkit;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class CircuitConstructionKitACVirtualLabApplication {
    public static void main(String[] args) throws InvocationTargetException, InterruptedException {
        ArrayList<String> a = new ArrayList<String>();
        a.add(CCKParameters.VIRTUAL_LAB);
        CircuitConstructionKitACApplication.main(a.toArray(new String[0]));
    }
}
