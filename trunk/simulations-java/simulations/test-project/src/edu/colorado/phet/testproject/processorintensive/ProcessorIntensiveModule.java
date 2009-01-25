package edu.colorado.phet.testproject.processorintensive;

import java.awt.Graphics;

import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.umd.cs.piccolo.nodes.PText;

public class ProcessorIntensiveModule extends PiccoloModule {
    public ProcessorIntensiveModule() {
        super( "Process Intensive Test", new ConstantDtClock( 30, 1 ) );
        PhetPCanvas panel = new PhetPCanvas(){
            public void paintComponent( Graphics g ) {
                super.paintComponent( g );
                try {
                    Thread.sleep( 1000 );
                }
                catch( InterruptedException e ) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        };
        setSimulationPanel( panel );
        setModel( new BaseModel() );

        final PText text = new PText( "hello" );
        text.setOffset( 50, 50 );
        panel.addScreenChild( text );
        addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                double x = 3;
                text.setText( "" + System.currentTimeMillis() + " at " + x );
            }
        } );
    }
}
