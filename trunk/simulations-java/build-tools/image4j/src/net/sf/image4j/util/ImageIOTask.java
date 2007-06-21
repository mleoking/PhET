/* Copyright 2007, University of Colorado */
package net.sf.image4j.util;

import net.sf.image4j.codec.ico.ICOEncoder;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageIOTask extends Task {
    private File inputFile;
    private File outputFile;
    
    private int outputWidth;
    private int outputHeight;

    public void execute() throws BuildException {
        try {
            String name = outputFile.getName();

            String extension = name.substring( name.lastIndexOf( '.' ) + 1 ).toLowerCase();

            BufferedImage inputImage = ImageIO.read( inputFile );

            int width  = outputWidth  == 0 ? inputImage.getWidth()  : outputWidth;
            int height = outputHeight == 0 ? inputImage.getHeight() : outputHeight;
            
            inputImage = ImageUtil.rescale( inputImage, width, height );

            if ( extension.equals("ico") ) {
                ICOEncoder.write( inputImage, outputFile );
            }
            else {
                ImageIO.write( inputImage, extension, outputFile );
            }
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

    public void setOutputWidth( int outputWidth ) {
        this.outputWidth = outputWidth;
    }

    public void setOutputHeight( int outputHeight ) {
        this.outputHeight = outputHeight;
    }



    public static void main( String[] args ) {
        ImageIOTask task = new ImageIOTask();

        task.setOutputWidth( 64 );
        task.setOutputHeight( 64 );
        task.setInputFile(  new File( "/Users/jdegoes/Documents/dev/simulations-java/simulations/balloons/screenshot.jpg" ) );
        task.setOutputFile( new File( "/Users/jdegoes/Documents/dev/simulations-java/simulations/balloons/screenshot.png" ) );

        try {
            task.execute();
        }
        catch( BuildException e ) {
            e.printStackTrace();
        }

    }
}
