package edu.colorado.phet.functions.buildafunction;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.dialogs.ColorChooserFactory;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.functions.FunctionsApplication;

public class BuildAFunctionPrototype2 extends PiccoloModule {
    private boolean showColorDialog = false;

    public BuildAFunctionPrototype2() {
        super( "Build a Function", new ConstantDtClock( 30 ) );
        setSimulationPanel( new BuildAFunctionPrototype2Canvas() );
        setClockControlPanel( null );
        setLogoPanelVisible( false );

        if ( showColorDialog ) {
            ColorChooserFactory.showDialog( "color", null, Constants.functionColor.get(), new ColorChooserFactory.Listener() {
                public void colorChanged( final Color color ) {
                    Constants.functionColor.set( color );
                }

                public void ok( final Color color ) {
                    Constants.functionColor.set( color );
                }

                public void cancelled( final Color originalColor ) {
                    Constants.functionColor.set( originalColor );
                }
            } );
        }
    }

    public static void main( String[] args ) { FunctionsApplication.runModule( args, new BuildAFunctionPrototype2() ); }
}