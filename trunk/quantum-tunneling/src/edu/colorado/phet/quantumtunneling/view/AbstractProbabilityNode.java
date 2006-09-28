/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.view;

import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.color.QTColorScheme;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * AbstractProbabilityNode is the base class for ReflectionProbabilityNode and TransmissionProbabilityNode.
 * <p>
 * ReflectionProbabilityNode is used to display a reflection probability value.
 * <p>
 * TransmissionProbabilityNode is used to display a transmission probability value.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class AbstractProbabilityNode extends PText {

    private static final DecimalFormat FORMAT = new DecimalFormat( "0.00" );
    private static final Color DEFAULT_COLOR = Color.BLACK;
    private static final Font FONT = new Font( QTConstants.FONT_NAME, Font.PLAIN, 18 );
    
    private String _label;
    private double _value;
    
    private AbstractProbabilityNode() {
        super();
        setPickable( false );
        setChildrenPickable( false );
        setFont( FONT );
        setTextPaint( DEFAULT_COLOR );
    }
    
    protected void setLabel( String label ) {
        if ( _label != label ) {
            _label = label;
            update();
        }
    }

    public void setValue( double value ) {
        if ( value != _value ) {
            _value = value;
            update();
        }
    }
    
    public void setColorScheme( QTColorScheme colorScheme ) {
        setTextPaint( colorScheme.getAnnotationColor() );
    }

    private void update() {
        String s = _label + "=" + FORMAT.format( _value );
        setText( s );
    }
    
    public static class ReflectionProbabilityNode extends AbstractProbabilityNode {
        public ReflectionProbabilityNode() {
            super();
            setLabel( SimStrings.get( "label.reflectionProbability" ) );
        }
    }
    
    public static class TransmissionProbabilityNode extends AbstractProbabilityNode {
        public TransmissionProbabilityNode() {
            super();
            setLabel( SimStrings.get( "label.transmissionProbability" ) );
        }
    }
}
