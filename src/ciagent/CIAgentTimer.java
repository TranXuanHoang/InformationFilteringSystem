package ciagent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Date;

/**
 * The <code>CIAgentTimer</code> class provides two basic functions
 * to <code>CIAgent</code>s:
 * <ul>
 * <li>The <i>autonomous behaviors</i> which the agent's
 * {@link CIAgent#processTimerPop()} method gets called every
 * {@link #sleepTime} milliseconds.
 * <li><i>Processing events in an asynchronous manner</i>
 * by queuing them up and then processing them every
 * {@link #asyncTime} milliseconds.
 * </ul>
 * Both of these behaviors are supported by a single thread.
 * 
 * @author Tran Xuan Hoang
 */
public class CIAgentTimer implements Runnable, Serializable {
	/** The serial version ID. */
	private static final long serialVersionUID = 1L;

	private CIAgent agent;			// owner agent
	private int sleepTime = 1000;	// millisecond (default to 1s)
	private boolean timerEnabled = true;
	private int asyncTime = 500;	// millisecond (default to 0.5s)

	/**	Used to exit thread that runs the {link #run()} method. */
	private boolean quit = false;

	/** Used to check timer variables. */
	private boolean debug = false;

	transient private Thread runnit = new Thread(this);

	/**
	 * Creates a timer, specifying the agent that owns the timer.
	 * @param agent the agent that owns this timer object.
	 */
	public CIAgentTimer(CIAgent agent) {
		this.agent = agent;
	}

	/**
	 * Retrieves the sleep time (in milliseconds).
	 * @return the amount of sleep time in milliseconds.
	 */
	public int getSleepTime() {
		return sleepTime;
	}

	/**
	 * Sets the time (in milliseconds) that determines how often an
	 * agent performs the autonomous behavior defined in its
	 * <code>processTimerPop</code> method.
	 * @param sleepTime the amount of sleep time in milliseconds,
	 */
	public void setSleepTime(int sleepTime) {
		this.sleepTime = sleepTime;
	}

	/**
	 * Retrieves the asynchronous time interval in milliseconds.
	 * @return the asynchronous time interval in milliseconds.
	 */
	public int getAsyncTime() {
		return asyncTime;
	}

	/**
	 * Sets the time (in milliseconds) that determines how often
	 * the asynchronous events on its event queue.
	 * @param asyncTime the time interval in milliseconds.
	 */
	public void setAsyncTime(int asyncTime) {
		this.asyncTime = asyncTime;
	}

	/**
	 * Starts the timer thread.
	 */
	public void startTimer() {
		timerEnabled = true;

		if (!runnit.isAlive()) {
			runnit.start();
		}
	}

	/**
	 * Indicates that timer events should not be fired.
	 */
	public void stopTimer() {
		timerEnabled = false;
	}

	/**
	 * Indicates that the timer thread should be ended.
	 */
	public void quitTimer() {
		quit = true;
	}

	/**
	 * Processes the asynchronous events and autonomous timer events
	 * periodically, with the interval based on the sleep time and
	 * the asynchronous event time.
	 */
	@Override
	public void run() {
		long startTime = 0;
		long curTime = 0;

		if (debug) {
			startTime = new Date().getTime(); // milliseconds
			curTime = startTime;
		}

		if (sleepTime < asyncTime) {
			asyncTime = sleepTime;
		}

		int numEventChecks = sleepTime / asyncTime;

		if (debug) {
			System.out.println("sleepTime = " + sleepTime +
					", asyncTime = " + asyncTime +
					", numEventChecks = " + numEventChecks);
		}

		while (!quit) {
			try {
				for (int i = 0; i < numEventChecks; i++) {
					Thread.sleep(asyncTime);

					if (debug) {
						curTime = new Date().getTime();
						System.out.println("async events timer at " +
								(curTime - startTime));
					}

					if (quit) {
						break;
					}

					agent.processAsynchronousEvents();
				} // end for

				if (timerEnabled && !quit) {
					if (debug) {
						curTime = new Date().getTime();
						System.out.println("timer events timer at " +
								(curTime - startTime));
					}

					agent.processTimerPop();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Deserializes the timer object from the specified input stream
	 * by re-initializing the object's transient variable and
	 * deserializing the object with
	 * {@link java.io.ObjectInputStream#defaultReadObject()}.
	 * @param inputStream from which this timer object is to be read.
	 * @throws IOException if any I/O error occurs.
	 * @throws ClassNotFoundException if any class file is not found.
	 */
	private void readObject(ObjectInputStream inputStream)
			throws IOException, ClassNotFoundException {
		// restore the transient variable
		runnit = new Thread(this);

		// restore the rest of the object
		inputStream.defaultReadObject();
	}
} // end class CIAgentTimer