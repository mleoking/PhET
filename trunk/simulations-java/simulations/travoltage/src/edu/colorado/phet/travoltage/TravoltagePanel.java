/*  */
package edu.colorado.phet.travoltage;

import java.awt.Font;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.help.MotionHelpBalloon;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * User: Sam Reid
 * Date: Jun 30, 2006
 * Time: 11:24:07 PM
 */

public class TravoltagePanel extends PhetPCanvas {
    private TravoltageRootNode travoltageRootNode;
    private boolean createTrajectories = false;
    private MotionHelpBalloon motionHelpBalloon;

    public TravoltagePanel( TravoltageModule travoltageModule ) {
        setDefaultRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );
        setAnimatingRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );
        setInteractingRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );

        travoltageRootNode = new TravoltageRootNode( travoltageModule, this, travoltageModule.getTravoltageModel() );
        addScreenChild( travoltageRootNode );

//        setCreateTrajectories();
        motionHelpBalloon = new MotionHelpBalloon( this, TravoltageResources.getString( "html.rub.the.foot.br.on.the.carpet.html" ) );
//        motionHelpBalloon = new MotionHelpBalloon( this, "<html>Move the leg<br>and arm.</html>" );
        motionHelpBalloon.setBalloonVisible( true );
        motionHelpBalloon.setFont( new PhetFont( Font.BOLD, 14 ) );
        getLayer().addChild( motionHelpBalloon );
        addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                motionHelpBalloon.setVisible( false );
            }
        } );
    }

    private void setCreateTrajectories() {
        addMouseListener( new MouseListener() {
            public void mouseClicked( MouseEvent e ) {
            }

            public void mouseEntered( MouseEvent e ) {
            }

            public void mouseExited( MouseEvent e ) {
            }

            public void mousePressed( MouseEvent e ) {
                System.out.print( e.getX() + ", " + e.getY() + ", " );
            }

            public void mouseReleased( MouseEvent e ) {
                System.out.println( e.getX() + ", " + e.getY() );
            }
        } );
        getTravoltageRootNode().getTravoltageBodyNode().getArmNode().setAngle( 0.0 );
        getTravoltageRootNode().getTravoltageBodyNode().getLegNode().setAngle( 0.0 );
        this.createTrajectories = true;
    }

    protected void sendInputEventToInputManager( InputEvent e, int type ) {
        if( createTrajectories ) {
            return;
        }
        super.sendInputEventToInputManager( e, type );
    }

    public TravoltageRootNode getTravoltageRootNode() {
        return travoltageRootNode;
    }

    public ElectronSetNode getElectronSetNode() {
        return travoltageRootNode.getElectronSetNode();
    }

    public void setSparkVisible( boolean b ) {
        travoltageRootNode.setSparkVisible( b );
    }

    public void showHelpBalloon() {
        motionHelpBalloon.animateTo( getTravoltageRootNode().getTravoltageBodyNode().getLegNode() );
    }
}
