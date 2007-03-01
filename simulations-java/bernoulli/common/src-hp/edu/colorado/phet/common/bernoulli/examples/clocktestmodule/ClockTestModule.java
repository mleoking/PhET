/**
 * Class: ClockTestModule
 * Package: edu.colorado.phet.common.examples.clocktestmodule
 * Author: Another Guy
 * Date: Jul 21, 2003
 */
package edu.colorado.phet.common.bernoulli.examples.clocktestmodule;

import edu.colorado.phet.common.bernoulli.application.Module;
import edu.colorado.phet.common.bernoulli.application.PhetApplication;
import edu.colorado.phet.common.bernoulli.view.ApplicationDescriptor;
import edu.colorado.phet.common.bernoulli.view.ApparatusPanel;
import edu.colorado.phet.common.bernoulli.view.graphics.Graphic;
import edu.colorado.phet.common.bernoulli.view.util.framesetup.FrameSetup;
import edu.colorado.phet.common.bernoulli.model.DynamicClock;
import edu.colorado.phet.common.bernoulli.model.ThreadPriority;
import edu.colorado.phet.common.bernoulli.model.BaseModel;
import edu.colorado.phet.common.bernoulli.model.ModelElement;

import javax.swing.*;
import java.awt.*;

public class ClockTestModule extends Module {
    double totalTime=0;
    public ClockTestModule() {

        super( "Clock test module." );
        setApparatusPanel(new ApparatusPanel() );
        setModel(new BaseModel() );
        getModel().addModelElement(new ModelElement() {

            public void stepInTime( double dt ) {
                totalTime+=dt;
            }
        } );
        getApparatusPanel().addGraphic(new Graphic() {
            public void paint( Graphics2D g ) {
                g.setFont(new Font("dialog",0,50) );
                g.setColor(Color.black);
                g.drawString(totalTime+"", 100,100);
            }
        }, 0);

    }

    //
    public void activate( PhetApplication app ) {
    }

    public void deactivate( PhetApplication app ) {
    }

    public static void main( String[] args ) {
        ClockTestModule module = new ClockTestModule();
        ApplicationDescriptor desc = new ApplicationDescriptor( "Hello PhET!", "A PhET platform development test.", ".01-beta-*", new FrameSetup() {
            public void initialize( JFrame frame ) {
                frame.setVisible(true);
                frame.setExtendedState( JFrame.MAXIMIZED_BOTH );
            }
        } );
//        ApplicationDescriptor desc=new ApplicationDescriptor("Hello PhET!","A PhET platform development test.",".01-beta-*",100,100,true);
        DynamicClock clock=new DynamicClock(20,20,ThreadPriority.NORMAL );
        PhetApplication tpa = new PhetApplication( desc, module ,clock);
//        m.setApplication(tpa);
        tpa.startApplication( module );

    }
}
