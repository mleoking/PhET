package net.sourceforge.retroweaver.runtime.java.lang.reflect;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import net.sourceforge.retroweaver.runtime.java.lang.TypeNotPresentException;
import net.sourceforge.retroweaver.runtime.java.lang.annotation.AIB;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;

public class ReflectionDescriptor implements ClassVisitor {

	private static final boolean DEBUG = false;

	private static final Map<Class, ReflectionDescriptor> descriptors = new HashMap<Class, ReflectionDescriptor>();

	public static ReflectionDescriptor getReflectionDescriptor(Class class_) {
		synchronized (descriptors) {
			ReflectionDescriptor d = descriptors.get(class_);
			if (d == null) {
				d = new ReflectionDescriptor(class_);
				descriptors.put(class_, d);
				if (DEBUG) d.debugMessage("Adding descriptor");
			}
			return d;			
		}
	}

	private final Class class_;

	private ReflectionDescriptor(Class class_) {
		this.class_ = class_;
		String name = class_.getName();

		String resource = "/" + name.replace('.', '/') + ".class";
if (DEBUG) System.out.println("Reading class file: " + resource);
		InputStream classStream = class_.getResourceAsStream(resource);
		parseStream(name, classStream);
	}

	protected ReflectionDescriptor(String name, InputStream classStream) {
		class_ = null;
		parseStream(name, classStream);
	}
	
	private void parseStream(String name, InputStream classStream) {
		try {
			ClassReader r = new ClassReader(classStream);
			r.accept(this, ClassReader.SKIP_CODE + ClassReader.SKIP_DEBUG
					+ ClassReader.SKIP_FRAMES);

		} catch (IOException e) {
			// Shouldn't generally happen
			throw new TypeNotPresentException(
					"[Retroweaver] Unable to read reflection data for: " + name, e);
		} finally {
			try {
				if (classStream != null) {
					classStream.close();
				}
			} catch (IOException e) { // NOPMD by xlv
			}
		}
	}

	private String internalName;

	private String enclosingClassName;

	private String enclosingMethodName;

	private String enclosingMethodDesc;

	public String getEnclosingClassName() {
		return enclosingClassName;
	}

	public void debugMessage(String msg) {
		System.out.println(msg +
				"\n\tclass: " + class_.getName() +
				"\n\tenclosingClassName: " + enclosingClassName +
				"\n\tenclosingMethodName: " + enclosingMethodName + ' ' + enclosingMethodDesc);
	}

