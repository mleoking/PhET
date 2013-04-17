// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.simsharingtestsim;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * @author Sam Reid
 */
public class SimSharingTestModule extends Module {
    public final Property<Vector2D> position = new Property<Vector2D>( new Vector2D() );

    public SimSharingTestModule() {
        super( "Test", new ConstantDtClock( 30 ) );
        setSimulationPanel( new PhetPCanvas() {{
            addScreenChild( new PhetPPath( new Rectangle2D.Double( 0, 0, 100, 100 ), Color.yellow, new BasicStroke( 2 ), Color.blue ) {{
                addInputEventListener( new CursorHandler() );
                addInputEventListener( new PBasicInputEventHandler() {
                    @Override public void mouseDragged( PInputEvent event ) {
                        position.set( position.get().plus( event.getDeltaRelativeTo( getParent() ) ) );
                    }
                } );
                position.addObserver( new VoidFunction1<Vector2D>() {
                    public void apply( Vector2D immutableVector2D ) {
                        setOffset( immutableVector2D.toPoint2D() );
                    }
                } );
            }} );
        }} );
    }
}
