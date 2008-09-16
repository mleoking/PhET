package edu.colorado.phet.electrichockey;

import java.awt.*;
import java.util.Vector;

public class FieldGrid //extends JLabel
{
    private ElectricHockeySimulationPanel electricHockeySimulationPanel;
    private int width, height;
    private int gridNbrWidth;            //number of grid points across width of field
    private int gridSpacing;            //grid spacing
    private int gridNbrHeight;            //number of grid points across height of field
    private Charge[][] gridChargeArray;    //+1 test charge on every grid point
    private Force[][] gridForceArray;    //net force on test charge at every grid point
    private double gridForceFactor;        //arbitrary scale factor controlling force arrow length
    private boolean antialias = false;

    public FieldGrid( int width, int height, ElectricHockeySimulationPanel electricHockeySimulationPanel ) {
        this.width = width;
        this.height = height;
        this.electricHockeySimulationPanel = electricHockeySimulationPanel;
        gridForceFactor = 25;
        gridNbrWidth = 25;
        gridSpacing = width / gridNbrWidth;
        gridNbrHeight = height / gridSpacing;
        gridChargeArray = new Charge[gridNbrWidth][gridNbrHeight];
        gridForceArray = new Force[gridNbrWidth][gridNbrHeight];
        initializeGridChargesAndForces();
    }

    public void initializeGridChargesAndForces() {

        for ( int i = 0; i < gridNbrWidth; i++ ) {
            for ( int j = 0; j < gridNbrHeight; j++ ) {
                Point pt = new Point( i * gridSpacing + gridSpacing / 2, j * gridSpacing + gridSpacing / 2 );
                gridChargeArray[i][j] = new Charge( pt, Charge.GRID );
                Force nullForceIJ = new Force( 0.0, 0.0, gridChargeArray[i][j], new Color( 250, 250, 250 ) );
                gridForceArray[i][j] = nullForceIJ;
            }
        }
    }

    public void updateGridForceArray() {
        Vector chargeList = electricHockeySimulationPanel.getModel().getChargeList();
        for ( int i = 0; i < gridNbrWidth; i++ ) {
            for ( int j = 0; j < gridNbrHeight; j++ ) {
                double gridIJNetX = 0.0;
                double gridIJNetY = 0.0;
                for ( int k = 0; k < chargeList.size(); k++ ) {
                    Charge chargeK = (Charge) chargeList.elementAt( k );
                    Force forceIJK = new Force( chargeK, gridChargeArray[i][j] );
                    gridIJNetX += forceIJK.getXComp();
                    gridIJNetY += forceIJK.getYComp();
                }
                //int grayscaleOffset=1;//Dubson's preference
                int grayscaleOffset = 0;
                //double power= 0.5;
                double power = 1.0;
                double forceMag = grayscaleOffset + Math.pow( ( Math.pow( gridIJNetX, 2.0 ) + Math.pow( gridIJNetY, 2.0 ) ), 0.5 ); //magnitude of net force
                //gridForceArray[i][j] = new edu.colorado.phet.ehockey.Force(Math.pow(gridIJNetX, 0.5), Math.pow(gridIJNetY, 0.5), gridChargeArray[i][j]);
                //gridForceArray[i][j] = new edu.colorado.phet.ehockey.Force(gridForceFactor*gridIJNetX, gridForceFactor*gridIJNetY, gridChargeArray[i][j]);
                int colorFactor = 100;
                double potential = Math.pow( forceMag, power );                    //white
                colorFactor = (int) ( -255.0 * potential / 300.0 + 255.0 );            //gray
                if ( colorFactor < 1 ) {
                    colorFactor = 1;                        //black
                }
                else if ( colorFactor > 255 ) {
                    colorFactor = 255;                //white
                }
                Color gridColor = new Color( colorFactor, colorFactor, colorFactor );
                //System.out.println("edu.colorado.phet.ehockey.Force = " + (int)forceMag + "  Factor = " + forceFactor);
                gridForceArray[i][j] = new Force( gridForceFactor * gridIJNetX / forceMag, gridForceFactor * gridIJNetY / forceMag, gridChargeArray[i][j], gridColor );
            }
        }
    }


    public void setGridNbrWidth( int gridNbrWidth ) {
        this.gridNbrWidth = gridNbrWidth;
    }

    public int getGridNbrWidth() {
        return gridNbrWidth;
    }

    public void paint( Graphics2D g2D ) {
        g2D.setColor( Color.white );
        g2D.setBackground( Color.white );
        g2D.fillRect( 0, 0, width, height );
        if ( antialias ) {
            g2D.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        }
        for ( int i = 0; i < gridNbrWidth; i++ ) {
            for ( int j = 0; j < gridNbrHeight; j++ ) {
                //draw arrows
                gridForceArray[i][j].paintGridArrow( g2D );
            }
        }
    }//end of paint

    public boolean isAntialias() {
        return antialias;
    }

    public void setAntialias( boolean antialias ) {
        this.antialias = antialias;
        electricHockeySimulationPanel.getPlayingField().updateBufferedImage();
        electricHockeySimulationPanel.getPlayingField().repaint();
    }
}//end of public class