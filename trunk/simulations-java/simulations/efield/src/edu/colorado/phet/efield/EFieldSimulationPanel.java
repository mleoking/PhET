// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.efield;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.Border;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.view.ResetAllButton;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
import edu.colorado.phet.efield.core.ParticleContainer;
import edu.colorado.phet.efield.core.RandomSystemFactory;
import edu.colorado.phet.efield.electricField.*;
import edu.colorado.phet.efield.gui.*;
import edu.colorado.phet.efield.gui.Painter;
import edu.colorado.phet.efield.gui.addRemove.AddRemove;
import edu.colorado.phet.efield.gui.addRemove.PanelAdapter;
import edu.colorado.phet.efield.gui.addRemove.SystemAdapter;
import edu.colorado.phet.efield.gui.media.MediaControl;
import edu.colorado.phet.efield.gui.mouse.ParticleGrabber;
import edu.colorado.phet.efield.gui.mouse.ParticleThrower;
import edu.colorado.phet.efield.gui.popupMenu.MenuConstructor;
import edu.colorado.phet.efield.gui.popupMenu.ParticlePopupListener;
import edu.colorado.phet.efield.gui.vectorChooser.DefaultVectorPainter;
import edu.colorado.phet.efield.gui.vectorChooser.VectorChooser;
import edu.colorado.phet.efield.gui.vectorChooser.VectorPainter;
import edu.colorado.phet.efield.laws.CoulombsLaw;
import edu.colorado.phet.efield.laws.ForceLawAdapter;
import edu.colorado.phet.efield.particleFactory.CustomizableParticleFactory;
import edu.colorado.phet.efield.particleFactory.ShowParticlePropertyDialog;
import edu.colorado.phet.efield.phys2d_efield.DoublePoint;
import edu.colorado.phet.efield.phys2d_efield.Particle;
import edu.colorado.phet.efield.phys2d_efield.System2D;
import edu.colorado.phet.efield.phys2d_efield.SystemRunner;

public class EFieldSimulationPanel extends JPanel {
    private ElectricFieldPainter electricFieldPainter;
    private ParticlePanel particlePanel;
    private IClock clock;
    private AddRemove addRemove;
    private ParticleGrabber particleGrabber;
    private System2D system2D;
    private MediaControl mediaControl;

    public EFieldSimulationPanel( IClock clock ) {
        this.clock = clock;
    }

    public void init() {
        int num = 0;
        int x = 50;
        int y = 50;
        int width = 300;
        int height = 300;
        ShowParticlePropertyDialog sppd = new ShowParticlePropertyDialog( 1, -1 );

        Particle def = sppd.getDialog().getProperties();
        CustomizableParticleFactory ef = new CustomizableParticleFactory( x, y, width, height, def );
        sppd.getDialog().addParticlePropertyListener( ef );

        RandomSystemFactory randFact = new RandomSystemFactory( 10, num, x, y, width, height );
        ElectricForceLaw fieldLaw = new ElectricForceLaw( new DoublePoint( 0, 0 ) );
        randFact.addForceLaw( fieldLaw );

        CoulombsLaw law = new CoulombsLaw( 100000 );
        ForceLawAdapter cla = new ForceLawAdapter( new Particle[0], law );
        randFact.addForceLaw( cla );

        system2D = randFact.newSystem();

        final SystemRunner systemRunner = new SystemRunner( system2D );
        clock.addClockListener( new ClockAdapter() {
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                systemRunner.step( clockEvent.getSimulationTimeChange() );
            }
        } );

        particlePanel = new ParticlePanel();
        BufferedImage bi = EFieldResources.loadBufferedImage( "electron9.gif" );
        ParticlePainter painter = new ImagePainter( bi );

        randFact.updatePanel( particlePanel, system2D, painter );
        particleGrabber = new ParticleThrower( particlePanel, system2D, systemRunner, 5, 18 );
        particlePanel.addMouseListener( particleGrabber );
        particlePanel.addMouseMotionListener( particleGrabber );

