package edu.colorado.phet.physics.observation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Mar 11, 2003
 * Time: 2:54:06 PM
 * To change this template use Options | File Templates.
 */
public class ObserverProxy /*extends Observable*/ implements InvocationHandler{

    private Class observerInterface;
    private Object observer;

    public ObserverProxy( Class observerInterface, Object observer ) {
        if( !observerInterface.isInstance( observer )) {
            throw new RuntimeException( "Observer not instance of ObserverInterface");
        }
        this.observerInterface = observerInterface;
        this.observer = observer;
    }

    public Object invoke( Object proxy, Method method, Object[] args )
            throws Throwable {
        Object result = null;
        if( !observerInterface.isInstance( observer )) {
            throw new RuntimeException( "Attempt to invoke method on observer of incorrect type.");
        }
        try {
            result = method.invoke( observer, args );
        }
        catch( IllegalAccessException e ) {
            e.printStackTrace();
        }
        catch( IllegalArgumentException e ) {
            e.printStackTrace();
        }
        catch( InvocationTargetException e ) {
            e.printStackTrace();
        }
        return result;
    }
}
