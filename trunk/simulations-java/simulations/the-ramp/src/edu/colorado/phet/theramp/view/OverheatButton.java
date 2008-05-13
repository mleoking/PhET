/*  */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ShadowHTMLNode;
import edu.colorado.phet.theramp.RampModule;
import edu.colorado.phet.theramp.TheRampStrings;
import edu.colorado.phet.theramp.model.RampPhysicalModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Jun 6, 2005
 * Time: 11:34:54 PM
 */

public class OverheatButton extends PNode {
    private RampPhysicalModel rampPhysicalModel;
    private RampPanel rampPanel;
    private RampModule module;

    public OverheatButton( final RampPanel rampPanel, final RampPhysicalModel rampPhysicalModel, RampModule module ) {
        this.module = module;
        this.rampPhysicalModel = rampPhysicalModel;
        this.rampPanel = rampPanel;
        ShadowHTMLNode shadowHTMLNode = new ShadowHTMLNode( TheRampStrings.getString( "message.overheated" ) );
        shadowHTMLNode.setColor( Color.red );
        shadowHTMLNode.setFont( new Font( PhetFont.getDefaultFontName(), Font.BOLD, 14 ) );
        addChild( shadowHTMLNode );
        JButton overheat = new JButton( TheRampStrings.getString( "controls.cool-ramp" ) );
        overheat.setFont( RampFontSet.getFontSet().getNormalButtonFont() );
        overheat.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                rampPanel.getRampModule().clearHeat();
            }
        } );
        PSwing buttonGraphic = new PSwing( overheat );
        buttonGraphic.setOffset( 0, shadowHTMLNode.getHeight() );
        addChild( buttonGraphic );
        module.getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                update();
            }
        } );
        update();
        buttonGraphic.setOffset( 0, shadowHTMLNode.getFullBounds().getHeight() + 5 );
    }

    private void update() {

        double max = rampPanel.getOverheatEnergy();
//        System.out.println( "<----OverheatButton.update" );
//        System.out.println( "rampPhysicalModel.getThermalEnergy() = " + rampPhysicalModel.getThermalEnergy() );
//        System.out.println( "max=" + max );
//        System.out.println( "" );
        if( rampPhysicalModel.getThermalEnergy() >= max && !getVisible() && module.numMaximizedBarGraphs() > 0 ) {
            setVisible( true );
            setPickable( true );
            setChildrenPickable( true );
//            setOffset( RampPanel.getDefaultRenderSize().width / 2, 50 );
            Rectangle2D r = rampPanel.getBarGraphSuite().getGlobalFullBounds();
//            System.out.println( "r = " + r );
            globalToLocal( r );

//            System.out.println( "globToLoc r=" + r );
            localToParent( r );
//            System.out.println( "locToPar r="+r );
//            setOffset( rampPanel.getBarGraphSuite().getXOffset(), rampPanel.getBarGraphSuite().getYOffset() + 100 );
            setOffset( r.getX() + 20, rampPanel.getHeight() / 2 );
        }
        else if( rampPhysicalModel.getThermalEnergy() < max ) {
            setVisible( false );
            setPickable( false );
            setChildrenPickable( false );
        }
    }
}
