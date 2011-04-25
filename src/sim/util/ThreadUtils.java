/*
 * BehaviorSim - version 1.0 
 * 
 * Copyright (C) 2010 The BehaviorSim Development Team, fasheng@cs.gsu.edu.
 * 
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * 
 * Info, Questions, Suggestions & Bugs Report to fasheng@cs.gsu.edu.
 *  
 */

package sim.util;

import sim.core.AppRunnable;
import sim.core.SimulationThread;

/**
 * Thread tools.
 * 
 * @author Fasheng Qiu
 */
public final class ThreadUtils {

	/**
	 * Set the time interval for the specified threads
	 * 
	 * @param threadNames
	 *            Name of threads to be set
	 * @param interval
	 *            The new time interval
	 */
	public static void setInterval(String[] threadNames, int interval) {
		if (threadNames == null || threadNames.length == 0) {
			return;
		}

		// Obtain and stop the analysis thread
		// Find the root thread group
		ThreadGroup root = Thread.currentThread().getThreadGroup().getParent();
		while (root.getParent() != null) {
			root = root.getParent();
		}
		setInterval(root, threadNames, interval);

	}

	/**
	 * Suspend the specified threads
	 * 
	 * @param threadNames
	 *            The name of threads to be suspended
	 */
	public static void suspendThread(String[] threadNames) {

		if (threadNames == null || threadNames.length == 0) {
			return;
		}

		// Obtain and stop the analysis thread
		// Find the root thread group
		ThreadGroup root = Thread.currentThread().getThreadGroup().getParent();
		while (root.getParent() != null) {
			root = root.getParent();
		}
		stop(root, threadNames);

	}

	/**
	 * 
	 * This method recursively visits all thread groups under 'group', and set
	 * the time interval for all threads.
	 * 
	 * @param group
	 *            ThreadGroup
	 * @param threadNames
	 *            The name of threads to set the interval
	 * @param interval
	 *            The new time interval
	 * 
	 */
	private static void setInterval(ThreadGroup group, String[] threadNames,
			int interval) {

		// Get threads in 'group'
		int numThreads = group.activeCount();
		Thread[] threads = new Thread[numThreads * 2];
		numThreads = group.enumerate(threads, false);

		// Enumerate each thread in `group'
		for (int i = 0; i < numThreads; i++) {
			// Get thread
			Thread thread = threads[i];
			for (int j = 0; j < threadNames.length; j++) {
				if (thread.getName().equals(threadNames[j])) {
					AppRunnable r = (AppRunnable) (((SimulationThread) thread)
							.getTarget());
					r.setDeltaT(interval);
				}
			}
		}

		// Get thread subgroups of `group'
		int numGroups = group.activeGroupCount();
		ThreadGroup[] groups = new ThreadGroup[numGroups * 2];
		numGroups = group.enumerate(groups, false);

		// Recursively visit each subgroup
		for (int i = 0; i < numGroups; i++) {
			setInterval(groups[i], threadNames, interval);
		}

	}

	/**
	 * 
	 * This method recursively visits all thread groups under 'group', and stop
	 * threads.
	 * 
	 * @param group
	 *            ThreadGroup
	 * @param threadNames
	 *            The name of threads to be stopped
	 * 
	 */
	private static void stop(ThreadGroup group, String[] threadNames) {
		// Get threads in `group'
		int numThreads = group.activeCount();
		Thread[] threads = new Thread[numThreads * 2];
		numThreads = group.enumerate(threads, false);

		// Enumerate each thread in `group'
		for (int i = 0; i < numThreads; i++) {
			// Get thread
			Thread thread = threads[i];
			for (int j = 0; j < threadNames.length; j++) {
				if (thread.getName().equals(threadNames[j])) {
					AppRunnable r = (AppRunnable) (((SimulationThread) thread)
							.getTarget());
					r.setSuspended(true);
				}
			}
		}

		// Get thread subgroups of `group'
		int numGroups = group.activeGroupCount();
		ThreadGroup[] groups = new ThreadGroup[numGroups * 2];
		numGroups = group.enumerate(groups, false);

		// Recursively visit each subgroup
		for (int i = 0; i < numGroups; i++) {
			stop(groups[i], threadNames);
		}
	}

}
