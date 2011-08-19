/**
 * Copyright (C) 2009 Samir Chekkal
 * This program is distributed under the terms of the GNU General Public License.
 */

/**
 * @author chekkal
 *
 */
package javashot.graph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Stack;

import javashot.util.Properties;

/**
 * Class used to keep track of called methods and classes and to write the end results in a ".dot" file which can be visualized by tools from
 * (http://www.graphviz.org/) and also manipulated by standard unix tools such as grep, awk, .... Every instrumented method will call the static
 * method Graph.pushNode at its start and the static Graph.popNode at its exit. The capture is not started until a class with the short name equals to
 * that of the configuration property startCaptureAt is called. Once the capture is started a ".dot" file is created in the directory designed by
 * variable captureHome inside a directory designing the current day, the name of the ".dot" file is a concatenation of the short name of the class
 * which fired the capture plus the hours plus minute plus seconds, the file is saved to disk when we encounter the note START in the stack .
 */
public class Graph {

	/**
	 * Stack used to keep track of the nodes
	 */
	private static ThreadLocal<Stack<String>> stack = new ThreadLocal<Stack<String>>();
	/**
	 * The first node pushed to the stack at the start of the capture, when we encounter it the next time we know that current capture has ended, so
	 * we create another file for the next capture.
	 */
	private static final String START = "_START_";
	/**
	 * The directory in which are stored ".dot" files. It is the capture directory inside the directory designed by the System property JAVASHOT_HOME
	 */
	private static String captureHome = Properties.getJavashotHome() + "/capture/";
	/**
	 * The short name of the class at which the capture will start
	 */
	private static String startCaptureAt = Properties.getStartCaptureAt();
	/**
	 * The record the full name of the class instead of a short one
	 */
	private static boolean useFullPackageClassName = Properties.getUseFullPackageClassName();
	/**
	 * The capture is started when ever we find the class designed by startCaptureAt
	 */
	private static ThreadLocal<Boolean> captureStarted = new ThreadLocal<Boolean>();
	/**
	 * Sequence id of method calls
	 */
	private static ThreadLocal<Long> sequenceId = new ThreadLocal<Long>();
	/*
	 * Thread identifier
	 */
	private static long threadIdGenerator = 1;
	private static ThreadLocal<Long> threadIdentifier = new ThreadLocal<Long>();
	/**
	 * The .dot file containing the description of the graph in the dot language. This file can be viewed by tools from (http://www.graphviz.org/) and
	 * also manipulated by standard unix tools such as grep, awk, ... to extract the desired information from a huge file
	 */
	private static ThreadLocal<BufferedWriter> dotFile = new ThreadLocal<BufferedWriter>();

	public static void pushNode(String className, String methodeName) {
		if (!useFullPackageClassName) {
			className = className.substring(className.lastIndexOf('.') + 1);
		}
		if ((captureStarted.get() == null || !captureStarted.get().booleanValue()) && startCaptureAt.equalsIgnoreCase(className)) {
			if (threadIdentifier.get() == null) {
				threadIdentifier.set(threadIdGenerator++);
			}
			try {
				String todayDir = createTodayDirectory();
				Calendar today = Calendar.getInstance();
				stack.set(new Stack<String>());
				Graph.dotFile.set(new BufferedWriter(new FileWriter(captureHome + todayDir + "Thread[" + threadIdentifier.get() + "]"
						+ startCaptureAt + "_" + String.format("%tH", today) + String.format("%tM", today) + String.format("%tS", today) + ".dot")));
				dotFile.get().write("digraph " + startCaptureAt + "{\n");
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("ERROR");
			}
			captureStarted.set(Boolean.TRUE);
			sequenceId.set((long) 1);
			stack.get().push(START);
		}
		if (captureStarted.get() != null && captureStarted.get().booleanValue()) {
			try {
				StringBuffer buffer = new StringBuffer(stack.get().peek()).append("->").append(className).append("[label=\"")
						.append(sequenceId.get()).append(":").append(methodeName).append("\"]\n");
				sequenceId.set(sequenceId.get() + 1);
				dotFile.get().write(buffer.toString());
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("ERROR");
			}
			stack.get().push(className);
		}
	}

	/**
	 * Create a directory for today's captures if it not already exists
	 */
	private static String createTodayDirectory() {
		SimpleDateFormat formater = new SimpleDateFormat("ddMMyyyy");
		String todayDir = formater.format(new java.util.Date()) + "/";
		File file = new File(captureHome + todayDir);
		file.mkdir();
		return todayDir;
	}

	public static void popNode() {
		if (captureStarted.get() != null && captureStarted.get().booleanValue()) {
			String node = stack.get().pop();
			try {
				StringBuffer buffer = new StringBuffer(node).append("->").append(stack.get().peek()).append("[label=\"").append(sequenceId.get())
						.append("\", style=dashed]\n");
				sequenceId.set(sequenceId.get() + 1);
				dotFile.get().write(buffer.toString());
			} catch (IOException e1) {
				e1.printStackTrace();
				throw new RuntimeException("ERROR");
			}
			if (START.equalsIgnoreCase(stack.get().peek())) {
				captureStarted.set(Boolean.FALSE);
				try {
					dotFile.get().write("}");
					Graph.dotFile.get().close();
				} catch (IOException e) {
					e.printStackTrace();
					throw new RuntimeException("ERROR");
				}
			}
		}
	}
}
