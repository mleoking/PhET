//Copyright (c) 2005, Thomas Aglassinger
//All rights reserved.
//
//Redistribution and use in source and binary forms, with or without
//modification, are permitted provided that the following conditions are met:
//
//* Redistributions of source code must retain the above copyright
 //notice, this list of conditions and the following disclaimer.
 //
 //* Redistributions in binary form must reproduce the above copyright
  //notice, this list of conditions and the following disclaimer in the
  //documentation and/or other materials provided with the distribution.
  //
  //* Neither the name of the author nor the names of its contributors
   //may be used to endorse or promote products derived from this software
   //without specific prior written permission.
   //
   //THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
   //IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
   //THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
   //PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
   //CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
   //EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
   //PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
   //PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
   //LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
   //NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
   //SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
package net.sf.wraplog;

/**
 * This class is not included in WrapLog version 1.1.
 * <p>
 * Updated to WrapLog version 1.1.
 *
 * @deprecated -- this class is no longer part of WrapLog.
 * @version 1.0
 */
public class Logger
        extends SystemLogger {
    private static Logger logger;

    private static synchronized Logger getLogger() {
        if (logger == null) {
            logger = new Logger();
        }
        return logger;
    }

    /**
     * @deprecated
     * @param name String
     * @return Logger
     */
    public static Logger getLogger(String name) {
        return getLogger();
    }

    /**
     * @deprecated
     * @param clazz Class
     * @return Logger
     */
    public static Logger getLogger(Class clazz) {
        if (clazz == null) {
            throw new NullPointerException("parameter clazz must not be null");
        }
        return getLogger(clazz.getName());
    }
}
