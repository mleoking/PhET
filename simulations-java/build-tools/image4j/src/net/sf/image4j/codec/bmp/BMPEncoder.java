/*
 * BMPEncoder.java
 *
 * Created on 11 May 2006, 04:19
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.image4j.codec.bmp;

import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import net.sf.image4j.io.LittleEndianOutputStream;

/**
 * Encodes images in BMP format.
 * @author Ian McDonagh
 */
public class BMPEncoder {
  
  /** Creates a new instance of BMPEncoder */
  private BMPEncoder() {
  }
  
  /**
   * Encodes and writes BMP data the output file
   * @param img the image to encode
   * @param file the file to which encoded data will be written
   * @throws java.io.IOException if an error occurs
   */
  public static void write(BufferedImage img, java.io.File file) throws IOException {
    write(img, new java.io.FileOutputStream(file));
  }
  
  /**
   * Encodes and writes BMP data to the output
   * @param img the image to encode
   * @param os the output to which encoded data will be written
   * @throws java.io.IOException if an error occurs
   */
  public static void write(BufferedImage img, java.io.OutputStream os) throws IOException {
    InfoHeader ih = createInfoHeader(img);
    
    int mapSize = 0;
    IndexColorModel icm = null;
    
    if (ih.sBitCount <= 8) {
      icm = (IndexColorModel) img.getColorModel();
      mapSize = icm.getMapSize();
    }
    
    int headerSize = 14 //file header
        + ih.iSize //info header
        ;
    int mapBytes = 4 * mapSize;
    int dataOffset = headerSize + mapBytes;
    
    int bytesPerLine = 0;
    
    switch (ih.sBitCount) {
      case 1:
        bytesPerLine = getBytesPerLine1(ih.iWidth);
        break;
      case 4:
        bytesPerLine = getBytesPerLine4(ih.iWidth);
        break;
      case 8:
        bytesPerLine = getBytesPerLine8(ih.iWidth);
        break;
      case 24:
        bytesPerLine = getBytesPerLine24(ih.iWidth);
        break;
      case 32:
        bytesPerLine = ih.iWidth * 4;
        break;
    }
    
    int fileSize = dataOffset + bytesPerLine * ih.iHeight;
    
    LittleEndianOutputStream out = new LittleEndianOutputStream(os);
    
    //write file header
    writeFileHeader(fileSize, dataOffset, out);
    
    //write info header
    ih.write(out);
    
    //write color map (bit count <= 8)
    if (ih.sBitCount <= 8) {
      writeColorMap(icm, out);
    }
    
    //write raster data
    switch (ih.sBitCount) {
      case 1:
        write1(img.getRaster(), out);
        break;
      case 4:
        write4(img.getRaster(), out);
        break;
      case 8:
        write8(img.getRaster(), out);
        break;
      case 24:
        write24(img.getRaster(), out);
        break;
      case 32:
        write32(img.getRaster(), img.getAlphaRaster(), out);
        break;
    }
  }
  
  /**
   * Creates an <tt>InfoHeader</tt> from the source image.
   * @param img the source image
   * @return the resultant <tt>InfoHeader</tt> structure
   */
  public static InfoHeader createInfoHeader(BufferedImage img) {
    InfoHeader ret = new InfoHeader();
    ret.iColorsImportant = 0;
    ret.iColorsUsed = 0;
    ret.iCompression = 0;
    ret.iHeight = img.getHeight();
    ret.iWidth = img.getWidth();
    ret.sBitCount = (short) img.getColorModel().getPixelSize();
    ret.iNumColors = 1 << (ret.sBitCount == 32 ? 24 : ret.sBitCount);
    ret.iImageSize = 0;
    return ret;
  }
  
  /**
   * Writes the file header.
   * @param fileSize the calculated file size for the BMP data being written
   * @param dataOffset the calculated offset within the BMP data where the actual bitmap begins
   * @param out the output to which the file header will be written
   * @throws java.io.IOException if an error occurs
   */
  public static void writeFileHeader(int fileSize, int dataOffset,
      net.sf.image4j.io.LittleEndianOutputStream out) throws IOException {
    //signature
    byte[] signature = BMPConstants.FILE_HEADER.getBytes("UTF-8");
    out.write(signature);
    //file size
    out.writeIntLE(fileSize);
    //reserved
    out.writeIntLE(0);
    //data offset
    out.writeIntLE(dataOffset);
  }
  
