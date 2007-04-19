package net.sourceforge.retroweaver.tests;

import java.util.Queue;
import java.util.PriorityQueue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ConcurrentTest extends AbstractTest {

	public void testConcurrent() {
		// Should not generate a warning
		ConcurrentHashMap map = new ConcurrentHashMap();

		assertNotNull("testConcurrent", map);
	}

	public void testConcurrentClassLiteral() {
		ConcurrentHashMap map = new ConcurrentHashMap();
		assertEquals("testConcurrentClassLiteral ConcurrentHashMap", ConcurrentHashMap.class, map.getClass());
	}

	public void testException() {
		try {
			throw new CancellationException();
		} catch (CancellationException e) {
			assertNotNull("testException", e);
		}
	}

	public void testExceptionClause() {
		try {
			throwExecutionException();
			fail("testExceptionClause");
		} catch (ExecutionException e) {
			assertNotNull("testExceptionClause", e);
		}
	}

	private void throwExecutionException() throws ExecutionException {
		throw new ExecutionException("foo", null);
	}

	public void testQueue() {
		ArrayBlockingQueue<String> q = new ArrayBlockingQueue<String>(100);
		q.add("hi");
		assertEquals("testQueue", "hi", q.peek());

		Queue<String> q2 = new ArrayBlockingQueue<String>(100);
		q2.add("hi");
		assertEquals("testQueue", "hi", q2.peek());

		Queue<String> q3 = new PriorityQueue<String>(100);
		q3.add("hi");
		assertEquals("testQueue", "hi", q3.peek());
	}

	public void testExecutor() {
		Executor queuedExecutor = Executors.newSingleThreadExecutor();

		assertTrue("testExecutor", queuedExecutor != null);
	}

	private Executor queuedExecutorField = null;

	public void testExecutorField() {
		queuedExecutorField = Executors.newSingleThreadExecutor();

		assertTrue("testExecutorField", queuedExecutorField != null);
	}

	public void testQueueAsField() {
		Queue<String> q3 = new PriorityQueue<String>(100);
		q3.add("hi");
		QueuePee<String> qp = new QueuePee<String>(q3);
		assertEquals("testQueue", "hi", qp.q.peek());
	}

	private static class QueuePee<T> {
		Queue<T> q;

		QueuePee(Queue<T> q) {
			this.q = q;
		}
	}

}
