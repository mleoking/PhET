package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.CCKImageSuite;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.components.Resistor;
import edu.colorado.phet.cck.phetgraphics_cck.circuit.components.ResistorColors;
import edu.colorado.phet.piccolo.PhetPNode;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Sep 20, 2006
 * Time: 12:05:30 PM
 * Copyright (c) Sep 20, 2006 by Sam Reid
 */
public class ResistorNode extends ComponentImageNode {
    private Resistor resistor;

    public ResistorNode( CCKModel model, Resistor resistor, Component component ) {
        super( model, resistor, CCKImageSuite.getInstance().getLifelikeSuite().getResistorImage(), component );
        this.resistor = resistor;
        addChild( new ColorBandsGraphic( this, resistor ) );
    }

    public class ColorBandsGraphic extends PhetPNode {
        private ComponentImageNode node;
        private Resistor resistor;

        public ColorBandsGraphic( ComponentImageNode node, Resistor resistor ) {
            this.node = node;
            this.resistor = resistor;
            update();
        }

        public void update() {
            removeAllChildren();
            int resistance = (int)resistor.getResistance();//resistorGraphic.getBranch().getint) ((Resistor) w).getResistance();
            Color[] c = new ResistorColors().to3Colors( resistance );
            int y = 3;
            int width = 5;
            int height = 19;

            PhetPPath path = new PhetPPath( new Rectangle( 15, y, width, height ), c[0] );
            addChild( path );

            addChild( new PhetPPath( new Rectangle( 25, y, width, height ), c[1] ) );

            addChild( new PhetPPath( new Rectangle( 35, y, width, height ), c[2] ) );
            if( c[3] != null ) {
                addChild( new PhetPPath( new Rectangle( 55, y, width, height ), c[3] ) );
            }
        }
    }
}
