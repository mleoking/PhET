package edu.colorado.phet.cck.phetgraphics_cck;

import edu.colorado.phet.cck.CCKResources;
import edu.colorado.phet.cck.common.JPopupMenuRepaintWorkaround;
import edu.colorado.phet.cck.model.Connection;
import edu.colorado.phet.cck.phetgraphics_cck.circuit.tools.Voltmeter;
import edu.colorado.phet.cck.phetgraphics_cck.circuit.tools.VoltmeterGraphic;
import edu.colorado.phet.common_cck.view.CompositeGraphic;
import edu.colorado.phet.common_cck.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common_cck.view.graphics.mousecontrols.Translatable;
import edu.colorado.phet.common_cck.view.graphics.transforms.ModelViewTransform2D;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jun 17, 2004
 * Time: 2:27:28 PM
 */
public class InteractiveVoltmeter extends CompositeGraphic {
    private Voltmeter voltmeter;
    private ModelViewTransform2D transform;
    private VoltmeterGraphic voltmeterGraphic;
    private CCKPhetgraphicsModule module;
    private boolean dragAll;

    public InteractiveVoltmeter( final VoltmeterGraphic voltmeterGraphic, final CCKPhetgraphicsModule module ) {
        super.setVisible( false );
        this.module = module;
        this.voltmeterGraphic = voltmeterGraphic;
        voltmeter = voltmeterGraphic.getVoltmeter();
        this.transform = module.getTransform();

        DefaultInteractiveGraphic unitInteraction = new DefaultInteractiveGraphic( voltmeterGraphic.getUnitGraphic() );
        unitInteraction.addCursorHandBehavior();
        unitInteraction.addMouseInputListener( new MouseInputAdapter() {
            // implements java.awt.event.MouseListener
            public void mousePressed( MouseEvent e ) {
                //if the leads are on the circuit, only the DVM goes.
//                Branch a = voltmeterGraphic.getRedLeadGraphic().detectBranch( module.getCircuitGraphic() );
//                Branch b = voltmeterGraphic.getBlackLeadGraphic().detectBranch( module.getCircuitGraphic() );
                Connection a = voltmeterGraphic.getRedLeadGraphic().getConnection( module.getCircuitGraphic() );
                Connection b = voltmeterGraphic.getBlackLeadGraphic().getConnection( module.getCircuitGraphic() );
                if( a == null && b == null ) {
                    dragAll = true;
                }
                else {
                    dragAll = false;
                }
            }
        } );
        unitInteraction.addTranslationBehavior( new Translatable() {
            public void translate( double dx, double dy ) {
                Point2D pt = InteractiveVoltmeter.this.transform.viewToModelDifferential( (int)dx, (int)dy );
                if( dragAll ) {
                    InteractiveVoltmeter.this.voltmeter.translateAll( pt.getX(), pt.getY() );
                }
                else {
                    InteractiveVoltmeter.this.voltmeter.translate( pt.getX(), pt.getY() );
                }
            }
        } );
        final JCheckBoxMenuItem jcbmi = new JCheckBoxMenuItem( CCKResources.getString( "InteractiveVoltmeter.VerticalLeadsCheckBox" ) );
        jcbmi.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setLeadsVertical( jcbmi.isSelected() );
            }
        } );
        JPopupMenu jpm = new JPopupMenuRepaintWorkaround( module.getApparatusPanel() );
        jpm.add( jcbmi );
        unitInteraction.addPopupMenuBehavior( jpm );
        addGraphic( unitInteraction );

        addGraphic( voltmeterGraphic.getRedCableGraphic() );
        addGraphic( voltmeterGraphic.getBlackCableGraphic() );

        DefaultInteractiveGraphic redLead = new DefaultInteractiveGraphic( voltmeterGraphic.getRedLeadGraphic() );
        redLead.addCursorHandBehavior();
        redLead.addTranslationBehavior( new Translatable() {
            public void translate( double dx, double dy ) {
                Point2D pt = InteractiveVoltmeter.this.transform.viewToModelDifferential( (int)dx, (int)dy );
                InteractiveVoltmeter.this.voltmeter.getRedLead().translate( pt.getX(), pt.getY() );
            }
        } );
        addGraphic( redLead );

        DefaultInteractiveGraphic blackLead = new DefaultInteractiveGraphic( voltmeterGraphic.getBlackLeadGraphic() );
        blackLead.addCursorHandBehavior();
        blackLead.addTranslationBehavior( new Translatable() {
            public void translate( double dx, double dy ) {
                Point2D pt = InteractiveVoltmeter.this.transform.viewToModelDifferential( (int)dx, (int)dy );
                InteractiveVoltmeter.this.voltmeter.getBlackLead().translate( pt.getX(), pt.getY() );
            }
        } );
        addGraphic( blackLead );
        setVisible( false );
    }

    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        voltmeterGraphic.setVisible( visible );
    }

    private void setLeadsVertical( boolean vertical ) {
        double redangle = 0;
        double blackangle = 0;
        if( !vertical ) {
            redangle = Math.PI / 8;
            blackangle = -redangle;
        }

        voltmeterGraphic.getBlackLeadGraphic().setAngle( blackangle );
        voltmeterGraphic.getRedLeadGraphic().setAngle( redangle );
        voltmeterGraphic.getBlackCableGraphic().changed();
        voltmeterGraphic.getRedCableGraphic().changed();
        module.getApparatusPanel().repaint();
    }

}
