package edu.colorado.phet.testlocation;

import java.awt.Color;
import java.awt.Font;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.phetgraphics.PhetMultiLineTextGraphic;
import edu.colorado.phet.testlocation.control.TestControlPanel;
import edu.colorado.phet.testlocation.model.CarModelElement;
import edu.colorado.phet.testlocation.model.WindmillModelElement;
import edu.colorado.phet.testlocation.view.CarOnRoadGraphic;
import edu.colorado.phet.testlocation.view.DebuggerGraphic;
import edu.colorado.phet.testlocation.view.SceneGraphic;
import edu.colorado.phet.testlocation.view.WindmillGraphic;

/**
 * TestModule is a module that demonstrates proposed changes
 * to the semantics of PhetGraphic's location.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TestModule extends Module {
 
    private static final double CAR_LAYER = 1;
    private static final double WINDMILL_LAYER = 2;
    private static final double SCENE_LAYER = 3;
    private static final double DEBUG_LAYER = 4;
    private static final double HELP_LAYER = Double.MAX_VALUE;
    
    public TestModule( ApplicationModel appModel ) {

        super( "Test Location" );

        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------

        // Clock
        AbstractClock clock = appModel.getClock();

        // Module model
        BaseModel model = new BaseModel();
        this.setModel( model );
        
        // Cars
        CarModelElement car1 = new CarModelElement( 500 /* range of travel */ );
        model.addModelElement( car1 );
        CarModelElement car2 = new CarModelElement( 250 /* range of travel */ );
        model.addModelElement( car2 );
        CarModelElement car3 = new CarModelElement( 600 /* range of travel */);
        model.addModelElement( car3 );
        
        // Windmills
        WindmillModelElement windmill1 = new WindmillModelElement( 5 /* number of blades */ );
        model.addModelElement( windmill1 );
        WindmillModelElement windmill2 = new WindmillModelElement( 6 /* number of blades */ );
        model.addModelElement( windmill2 );
        WindmillModelElement windmill3 = new WindmillModelElement( 6 /* number of blades */ );
        model.addModelElement( windmill3 );
        WindmillModelElement windmill4 = new WindmillModelElement( 7 /* number of blades */ );
        model.addModelElement( windmill4 );
        
        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        // Apparatus Panel
        ApparatusPanel apparatusPanel = new ApparatusPanel2( model, clock );
        apparatusPanel.setBackground( Color.DARK_GRAY );
        setApparatusPanel( apparatusPanel );
        
        // Car on a Road
        CarOnRoadGraphic carOnRoadGraphic1 = new CarOnRoadGraphic( apparatusPanel, car1 );
        carOnRoadGraphic1.setLocation( 100, 200 );
        apparatusPanel.addGraphic( carOnRoadGraphic1, CAR_LAYER );
        
        // Car on a Road, scaled and rotated
        CarOnRoadGraphic carOnRoadGraphic2 = new CarOnRoadGraphic( apparatusPanel, car2 );
        carOnRoadGraphic2.setLocation( 300, 300 );
        carOnRoadGraphic2.rotate( Math.toRadians( 15 ) );
        carOnRoadGraphic2.scale( 0.5 );
        apparatusPanel.addGraphic( carOnRoadGraphic2, CAR_LAYER );
        
        // Windmill
        WindmillGraphic windmillGraphic1 = new WindmillGraphic( apparatusPanel, windmill1 );
        windmillGraphic1.setLocation( 150, 400 );
        apparatusPanel.addGraphic( windmillGraphic1, WINDMILL_LAYER );
        
        // A pair of scaled windmills, one the reflection of the other.
        WindmillGraphic windmillGraphic2 = new WindmillGraphic( apparatusPanel, windmill2 );
        windmillGraphic2.setLocation( 400, 500 );
        windmillGraphic2.scale( 0.5 );
        apparatusPanel.addGraphic( windmillGraphic2, WINDMILL_LAYER );
        WindmillGraphic windmillGraphic3 = new WindmillGraphic( apparatusPanel, windmill3 );
        windmillGraphic3.setLocation( 550, 500 );
        windmillGraphic3.scale( 0.5 );
        windmillGraphic3.scale( -1, 1 ); // reflect horizontally
        apparatusPanel.addGraphic( windmillGraphic3, WINDMILL_LAYER );   
        
        // Scene with Windmill and Car on Road.
        SceneGraphic sceneGraphic = new SceneGraphic( apparatusPanel, windmill4, car3 );
        sceneGraphic.setLocation( 550, 250 );
        apparatusPanel.addGraphic( sceneGraphic, SCENE_LAYER );
        
        // Graphics debugger
        DebuggerGraphic debugger = new DebuggerGraphic( apparatusPanel );
        debugger.setBoundsEnabled( false );
        debugger.setBoundsColor( Color.RED );
        debugger.setLocationEnabled( true );
        debugger.setLocationColor( Color.GREEN );
        debugger.add( carOnRoadGraphic1 );
        debugger.add( carOnRoadGraphic2 );
        debugger.add( windmillGraphic1 );
        debugger.add( windmillGraphic2 );
        debugger.add( windmillGraphic3 );
        debugger.add( sceneGraphic );
        apparatusPanel.addGraphic( debugger, DEBUG_LAYER );
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------

        // Control Panel
        TestControlPanel controlPanel = new TestControlPanel( this, debugger );
        setControlPanel( controlPanel );
        
        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------
        
        String[] lines = { 
                "Translate by dragging with the LEFT mouse button.", 
                "Rotate by dragging horizontally with the RIGHT mouse button.",
                "Use the control panel to change the visibility of bounds & registration points." };
        Font font = new Font( "Serif", Font.BOLD, 18 );
        PhetMultiLineTextGraphic mlt = new PhetMultiLineTextGraphic( apparatusPanel, lines, font, 100, 30, Color.RED );
        apparatusPanel.addGraphic( mlt, HELP_LAYER );
    }
}