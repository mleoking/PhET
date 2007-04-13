import java.awt.*;
import java.awt.image.*;
import java.net.URL;
import JSci.maths.*;
import JSci.maths.matrices.DoubleMatrix;
import JSci.maths.matrices.DoubleSparseMatrix;
import JSci.maths.vectors.DoubleSparseVector;
import JSci.maths.wavelet.*;
import JSci.util.ArrayCaster;

/**
* This is a simple implementation of the PixelGrabber class
* to allow for easier image processing.
* Basically, it reads a graphic file and allows you to get
* integer arrays from it for convenient processing.
* The name of the class comes from the fact that the image
* is stored as an internal int[][] array.
*
* This might seem inefficient since the JDK stores images
* as an int[] array and that we must go back and forth
* between the two formats.
* It is indeed slower, but as far as image processing is concerned,
* it is much simpler to work with an int[][] array.
* Moreover, if the processing is moderatly involved,
* it won't make much difference.
* @author Daniel Lemire
*/
public final class PixelArray implements ImageObserver {
	private int width=-1;
	private int height=-1;
  /********************************************
  * The RGB model is assumed
  *********************************************/
	ColorModel cm=ColorModel.getRGBdefault();
        private boolean loaded=false;
        private int[][] array;

        private PixelArray() {}

  /****************************************************
  * Constructor
  * @param filename file containing the image
  * @exception IllegalArgumentException if the file
  *   can't be open. Either the format is wrong or the
  *   file cannot be found.
  *****************************************************/
  public PixelArray(String filename) {
    waitForImage(Toolkit.getDefaultToolkit().getImage(filename));
  }
  /****************************************************
  * Constructor
  * @param filename file containing the image
  * @exception IllegalArgumentException if the file
  *   can't be open. Either the format is wrong or the
  *   file cannot be found.
  *****************************************************/
  public PixelArray(URL url) {
    waitForImage(Toolkit.getDefaultToolkit().getImage(url));
  }

  public PixelArray (int[][] I) {
    width=I[0].length;
    height=I.length;
    array=ArrayMath.copy(I);
  }
  public PixelArray (double[][] D) {
    width=D[0].length;
    height=D.length;
    array=ArrayMath.copy(ArrayCaster.toInt(D));
  }

  public Object clone() {
    PixelArray pa=new PixelArray();
    pa.loaded=true;
    pa.array=new int[this.height][this.width];
    for(int k=0;k<width;k++) {
      for(int l=0;l<height;l++) {
        pa.array[l][k]=this.array[l][k];
      }
    }
    pa.cm=this.cm;
    pa.height=this.height;
    pa.width=this.width;
    return(pa);
  }


        public int getWidth() {
                return(width);
        }
        public int getHeight() {
                return(height);
        }

  private void computeArray (int[] p) {
    array=new int[height][width];
    for(int k=0;k<width;k++) {
      for(int l=0;l<height;l++) {
        array[l][k]=p[k+l*width];
      }
    }
  }


  public void setRedArray(int[][] I) {
    int g,b,a;
    width=I[0].length;
    height=I.length;
    array=new int[height][width];
    for(int k=0;k<width;k++) {
      for(int l=0;l<height;l++) {
        g=getGreen(l,k);
        b=getBlue(l,k);
        a=getAlpha(l,k);
        array[l][k]=RGBtoInt(I[l][k],g,b,a);
      }
    }
  }
  public void setGreenArray(int[][] I) {
    int r,b,a;
    width=I[0].length;
    height=I.length;
    array=new int[height][width];
    for(int k=0;k<width;k++) {
      for(int l=0;l<height;l++) {
        r=getRed(l,k);
        b=getBlue(l,k);
        a=getAlpha(l,k);
        array[l][k]=RGBtoInt(r,I[l][k],b,a);
      }
    }
  }
  public void setBlueArray(int[][] I) {
    int r,g,a;
    width=I[0].length;
    height=I.length;
    array=new int[height][width];
    for(int k=0;k<width;k++) {
      for(int l=0;l<height;l++) {
        r=getGreen(l,k);
        g=getBlue(l,k);
        a=getAlpha(l,k);
        array[l][k]=RGBtoInt(r,g,I[l][k],a);
      }
    }
  }
  public void setAlphaArray(int[][] I) {
    int r,g,a;
    width=I[0].length;
    height=I.length;
    array=new int[height][width];
    for(int k=0;k<width;k++) {
      for(int l=0;l<height;l++) {
        r=getGreen(l,k);
        g=getBlue(l,k);
        a=getAlpha(l,k);
        array[l][k]=RGBtoInt(r,g,I[l][k],a);
      }
    }
  }

