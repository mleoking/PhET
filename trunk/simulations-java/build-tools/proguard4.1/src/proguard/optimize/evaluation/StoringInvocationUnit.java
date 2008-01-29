/*
 * ProGuard -- shrinking, optimization, obfuscation, and preverification
 *             of Java bytecode.
 *
 * Copyright (c) 2002-2007 Eric Lafortune (eric@graphics.cornell.edu)
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package proguard.optimize.evaluation;

import proguard.classfile.*;
import proguard.classfile.constant.RefConstant;
import proguard.evaluation.BasicInvocationUnit;
import proguard.evaluation.value.*;
import proguard.optimize.info.*;

/**
 * This InvocationUbit stores parameter values and return values with the
 * methods that are invoked.
 *
 * @see LoadingInvocationUnit
 * @author Eric Lafortune
 */
public class StoringInvocationUnit
extends      BasicInvocationUnit
{
    // Implementations for BasicInvocationUnit.

    protected void setFieldClassValue(Clazz          clazz,
                                      RefConstant    refConstant,
                                      ReferenceValue value)
    {
        Member referencedMember = refConstant.referencedMember;
        if (referencedMember != null)
        {
            generalizeFieldClassValue((Field)referencedMember, value);
        }
    }


    protected void setFieldValue(Clazz       clazz,
                                 RefConstant refConstant,
                                 Value       value)
    {
        Member referencedMember = refConstant.referencedMember;
        if (referencedMember != null)
        {
            generalizeFieldValue((Field)referencedMember, value);
        }
    }


    protected void setMethodParameterValue(Clazz       clazz,
                                           RefConstant refConstant,
                                           int         parameterIndex,
                                           Value       value)
    {
        Member referencedMember = refConstant.referencedMember;
        if (referencedMember != null)
        {
            generalizeMethodParameterValue((Method)referencedMember,
                                           parameterIndex,
                                           value);
        }
    }


    protected void setMethodReturnValue(Clazz  clazz,
                                        Method method,
                                        Value  value)
    {
        generalizeMethodReturnValue(method, value);
    }


    // Small utility methods.

    private static void generalizeFieldClassValue(Field field, ReferenceValue value)
    {
        FieldOptimizationInfo info = FieldOptimizationInfo.getFieldOptimizationInfo(field);
        if (info != null)
        {
            info.generalizeReferencedClass(value);
        }
    }


    public static ReferenceValue getFieldClassValue(Field field)
    {
        FieldOptimizationInfo info = FieldOptimizationInfo.getFieldOptimizationInfo(field);
        return info != null ?
            info.getReferencedClass() :
            null;
    }


    private static void generalizeFieldValue(Field field, Value value)
    {
        FieldOptimizationInfo info = FieldOptimizationInfo.getFieldOptimizationInfo(field);
        if (info != null)
        {
            info.generalizeValue(value);
        }
    }


    public static Value getFieldValue(Field field)
    {
        FieldOptimizationInfo info = FieldOptimizationInfo.getFieldOptimizationInfo(field);
        return info != null ?
            info.getValue() :
            null;
    }


    private static void generalizeMethodParameterValue(Method method, int parameterIndex, Value value)
    {
        MethodOptimizationInfo info = MethodOptimizationInfo.getMethodOptimizationInfo(method);
        if (info != null)
        {
            info.generalizeParameter(parameterIndex, value);
        }
    }


    public static Value getMethodParameterValue(Method method, int parameterIndex)
    {
        MethodOptimizationInfo info = MethodOptimizationInfo.getMethodOptimizationInfo(method);
        return info != null ?
            info.getParameter(parameterIndex) :
            null;
    }


    private static void generalizeMethodReturnValue(Method method, Value value)
    {
        MethodOptimizationInfo info = MethodOptimizationInfo.getMethodOptimizationInfo(method);
        if (info != null)
        {
            info.generalizeReturnValue(value);
        }
    }


    public static Value getMethodReturnValue(Method method)
    {
        MethodOptimizationInfo info = MethodOptimizationInfo.getMethodOptimizationInfo(method);
        return info != null ?
            info.getReturnValue() :
            null;
    }
}