	public Class getEnclosingClass() {
		//debugMessage("getEnclosingClass");
		if (enclosingClassName == null) {
			return null;
		}

		try {
			//debugMessage("getEnclosingClass");
			String name = enclosingClassName.replace('/', '.');
			Class c = class_.getClassLoader().loadClass(name);
		
			return c;
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public Method getEnclosingMethod() {
		//debugMessage("getEnclosingMethod");
		if (enclosingMethodName == null) {
			return null;
		}

		return getMethod(getEnclosingClass(), enclosingMethodName, enclosingMethodDesc);
	}

	public Constructor getEnclosingConstructor() {
		//debugMessage("getEnclosingMethod");
		if (enclosingMethodName == null) {
			return null;
		}

		return getConstructor(getEnclosingClass(), enclosingMethodDesc);
	}

	private Method getMethod(Class class_, String name, String desc) {
		org.objectweb.asm.Type[] types = org.objectweb.asm.Type.getArgumentTypes(desc);

	outer_loop:
		for (Method m : class_.getDeclaredMethods()) {
			final org.objectweb.asm.Type[] methodTypes = org.objectweb.asm.Type.getArgumentTypes(m);
			if (!m.getName().equals(name)
					|| methodTypes.length != types.length) {
				continue;
			}
			for (int i = 0; i < types.length; ++i) {
				if (!types[i].equals(methodTypes[i])) {
					continue outer_loop;
				}
			}
			return m;
		}
		return null;
	}

	private Constructor getConstructor(Class class_, String desc) {
		org.objectweb.asm.Type[] types = org.objectweb.asm.Type.getArgumentTypes(desc);

	outer_loop:
		for (Constructor c : class_.getDeclaredConstructors()) {
			final Class[] constructorTypes = c.getParameterTypes();
			if (constructorTypes.length != types.length) {
				continue;
			}
			for (int i = 0; i < types.length; ++i) {
				if (!types[i].equals(org.objectweb.asm.Type.getType(constructorTypes[i]))) {
					continue outer_loop;
				}
			}
			return c;
		}
		return null;
	}

	private void parseSignature(String signature, boolean isInterface, String superName, String[] interfaces) {
		if (signature == null) {
			typeParameters = new TypeVariable[0];
			genericSuperclass = superName==null?null:new ClassTypeImpl(superName.replaceAll("/", "."));
			if (interfaces == null) {
				genericInterfaces = new Type[0];
			} else {
				genericInterfaces = new Type[interfaces.length];
				for(int i = 0; i < interfaces.length; i++) {
					genericInterfaces[i] = new ClassTypeImpl(interfaces[i].replaceAll("/", "."), true);
				}
			}
		} else {
			if (DEBUG) System.out.println("Parsing " + signature);
			SignatureReader r = new SignatureReader(signature);
			ClassTypeImpl currentType = new ClassTypeImpl(internalName.replaceAll("/", "."), isInterface);
			SigVisitor v = new SigVisitor(currentType, false);
			r.accept(v);
			v.endParsing();
			
			typeParameters = v.typeParameters;
			genericSuperclass = v.genericSuperclass;
			genericInterfaces = v.genericInterfaces;
		}
		if (isInterface) {
			genericSuperclass = null;
		}

	}

	private TypeVariable[] typeParameters;

	private Type genericSuperclass;

	private Type[] genericInterfaces;

	public TypeVariable[] getTypeParameters() throws GenericSignatureFormatError {
		return typeParameters;
	}

	public Type getGenericSuperclass() throws GenericSignatureFormatError, TypeNotPresentException, MalformedParameterizedTypeException {
		return genericSuperclass;
	}

	public Type[] getGenericInterfaces() throws GenericSignatureFormatError, TypeNotPresentException, MalformedParameterizedTypeException {
		return genericInterfaces;
	}

    /**
     * Visits the header of the class.
     * 
     * @param version the class version.
     * @param access the class's access flags (see {@link Opcodes}). This
     *        parameter also indicates if the class is deprecated.
     * @param name the internal name of the class (see
     *        {@link Type#getInternalName() getInternalName}).
     * @param signature the signature of this class. May be <tt>null</tt> if
     *        the class is not a generic one, and does not extend or implement
     *        generic classes or interfaces.
     * @param superName the internal of name of the super class (see
     *        {@link Type#getInternalName() getInternalName}). For interfaces,
     *        the super class is {@link Object}. May be <tt>null</tt>, but
     *        only for the {@link Object} class.
     * @param interfaces the internal names of the class's interfaces (see
     *        {@link Type#getInternalName() getInternalName}). May be
     *        <tt>null</tt>.
     */
    public void visit(
            int version,
            int access,
            String name,
            String signature,
            String superName,
            String[] interfaces) {
    	internalName = name;
    	boolean isInterface = ( access & Opcodes.ACC_INTERFACE ) == Opcodes.ACC_INTERFACE;
    	parseSignature(signature, isInterface, superName, interfaces);
    }

    public void visitSource(String source, String debug) {}

    /**
     * Visits the enclosing class of the class. This method must be called only
     * if the class has an enclosing class.
     * 
     * @param owner internal name of the enclosing class of the class.
     * @param name the name of the method that contains the class, or
     *        <tt>null</tt> if the class is not enclosed in a method of its
     *        enclosing class.
     * @param desc the descriptor of the method that contains the class, or
     *        <tt>null</tt> if the class is not enclosed in a method of its
     *        enclosing class.
     */
    public void visitOuterClass(String owner, String name, String desc) {
    	enclosingClassName = owner;
    	enclosingMethodName = name;
    	enclosingMethodDesc = desc;
    	//debugMessage("visitOuterClass");
    }

    /**
     * Visits information about an inner class. This inner class is not
     * necessarily a member of the class being visited.
     * 
     * @param name the internal name of an inner class (see
     *        {@link Type#getInternalName() getInternalName}).
     * @param outerName the internal name of the class to which the inner class
     *        belongs (see {@link Type#getInternalName() getInternalName}). May
     *        be <tt>null</tt>.
     * @param innerName the (simple) name of the inner class inside its
     *        enclosing class. May be <tt>null</tt> for anonymous inner
     *        classes.
     * @param access the access flags of the inner class as originally declared
     *        in the enclosing class.
     */
    public void visitInnerClass(
            String name,
            String outerName,
            String innerName,
            int access) {
    	if (name.equals(internalName)) {
    		if (outerName != null) {
    			enclosingClassName = outerName;
    		}
    	}
    	//debugMessage("visitInnerClass");
    }

    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
    	return AIB.EMPTY_VISITOR;
    }

    public void visitAttribute(Attribute attr) {}

    public FieldVisitor visitField(
            int access,
            String name,
            String desc,
            String signature,
            Object value) { return null; }

    public MethodVisitor visitMethod(
            int access,
            String name,
            String desc,
            String signature,
            String[] exceptions) { return null; }

    public void visitEnd() {}

    public static void main(String args[]) {
    	String signature = "<E:Ljava/lang/String;>Ljava/util/LinkedList<TE;>;Ljava/io/Serializable;Ljava/lang/Comparable<TE;>;";
    	String internalName = "blah.TopLevel";

    	System.out.println("Parsing " + signature);
		SignatureReader r = new SignatureReader(signature);
		ClassTypeImpl currentType = new ClassTypeImpl(internalName.replaceAll("/", "."), false);
		SigVisitor v = new SigVisitor(currentType, false);
		r.accept(v);
		v.endParsing();
		
		System.out.println(v.genericInterfaces.length);
		for(Type t: v.genericInterfaces) {
			System.out.println(t);
		}
    }

    private static class SigVisitor implements SignatureVisitor {
		protected TypeVariable[] typeParameters;
		protected Type genericSuperclass;
		protected Type[] genericInterfaces;
	
		private Stack<Type> stack = new Stack<Type>();

		private Set<TypeVariableImpl> typeVariables = new HashSet<TypeVariableImpl>();

		private LinkedList<TypeVariableImpl> formalTypeParameters = new LinkedList<TypeVariableImpl>();

		private StringBuffer declaration;

	    private boolean isInterface;

	    private ClassTypeImpl currentType;

	    private boolean seenFormalParameter;

	    private boolean seenInterfaceBound;

	    private boolean seenParameter;

	    private boolean seenInterface;

	    private StringBuffer returnType;

	    private StringBuffer exceptions;

	    /**
	     * Stack used to keep track of class types that have arguments. Each element
	     * of this stack is a boolean encoded in one bit. The top of the stack is
	     * the lowest order bit. Pushing false = *2, pushing true = *2+1, popping =
	     * /2.
	     */
	    private int argumentStack;

	    private Stack<Integer> argumentStackPosition = new Stack<Integer>();

	    /**
	     * Stack used to keep track of array class types. Each element of this stack
	     * is a boolean encoded in one bit. The top of the stack is the lowest order
	     * bit. Pushing false = *2, pushing true = *2+1, popping = /2.
	     */
	    private int arrayStack;

	    private String separator = "";

	    public SigVisitor(final ClassTypeImpl currentType, final boolean isInterface) {
	        this.isInterface = isInterface;
	        this.declaration = new StringBuffer();
	        this.currentType = currentType;
	    }

	    private SigVisitor(final StringBuffer buf) {
	        this.declaration = buf;
	    }

	    /**
	     * Visits a formal type parameter.
	     * 
	     * @param name the name of the formal parameter.
	     */
	    public void visitFormalTypeParameter(final String name) {
	    	if (seenFormalParameter) {
	    		endFormal();
	    	}
	    	TypeVariableImpl t = new TypeVariableImpl(name);
	    	typeVariables.add(t);
	    	formalTypeParameters.add(t);
	    	if (DEBUG) System.out.println("pushing visitFormalTypeParameter " + t);

	    	declaration.append(seenFormalParameter ? ", " : "<").append(name);
	        seenFormalParameter = true;
	        seenInterfaceBound = false;
	    }

	    /**
	     * Visits the class bound of the last visited formal type parameter.
	     * 
	     * @return a non null visitor to visit the signature of the class bound.
	     */
	    public SignatureVisitor visitClassBound() {
	        separator = " extends ";
	        startType();
	        return this;
	    }

	    /**
	     * Visits an interface bound of the last visited formal type parameter.
	     * 
	     * @return a non null visitor to visit the signature of the interface bound.
	     */
	    public SignatureVisitor visitInterfaceBound() {
	        separator = seenInterfaceBound ? ", " : " extends ";
	        seenInterfaceBound = true;
	        startType();
	        return this;
	    }

	    /**
	     * Visits the type of the super class.
	     * 
	     * @return a non null visitor to visit the signature of the super class
	     *         type.
	     */
	    public SignatureVisitor visitSuperclass() {
	        endFormals();
	        separator = " extends ";
	        startType();
	        return this;
	    }

	    /**
	     * Visits the type of an interface implemented by the class.
	     * 
	     * @return a non null visitor to visit the signature of the interface type.
	     */
	    public SignatureVisitor visitInterface() {
	    	if (!seenInterface) {
	    		endGenericSuperclass();
	    	}
	        separator = seenInterface ? ", " : isInterface
	                ? " extends "
	                : " implements ";
	        seenInterface = true;
	        startType();
	        return this;
	    }

	    /**
	     * Visits the type of a method parameter.
	     * 
	     * @return a non null visitor to visit the signature of the parameter type.
	     */
	    public SignatureVisitor visitParameterType() {
	        endFormals();
	        if (!seenParameter) {
	            seenParameter = true;
	            declaration.append('(');
	        } else {
	            declaration.append(", ");
	        }
	        startType();
	        return this;
	    }

	    /**
	     * Visits the return type of the method.
	     * 
	     * @return a non null visitor to visit the signature of the return type.
	     */
	    public SignatureVisitor visitReturnType() {
	        endFormals();
	        if (!seenParameter) {
	            declaration.append('(');
	        } else {
	            seenParameter = false;
	        }
	        declaration.append(')');
	        returnType = new StringBuffer();
	        return new SigVisitor(returnType);
	    }

	    /**
	     * Visits the type of a method exception.
	     * 
	     * @return a non null visitor to visit the signature of the exception type.
	     */
	    public SignatureVisitor visitExceptionType() {
	        if (exceptions == null) {
	            exceptions = new StringBuffer();
	        } else {
	            exceptions.append(", ");
	        }
	        // startType();
	        return new SigVisitor(exceptions);
	    }

	    /**
	     * Visits a signature corresponding to a primitive type.
	     * 
	     * @param descriptor the descriptor of the primitive type, or 'V' for
	     *        <tt>void</tt>.
	     */
	    public void visitBaseType(final char descriptor) {
	    	if (DEBUG) System.out.println("visitBaseType " + descriptor);
	        switch (descriptor) {
	            case 'V':
	                declaration.append("void");
	                break;
	            case 'B':
	                declaration.append("byte");
	                break;
	            case 'J':
	                declaration.append("long");
	                break;
	            case 'Z':
	                declaration.append("boolean");
	                break;
	            case 'I':
	                declaration.append("int");
	                break;
	            case 'S':
	                declaration.append("short");
	                break;
	            case 'C':
	                declaration.append("char");
	                break;
	            case 'F':
	                declaration.append("float");
	                break;
	            // case 'D':
	            default:
	                declaration.append("double");
	                break;
	        }
	        endType();
	    }

	    /**
	     * Visits a signature corresponding to a type variable.
	     * 
	     * @param name the name of the type variable.
	     */
	    public void visitTypeVariable(final String name) {
	    	if (DEBUG) System.out.println("visitTypeVariable " + name);
	    	TypeVariableImpl t = new TypeVariableImpl(name);
	    	typeVariables.add(t);
	    	stack.push(t);
	        declaration.append(name);
	        endType();
	    }

	    /**
	     * Visits a signature corresponding to an array type.
	     * 
	     * @return a non null visitor to visit the signature of the array element
	     *         type.
	     */
	    public SignatureVisitor visitArrayType() {
	        startType();
	        arrayStack |= 1;
	        return this;
	    }

	    /**
	     * Starts the visit of a signature corresponding to a class or interface
	     * type.
	     * 
	     * @param name the internal name of the class or interface.
	     */
	    public void visitClassType(final String name) {
	    	ClassTypeImpl t = new ClassTypeImpl(name.replace('/', '.'));
	    	stack.push(t);
	    	if (DEBUG) System.out.println("visitClassType " + name.replace('/', '.'));

	    	if (!"java/lang/Object".equals(name)) {
	            declaration.append(separator).append(name.replace('/', '.'));
	        } else {
	            // Map<java.lang.Object,java.util.List>
	            // or
	            // abstract public V get(Object key); (seen in Dictionary.class)
	            // should have Object
	            // but java.lang.String extends java.lang.Object is unnecessary
	            boolean needObjectClass = argumentStack % 2 != 0 || seenParameter;
	            if (needObjectClass) {
	                declaration.append(separator).append(name.replace('/', '.'));
	            }
	        }
	        separator = "";
	        argumentStack *= 2;
	        argumentStackPosition.push(stack.size());
	    }

	    /**
	     * Visits an inner class.
	     * 
	     * @param name the local name of the inner class in its enclosing class.
	     */
	    public void visitInnerClassType(final String name) {
	    	if (DEBUG) System.out.println("visitInnerClassType " + name);
	        if (argumentStack % 2 != 0) {
	            declaration.append('>');
	        }
	        argumentStack /= 2;
	        argumentStackPosition.pop();
	        declaration.append('.');
	        declaration.append(separator).append(name.replace('/', '.'));
	        separator = "";
	        argumentStack *= 2;
	        argumentStackPosition.push(stack.size());
	    }

	    /**
	     * Visits an unbounded type argument of the last visited class or inner
	     * class type.
	     */
	    public void visitTypeArgument() {
	    	if (DEBUG) System.out.println("visitTypeArgument");
	        if (argumentStack % 2 == 0) {
	            ++argumentStack;
	            declaration.append('<');
	        } else {
	            declaration.append(", ");
	        }
	        declaration.append('?');
	    }

	    /**
	     * Visits a type argument of the last visited class or inner class type.
	     * 
	     * @param wildcard '+', '-' or '='.
	     * @return a non null visitor to visit the signature of the type argument.
	     */
	    public SignatureVisitor visitTypeArgument(final char tag) {
	        if (argumentStack % 2 == 0) {
	            ++argumentStack;
	            declaration.append('<');
	        } else {
	            declaration.append(", ");
	        }

	        if (tag == SignatureVisitor.EXTENDS) {
	            declaration.append("? extends ");
	        } else if (tag == SignatureVisitor.SUPER) {
	            declaration.append("? super ");
	        }

	        startType();
	        return this;
	    }

	    /**
	     * Ends the visit of a signature corresponding to a class or interface type.
	     */
	    public void visitEnd() {
	    	if (DEBUG) System.out.println("visitEnd");
	    	if (DEBUG) System.out.println("\t" + declaration);
	        if (argumentStack % 2 != 0) {
		        int stackPos = argumentStackPosition.peek().intValue() - 1;

	        	Type raw = stack.elementAt(stackPos);
	        	Type owner = null; // FIXME
	        	
	        	int l = stack.size() - stackPos - 1;
	        	Type args[] = new Type[l];
	        	for(int i = l-1; i >= 0; i--) {
	        		args[i] = stack.pop();
	        	}
	        	stack.pop();
	        	ParameterizedTypeImpl t = new ParameterizedTypeImpl(owner, args, raw);
	        	stack.push(t);
	            declaration.append('>');
	        }
	        argumentStack /= 2;
	        argumentStackPosition.pop();
	        endType();
	    }

	    public String getDeclaration() {
	        return declaration.toString();
	    }

	    public String getReturnType() {
	        return returnType == null ? null : returnType.toString();
	    }

	    public String getExceptions() {
	        return exceptions == null ? null : exceptions.toString();
	    }

	    // -----------------------------------------------

	    private void endFormal() {
	    	Type bounds[] = stack.toArray(new Type[stack.size()]);
	    	stack.removeAllElements();
	    	formalTypeParameters.getLast().setBounds(bounds);
	    }

	    private void endGenericSuperclass() {
	    	genericSuperclass = stack.pop();
	    }

	    public void endParsing() {
	    	if (!seenInterface) {
	    		endGenericSuperclass();
	    		genericInterfaces = new Type[0];
	    	} else {
	    		genericInterfaces = stack.toArray(new Type[stack.size()]);
	    		stack.removeAllElements();
	    		for(Type t: genericInterfaces) {
	    			if (t instanceof ParameterizedTypeImpl) {
	    				ParameterizedTypeImpl p = (ParameterizedTypeImpl) t;
	    				t = p.raw;
	    			}
	    			if (t instanceof ClassTypeImpl) {
	    				ClassTypeImpl c = (ClassTypeImpl) t;
	    				c.isInterface = true;
	    			}
	    		}
	    	}
	    	currentType.setTypeParameters(typeParameters);
	    	for(TypeVariableImpl t: typeVariables) {
	    		t.setGenericDeclaration(currentType);
	    	}
	    }

	    private void endFormals() {
	        if (seenFormalParameter) {
	        	endFormal();
	            declaration.append('>');
	            seenFormalParameter = false;
	        }
			typeParameters = formalTypeParameters.toArray(new TypeVariable[formalTypeParameters.size()]);
	    }

	    private void startType() {
	        arrayStack *= 2;
	    }

	    private void endType() {
	        if (arrayStack % 2 != 0) {
	            while (arrayStack % 2 != 0) {
	            	Type t = stack.pop();
	            	stack.push(new GenericArrayTypeImpl(t));
	                arrayStack /= 2;
	                declaration.append("[]");
	            }
	        } else {
	            arrayStack /= 2;
	        }
	    }
	}
	
	public static class ClassTypeImpl implements Type, GenericDeclaration {
		private final String name;
		private boolean isInterface;
		private TypeVariable<?>[] typeParameters;
		public ClassTypeImpl(String name) {
			this.name = name;
			isInterface = false;
		}
		public ClassTypeImpl(String name, boolean isInterface) {
			this.name = name;
			this.isInterface = isInterface;
		}
		public String toString() { return (isInterface?"interface ":"class ") + name; }
		public TypeVariable<?>[] getTypeParameters() {
			return typeParameters;
		}
		public void setTypeParameters(TypeVariable<?>[] typeParameters) {
			this.typeParameters = typeParameters;
		}
	}

	public static class GenericArrayTypeImpl implements GenericArrayType {
		public GenericArrayTypeImpl(Type genericComponentType) {
			this.genericComponentType = genericComponentType;
		}
		private Type genericComponentType;
		public Type getGenericComponentType() throws TypeNotPresentException, MalformedParameterizedTypeException {
			return genericComponentType;
		}
	}

	public static class ParameterizedTypeImpl implements ParameterizedType {
		public ParameterizedTypeImpl(Type owner, Type args[], Type raw) {
			this.owner = owner;
			this.args = args;
			this.raw = raw;
		}
		private Type owner, args[], raw;

		public Type[] getActualTypeArguments() throws TypeNotPresentException, MalformedParameterizedTypeException {
			return args;
		}
		public Type getOwnerType() {
			return owner;
		}
		public Type getRawType() {
			return raw;
		}
		public String toString() {
			StringBuffer sb = new StringBuffer();
			String s = raw.toString();
			if (s.startsWith("class ")) {
				s = s.substring(6);
			} else if (s.startsWith("interface ")) {
				s = s.substring(10);
			}
			sb.append(s);
			if (args.length != 0) {
				sb.append('<');
				boolean first = true;
				for(Type t: args) {
					if (!first) {
						sb.append(", ");
					}
					first = false;
					s = t.toString();
					if (s.startsWith("class ")) {
						s = s.substring(6);
					}
					sb.append(s);
				}
				sb.append('>');
			}
			return sb.toString();
		}
	}

	public static class TypeVariableImpl<D extends GenericDeclaration> implements TypeVariable {
		private final String name;

		public TypeVariableImpl(String name) {
			this.name = name;
		}

		private Type[] bounds;
		private D genericDeclaration;

		protected void setBounds(Type[] bounds) {
			this.bounds = bounds;
		}
		public Type[] getBounds() throws TypeNotPresentException, MalformedParameterizedTypeException {
			return bounds;
		}

		protected void setGenericDeclaration(D d) {
			genericDeclaration = d;
		}
		public D getGenericDeclaration() {
			return genericDeclaration;
		}

		public String getName() {
			return name;
		}
		
		public String toString() { return name; }
	}

	public static class WildcardTypeImpl implements WildcardType {
		public Type[] getLowerBounds() throws TypeNotPresentException, MalformedParameterizedTypeException {
			throw new UnsupportedOperationException("NotImplemented");
		}

		public Type[] getUpperBounds() throws TypeNotPresentException, MalformedParameterizedTypeException {
			throw new UnsupportedOperationException("NotImplemented");
		}
	}

}
