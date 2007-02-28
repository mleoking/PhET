package edu.colorado.phet.bernoulli.pump;

import edu.colorado.phet.bernoulli.BernoulliModule;
import edu.colorado.phet.bernoulli.pipe.VolumeGraphic;
import edu.colorado.phet.common.model.ModelElement;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Aug 19, 2003
 * Time: 10:40:36 PM
 * Copyright (c) Aug 19, 2003 by Sam Reid
 */
public class PumpControlPanel extends JPanel {

    public PumpControlPanel( final BernoulliModule module ) {
        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );


//        final JCheckBox antialias = new JCheckBox("Antialias", BernoulliModule.antialias);
//        antialias.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                BernoulliModule.antialias = antialias.isSelected();
//            }
//        });
//        add(antialias);

        JButton zoomIn = new JButton( "Zoom to Pump" );
        zoomIn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.zoomToPump();
            }
        } );
        add( zoomIn );

//        JButton zoomToPipe = new JButton("Zoom To Pipe");
//        zoomToPipe.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                module.zoomToPipe();
//            }
//        });
//        add(zoomToPipe);

        JButton zoomToWaterTower = new JButton( "Full Zoom" );
        zoomToWaterTower.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.zoomToFull();
            }
        } );
        add( zoomToWaterTower );

        JButton refillWaterTower = new JButton( "Refill Water Tower" );
        refillWaterTower.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.getWaterTower().setFractionalWaterVolume( .80 );
                module.getRepaintManager().update();
            }
        } );
        JButton zoomOutButton = new JButton( "zoom out" );
        final ModelElement zoomOutElement = new ModelElement() {
            public void stepInTime( double v ) {
                Rectangle2D.Double rect = module.getTransform().getModelBounds();
                double dx = .1;
                double dy = .1;

                double x = rect.x - dx;
                double y = rect.y - dy;
                double width = rect.width + dx * 2;
                double height = rect.height + dy * 2;
                module.getTransform().setModelBounds( new Rectangle2D.Double( x, y, width, height ) );
            }
        };

        final ModelElement zoomInElement = new ModelElement() {
            public void stepInTime( double v ) {
                Rectangle2D.Double rect = module.getTransform().getModelBounds();
                double dx = -.1;
                double dy = -.1;

                double x = rect.x - dx;
                double y = rect.y - dy;
                double width = rect.width + dx * 2;
                double height = rect.height + dy * 2;
                module.getTransform().setModelBounds( new Rectangle2D.Double( x, y, width, height ) );
            }
        };

        zoomOutButton.addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                module.getModel().addModelElement( zoomOutElement );
            }

            public void mouseReleased( MouseEvent e ) {
                module.getModel().removeModelElement( zoomOutElement );
            }
        } );

        JButton zoomInButton = new JButton( "zoom in" );
        zoomInButton.addMouseListener( new MouseAdapter() {
            public void mouseReleased( MouseEvent e ) {
                module.getModel().removeModelElement( zoomInElement );
            }

            public void mousePressed( MouseEvent e ) {
                module.getModel().addModelElement( zoomInElement );
            }
        } );
        final ModelElement panLeftElement = new ModelElement() {
            public void stepInTime( double v ) {
                Rectangle2D.Double rect = module.getTransform().getModelBounds();
                double dx = -.18;
                double dy = 0;

                double x = rect.x - dx;
                double y = rect.y - dy;
                double width = rect.width;// + dx * 2;
                double height = rect.height;// + dy * 2;
                module.getTransform().setModelBounds( new Rectangle2D.Double( x, y, width, height ) );
            }
        };
        final ModelElement panUpElement = new ModelElement() {
            public void stepInTime( double v ) {
                Rectangle2D.Double rect = module.getTransform().getModelBounds();
                double dx = 0;//.18;
                double dy = -.180;

                double x = rect.x - dx;
                double y = rect.y - dy;
                double width = rect.width;// + dx * 2;
                double height = rect.height;// + dy * 2;
                module.getTransform().setModelBounds( new Rectangle2D.Double( x, y, width, height ) );
            }
        };
        final ModelElement panDownElement = new ModelElement() {
            public void stepInTime( double v ) {
                Rectangle2D.Double rect = module.getTransform().getModelBounds();
                double dx = 0;//.18;
                double dy = .180;

                double x = rect.x - dx;
                double y = rect.y - dy;
                double width = rect.width;// + dx * 2;
                double height = rect.height;// + dy * 2;
                module.getTransform().setModelBounds( new Rectangle2D.Double( x, y, width, height ) );
            }
        };
        final ModelElement panRightElement = new ModelElement() {
            public void stepInTime( double v ) {
                Rectangle2D.Double rect = module.getTransform().getModelBounds();
                double dx = .18;
                double dy = 0;

                double x = rect.x - dx;
                double y = rect.y - dy;
                double width = rect.width;// + dx * 2;
                double height = rect.height;// + dy * 2;
                module.getTransform().setModelBounds( new Rectangle2D.Double( x, y, width, height ) );
            }
        };
        JButton panLeft = new JButton( "Pan Right" );
        panLeft.addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                module.getModel().addModelElement( panLeftElement );
            }

            public void mouseReleased( MouseEvent e ) {
                module.getModel().removeModelElement( panLeftElement );
            }
        } );
        JButton panRight = new JButton( "Pan Left" );
        panRight.addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                module.getModel().addModelElement( panRightElement );
            }

            public void mouseReleased( MouseEvent e ) {
                module.getModel().removeModelElement( panRightElement );
            }
        } );
        JButton panDown = new JButton( "Pan Down" );
        panDown.addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                module.getModel().addModelElement( panDownElement );
            }

            public void mouseReleased( MouseEvent e ) {
                module.getModel().removeModelElement( panDownElement );
            }
        } );

        JButton panUp = new JButton( "Pan Up" );
        panUp.addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                module.getModel().addModelElement( panUpElement );
            }

            public void mouseReleased( MouseEvent e ) {
                module.getModel().removeModelElement( panUpElement );
            }
        } );

        add( refillWaterTower );
        add( zoomOutButton );
        add( zoomInButton );

//        final JCheckBox enableAutoPump = new JCheckBox( "AutoPump" );
//        enableAutoPump.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                AutoPump.active = enableAutoPump.isSelected();
//            }
//        } );
//        add( enableAutoPump );

//        final JCheckBox pipeOn = new JCheckBox("Pipe", true);
//        pipeOn.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                module.setPipeOn(pipeOn.isSelected());
//            }
//        });
//        add(pipeOn);

//        final JCheckBox showDebugData = new JCheckBox("Show pipe debug data.");
//        showDebugData.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                VolumeGraphic.showDebuggingGraphics = showDebugData.isSelected();
//            }
//        });
//        add(showDebugData);
        final JCheckBox showVolumeWidth = new JCheckBox( "Show Volume Dimensions", VolumeGraphic.showWidthAndHeight );
        showVolumeWidth.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                VolumeGraphic.showWidthAndHeight = showVolumeWidth.isSelected();
            }
        } );
        add( showVolumeWidth );
        add( panLeft );
        add( panRight );
        add( panDown );
        add( panUp );
    }

}
