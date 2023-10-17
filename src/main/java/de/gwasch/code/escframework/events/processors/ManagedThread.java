package de.gwasch.code.escframework.events.processors;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import de.gwasch.code.escframework.events.events.ActivateEvent;
import de.gwasch.code.escframework.events.events.Callback;
import de.gwasch.code.escframework.events.events.CancelEvent;
import de.gwasch.code.escframework.events.events.DeactivateEvent;
import de.gwasch.code.escframework.events.events.Event;
import de.gwasch.code.escframework.events.events.MainPushAction;
import de.gwasch.code.escframework.events.events.PushAction;
import de.gwasch.code.escframework.events.events.SuspendEvent;
import de.gwasch.code.escframework.events.events.SwingPushAction;
import de.gwasch.code.escframework.events.events.TimerAction;
import de.gwasch.code.escframework.events.listeners.CallbackListener;
import de.gwasch.code.escframework.events.listeners.ProcessListener;
import de.gwasch.code.escframework.utils.logging.Logger;

//todo, deactivate-callback kommt nach dem process-event-callback; suspend/cancel-callback kommt ggf. schon davor. Problem?

/**
 * A {@code ManagedThread} transfers an incoming event to a worker thread.
 * Besides all other standard processors a {@code ManagedThread} is thread-safe.
 * It ensures that predecessor processors contain their {@link Callback}s in
 * their thread via {@link PushAction}. The framework provides
 * {@link MainPushAction} to give feedback to Java's main thread and
 * {@link SwingPushAction} to give feedback to Swing's Event Dispatch Thread
 * (see <a href="https://docs.oracle.com/javase/tutorial/uiswing/">Creating a
 * GUI With Swing</a>).
 * 
 * @param <E> the event type considered by the {@code ManagedThread}
 */
public class ManagedThread<E extends Event> extends Processor<E> {

	class CallbackHandler implements CallbackListener<E> {

		public void finish(E event, boolean success) {

			lock.lock();

			assert Thread.currentThread() == thread;
			assert event == currentEvent;
			Logger.log("finish", ManagedThread.this, event);

			currentEvent = null;
			// todo, ManagedThread sollte selbst erkennen, in welchen Thread zurückgeschickt
			// werden muss
			// das müsste aber immer derselbe sein
			callback(event, success, pushAction.clone());

			lock.unlock();
		}
	}

	class TheThread extends Thread {

		public void run() {

			lock.lock();
			forwardActivate(activateEvent, new ActivateCallbackHandler());

			while (true) {

				if (currentEvent == null) {
					try {
						condition.await();
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}

				if (isActive) {
					assert currentEvent != null;
					Logger.log("run", ManagedThread.this, currentEvent);
					forward(currentEvent, callbackHandler);

				}

				if (!isActive) {
					assert deactivateCallbackHandler.finishCalled; // todo, kann bei ungünstigem Threadwechsel false
																	// sein. Hier muss auf finishCalled gewartet werden
					callback(deactivateEvent, true, pushAction.clone());
					break;
				}
			}

			lock.unlock();
		}
	}

	class ProcessHandler implements ProcessListener<E> {

		public void process(E event) {

			lock.lock();

//			assert SwingUtilities.isEventDispatchThread();
			assert currentEvent == null;
//			Logger.log("process", ManagedThread.this, event);

			if (event instanceof TimerAction<?>) {
				TimerAction<?> ta = (TimerAction<?>) event;
				ta.setCondition(condition);
			}

			currentEvent = event;
			condition.signal();

			lock.unlock();
		}
	}

	class ActivateHandler implements ProcessListener<ActivateEvent> {

		public void process(ActivateEvent event) {

			if (thread != null)
				return;

			thread = new TheThread();
			thread.setName(ManagedThread.class.getSimpleName() + lastThreadId++);
			activateEvent = event;
			isActive = true;

			thread.start();
		}
	}

	class ActivateCallbackHandler implements CallbackListener<ActivateEvent> {

		public void finish(ActivateEvent event, boolean success) {

			callback(event, success, pushAction.clone());
		}
	}

	class DeactivateHandler implements ProcessListener<DeactivateEvent> {

		public void process(DeactivateEvent event) {

			deactivateEvent = event;
			isActive = false;
			deactivateCallbackHandler.finishCalled = false;
			forwardDeactivate(event, deactivateCallbackHandler);

			lock.lock();
			condition.signal();
			lock.unlock();
		}
	}

	class DeactivateCallbackHandler implements CallbackListener<DeactivateEvent> {

		private boolean finishCalled;

		public void finish(DeactivateEvent event, boolean success) {
			finishCalled = true;
		}
	}

	class SuspendHandler implements ProcessListener<SuspendEvent> {

		public void process(SuspendEvent event) {

			forwardSuspend(event);

			lock.lock();

			if (currentEvent != null) {
				condition.signal();
			}
			lock.unlock();
		}
	}

	class CancelHandler implements ProcessListener<CancelEvent> {

		public void process(CancelEvent event) {

			forwardCancel(event);

			lock.lock();

			if (currentEvent != null) {
				condition.signal();
			}
			lock.unlock();
		}
	}

	private final ReentrantLock lock = new ReentrantLock();
	private final Condition condition = lock.newCondition();

	private static int lastThreadId = 0;

	private Thread thread;
	private volatile boolean isActive;
	private final PushAction pushAction;

	private volatile E currentEvent;

	private final CallbackListener<E> callbackHandler;
	private final DeactivateCallbackHandler deactivateCallbackHandler;

	private volatile ActivateEvent activateEvent;
	private volatile DeactivateEvent deactivateEvent;

	/**
	 * Constructs a {@code ManagedThread}.
	 * 
	 * @param name       the name of this {@code Processor}
	 * @param pushAction action to provide {@link Callback}s within the caller
	 *                   thread
	 * 
	 * @see MainPushAction
	 * @see SwingPushAction
	 */
	public ManagedThread(String name, PushAction pushAction) {
		super(name);

		thread = null;
		isActive = false;
		this.pushAction = pushAction;

		currentEvent = null;

		callbackHandler = new CallbackHandler();
		deactivateCallbackHandler = new DeactivateCallbackHandler();
		activateEvent = null;
		deactivateEvent = null;

		installListener(null, new ProcessHandler());
		installListener(ActivateEvent.class, new ActivateHandler());
		installListener(DeactivateEvent.class, new DeactivateHandler());
		installListener(SuspendEvent.class, new SuspendHandler());
		installListener(CancelEvent.class, new CancelHandler());
	}

	/**
	 * Constructs a {@code ManagedThread}. The name of this {@code Processor} is an
	 * empty {@code String}.
	 * 
	 * @param pushAction
	 */
	public ManagedThread(PushAction pushAction) {
		this("", pushAction);
	}
}
