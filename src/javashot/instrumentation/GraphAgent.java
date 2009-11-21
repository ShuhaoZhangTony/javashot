/**
 * Copyright (C) 2009 Samir Chekkal
 * This program is distributed under the terms of the GNU General Public License.
 */

/**
 * @author chekkal
 *
 */
package javashot.instrumentation;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javashot.util.Properties;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.NotFoundException;

/**
 * To understand this class you should learn about Java Instrumentation. The System property JAVASHOT_HOME must be set in you system which points to
 * the directory containing javashot.properties file and the directory capture for ".dot" files saving. to instrument a Java project (whether its a
 * Standard application or a J2EE one) add the following java option when you start your application:<br>
 * -javaagent:<DIRECTORY_FOR_JAVASHOT_HOME>/javashot.jar
 */
public class GraphAgent implements ClassFileTransformer {
	/**
	 * Array of patterns (regexp) for the classes to be instrumented; if empty no class will be instrumented. to set this parameter modify the
	 * corresponding entry in javashot.properties file.
	 */
	private final ArrayList<Pattern> instrumentationClassPattern = Properties.getInstrumentationClassPattern();

	public static void premain(String agentArgument, Instrumentation instrumentation) {
		instrumentation.addTransformer(new GraphAgent());
	}

	public byte[] transform(ClassLoader loader, String className, Class clazz, java.security.ProtectionDomain domain, byte[] bytes) {
		boolean enhanceClass = false;
		for (Pattern pattern : instrumentationClassPattern) {
			if (pattern.matcher(className.toLowerCase()).matches()) {
				enhanceClass = true;
				break;
			}
		}
		if (enhanceClass) {
			return enhanceClass(className, bytes);
		} else {
			return bytes;
		}
	}

	private byte[] enhanceClass(String name, byte[] b) {
		ClassPool pool = ClassPool.getDefault();
		CtClass clazz = null;
		try {
			clazz = pool.makeClass(new java.io.ByteArrayInputStream(b));
			if (!clazz.isInterface()) {
				CtBehavior[] methods = clazz.getDeclaredBehaviors();
				for (int i = 0; i < methods.length; i++) {
					if (!methods[i].isEmpty()) {
						enhanceMethod(methods[i], clazz.getName());
					}
				}
				b = clazz.toBytecode();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Could not instrument  " + name + ",  exception : " + e.getMessage());
		} finally {
			if (clazz != null) {
				clazz.detach();
			}
		}
		return b;
	}

	/**
	 * Adds static call to methods javashot.graph.Graph.pushNode and javashot.graph.Graph.popNode for every instrumented method.
	 * 
	 * @see javashot.graph.Graph#pushNode pushNode
	 * @see javashot.graph.Graph#pushNode popNode
	 */
	private void enhanceMethod(CtBehavior method, String className) throws NotFoundException, CannotCompileException {
		method.insertBefore("javashot.graph.Graph.pushNode(\"" + className + "\",\"" + method.getName() + "\");");
		method.insertAfter("javashot.graph.Graph.popNode();");
	}
}
