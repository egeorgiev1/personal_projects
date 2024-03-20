/** TaskQueue.java */
package org.com1028.main;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Represents a general single threaded worker queue to eliminate synchronization problems in the
 * application
 *
 * @author Emil Georgiev
 */
public class TaskQueue {

    /** Holds the worker queue */
    private static ExecutorService exec;

    /**
     * Adds a task to the queue
     *
     * @param toAdd
     *          The task to be added
     */
    public static void addTask(Runnable toAdd) {
        if(exec == null) {
            exec = Executors.newSingleThreadExecutor();
        }

        exec.execute(toAdd);
    }
}
