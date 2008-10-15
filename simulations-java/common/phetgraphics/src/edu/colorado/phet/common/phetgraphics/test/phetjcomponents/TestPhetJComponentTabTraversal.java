package edu.colorado.phet.common.phetgraphics.test.phetjcomponents;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;

import javax.swing.JTextField;

import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.application.DeprecatedPhetApplicationLauncher;
import edu.colorado.phet.common.phetgraphics.application.PhetGraphicsModule;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel2;
import edu.colorado.phet.common.phetgraphics.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.HTMLGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetGraphic;

/**
 * Tests tab traversal of Swing components that are wrapped by PhetJComponent.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestPhetJComponentTabTraversal {

    public static void main( String args[] ) throws IOException {
        TestPhetJComponentTabTraversal test = new TestPhetJComponentTabTraversal( args );
    }

    public TestPhetJComponentTabTraversal( String[] args ) throws IOException {

        // Create the app.
        String title = "TestPhetJComponentTabTraversal";
        IClock clock = new SwingClock( 40, 1 );

        DeprecatedPhetApplicationLauncher app = new DeprecatedPhetApplicationLauncher( args, "title", "desc", "version" );

        // Add modules.
        PhetGraphicsModule module = new TestModule( clock );
        app.setModules( new PhetGraphicsModule[]{module} );

        // Start the app.
        app.startApplication();
    }

    private class TestModule extends PhetGraphicsModule {
        public TestModule( IClock clock ) {
            super( "Test Module", clock );

            // Model
            BaseModel model = new BaseModel();
            setModel( model );

            // Apparatus Panel
            ApparatusPanel2 apparatusPanel = new ApparatusPanel2( clock );
            apparatusPanel.setBackground( Color.WHITE );
            setApparatusPanel( apparatusPanel );

            Font font = new PhetFont( Font.PLAIN, 14 );

            // Instructions
            String html = "<html>Click in a text field.<br>Then use Tab or Shift-Tab to move between text fields.</html>";
            HTMLGraphic instructions = new HTMLGraphic( apparatusPanel, font, html, Color.BLACK );
            instructions.setLocation( 15, 15 );
            apparatusPanel.addGraphic( instructions );

            // JTextFields, wrapped by PhetJComponent.
            for ( int i = 0; i < 5; i++ ) {
                JTextField textField = new JTextField();
                textField.setFont( font );
                textField.setColumns( 3 );
                PhetGraphic textFieldGraphic = PhetJComponent.newInstance( apparatusPanel, textField );
                textFieldGraphic.setLocation( 20 + ( i * 60 ), 100 );
                apparatusPanel.addGraphic( textFieldGraphic );
            }
        }
    }
}