  /**
   * Writes the colour map resulting from the source <tt>IndexColorModel</tt>.
   * @param icm the source <tt>IndexColorModel</tt>
   * @param out the output to which the colour map will be written
   * @throws java.io.IOException if an error occurs
   */
  public static void writeColorMap(IndexColorModel icm, net.sf.image4j.io.LittleEndianOutputStream out) throws IOException {
    int mapSize = icm.getMapSize();
    for (int i = 0; i < mapSize; i++) {
      int rgb = icm.getRGB(i);
      int r = (rgb >> 16) & 0xFF;
      int g = (rgb >> 8) & 0xFF;
      int b = (rgb) &0xFF;
      out.writeByte(b);
      out.writeByte(g);
      out.writeByte(r);
      out.writeByte(0);
    }
  }
  
  /**
   * Calculates the number of bytes per line required for the given width in pixels,
   * for a 1-bit bitmap.  Lines are always padded to the next 4-byte boundary.
   * @param width the width in pixels
   * @return the number of bytes per line
   */
  public static int getBytesPerLine1(int width) {
    int ret = (int) width / 8;
    if (ret % 4 != 0) {
      ret = (ret / 4 + 1) * 4;
    }
    return ret;
  }
  
  /**
   * Calculates the number of bytes per line required for the given with in pixels,
   * for a 4-bit bitmap.  Lines are always padded to the next 4-byte boundary.
   * @param width the width in pixels
   * @return the number of bytes per line
   */
  public static int getBytesPerLine4(int width) {
    int ret = (int) width / 2;
    if (ret % 4 != 0) {
      ret = (ret / 4 + 1) * 4;
    }
    return ret;
  }
  
  /**
   * Calculates the number of bytes per line required for the given with in pixels,
   * for a 8-bit bitmap.  Lines are always padded to the next 4-byte boundary.
   * @param width the width in pixels
   * @return the number of bytes per line
   */
  public static int getBytesPerLine8(int width) {
    int ret = width;
    if (ret % 4 != 0) {
      ret = (ret / 4 + 1) * 4;
    }
    return ret;
  }
  
  /**
   * Calculates the number of bytes per line required for the given with in pixels,
   * for a 24-bit bitmap.  Lines are always padded to the next 4-byte boundary.
   * @param width the width in pixels
   * @return the number of bytes per line
   */
  public static int getBytesPerLine24(int width) {
    int ret = width * 3;
    if (ret % 4 != 0) {
      ret = (ret / 4 + 1) * 4;
    }
    return ret;
  }
  
  /**
   * Calculates the size in bytes of a bitmap with the specified size and colour depth.
   * @param w the width in pixels
   * @param h the height in pixels
   * @param bpp the colour depth (bits per pixel)
   * @return the size of the bitmap in bytes
   */
  public static int getBitmapSize(int w, int h, int bpp) {
    int bytesPerLine = 0;
    switch (bpp) {
      case 1:
        bytesPerLine = getBytesPerLine1(w);
        break;
      case 4:
        bytesPerLine = getBytesPerLine4(w);
        break;
      case 8:
        bytesPerLine = getBytesPerLine8(w);
        break;
      case 24:
        bytesPerLine = getBytesPerLine24(w);
        break;
      case 32:
        bytesPerLine = w * 4;
        break;
    }
    int ret = bytesPerLine * h;
    return ret;
  }
  
  /**
   * Encodes and writes raster data as a 1-bit bitmap.
   * @param raster the source raster data
   * @param out the output to which the bitmap will be written
   * @throws java.io.IOException if an error occurs
   */
  public static void write1(Raster raster, net.sf.image4j.io.LittleEndianOutputStream out) throws IOException {
    int bytesPerLine = getBytesPerLine1(raster.getWidth());
    
    byte[] line = new byte[bytesPerLine];
    
    for (int y = raster.getHeight() - 1; y >= 0; y--) {
      for (int i = 0; i < bytesPerLine; i++) {
        line[i] = 0;
      }
      
      for (int x = 0; x < raster.getWidth(); x++) {
        int bi = x / 8;
        int i = x % 8;
        int index = raster.getSample(x, y, 0);
        line[bi] = setBit(line[bi], i, index);
      }
      
      out.write(line);
    }
  }
  