        setLayout( new BorderLayout() );
        add( particlePanel, BorderLayout.CENTER );
        validate();

        PiccoloClockControlPanel clockControlPanel = new PiccoloClockControlPanel( clock );
        ResetAllButton resetAllButton = new ResetAllButton( new Resettable() {
            public void reset() {
                doReset();
            }
        }, this );

        addRemove = new AddRemove( new Vector(), ef, particlePanel, painter );
        addRemove.add( new PanelAdapter( particlePanel, painter ) );
        SystemAdapter sa = new SystemAdapter( system2D );
        addRemove.add( sa );

        for ( int i = 0; i < system2D.numLaws(); i++ ) {
            if ( system2D.lawAt( i ) instanceof ParticleContainer ) {
                addRemove.add( (ParticleContainer) system2D.lawAt( i ) );
            }
        }

        JPanel addRemovePanel = addRemove.getJPanel();

        JButton propertyButton = new JButton( EFieldResources.getString( "FieldNode2.PropertiesButton" ) );
        propertyButton.addActionListener( sppd );
        addRemovePanel.add( propertyButton );


        JPanel southPanel = new JPanel();
        southPanel.setLayout( new BoxLayout( southPanel, BoxLayout.X_AXIS ) );
        southPanel.add( clockControlPanel );
        clockControlPanel.add( resetAllButton );
        southPanel.add( addRemovePanel );
        VectorPainter vp = new DefaultVectorPainter( Color.blue, new BasicStroke( 6 ), Math.PI / 8, 10 );

        VectorChooser fieldPanel = new VectorChooser( 130, 130, 1, 1, vp );
        fieldPanel.addListener( fieldLaw );
        southPanel.add( fieldPanel );

        Border etched = BorderFactory.createEtchedBorder();
        Border b = BorderFactory.createTitledBorder( etched, EFieldResources.getString( "FieldNode2.ExternalFieldBorder" ) );
        fieldPanel.setBorder( b );
        add( southPanel, BorderLayout.SOUTH );

        Painter wally = new WallPainter( x - 15, y - 15, width + 25, height + 25, new BasicStroke( 8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER ), Color.blue );
        particlePanel.add( wally );

        int numFieldDots = 6;

        VectorPainter vp2 = new DefaultVectorPainter( Color.blue, new BasicStroke( 2 ), Math.PI / 8, 4 );
        electricFieldPainter = new ElectricFieldPainter( x, y, width, height, numFieldDots, numFieldDots, vp2 );
        electricFieldPainter.addSource( fieldLaw );
        int maxArrowLength = 25;
        ChargeFieldSource cs = new ChargeFieldSource( cla, 120000, maxArrowLength ); //sa
        electricFieldPainter.addSource( cs );

        particlePanel.add( electricFieldPainter );

        MenuConstructor emcee = new FieldMenuConstructor( cs, particlePanel );
        ParticlePopupListener ml = new ParticlePopupListener( particlePanel, emcee );
        particlePanel.addMouseListener( ml );

        mediaControl = new MediaControl( clock, systemRunner, randFact, particlePanel, painter );
        mediaControl.add( addRemove );
        mediaControl.add( particleGrabber );
    }

    private void doReset() {
        mediaControl.reset();
    }

    public JMenu getMenu() {
        DiscreteFieldSlider dfs = new DiscreteFieldSlider( electricFieldPainter, particlePanel );
        JMenuItem fieldDiscretionItem = new JMenuItem( EFieldResources.getString( "FieldNode2.SetFieldDiscretenessMenuItem" ) );
        fieldDiscretionItem.addActionListener( dfs );
        JMenu fieldMenu = new JMenu( EFieldResources.getString( "FieldNode2.ElectricFieldMenuTitle" ) );
        fieldMenu.add( fieldDiscretionItem );
        return fieldMenu;
    }
}
