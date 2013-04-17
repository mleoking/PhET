package org.jmol.util;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class OutputStringBuffer {

  StringBuffer sb;
  BufferedWriter bw;
  long nBytes;
  
  public OutputStringBuffer(BufferedOutputStream os) {
    if (os == null) {
      sb = new StringBuffer();
    } else {     
      OutputStreamWriter osw = new OutputStreamWriter(os);
      bw = new BufferedWriter(osw, 8192);
    }
  }
  
  public OutputStringBuffer append(String s) {
    if (bw == null) {
      sb.append(s);
    } else {
      nBytes += s.length();
      try {
        bw.write(s);
      } catch (IOException e) {
        // TODO
      }      
    }
    return this;
  }

  public long length() {
    return (bw == null ? sb.length() : nBytes);
  }
  
  @Override
  public String toString() {
    if (bw != null)
      try {
        bw.flush();
      } catch (IOException e) {
        // TODO
      }
    return (bw == null ? sb.toString() : nBytes + " bytes");
  }

  public OutputStringBuffer append(char c) {
    if (bw == null) {
      sb.append(c);
    } else {
      nBytes += 1;
      try {
        bw.write(c);
      } catch (IOException e) {
        // TODO
      }      
    }
    return this;
  }
}
