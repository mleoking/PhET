/** Sam Reid*/
package edu.colorado.phet.forces1d;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.phetgraphics.BufferedPhetGraphic;
import edu.colorado.phet.common.view.plaf.PhetLookAndFeel;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.forces1d.model.Force1DModel;
import edu.colorado.phet.forces1d.view.Force1DPanel;

import javax.swing.*;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Nov 12, 2004
 * Time: 10:06:43 PM
 * Copyright (c) Nov 12, 2004 by Sam Reid
 */
public class Force1DModule extends Module {
    private Force1DModel forceModel;
    private Force1DPanel forcePanel;
    private Force1dControlPanel forceControlPanel;

    public Force1DModule() throws IOException {
        super( "Force1D" );

        forceModel = new Force1DModel( this );
        forcePanel = new Force1DPanel( this );
        setApparatusPanel( forcePanel );
        setModel( new BaseModel() );
        forceControlPanel = new Force1dControlPanel( this );
        setControlPanel( forceControlPanel );
        addModelElement( forceModel );
        ModelElement updateGraphics = new ModelElement() {
            public void stepInTime( double dt ) {
                forcePanel.updateGraphics();
            }
        };
        addModelElement( updateGraphics );
    }

    public Force1DModel getForceModel() {
        return forceModel;
    }

    public Force1DPanel getForcePanel() {
        return forcePanel;
    }

    public void reset() {
        forceModel.reset();
        forcePanel.reset();
    }

    public void cursorMovedToTime( double modelX ) {
    }

    public void relayout() {
    }

    public BufferedPhetGraphic getBackground() {
        return forcePanel.getBufferedGraphic();
    }

    public static void main( String[] args ) throws UnsupportedLookAndFeelException, IOException {
        UIManager.setLookAndFeel( new PhetLookAndFeel() );
        final Force1DModule module = new Force1DModule();
        AbstractClock clock = new SwingTimerClock( 1, 30 );
        FrameSetup frameSetup = new FrameSetup.MaxExtent( new FrameSetup.CenteredWithInsets( 200, 200 ) );
        ApplicationModel model = new ApplicationModel( "Forces 1D", "Force1d applet", "1.0Alpha",
                                                       frameSetup, module, clock );
        PhetApplication phetApplication = new PhetApplication( model );
        phetApplication.startApplication();
        module.getForcePanel().setSize( module.getForcePanel().getSize().width - 1, module.getForcePanel().getSize().height - 1 );
        module.getForcePanel().relayout();
        module.getForcePanel().revalidate();
    }

}
