/* Copyright 2005-2007, University of Colorado */

package edu.colorado.phet.phetgraphicsdemo.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.phetgraphicsdemo.PhetGraphicsDemoResources;
import edu.colorado.phet.phetgraphicsdemo.model.WindmillModelElement;

/**
 * WindmillGraphic is the graphic representation of a windmill.
 * It demonstrates the creating of a composite graphic using 
 * composite and non-composite graphics. The windmill's origin
 * is at the point where the blades rotate.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class WindmillGraphic extends CompositePhetGraphic {

    public WindmillGraphic( Component component, WindmillModelElement windmillModelElement ) {
        super( component );

        // Graphics components
        BufferedImage image = PhetGraphicsDemoResources.getImage( "house.png" );
        PhetImageGraphic house = new PhetImageGraphic( component, image );
        house.setLocation( -65, -40 );
        WindmillBladesGraphic blades = new WindmillBladesGraphic( component, windmillModelElement );
        blades.setLocation( 0, 0 );
        addGraphic( house );
        addGraphic( blades );

        // Interactivity
        InteractivityHandler.register( this );
    }

    /**
     * WindmillBladesGraphic is a collection of windmill blades.
     * The origin is a the point where the blades rotate.
     */
    public class WindmillBladesGraphic extends CompositePhetGraphic implements SimpleObserver {

        private WindmillModelElement _windmillModelElement;

        public WindmillBladesGraphic( Component component, WindmillModelElement windmillModelElement ) {
            super( component );

            _windmillModelElement = windmillModelElement;
            _windmillModelElement.addObserver( this );

            // Create the blades of the windmill.
            int numberOfBlades = _windmillModelElement.getNumberOfBlades();
            double deltaAngle = 360.0 / numberOfBlades;
            for( int i = 0; i < numberOfBlades; i++ ) {
                WindmillBladeGraphic blade = new WindmillBladeGraphic( component );
                blade.rotate( Math.toRadians( i * deltaAngle ) );
                addGraphic( blade );
            }
        }

        public void finalize() {
            _windmillModelElement.removeObserver( this );
        }

        public void update() {
            clearTransform();
            rotate( Math.toRadians( _windmillModelElement.getRotationAngle() ) );
        }
    }

    /**
     * WindmillBladeGraphic is a single windmill blade.
     * The origin is at the point where the blade rotates.
     */
    public class WindmillBladeGraphic extends PhetShapeGraphic {

        public WindmillBladeGraphic( Component component ) {
            super( component );

            // Blade shape, with (0,0) at the blade's rotation point.
            GeneralPath bladePath = new GeneralPath();
            bladePath.moveTo( 0, 0 );
            bladePath.lineTo( -20, -100 );
            bladePath.lineTo( 20, -100 );
            bladePath.closePath();

            setShape( bladePath );
            setPaint( Color.BLACK );

            RenderingHints hints = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
            setRenderingHints( hints );
        }
    }

}