/*******************************************************************************
 * Copyright (c) 2011, Daniel Murphy
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL DANIEL MURPHY BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package org.jbox2d.pooling;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MutableStack<E, T extends E> implements IDynamicStack<E> {
	
	private static final Logger log = LoggerFactory.getLogger(MutableStack.class);
	
	private T[] stack;
	private int index;
	private int size;
	private final Class<T> sClass;
	
	private final Class<?>[] params;
	private final Object[] args;
	
	public MutableStack(Class<T> argClass, int argInitSize){
		this(argClass, argInitSize, null, null);
	}
	
	@SuppressWarnings("unchecked")
	public MutableStack(Class<T> argClass, int argInitSize, Class<?>[] argParam, Object[] argArgs){
		index = 0;
		size = argInitSize;
		sClass = argClass;
		params = argParam;
		args = argArgs;
		
		stack = (T[]) Array.newInstance(sClass, argInitSize);
		for(int i=0; i<argInitSize; i++){
			try {
				if(params != null){
					stack[i] = sClass.getConstructor(params).newInstance(args);
				}else{
					stack[i] = sClass.newInstance();
				}
			}
			catch (InstantiationException e) {
				log.error("Error creating pooled object " + sClass.getSimpleName(), e);
				assert(false) : "Error creating pooled object " + sClass.getCanonicalName();
			} catch (IllegalAccessException e) {
				log.error("Error creating pooled object " + sClass.getSimpleName(), e);
				assert(false) : "Error creating pooled object " + sClass.getCanonicalName();
			} catch (IllegalArgumentException e) {
				log.error("Error creating pooled object " + sClass.getSimpleName(), e);
				assert(false) : "Error creating pooled object " + sClass.getCanonicalName();
			} catch (SecurityException e) {
				log.error("Error creating pooled object " + sClass.getSimpleName(), e);
				assert(false) : "Error creating pooled object " + sClass.getCanonicalName();
			} catch (InvocationTargetException e) {
				log.error("Error creating pooled object " + sClass.getSimpleName(), e);
				assert(false) : "Error creating pooled object " + sClass.getCanonicalName();
			} catch (NoSuchMethodException e) {
				log.error("Error creating pooled object " + sClass.getSimpleName(), e);
				assert(false) : "Error creating pooled object " + sClass.getCanonicalName();
			}
		}
		index = 0;
	}
	
	@SuppressWarnings("unchecked")
	private void extendStack(){
		T[] newStack = (T[]) Array.newInstance(sClass, size*2);
		System.arraycopy(stack, 0, newStack, 0, size);
		for(int i=0; i<newStack.length; i++){
			try {
				if(params != null){
					newStack[i] = sClass.getConstructor(params).newInstance(args);
				}else{
					newStack[i] = sClass.newInstance();
				}
			} catch (InstantiationException e) {
				log.error("Error creating pooled object " + sClass.getSimpleName(), e);
				assert(false) : "Error creating pooled object " + sClass.getCanonicalName();
			} catch (IllegalAccessException e) {
				log.error("Error creating pooled object " + sClass.getSimpleName(), e);
				assert(false) : "Error creating pooled object " + sClass.getCanonicalName();
			} catch (IllegalArgumentException e) {
				log.error("Error creating pooled object " + sClass.getSimpleName(), e);
				assert(false) : "Error creating pooled object " + sClass.getCanonicalName();
			} catch (SecurityException e) {
				log.error("Error creating pooled object " + sClass.getSimpleName(), e);
				assert(false) : "Error creating pooled object " + sClass.getCanonicalName();
			} catch (InvocationTargetException e) {
				log.error("Error creating pooled object " + sClass.getSimpleName(), e);
				assert(false) : "Error creating pooled object " + sClass.getCanonicalName();
			} catch (NoSuchMethodException e) {
				log.error("Error creating pooled object " + sClass.getSimpleName(), e);
				assert(false) : "Error creating pooled object " + sClass.getCanonicalName();
			}
		}
		stack = newStack;
		size = newStack.length;
	}
	
	/* (non-Javadoc)
	 * @see org.jbox2d.pooling.IDynamicStack#pop()
	 */
	public final E pop(){
		if(index >= size){
			extendStack();
		}
		return stack[index++];
	}
	
	/* (non-Javadoc)
	 * @see org.jbox2d.pooling.IDynamicStack#push(E)
	 */
	@SuppressWarnings("unchecked")
	public final void push(E argObject){
		assert(index > 0);
		stack[--index] = (T)argObject;
	}
}