  private void setUniformGrey(int[][] I) {
    width=I[0].length;
    height=I.length;
    array=new int[height][width];
    for(int k=0;k<width;k++) {
      for(int l=0;l<height;l++) {
        array[l][k]=RGBtoInt(I[l][k],I[l][k],I[l][k],255);
      }
    }
  }

  /*************************************
  * Part of the interface ImageObserver
  **************************************/
  public boolean imageUpdate(Image img1, int parm2, int parm3, int parm4, int parm5, int parm6) {
    if((parm3<0)||(parm4<0)) {
       throw new IllegalArgumentException("Could not load image.");
    }
    width=img1.getWidth(this);
    height=img1.getHeight(this);
    loaded=true;
    return(false);
  }
  public synchronized void waitForImage(Image img) {
	  while (loaded==false) {
      try {
        width=img.getWidth(this);
        wait(100);
      } catch (InterruptedException e) {}
	  }
    int[] p=new int[width*height];
    PixelGrabber pg=new PixelGrabber(img,0,0,width,height,p,0,width);
    try {
	    pg.grabPixels();
    } catch (InterruptedException e) {
        System.err.println("interrupted waiting for pixels!");
        return;
    }
    if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
        System.err.println("image fetch aborted or errored");
        return;
    }
    computeArray(p);

  }


  public static int RGBtoInt(int r, int g, int b, int a) {
    return ((a << 24)|(r << 16)|(g << 8)| b);
  }

  public void invert() {
   int r,g,b,a;
    for (int y = 0; y < getWidth(); y++)
	    for (int x = 0 ; x < getHeight(); x++) {
			  r = getRed  (x,y);
        g = getGreen (x,y);
        b = getBlue (x,y);
			  a = getAlpha(x,y);
			  setPixel(x,y,255-r,255-g,255-b,a);
	  }
  }

  public void makeGrayFromRed() {
   int r,a;
    for (int y = 0; y < getWidth(); y++)
	    for (int x = 0 ; x < getHeight(); x++) {
			  r = getRed  (x,y);
			  a = getAlpha(x,y);
			  setPixel(x,y,r,r,r,a);
	  }
  }
  public void makeGrayFromGreen() {
   int g,a;
    for (int y = 0; y < getWidth(); y++)
	    for (int x = 0 ; x < getHeight(); x++) {
			  g = getGreen  (x,y);
			  a = getAlpha(x,y);
			  setPixel(x,y,g,g,g,a);
	  }
  }
  public void makeGrayFromBlue() {
   int b,a;
    for (int y = 0; y < getWidth(); y++)
	    for (int x = 0 ; x < getHeight(); x++) {
			  b = getBlue  (x,y);
			  a = getAlpha(x,y);
			  setPixel(x,y,b,b,b,a);
	  }
  }

        public void makeRed() {
                int r,a;
                for (int y = 0; y < getWidth(); y++)
	               for (int x = 0 ; x < getHeight(); x++) {
                                r = getRed  (x,y);
                                a = getAlpha(x,y);
                                setPixel(x,y,r,0,0,a);
	       }
        }
        public void makeGreen() {
                int g,a;
                for (int y = 0; y < getWidth(); y++)
                        for (int x = 0 ; x < getHeight(); x++) {
                                g = getGreen  (x,y);
                                a = getAlpha(x,y);
                                setPixel(x,y,0,g,0,a);
	       }
        }
        public void makeBlue() {
                int b,a;
                for (int y = 0; y < getWidth(); y++)
	               for (int x = 0 ; x < getHeight(); x++) {
                                b = getBlue  (x,y);
                                a = getAlpha(x,y);
                                setPixel(x,y,0,0,b,a);
	       }
        }
        public void setPixel(int x,int y, int r, int g, int b, int a) {
                array[x][y]=RGBtoInt(r,g,b,a);
        }

  /*********************************
  * Allow to change the array
  * representing the image
  * @exception IllegalArgumentException if array doesn't make a matrix
  **********************************/
  public void setArray (int[][] s) {
    array=new int[s.length][s[0].length];
    width=s[0].length;
    height=s.length;
    for(int k=0;k<width;k++) {
      if(s[k].length!=s[0].length) {
        throw new IllegalArgumentException("Array doesn't make a matrix.");
      }
      for(int l=0;l<height;l++) {
        array[k][l]=s[k][l];
      }
    }
  }

        public int getRed(int x, int y) {
                return cm.getRed(array[x][y]);
        }
        public int getGreen(int x, int y) {
                return cm.getGreen(array[x][y]);
        }
        public int getBlue(int x, int y) {
                return cm.getBlue(array[x][y]);
        }
        public int getAlpha(int x, int y) {
                return cm.getAlpha(array[x][y]);
        }

        public int[][] getRedArray() {
                int[][] ans=new int[width][height];
                for(int k=0;k<width;k++) {
                        for(int l=0;l<height;l++)
                                ans[k][l]=cm.getRed(array[l][k]);
                }
                return(ans);
        }
        public int[][] getGreenArray() {
                int[][] ans=new int[width][height];
                for(int k=0;k<width;k++) {
                        for(int l=0;l<height;l++)
                                ans[k][l]=cm.getGreen(array[l][k]);
                }
                return(ans);
        }
        public int[][] getBlueArray() {
                int[][] ans=new int[width][height];
                for(int k=0;k<width;k++) {
                        for(int l=0;l<height;l++)
                                ans[k][l]=cm.getBlue(array[l][k]);
                }
                return(ans);
        }
        public int[][] getAlphaArray(int x, int y) {
                int[][] ans=new int[width][height];
                for(int k=0;k<width;k++) {
                        for(int l=0;l<height;l++)
                                ans[k][l]=cm.getAlpha(array[l][k]);
                }
                return(ans);
        }

  public int[][] getArray(int x, int y) {
    int[][] ans=new int[width][height];
    for(int k=0;k<width;k++) {
      for(int l=0;l<height;l++) {
        ans[k][l]=array[k][l];
      }
    }
    return(ans);
	}

  /**********************************************
  * Fast Wavelet Transform
  * This method assumes a dyadic multiresolution.
  * This implementation is temporary, expect it
  * to be slow. It is meant to be easily
  * understood.
  * One good thing about this method is that
  * it will handle the boundary automatically
  * (as long as the chosen Multiresolution
  * handles them).
  * Also, it will work with any Multiresolution object.
  * Only the red component is treated.
  ***********************************************/
  public PixelArray[][] redFWT(Multiresolution m) {
    double[][] doublearray=ArrayCaster.toDouble(getRedArray());
    return(FWT(m,doublearray));
  }

  /**********************************************
  * Fast Wavelet Transform
  * This method assumes a dyadic multiresolution.
  * This implementation is temporary, expect it
  * to be slow. It is meant to be easily
  * understood.
  * One good thing about this method is that
  * it will handle the boundary automatically
  * (as long as the chosen Multiresolution
  * handles them).
  * Also, it will work with any Multiresolution object.
  * Only the green component is treated.
  ***********************************************/
  public PixelArray[][] greenFWT(Multiresolution m) {
    double[][] doublearray=ArrayCaster.toDouble(getGreenArray());
    return(FWT(m,doublearray));
  }

  /**********************************************
  * Fast Wavelet Transform
  * This method assumes a dyadic multiresolution.
  * This implementation is temporary, expect it
  * to be slow. It is meant to be easily
  * understood.
  * One good thing about this method is that
  * it will handle the boundary automatically
  * (as long as the chosen Multiresolution
  * handles them).
  * Also, it will work with any Multiresolution object.
  * Only the blue component is treated.
  ***********************************************/
  public PixelArray[][] blueFWT(Multiresolution m) {
    double[][] doublearray=ArrayCaster.toDouble(getBlueArray());
    return(FWT(m,doublearray));
  }

  private PixelArray[][] FWT(Multiresolution m, double[][] doublearray) {
    final int pwidth=m.previousDimension(width);
    final int wavewidth=width-pwidth;
    final int pheight=m.previousDimension(height);
    final int waveheight=height-pheight;
    DoubleSparseVector[] wcachelow=new DoubleSparseVector[pwidth] ;
    DoubleSparseVector[] hcachelow=new DoubleSparseVector[pheight] ;
    DoubleSparseVector[] wcachehigh=new DoubleSparseVector[wavewidth] ;
    DoubleSparseVector[] hcachehigh=new DoubleSparseVector[waveheight] ;
    for(int k=0;k<pwidth;k++) {
      wcachelow[k]=new DoubleSparseVector(m.primaryScaling(pwidth,k).evaluate(1));
    }
    for(int k=0;k<pheight;k++) {
      hcachelow[k]=new DoubleSparseVector(m.primaryScaling(pheight,k).evaluate(1));
    }
    for(int k=0;k<wavewidth;k++) {
      wcachehigh[k]=new DoubleSparseVector(m.primaryWavelet(pwidth,k).evaluate(0));
    }
    for(int k=0;k<waveheight;k++) {
      hcachehigh[k]=new DoubleSparseVector(m.primaryWavelet(pheight,k).evaluate(0));
    }

        DoubleSparseMatrix temp;
        DoubleMatrix imgMat=new DoubleMatrix(doublearray);
    double[][] lowpass=new double[pwidth][pheight];
    for(int k=0;k<pwidth;k++) {
      for(int l=0;l<pheight;l++) {
        temp=wcachelow[k].tensorProduct(hcachelow[l]);
        lowpass[k][l]=temp.scalarProduct(imgMat);
      }
    }
    double[][] highlowpass=new double[wavewidth][pheight];
    for(int k=0;k<wavewidth;k++) {
      for(int l=0;l<pheight;l++) {
        temp=wcachehigh[k].tensorProduct(hcachelow[l]);
        highlowpass[k][l]=temp.scalarProduct(imgMat);
      }
    }
    double[][] lowhighpass=new double[pwidth][waveheight];
    for(int k=0;k<pwidth;k++) {
      for(int l=0;l<waveheight;l++) {
        temp=wcachelow[k].tensorProduct(hcachehigh[l]);
        lowhighpass[k][l]=temp.scalarProduct(imgMat);
      }
    }
    double[][] highhighpass=new double[wavewidth][waveheight];
    for(int k=0;k<wavewidth;k++) {
      for(int l=0;l<waveheight;l++) {
        temp=wcachehigh[k].tensorProduct(hcachehigh[l]);
        highhighpass[k][l]=temp.scalarProduct(imgMat);
      }
    }
    PixelArray[][] ans=new PixelArray[2][2];
    ans[0][0]=new PixelArray();
    ans[0][0].setUniformGrey(toIntegerArray0_255(lowpass));
    ans[1][0]=new PixelArray();
    ans[1][0].setUniformGrey(toIntegerArray0_255(highlowpass));
    ans[0][1]=new PixelArray();
    ans[0][1].setUniformGrey(toIntegerArray0_255(lowhighpass));
    ans[1][1]=new PixelArray();
    ans[1][1].setUniformGrey(toIntegerArray0_255(highhighpass));
    return(ans);
  }


  private int[][] toIntegerArray0_255(double[][] v) {
    double max=ArrayMath.max(v);
    double min=ArrayMath.min(v);
    if(max==min) {
      max=min+Double.MIN_VALUE;
    }
    int[][] ans=new int[v.length][v[0].length];
    for(int k=0;k<v.length;k++) {
      for(int l=0;l<v[0].length;l++) {
        ans[k][l]=(int) Math.round((v[k][l]-min)/(max-min)*255);
      }
    }
    return(ans);
  }

  /***********************************
  * Get the image back
  ************************************/
  public Image rebuildImage() {
    int[] p=new int[height*width];
    for(int k=0;k<width;k++) {
      for(int l=0;l<height;l++) {
        p[l*width+k]=array[l][k];
      }
    }
    MemoryImageSource source=new MemoryImageSource(width,height,cm,p,0,width);
	  return(Toolkit.getDefaultToolkit().createImage(source));
  }

  public static Image buildImage(int[][] a) {
    int h=a.length;
    int w=a[0].length;
    int[] p=new int[h*w];
    for(int k=0;k<w;k++) {
      for(int l=0;l<h;l++) {
        p[l*w+k]=a[l][k];
      }
    }
    MemoryImageSource source=new MemoryImageSource(w,h,ColorModel.getRGBdefault(),p,0,w);
	  return(Toolkit.getDefaultToolkit().createImage(source));
  }
}

