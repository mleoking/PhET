/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.view.graph;

import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.Line2D;

import edu.colorado.phet.common.phetcommon.util.ConstantPowerOfTenNumberFormat;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * LinearYAxisNode is the linear y-axis (tick marks and labels) for the bar graph.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LinearYAxisNode extends PComposite {
    
    private final PDimension _graphOutlineSize;
    private final int _numberOfTicks;
    private final double _topMargin;
    private int _exponent;
    private final double _mantissaSpacing;
    private final double _tickLength;
    private final Stroke _tickStroke;
    private final Color _tickColor;
    private final Font _labelFont;
    private final Color _labelColor;
    private final Stroke _gridlineStroke;
    private final Color _gridlineColor;
    
    private final PNode _partsNode;
    
    public LinearYAxisNode( PDimension graphOutlineSize, int numberOfTicks, 
            double topMargin, int exponent, double mantissaSpacing, 
            double tickLength, Stroke tickStroke, Color tickColor, Font labelFont, Color labelColor,
            Stroke gridlineStroke, Color gridlineColor ) {
        
        setPickable( false );
        setChildrenPickable( false );
        
        _graphOutlineSize = new PDimension( graphOutlineSize );
        _numberOfTicks = numberOfTicks;
        _topMargin = topMargin;
        _exponent = exponent;
        _mantissaSpacing = mantissaSpacing;
        _tickLength = tickLength;
        _tickStroke = tickStroke;
        _tickColor = tickColor;
        _labelFont = labelFont;
        _labelColor = labelColor;
        _gridlineStroke = gridlineStroke;
        _gridlineColor = gridlineColor;

        _partsNode = new PComposite();
        addChild( _partsNode );
        
        update();
    }

    public void setTicksExponent( int exponent ) {
        if ( exponent != _exponent ) {
            _exponent = exponent;
            update();
        }
    }

    private void update() {

        _partsNode.removeAllChildren();

        final double usableHeight = _graphOutlineSize.getHeight() - _topMargin;
        final double tickSpacing = usableHeight / ( _numberOfTicks - 1 );

        final ConstantPowerOfTenNumberFormat labelFormat = new ConstantPowerOfTenNumberFormat( "0", _exponent );

        double y = _graphOutlineSize.getHeight();
        for ( int i = 0; i < _numberOfTicks; i++ ) {

            if ( i % 2 == 0 ) {

                PPath leftTickNode = new PPath( new Line2D.Double( -( _tickLength / 2 ), y, +( _tickLength / 2 ), y ) );
                leftTickNode.setStroke( _tickStroke );
                leftTickNode.setStrokePaint( _tickColor );
                _partsNode.addChild( leftTickNode );

                String s = null;
                if ( i == 0 ) {
                    s = "0";
                }
                else {
                    double mantissa = i * _mantissaSpacing * Math.pow( 10, _exponent );
                    s = labelFormat.format( mantissa );
                }
                HTMLNode labelNode = new HTMLNode( s );
                labelNode.setFont( _labelFont );
                labelNode.setHTMLColor( _labelColor );
                double xOffset = leftTickNode.getFullBoundsReference().getMinX() - labelNode.getFullBoundsReference().getWidth() - 5;
                double yOffset = leftTickNode.getFullBoundsReference().getCenterY() - ( labelNode.getFullBoundsReference().getHeight() / 2 );
                labelNode.setOffset( xOffset, yOffset );
                _partsNode.addChild( labelNode );
            }

            PPath gridlineNode = new PPath( new Line2D.Double( +( _tickLength / 2 ), y, _graphOutlineSize.getWidth() - ( _tickLength / 2 ), y ) );
            gridlineNode.setStroke( _gridlineStroke );
            gridlineNode.setStrokePaint( _gridlineColor );
            _partsNode.addChild( gridlineNode );

            y -= tickSpacing;
        }
    }
}
