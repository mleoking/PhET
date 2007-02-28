import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.applet.*;
import java.io.*;
import JSci.maths.wavelet.symmlet8.*;

/**
* This applet is meant to illustrate the FWT classes in JSci.
* It is both speedy and convenient for some
* intensive applications such as image processing.
*
* @author Daniel Lemire
*/
public final class ImageTransform extends Applet {
  final int W=256;
  final FastSymmlet8 fwt=new FastSymmlet8();
  final float[][] mY=new float[W][W];
  final float[][] mI=new float[W][W];
  final float[][] mQ=new float[W][W];
  Image lena;
  Image lenaTransformed;

  public void init() {
    System.err.println("(C) 1999 Daniel Lemire, Ph.D.");
    setSize(2*W,W);
    System.err.println("Loading image...");
    MediaTracker mt=new MediaTracker(this);
    Class thisclass=this.getClass();
    InputStream is=thisclass.getResourceAsStream("lena.gif");
    if(is!=null)
      System.err.println("Image found...");
    else
      System.err.println("Image cannot be found!");
    int b;
    ByteArrayOutputStream  bais=new ByteArrayOutputStream();
    try {
      while((b=is.read())!=-1) {
        bais.write(b);
      }
    } catch(IOException ioe) {
      ioe.printStackTrace();
    }
    System.err.println("Image read...");
    lena=Toolkit.getDefaultToolkit().createImage(bais.toByteArray());
    mt.addImage(lena,0,W,W);
    try {
      mt.waitForAll();
    } catch (InterruptedException ie) {
      ie.printStackTrace();
    }
    System.err.println("Image loaded...");
    int[] pixels = new int[W * W];
      PixelGrabber pg = new PixelGrabber(lena, 0, 0, W, W, pixels, 0, W);
    try {
    pg.grabPixels();
    } catch (InterruptedException ie) {
      ie.printStackTrace();
    }
    byte red,green,blue;
    for(int k=0;k<W;k++) {
      for(int l=0;l<W;l++) {
        red = (byte)( ((pixels[k*W+l]>>16) & 0xFF) -128);
				green = (byte)( ((pixels[k*W+l]>>8) & 0xFF) - 128);
				blue = (byte)( (pixels[k*W+l] & 0xFF)  - 128);
        mY[k][l] = (  red*.299f+green*.587f+blue*.114f);
      }
    }
        transform();
        quantize();
   final int two24 = twoPower(24);
   final int two16 = twoPower(16);
   final int two8 = twoPower(8);
    for(int k=0;k<W;k++) {
      for(int l=0;l<W;l++) {
        pixels[k*W+l]=(int)mY[k][l]*two16 +(int)mY[k][l]*two8 + (int)mY[k][l] -two24;
      }
    }
    MemoryImageSource topmis=new MemoryImageSource(W, W, pixels, 0, W);
    lenaTransformed=createImage(topmis);
    mt.addImage(lenaTransformed,1,W,W);
    try {
      mt.waitForAll();
    } catch (InterruptedException ie) {
      ie.printStackTrace();
    }
        this.setFont(new Font("Default",Font.BOLD,12));
  }

  private static int twoPower(int exp)
    {
       int result = 1;
       for(int k = 0; k < exp; ++k)
          result = 2*result;
        return result;
     }

  public String getAppletInfo() {
    return "This applet is meant to illustrate the FWT classes in JSci";
  }

  private void transform() {
    transformColumns();
    transformRows();
  }
  private void transformColumns() {
        float[] ColumnVector=new float[W];
        for(int col = 0; col <W; ++col) {
                for(int row = 0; row < W; ++row)
                        ColumnVector[row] = mY[row][col];
                fwt.transform(ColumnVector);
                for(int row = 0; row < W; ++row)
                        mY[row][col] = ColumnVector[row];
        }
  }
  private void transformRows() {
     for(int row = 0; row < W; ++row) {
       fwt.transform(mY[row]);
     }
  }

  private void quantize() {
    float max=mY[0][0];
    float min=mY[0][0];
    for(int k=0;k<W;k++) {
      for(int l=0;l<W;l++) {
        max=Math.max(mY[k][l],max);
        min=Math.min(mY[k][l],min);
      }
    }
    for(int k=0;k<W;k++) {
      for(int l=0;l<W;l++) {
        mY[k][l]=Math.round((mY[k][l]-min)/(max-min)*255.0);
      }
    }
  }

  public void paint(Graphics g) {
	  FontMetrics fm=this.getFontMetrics(this.getFont());
	  int fontheight=fm.getHeight();
    g.setColor(Color.white);
    g.drawImage(lena,0,0,this);
    g.drawImage(lenaTransformed,256,0,this);
    g.drawString("Real time computation of the Fast Wavelet Transform in Java",10,W-4*fontheight);
    g.drawString("using "+(int)(Math.log(W)/Math.log(2)-3)+" iterations and Symmlet8 wavelets.",10,W-3*fontheight);
    g.drawString ("(Only the Y channel is shown.)",10,W-2*fontheight);
    g.drawString("Notice how economical the FWT representation is!",10,W-fontheight);
  }
}


