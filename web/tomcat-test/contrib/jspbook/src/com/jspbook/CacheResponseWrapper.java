/*
 * Copyright 2003 Jayson Falkner (jayson@jspinsider.com)
 * This code is from "Servlets and JavaServer pages; the J2EE Web Tier",
 * http://www.jspbook.com. You may freely use the code both commercially
 * and non-commercially. If you like the code, please pick up a copy of
 * the book and help support the authors, development of more free code,
 * and the JSP/Servlet/J2EE community.
 */
package com.jspbook;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class CacheResponseWrapper
    extends HttpServletResponseWrapper {
  protected HttpServletResponse origResponse = null;
  protected ServletOutputStream stream = null;
  protected PrintWriter writer = null;
  protected OutputStream cache = null;

  public CacheResponseWrapper(HttpServletResponse response,
      OutputStream cache) {
    super(response);
    origResponse = response;
    this.cache = cache;
  }

  public ServletOutputStream createOutputStream()
      throws IOException {
    return (new CacheResponseStream(origResponse, cache));
  }

  public void flushBuffer() throws IOException {
    stream.flush();
  }

  public ServletOutputStream getOutputStream()
      throws IOException {
    if (writer != null) {
      throw new IllegalStateException(
        "getWriter() has already been called!");
    }

    if (stream == null)
      stream = createOutputStream();
    return (stream);
  }

  public PrintWriter getWriter() throws IOException {
    if (writer != null) {
      return (writer);
    }

    if (stream != null) {
      throw new IllegalStateException(
        "getOutputStream() has already been called!");
    }

   stream = createOutputStream();
   writer = new PrintWriter(new OutputStreamWriter(stream, "UTF-8"));
   return (writer);
  }
}
