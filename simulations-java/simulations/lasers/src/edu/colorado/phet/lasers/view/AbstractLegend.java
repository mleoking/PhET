/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.view;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common.quantum.model.Atom;
import edu.colorado.phet.common.quantum.model.AtomicState;
import edu.colorado.phet.common.quantum.model.Photon;
import edu.colorado.phet.lasers.model.LaserModel;

/*
 * @author Ron LeMaster
 * @version $Revision$
 */
public class AbstractLegend extends JPanel {
    private GridBagConstraints iconGbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                                 1, 1, 1, 1,
                                                                 GridBagConstraints.CENTER,
                                                                 GridBagConstraints.NONE,
                                                                 new Insets( 0, 60, 5, 10 ),
                                                                 0, 0 );
    private GridBagConstraints labelGbc = new GridBagConstraints( 1, GridBagConstraints.RELATIVE,
                                                                  1, 1, 1, 1,
                                                                  GridBagConstraints.WEST,
                                                                  GridBagConstraints.NONE,
                                                                  new Insets( 0, 0, 5, 10 ),
                                                                  0, 0 );

    public AbstractLegend() {
        super( new GridBagLayout() );
        setBorder( new TitledBorder( SimStrings.getInstance().getString( "Legend.title" ) ) );
    }

    protected BufferedImage getPhotonImage( double wavelength ) {
        Photon photon = new Photon( wavelength, new Point2D.Double(), new Vector2D.Double() );
        return PhotonGraphic.getInstance( this, photon ).getImage();
    }

    public void addForKey( Image image, String key ) {
        String text = SimStrings.getInstance().getString( key );
        addLegendItem( image, text );
    }

    public void addLegendItem( Image image, String text ) {
        add( new JLabel( new ImageIcon( image ) ), iconGbc );
        add( new JLabel( text ), labelGbc );
    }

    public void add3PhotonLegendItems() {
        addLegendItem( getPhotonImage( 680 ), SimStrings.getInstance().getString( "Legend.photon" ) + " (" + SimStrings.getInstance().getString( "Color.red" ) + ")" );
        addLegendItem( getPhotonImage( 470 ), SimStrings.getInstance().getString( "Legend.photon" ) + " (" + SimStrings.getInstance().getString( "Color.blue" ) + ")" );
        addLegendItem( getPhotonImage( 800 ), SimStrings.getInstance().getString( "Legend.photon" ) + " (" + SimStrings.getInstance().getString( "Color.ir" ) + ")" );
    }

    protected BufferedImage getAtomImage() {
        LaserModel quantumModel = new LaserModel( 1 );
        Atom atom = new Atom( quantumModel, 3 );
        atom.setStates( new AtomicState[]{
                quantumModel.getGroundState(),
                quantumModel.getMiddleEnergyState(),
                quantumModel.getHighEnergyState()
        } );
        atom.setCurrState( quantumModel.getGroundState() );
        AnnotatedAtomGraphic atomGraphic = new AnnotatedAtomGraphic( this, atom );
        BufferedImage atomBI = new BufferedImage( atomGraphic.getWidth(), atomGraphic.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE );
        Graphics2D g2BI = (Graphics2D) atomBI.getGraphics();
        g2BI.translate( atomGraphic.getWidth() / 2, atomGraphic.getHeight() / 2 );
        g2BI.scale( .8, .8 );
        atomGraphic.paint( g2BI );
        return atomBI;
    }
}