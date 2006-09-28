/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.control;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.*;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.quantumtunneling.enums.PotentialType;


/**
 * PotentialComboBox is the combo box for choosing the potential type.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PotentialComboBox extends JComboBox {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Dimension ICON_SIZE = new Dimension( 50, 20 );
    private static final Stroke ICON_STROKE = new BasicStroke( 2f );
    private static final int ICON_MARGIN = 4;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Color _potentialColor;
    private ArrayList _choices;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public PotentialComboBox() {
        
        _potentialColor = Color.BLACK;
        
        initChoices();
        
        PotentialComboBoxRenderer renderer = new PotentialComboBoxRenderer();
        setRenderer( renderer );
    }
    
    /*
     * Initializes the choices in the combo box.
     */
    private void initChoices() {
        
        if ( _choices == null ) {      
            _choices = new ArrayList();
        }
        else {
            _choices.clear();
        }
        
        ImageIcon constantIcon = createConstantIcon( _potentialColor );
        ImageIcon stepIcon = createStepIcon( _potentialColor );
        ImageIcon singleBarrierIcon = createSingleBarrierIcon( _potentialColor );
        ImageIcon doubleBarrierIcon = createDoubleBarrierIcon( _potentialColor );

        PotentialChoice constantItem = new PotentialChoice( PotentialType.CONSTANT, SimStrings.get( "choice.potential.constant" ), constantIcon );
        PotentialChoice stepItem = new PotentialChoice( PotentialType.STEP, SimStrings.get( "choice.potential.step" ), stepIcon );
        PotentialChoice singleBarrierItem = new PotentialChoice( PotentialType.SINGLE_BARRIER, SimStrings.get( "choice.potential.barrier" ), singleBarrierIcon );
        PotentialChoice doubleBarrierItem = new PotentialChoice( PotentialType.DOUBLE_BARRIER, SimStrings.get( "choice.potential.double" ), doubleBarrierIcon );

        _choices.add( constantItem );
        _choices.add( stepItem );
        _choices.add( singleBarrierItem );
        _choices.add( doubleBarrierItem );
        
        removeAllItems();
        Iterator i = _choices.iterator();
        while( i.hasNext() ) {
            addItem( i.next() );
        }
        setMaximumRowCount( _choices.size() );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the color used to draw potential energy icons.
     * This causes the choices in the combo box to be rebuilt,
     * and will cause ItemEvents to be fired.  You may want to 
     * temporarily remove ItemListeners before calling this method.
     * 
     * @param color
     */
    public void setPotentialColor( Color color ) {
        _potentialColor = color;
        PotentialType selectedType = getSelectedPotentialType();
        initChoices();
        setSelectedPotentialType( selectedType );
    }
    
    /**
     * Gets the current selection.
     * 
     * @return a PotentialType
     */
    public PotentialType getSelectedPotentialType() {
        return ((PotentialChoice)getSelectedItem()).getPotentialType();
    }
    
    /**
     * Sets the current selection.
     * 
     * @param potentialType
     */
    public void setSelectedPotentialType( PotentialType potentialType ) {
        Iterator i = _choices.iterator();
        while ( i.hasNext() ) {
            PotentialChoice choice = (PotentialChoice) i.next();
            if ( choice.getPotentialType() == potentialType ) {
                setSelectedItem( choice );
                break;
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Icon creators
    //----------------------------------------------------------------------------
    
    /*
     * Creates the "constant potential" icon.
     */
    private static ImageIcon createConstantIcon( Color color ) {
        final int w = ICON_SIZE.width;
        final int h = ICON_SIZE.height;
        BufferedImage bi = new BufferedImage( w, h, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2 = bi.createGraphics();
        GeneralPath path = new GeneralPath();
        path.moveTo( 0, h/2 );
        path.lineTo( w/2, h/2 );
        g2.setStroke( ICON_STROKE );
        g2.setPaint( color );
        g2.draw( path );
        return new ImageIcon( bi );
    }
    
    /*
     * Creates the "step potential" icon.
     */
    private static ImageIcon createStepIcon( Color color ) {
        final int w = ICON_SIZE.width;
        final int h = ICON_SIZE.height;
        BufferedImage bi = new BufferedImage( w, h, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2 = bi.createGraphics();
        GeneralPath path = new GeneralPath();
        path.moveTo( 0, h - ICON_MARGIN );
        path.lineTo( w/3, h - ICON_MARGIN );
        path.lineTo( w/3, ICON_MARGIN );
        path.lineTo( 2*w/3, ICON_MARGIN );
        g2.setStroke( ICON_STROKE );
        g2.setPaint( color );
        g2.draw( path );
        return new ImageIcon( bi );
    }
    
    /*
     * Creates the "single barrier potential" icon.
     */
    private static ImageIcon createSingleBarrierIcon( Color color ) {
        final int w = ICON_SIZE.width;
        final int h = ICON_SIZE.height;
        BufferedImage bi = new BufferedImage( w, h, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2 = bi.createGraphics();
        GeneralPath path = new GeneralPath();
        path.moveTo( 0, h - ICON_MARGIN );
        path.lineTo( w/5, h - ICON_MARGIN );
        path.lineTo( w/5, ICON_MARGIN );
        path.lineTo( 2*w/5, ICON_MARGIN );
        path.lineTo( 2*w/5, h - ICON_MARGIN );
        path.lineTo( 3*w/5, h - ICON_MARGIN );
        g2.setStroke( ICON_STROKE );
        g2.setPaint( color );
        g2.draw( path );
        return new ImageIcon( bi );
    }
    
    /*
     * Creates the "double barrier potential" icon.
     */
    private static ImageIcon createDoubleBarrierIcon( Color color ) {
        final int w = ICON_SIZE.width;
        final int h = ICON_SIZE.height;
        BufferedImage bi = new BufferedImage( w, h, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2 = bi.createGraphics();
        GeneralPath path = new GeneralPath();
        path.moveTo( 0, h - ICON_MARGIN );
        path.lineTo( w/5, h - ICON_MARGIN );
        path.lineTo( w/5, ICON_MARGIN );
        path.lineTo( 2*w/5, ICON_MARGIN );
        path.lineTo( 2*w/5, h - ICON_MARGIN );
        path.lineTo( 3*w/5, h - ICON_MARGIN );
        path.lineTo( 3*w/5, ICON_MARGIN );
        path.lineTo( 4*w/5, ICON_MARGIN );
        path.lineTo( 4*w/5, h - ICON_MARGIN );
        path.lineTo( w, h - ICON_MARGIN );
        g2.setStroke( ICON_STROKE );
        g2.setPaint( color );
        g2.draw( path );
        return new ImageIcon( bi );
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /*
     * PotentialChoice is the object that is added to the combo box.
     * Each of these objects represents a choice.
     */
    private static class PotentialChoice {

        private PotentialType _potentialType;
        private String _label;
        private ImageIcon _imageIcon;

        public PotentialChoice( PotentialType potentialType, String label, ImageIcon imageIcon ) {
            _potentialType = potentialType;
            _label = label;
            _imageIcon = imageIcon;
        }

        public PotentialType getPotentialType() {
            return _potentialType;
        }

        public ImageIcon getImageIcon() {
            return _imageIcon;
        }
        
        public void setImageIcon( ImageIcon imageIcon ) {
            _imageIcon = imageIcon;
        }
        
        public String getLabel() {
            return _label;
        }
    }
    
    /*
     * PotentialComboBoxRenderer renders a combo box choice as 
     * a text label with an icon positioned to the right of the text.
     */
    private class PotentialComboBoxRenderer extends JLabel implements ListCellRenderer {

        public PotentialComboBoxRenderer() {
            super();
            setOpaque( true ); // for Macintosh
            setHorizontalTextPosition( SwingConstants.LEFT ); // text to left of icon
        }
        
        public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
            
            if ( isSelected ) {
                setBackground( list.getSelectionBackground() );
                setForeground( list.getSelectionForeground() );
            }
            else {
                setBackground( list.getBackground() );
                setForeground( list.getForeground() );
            }

            // Set the icon and text.
            PotentialChoice choice = (PotentialChoice) value;
            setText( choice.getLabel() );
            setIcon( choice.getImageIcon() );

            return this;
        }

    }
}
