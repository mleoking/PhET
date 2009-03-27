package edu.colorado.phet.circuitconstructionkit.view.piccolo.lifelike;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.components.Resistor;
import edu.colorado.phet.circuitconstructionkit.view.CCKImageSuite;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.ComponentImageNode;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.ResistorColors;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;

/**
 * User: Sam Reid
 * Date: Sep 20, 2006
 * Time: 12:05:30 PM
 */
public class ResistorNode extends ComponentImageNode {
    private Resistor resistor;
    private CCKModule module;
    private ColorBandNode colorBandNode;
    private SimpleObserver resistorObserver = new SimpleObserver() {
        public void update() {
            ResistorNode.this.update();
        }
    };

    public ResistorNode( CCKModel model, final Resistor resistor, JComponent component, final CCKModule module ) {
        super( model, resistor, CCKImageSuite.getInstance().getLifelikeSuite().getResistorImage(), component, module );
        this.resistor = resistor;
        this.module = module;
        colorBandNode = new ColorBandNode( this, resistor );
        addChild( colorBandNode );
        resistor.addObserver( resistorObserver );
    }

    public void delete() {
        super.delete();
        resistor.removeObserver( resistorObserver );
    }

//    protected JPopupMenu createPopupMenu() {
//        return new ComponentMenu.ResistorMenu( resistor, module );
//    }

    protected void update() {
        super.update();
        if ( colorBandNode != null ) {
            colorBandNode.update();
        }
    }

    public class ColorBandNode extends PhetPNode {
        private ComponentImageNode node;
        private Resistor resistor;

        public ColorBandNode( ComponentImageNode node, Resistor resistor ) {
            this.node = node;
            this.resistor = resistor;
            update();
        }

        public void update() {
            removeAllChildren();
            int resistance = (int) resistor.getResistance();//resistorGraphic.getBranch().getint) ((Resistor) w).getResistance();
            Color[] c = new ResistorColors().to3Colors( resistance );
            int y = 2;
            int width = 5;
            int height = 22;

            PhetPPath path = new PhetPPath( new Rectangle( 22, y, width, height ), c[0] );
            addChild( path );

            addChild( new PhetPPath( new Rectangle( 32, y, width, height ), c[1] ) );

            addChild( new PhetPPath( new Rectangle( 42, y, width, height ), c[2] ) );
            if ( c[3] != null ) {
                addChild( new PhetPPath( new Rectangle( 53, y, width, height ), c[3] ) );
            }
        }
    }
}
