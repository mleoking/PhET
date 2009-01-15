package edu.colorado.phet.simtemplate.module.intensive;

import java.awt.*;

import edu.colorado.phet.simtemplate.module.example.ExampleModule;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Jan 15, 2009
 * Time: 2:25:41 PM
 */
public class ProcessorIntensiveModule extends ExampleModule {
    public ProcessorIntensiveModule( Frame parentFrame ) {
        super( parentFrame );
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
        panel.addScreenChild( text );
        addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                double x = 3;
                text.setText( "" + System.currentTimeMillis() + " at " + x );
            }
        } );
    }
}
