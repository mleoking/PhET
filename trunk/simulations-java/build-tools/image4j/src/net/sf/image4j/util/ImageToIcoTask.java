/* Copyright 2007, University of Colorado */
package net.sf.image4j.util;

import net.sf.image4j.codec.ico.ICOEncoder;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageToIcoTask extends Task {
    private File inputFile;
    private File outputFile;

    private static BufferedImage rescale( BufferedImage in, int width, int height ) {
        BufferedImage newImage = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );

        Graphics2D g2 = newImage.createGraphics();
        AffineTransform at = AffineTransform.getScaleInstance( (double)width/in.getWidth(), (double)height/in.getHeight() );
        g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC );
        g2.setRenderingHint( RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY );
        g2.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
        g2.drawRenderedImage( in, at );
        return newImage;
    }

    public void execute() throws BuildException {
        try {
            BufferedImage inputImage = ImageIO.read( inputFile );
            
            inputImage = rescale( inputImage, 64, 64 );

            ICOEncoder.write( inputImage, getOutputFile() );
        }
        catch( IOException e ) {
            throw new BuildException( e );
        }
    }

    public void setInputFile( File inputFile ) {
        this.inputFile = inputFile;
    }

    public void setOutputFile( File outputFile ) {
        this.outputFile = outputFile;
    }

    private File getOutputFile() {
        if (outputFile == null ) {
            String name = inputFile.getName();

            outputFile = new File( inputFile.getParentFile(),
                name.substring(0, name.lastIndexOf( '.' )) + ".ico" 
           );

        }

        return outputFile;

    }

    public static void main( String[] args ) {
        ImageToIcoTask task = new ImageToIcoTask();

        task.setInputFile( new File( "/Users/jdegoes/Documents/dev/simulations-java/simulations/balloons/screenshot.jpg" ) );

        try {
            task.execute();
        }
        catch( BuildException e ) {
            e.printStackTrace();
        }

    }
}
