package nachos.threads;

import nachos.machine.*;

import java.util.LinkedList;

/**
 * Uses the hardware timer to provide preemption, and to allow threads to sleep
 * until a certain time.
 */
public class Alarm {
    /**
     * Allocate a new Alarm. Set the machine's timer interrupt handler to this
     * alarm's callback.
     *
     * <p><b>Note</b>: Nachos will not function correctly with more than one
     * alarm.
     */
    public Alarm() {
        threadQueue = new LinkedList<AlarmThread>();
        Machine.timer().setInterruptHandler(new Runnable() {
            public void run() {
                timerInterrupt();
            }
        });
    }

    /**
     * The timer interrupt handler. This is called by the machine's timer
     * periodically (approximately every 500 clock ticks). Causes the current
     * thread to yield, forcing a context switch if there is another thread
     * that should be run.
     */
    public void timerInterrupt() {
        AlarmThread temp;
        while (!threadQueue.isEmpty() && Machine.timer().getTime() >= threadQueue.peek().wakeTime) {
            temp = threadQueue.poll();
            temp.lock.acquire();
            boolean intStatus = Machine.interrupt().disable();
            temp.cond.wake();
            Machine.interrupt().restore(intStatus);
            temp.lock.release();
        }
        KThread.currentThread().yield();
    }

    /**
     * Put the current thread to sleep for at least <i>x</i> ticks,
     * waking it up in the timer interrupt handler. The thread must be
     * woken up (placed in the scheduler ready set) during the first timer
     * interrupt where
     *
     * <p><blockquote>
     * (current time) >= (WaitUntil called time)+(x)
     * </blockquote>
     *
     * @param x the minimum number of clock ticks to wait.
     * @see nachos.machine.Timer#getTime()
     */
    public void waitUntil(long x) {
        long wakeTime = Machine.timer().getTime() + x;
        boolean intStatus = Machine.interrupt().disable();
        Machine.interrupt().restore(intStatus);
        while (wakeTime > Machine.timer().getTime()) {
            AlarmThread at = new AlarmThread(KThread.currentThread(), wakeTime);
            threadQueue.add(at);
            at.lock.acquire();
            at.cond.sleep();
            at.lock.release();
        }
    }

    private class AlarmThread implements Comparable<AlarmThread> {
        KThread thread;
        long wakeTime;
        Condition2 cond;
        Lock lock;

        public AlarmThread(KThread thread, long wakeTime) {
            this.thread = thread;
            this.wakeTime = wakeTime;
            lock = new Lock();
            cond = new Condition2(lock);
        }

        public int compareTo(AlarmThread thread) {
            if (this.wakeTime < thread.wakeTime)
                return -1;
            else if (this.wakeTime > thread.wakeTime)
                return 1;
            else return 0;
        }
    }

    private static LinkedList<AlarmThread> threadQueue;
}
