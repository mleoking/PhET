package edu.colorado.phet.fitness.test;

public class A {
    public void addObserver( AObserver aObserver ) {
    }

    public static void main( String[] args ) {
        A a = new A();
        AObserver2 observer2 = new AObserver2( a, "hello" );//throws a nullpointerexception
        AObserver3 observer3 = new AObserver3( a, "hello" );//ugly workaround
    }
}

class AObserver {
    A a;

    AObserver( A a ) {
        this.a = a;
        a.addObserver( this );
        aChanged();
    }

    //synchronize state with model object
    void aChanged() {
        //update self
    }
}

class AObserver2 extends AObserver {
    Object state;

    AObserver2( A a, Object state ) {
        super( a );
        this.state = state;
        aChanged();
    }

    void aChanged() {
        super.aChanged();
        state.toString();//throws nullpointerexception when called from AObserver() constructor
    }
}

class AObserver3 extends AObserver {
    Object state;

    AObserver3( A a, Object state ) {
        super( a );
        this.state = state;
        aChanged();
    }

    void aChanged() {
        super.aChanged();
        if ( state != null ) {
            state.toString();
        }
    }
}