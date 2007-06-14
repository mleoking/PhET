//Copyright (c) 2005, Thomas Aglassinger
//All rights reserved.
//
//Redistribution and use in source and binary forms, with or without
//modification, are permitted provided that the following conditions are met:
//
// * Redistributions of source code must retain the above copyright
//notice, this list of conditions and the following disclaimer.
//
// * Redistributions in binary form must reproduce the above copyright
//notice, this list of conditions and the following disclaimer in the
//documentation and/or other materials provided with the distribution.
//
// * Neither the name of the author nor the names of its contributors
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
 * Declaration of different logging levels.
 * <p>
 * Updated to WrapLog version 1.1.
 *
 * @author Thomas Aglassinger
 */
public class Level {
    /** Logging level for messages that usually are of no interest for the user. */
    public static final int DEBUG = 0;

    /**
     * Logging level for messages that explain why the desired operation cannot
     * be performed.
     * <p>
     * Note: a simple way for deriving goog error messages is to make them fit
     * the pattern: <blockquote>cannot do something: actual state must match
     * expectation. </blockquote> Just fill in "do something", "actual state"
     * and "expectation".<p> In case your code is just reporting a Java
     * <code>Exception</code> that happens at a lower level on the call stack,
     * use the pattern: <blockquote>cannot do something: &lt;
     * <code>exception.getMessage()</code> &gt;. </blockquote> In practice,
     * this often results into sucky error messages, but therer is not much you
     * can do about it except making sure that if your own
     * <code>Exceptions</code> have a useful <code>getMessage()</code>.
     */
    public static final int ERROR = 3;

    /**
     * Logging level for messages that tell details about normal operations
     * currently going on.
     */
    public static final int INFO = 1;

    /**
     * Logging level for message that notify that the things can be processed,
     * but the user might want to take a closer look at the current situation.
     * <p>
     * Note: warnings are a good indicator for bad design. They hint at the
     * developer being to dumb to resolve a situation and therefor delegating
     * the responsibility to the user. Preferrably, the code shoud be changed to
     * either log <code>INFO</code>, throw an exception or log
     * <code>ERROR</code>.
     */
    public static final int WARN = 2;
}
