/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.control;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.QuadCurve2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.*;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.enum.WellType;
import edu.colorado.phet.common.view.util.SimStrings;


/**
 * BSWellComboBox is the combo box for choosing the well type.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSWellComboBox extends JComboBox {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Stroke ICON_STROKE = new BasicStroke( 2f );
    private static final Color ICON_COLOR = BSConstants.POTENTIAL_ENERGY_COLOR;
    private static final int ICON_MARGIN = 4;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private static ArrayList _choices;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public BSWellComboBox() {
        _choices = new ArrayList();
        WellComboBoxRenderer renderer = new WellComboBoxRenderer();
        setRenderer( renderer );
    }
    
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Clears the set of choices.
     */
    public void clearChoices() {
        _choices.clear();
        removeAllItems();
    }
    
    /**
     * Adds a well type choice.
     * 
     * @param wellType
     */
    public void addChoice( WellType wellType ) {
        if ( wellType == WellType.COULOMB ) {
            ImageIcon icon = createCoulombIcon();
            WellChoice item = new WellChoice( wellType, SimStrings.get( "choice.well.coulomb" ), icon );
            _choices.add( item );
            addItem( item );
        }
        else if ( wellType == WellType.HARMONIC_OSCILLATOR ) {
            ImageIcon icon = createHarmonicOscillatorIcon();
            WellChoice item = new WellChoice( wellType, SimStrings.get( "choice.well.harmonicOscillator" ), icon );
            _choices.add( item );
            addItem( item );
        }
        else if ( wellType == WellType.SQUARE ) {
            ImageIcon icon = createSquareIcon();
            WellChoice item = new WellChoice( wellType, SimStrings.get( "choice.well.square" ), icon );
            _choices.add( item );
            addItem( item );
        }
        else if ( wellType == WellType.ASYMMETRIC ) {
            ImageIcon icon = createAsymmetricIcon();
            WellChoice item = new WellChoice( wellType, SimStrings.get( "choice.well.asymmetric" ), icon );
            _choices.add( item );
            addItem( item );
            
        }
        else {
            throw new IllegalArgumentException( "unsupported well type: " + wellType );
        }
        setMaximumRowCount( _choices.size() );
    }
    
    /**
     * Gets the current selection.
     * 
     * @return a WellType
     */
    public WellType getSelectedWellType() {
        return ((WellChoice)getSelectedItem()).getWellType();
    }
    
    /**
     * Sets the current selection.
     * 
     * @param wellType
     */
    public void setSelectedWellType( WellType potentialType ) {
        Iterator i = _choices.iterator();
        while ( i.hasNext() ) {
            WellChoice choice = (WellChoice) i.next();
            if ( choice.getWellType() == potentialType ) {
                setSelectedItem( choice );
                break;
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Icon creators
    //----------------------------------------------------------------------------
    
    /*
     * Creates the "Coulomb" well icon.
     */
    private static ImageIcon createCoulombIcon() {
        final int w = 20;
        final int h = 20;
        BufferedImage bi = new BufferedImage( w, h, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2 = bi.createGraphics();
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        QuadCurve2D curve1 = new QuadCurve2D.Double();
        QuadCurve2D curve2 = new QuadCurve2D.Double();
        curve1.setCurve( 0, 4, 8, 5, 7, 16 );
        curve2.setCurve( 10, 16, 11, 5, 17, 4 );
        g2.setStroke( ICON_STROKE );
        g2.setPaint( ICON_COLOR );
        g2.draw( curve1 );
        g2.draw( curve2 );
        return new ImageIcon( bi );
    }
    
    /*
     * Creates the "Harmonic Oscillator" well icon.
     */
    private static ImageIcon createHarmonicOscillatorIcon() {
        final int w = 17;
        final int h = 20;
        BufferedImage bi = new BufferedImage( w, h, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2 = bi.createGraphics();
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        QuadCurve2D curve = new QuadCurve2D.Double();
        curve.setCurve( 0, 3, w/2, 30, w, 3 );
        g2.setStroke( ICON_STROKE );
        g2.setPaint( ICON_COLOR );
        g2.draw( curve );
        return new ImageIcon( bi );
    }
    
    /*
     * Creates the "Square" well icon.
     */
    private static ImageIcon createSquareIcon() {
        final int w = 50;
        final int h = 20;
        BufferedImage bi = new BufferedImage( w, h, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2 = bi.createGraphics();
        GeneralPath path = new GeneralPath();
        path.moveTo( 0, ICON_MARGIN );
        path.lineTo( w/5, ICON_MARGIN );
        path.lineTo( w/5, h - ICON_MARGIN );
        path.lineTo( 2*w/5, h - ICON_MARGIN );
        path.lineTo( 2*w/5, ICON_MARGIN );
        path.lineTo( 3*w/5, ICON_MARGIN );
        g2.setStroke( ICON_STROKE );
        g2.setPaint( ICON_COLOR );
        g2.draw( path );
        return new ImageIcon( bi );
    }
    
    /*
     * Creates the "Asymmetric" well icon.
     */
    private static ImageIcon createAsymmetricIcon() {
        final int w = 50;
        final int h = 20;
        BufferedImage bi = new BufferedImage( w, h, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2 = bi.createGraphics();
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        GeneralPath path = new GeneralPath();
        path.moveTo( 0, ICON_MARGIN );
        path.lineTo( w/5, ICON_MARGIN );
        path.lineTo( w/5, h - ICON_MARGIN );
        path.lineTo( 2*w/5, ICON_MARGIN );
        path.lineTo( 3*w/5, ICON_MARGIN );
        g2.setStroke( ICON_STROKE );
        g2.setPaint( ICON_COLOR );
        g2.draw( path );
        return new ImageIcon( bi );
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /**
     * WellChoice is the object that is added to the combo box.
     * Each of these objects represents a choice.
     */
    public static class WellChoice {

        private WellType _wellType;
        private String _label;
        private ImageIcon _imageIcon;

        public WellChoice( WellType wellType, String label, ImageIcon imageIcon ) {
            _wellType = wellType;
            _label = label;
            _imageIcon = imageIcon;
        }

        public WellType getWellType() {
            return _wellType;
        }

        public ImageIcon getImageIcon() {
            return _imageIcon;
        }

        public String getLabel() {
            return _label;
        }
    }
    
    /*
     * WellComboBoxRenderer renders a combo box choice as 
     * a text label with an icon positioned to the right of the text.
     */
    private static class WellComboBoxRenderer extends JLabel implements ListCellRenderer {

        public WellComboBoxRenderer() {
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
            WellChoice choice = (WellChoice) value;
            setText( choice.getLabel() );
            setIcon( choice.getImageIcon() );

            return this;
        }

    }
}
