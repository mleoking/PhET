/*
 * ProGuard -- shrinking, optimization, obfuscation, and preverification
 *             of Java bytecode.
 *
 * Copyright (c) 2002-2007 Eric Lafortune (eric@graphics.cornell.edu)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package proguard.optimize;

import proguard.classfile.*;
import proguard.classfile.attribute.Attribute;
import proguard.classfile.attribute.annotation.*;
import proguard.classfile.attribute.visitor.AttributeVisitor;
import proguard.classfile.editor.ConstantPoolEditor;
import proguard.classfile.util.*;
import proguard.classfile.visitor.MemberVisitor;
import proguard.optimize.info.*;
import proguard.optimize.peephole.VariableShrinker;

/**
 * This MemberVisitor removes unused parameters in the descriptors of the
 * methods that it visits.
 *
 * @see ParameterUsageMarker
 * @see VariableUsageMarker
 * @see VariableShrinker
 * @author Eric Lafortune
 */
public class MethodDescriptorShrinker
extends      SimplifiedVisitor
implements   MemberVisitor,
             AttributeVisitor
{
    private static final boolean DEBUG = false;


    private final MemberVisitor extraParameterMemberVisitor;

    private final ConstantPoolEditor constantPoolEditor = new ConstantPoolEditor();


    /**
     * Creates a new MethodDescriptorShrinker.
     */
    public MethodDescriptorShrinker()
    {
        this(null);
    }


    /**
     * Creates a new MethodDescriptorShrinker with an extra visitor.
     * @param extraParameterMemberVisitor an optional extra visitor for all
     *                                    methods whose parameters have been
     *                                    simplified.
     */
    public MethodDescriptorShrinker(MemberVisitor extraParameterMemberVisitor)
    {
        this.extraParameterMemberVisitor = extraParameterMemberVisitor;
    }


    // Implementations for MemberVisitor.

    public void visitProgramMethod(ProgramClass programClass, ProgramMethod programMethod)
    {
        // Update the descriptor if it has any unused parameters.
        String descriptor    = programMethod.getDescriptor(programClass);
        String newDescriptor = shrinkDescriptor(programClass, programMethod);
        if (!descriptor.equals(newDescriptor))
        {
            // Shrink the parameter annotations.
            programMethod.attributesAccept(programClass, this);

            String name = programMethod.getName(programClass);
            String newName;

            // Is it a class instance initializer?
            if (name.equals(ClassConstants.INTERNAL_METHOD_NAME_INIT))
            {
                // Is there already another initializer with the same descriptor?
                if (programClass.findMethod(name, newDescriptor) != null)
                {
                    // Cancel the shrinking. Mark all parameters.
                    ParameterUsageMarker.markUsedParameters(programMethod, -1L);

                    return;
                }

                // We have to keep the initializer's name.
                newName = name;
            }
            else
            {
                // Create a unique name.
                newName = name + ClassConstants.SPECIAL_MEMBER_SEPARATOR + Long.toHexString(Math.abs((descriptor).hashCode()));
            }

            if (DEBUG)
            {
                System.out.println("MethodDescriptorShrinker:");
                System.out.println("  Class file        = "+programClass.getName());
                System.out.println("  Method name       = "+name);
                System.out.println("                   -> "+newName);
                System.out.println("  Method descriptor = "+descriptor);
                System.out.println("                   -> "+newDescriptor);
            }

            // Update the name, if necessary.
            if (!newName.equals(name))
            {
                programMethod.u2nameIndex =
                constantPoolEditor.addUtf8Constant(programClass, newName);
            }

            // Clear the unused referenced classes.
            shrinkReferencedClasses(programClass, programMethod);

            // Update the descriptor.
            programMethod.u2descriptorIndex =
                constantPoolEditor.addUtf8Constant(programClass, newDescriptor);

            // Visit the method, if required.
            if (extraParameterMemberVisitor != null)
            {
                extraParameterMemberVisitor.visitProgramMethod(programClass, programMethod);
            }
        }
    }


    // Implementations for AttributeVisitor.

    public void visitAnyAttribute(Clazz clazz, Attribute attribute) {}


    public void visitAnyParameterAnnotationsAttribute(Clazz clazz, Method method, ParameterAnnotationsAttribute parameterAnnotationsAttribute)
    {
        int[]          annotationsCounts = parameterAnnotationsAttribute.u2parameterAnnotationsCount;
        Annotation[][] annotations       = parameterAnnotationsAttribute.parameterAnnotations;

        // All parameters of non-static methods are shifted by one in the local
        // variable frame.
        int parameterIndex =
            (method.getAccessFlags() & ClassConstants.INTERNAL_ACC_STATIC) != 0 ?
                0 : 1;

        int annotationIndex    = 0;
        int newAnnotationIndex = 0;

        // Go over the parameters.
        String descriptor = method.getDescriptor(clazz);
        InternalTypeEnumeration internalTypeEnumeration =
            new InternalTypeEnumeration(descriptor);

        while (internalTypeEnumeration.hasMoreTypes())
        {
            String type = internalTypeEnumeration.nextType();
            if (ParameterUsageMarker.isParameterUsed(method, parameterIndex))
            {
                annotationsCounts[newAnnotationIndex] = annotationsCounts[annotationIndex];
                annotations[newAnnotationIndex++]     = annotations[annotationIndex];
            }

            annotationIndex++;

            parameterIndex += ClassUtil.isInternalCategory2Type(type) ? 2 : 1;
        }

        // Update the number of parameters.
        parameterAnnotationsAttribute.u2parametersCount = newAnnotationIndex;

        // Clear the unused entries.
        while (newAnnotationIndex < annotationIndex)
        {
            annotationsCounts[newAnnotationIndex] = 0;
            annotations[newAnnotationIndex++]     = null;
        }
    }


    // Small utility methods.

    /**
     * Returns a shrunk descriptor of the given method.
     */
    public static String shrinkDescriptor(ProgramClass  clazz,
                                          ProgramMethod method)
    {
        // All parameters of non-static methods are shifted by one in the local
        // variable frame.
        int parameterIndex =
            (method.getAccessFlags() & ClassConstants.INTERNAL_ACC_STATIC) != 0 ?
                0 : 1;

        // Go over the parameters.
        InternalTypeEnumeration internalTypeEnumeration =
            new InternalTypeEnumeration(method.getDescriptor(clazz));

        StringBuffer newDescriptorBuffer = new StringBuffer();
        newDescriptorBuffer.append(ClassConstants.INTERNAL_METHOD_ARGUMENTS_OPEN);

        if (DEBUG)
        {
            System.out.println("MethodDescriptorShrinker: "+method.getName(clazz)+method.getDescriptor(clazz));
            System.out.println("  parameter size = " + ParameterUsageMarker.getParameterSize(method));
        }

        while (internalTypeEnumeration.hasMoreTypes())
        {
            String type = internalTypeEnumeration.nextType();
            if (ParameterUsageMarker.isParameterUsed(method, parameterIndex))
            {
                newDescriptorBuffer.append(type);
            }
            else if (DEBUG)
            {
                System.out.println("  Deleting parameter #"+parameterIndex+" ("+type+")");
            }

            parameterIndex += ClassUtil.isInternalCategory2Type(type) ? 2 : 1;
        }

        newDescriptorBuffer.append(ClassConstants.INTERNAL_METHOD_ARGUMENTS_CLOSE);
        newDescriptorBuffer.append(internalTypeEnumeration.returnType());

        return newDescriptorBuffer.toString();
    }


    /**
     * Shrinks the array of referenced classes of the given method.
     */
    private static void shrinkReferencedClasses(ProgramClass  clazz,
                                                ProgramMethod method)
    {
        Clazz[] referencedClasses = method.referencedClasses;

        if (referencedClasses != null)
        {
            // All parameters of non-static methods are shifted by one in the local
            // variable frame.
            int parameterIndex =
                (method.getAccessFlags() & ClassConstants.INTERNAL_ACC_STATIC) != 0 ?
                    0 : 1;

            int referencedClassIndex    = 0;
            int newReferencedClassIndex = 0;

            // Go over the parameters.
            String descriptor = method.getDescriptor(clazz);
            InternalTypeEnumeration internalTypeEnumeration =
                new InternalTypeEnumeration(descriptor);

            while (internalTypeEnumeration.hasMoreTypes())
            {
                String type = internalTypeEnumeration.nextType();
                if (ClassUtil.isInternalArrayType(type))
                {
                    type = ClassUtil.internalTypeFromArrayType(type);
                }

                if (ClassUtil.isInternalClassType(type))
                {
                    if (ParameterUsageMarker.isParameterUsed(method, parameterIndex))
                    {
                        referencedClasses[newReferencedClassIndex++] =
                            referencedClasses[referencedClassIndex];
                    }

                    referencedClassIndex++;
                }

                parameterIndex += ClassUtil.isInternalCategory2Type(type) ? 2 : 1;
            }

            // Also look at the return value.
            String type = internalTypeEnumeration.returnType();
            if (ClassUtil.isInternalArrayType(type))
            {
                type = ClassUtil.internalTypeFromArrayType(type);
            }

            if (ClassUtil.isInternalClassType(type))
            {
                referencedClasses[newReferencedClassIndex++] =
                    referencedClasses[referencedClassIndex];

                referencedClassIndex++;
            }

            // Clear the unused entries.
            while (newReferencedClassIndex < referencedClassIndex)
            {
                referencedClasses[newReferencedClassIndex++] = null;
            }
        }
    }
}
