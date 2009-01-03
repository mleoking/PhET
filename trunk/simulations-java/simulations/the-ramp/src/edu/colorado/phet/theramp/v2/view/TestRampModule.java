package edu.colorado.phet.theramp.v2.view;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.theramp.v2.model.RampModel;

public class TestRampModule extends Module {
    private RampModel currentState=new RampModel();

    public TestRampModule() {
        super( "test ramp", new ConstantDtClock( 30, 1 ) );
        setSimulationPanel( new RampSimPanel(this) );
        addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                currentState = currentState.update( dt );
                notifyListeners();
            }
        } );
    }

    private ArrayList listeners = new ArrayList();

    public RampModel getCurrentState() {
        return currentState;
    }

    public static interface Listener {
        public void notifyChanged();
    }

    public void notifyListeners() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).notifyChanged();
        }
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }
}
