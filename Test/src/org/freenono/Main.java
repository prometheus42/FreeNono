package org.freenono;

import org.freenono.serializer.CourseSerializerTest;
import org.freenono.serializer.NonogramSerializerTest;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * @author Markus Wichmann
 * 
 */
public class Main {

	public static void main(String[] args) {

		Class<?>[] classes = { NonogramSerializerTest.class,
				CourseSerializerTest.class, };

		Result result = JUnitCore.runClasses(classes);
		if (result.wasSuccessful()) {
			System.out.println("Everything OK!");
		} else {
			for (Failure failure : result.getFailures()) {
				System.out.println(failure.toString());
			}
		}
	}
}