  /**
   * Encodes and writes raster data as a 4-bit bitmap.
   * @param raster the source raster data
   * @param out the output to which the bitmap will be written
   * @throws java.io.IOException if an error occurs
   */
  public static void write4(Raster raster, net.sf.image4j.io.LittleEndianOutputStream out) throws IOException {
    int bytesPerLine = getBytesPerLine4(raster.getWidth());
    
    byte[] line = new byte[bytesPerLine];
    
    for (int y = raster.getHeight() - 1; y >= 0; y--) {
      for (int i = 0; i < bytesPerLine; i++) {
        line[i] = 0;
      }
      
      for (int x = 0; x < raster.getWidth(); x++) {
        int bi = x / 2;
        int i = x % 2;
        int index = raster.getSample(x, y, 0);
        line[bi] = setNibble(line[bi], i, index);
      }
      
      out.write(line);
    }
  }
  
  /**
   * Encodes and writes raster data as a 8-bit bitmap.
   * @param raster the source raster data
   * @param out the output to which the bitmap will be written
   * @throws java.io.IOException if an error occurs
   */
  public static void write8(Raster raster, net.sf.image4j.io.LittleEndianOutputStream out) throws IOException {
    int bytesPerLine = getBytesPerLine8(raster.getWidth());
    
    for (int y = raster.getHeight() - 1; y >= 0; y--) {
      for (int x = 0; x < raster.getWidth(); x++) {
        int index = raster.getSample(x, y, 0);
        
        out.writeByte(index);
      }
      for (int i = raster.getWidth(); i < bytesPerLine; i++) {
        out.writeByte(0);
      }
      
    }
  }
  
  /**
   * Encodes and writes raster data as a 24-bit bitmap.
   * @param raster the source raster data
   * @param out the output to which the bitmap will be written
   * @throws java.io.IOException if an error occurs
   */
  public static void write24(Raster raster, net.sf.image4j.io.LittleEndianOutputStream out) throws IOException {
    int bytesPerLine = getBytesPerLine24(raster.getWidth());
    
    for (int y = raster.getHeight() - 1; y >= 0; y--) {
      for (int x = 0; x < raster.getWidth(); x++) {
        int r = raster.getSample(x, y, 0);
        int g = raster.getSample(x, y, 1);
        int b = raster.getSample(x, y, 2);
        out.writeByte(b);
        out.writeByte(g);
        out.writeByte(r);
      }
      
      for (int i = raster.getWidth() * 3; i < bytesPerLine; i++) {
        out.writeByte(0);
      }
    }
  }
  
  /**
   * Encodes and writes raster data, together with alpha data, as a 32-bit bitmap.
   * @param raster the source raster data
   * @param alpha the source alpha data
   * @param out the output to which the bitmap will be written
   * @throws java.io.IOException if an error occurs
   */
  public static void write32(Raster raster, Raster alpha, net.sf.image4j.io.LittleEndianOutputStream out) throws IOException {
    for (int y = raster.getHeight() - 1; y >= 0; y--) {
      for (int x = 0; x < raster.getWidth(); x++) {
        int r = raster.getSample(x, y, 0);
        int g = raster.getSample(x, y, 1);
        int b = raster.getSample(x, y, 2);
        int a = alpha.getSample(x, y, 0);
        out.writeByte(b);
        out.writeByte(g);
        out.writeByte(r);
        out.writeByte(a);
      }
    }
  }
  
  /**
   * Sets a particular bit in a byte.
   * @param bits the source byte
   * @param index the index of the bit to set
   * @param bit the value for the bit, which should be either <tt>0</tt> or <tt>1</tt>.
   * @param the resultant byte
   */
  private static byte setBit(byte bits, int index, int bit) {
    if (bit == 0) {
      bits &= ~(1 << (7 - index));
    } else {
      bits |= 1 << (7 - index);
    }
    return bits;
  }
  
  /**
   * Sets a particular nibble (4 bits) in a byte.
   * @param nibbles the source byte
   * @param index the index of the nibble to set
   * @param the value for the nibble, which should be in the range <tt>0x0..0xF</tt>.
   */
  private static byte setNibble(byte nibbles, int index, int nibble) {
    nibbles |= (nibble << ((1 - index) * 4));
    
    return nibbles;
  }
  
  /**
   * Calculates the size in bytes for a colour map with the specified bit count.
   * @param sBitCount the bit count, which represents the colour depth
   * @return the size of the colour map, in bytes if <tt>sBitCount</tt> is less than or equal to 8, 
   * otherwise <tt>0</tt> as colour maps are only used for bitmaps with a colour depth of 8 bits or less.
   */
  public static int getColorMapSize(short sBitCount) {
    int ret = 0;
    if (sBitCount <= 8) {
      ret = (1 << sBitCount) * 4;
    }
    return ret;
  }
    
}
