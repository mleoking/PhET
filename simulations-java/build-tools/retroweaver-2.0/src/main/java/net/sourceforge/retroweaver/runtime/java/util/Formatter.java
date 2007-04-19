package net.sourceforge.retroweaver.runtime.java.util;

import java.util.Locale;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.io.IOException;

public class Formatter {

	public Formatter() { this(new StringBuilder(), Locale.getDefault()); }

	public Formatter(Appendable a) { this(a, Locale.getDefault()); }

    public Formatter(Locale l) { this(new StringBuilder(), l); }

    public Formatter(Appendable a, Locale l) {
    	buffer = a == null?new StringBuilder():a;

    	try {
    		appendMethod = buffer.getClass().getMethod("append", new Class[] { String.class });
    	} catch (NoSuchMethodException e) {
    		throw new RuntimeException(e);
    	}
    	locale = l;
    }

	private Appendable buffer;

	private Method appendMethod;

	private Locale locale;
	
	private boolean closed;

	private IOException ioe;

    public Locale locale() {
    	if (closed) {
    		throw new FormatterClosedException();
    	}
    	return locale;
    }

    public Appendable out() {
    	if (closed) {
    		throw new FormatterClosedException();
    	}
    	return buffer;
    }

	public String toString() {
    	if (closed) {
    		throw new FormatterClosedException();
    	}
		return buffer.toString();
	}

	public void flush() {
    	if (closed) {
    		throw new FormatterClosedException();
    	}
    	
    	// Flushable is 1.5+
    	try {
    		Method m = buffer.getClass().getMethod("flush", new Class<?>[0]);
    		m.invoke(buffer, new Object[0]);
    	} catch (Exception e) {
    		// ignored;
    	}
	}

	public void close() {
		if (!closed) {
			closed = true;
	    	
	    	// Closeable is 1.5+
	    	try {
	    		Method m = buffer.getClass().getMethod("close", new Class<?>[0]);
	    		m.invoke(buffer, new Object[0]);
	    	} catch (Exception e) {
	    		// ignored;
	    	}
		}
	}

	public IOException ioException() {
		return ioe;
	}

	public Formatter format(String format, Object... args) throws IllegalFormatException, FormatterClosedException {
		return format(locale, format, args);
	}

	public Formatter format(Locale l, String format, Object... args) throws IllegalFormatException, FormatterClosedException {
		if (closed) {
			throw new FormatterClosedException();
		}

//System.err.println("Format: " + format + ' ' + args.length);
//for (Object a: args) System.err.println("\t" + a.getClass() + ": " + a);
			
		int start = 0;
		int end;
		int argIndex = 0;

		while (true) {
			try {
				end = format.indexOf('%', start);
				if (end == -1) {
					append(format.substring(start, format.length()));
					break;
				}
				append(format.substring(start, end));
				if (end == format.length()) {
					throw new IllegalFormatException();
				}
				char c = format.charAt(end+1);
				Object o = args[argIndex++];
				switch (c) {
					case '%':
						append("%");
						break;
					case 's':
						append(o==null?null:o.toString());
						break;
					case 'd':
						append(o.toString());
						break;
					default:
						throw new IllegalFormatException();
				}
				start = end + 2;
			} catch (IOException ioe) {
				this.ioe = ioe;
			}
		}

		return this;
	}

	private void append(String s) throws IOException {
		try {
			appendMethod.invoke(buffer, s);
		} catch (InvocationTargetException ite) {
			if (ite.getCause() instanceof IOException) {
				throw (IOException) ite.getCause();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}

