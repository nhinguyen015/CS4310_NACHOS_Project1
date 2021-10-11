package nachos.threads;

import nachos.machine.*;

import java.util.PriorityQueue;

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
        lock = new Lock();
        cond = new Condition2(lock);
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
    //wake all threads that exceed current time, then yield
    public void timerInterrupt() {
        lock.acquire();
        while (!waitQueue.isEmpty() && Machine.timer().getTime() >= waitQueue.peek().wakeTime) {
            cond.wake();
        }
        KThread.currentThread().yield();
        lock.release();
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
        // for now, cheat just to get something working (busy waiting is bad)
        long wakeTime = Machine.timer().getTime() + x;
        AlarmThread alarmThread = new AlarmThread(KThread.currentThread(), wakeTime, cond, lock);
        lock.acquire();
        waitQueue.add(alarmThread);
        cond.sleep();
        lock.release();
    }

    private class AlarmThread implements Comparable<AlarmThread>{
        KThread thread;
        long wakeTime;
        Condition2 cond;
        Lock lock;

        public AlarmThread(KThread thread, long wakeTime, Condition2 cond, Lock lock){
            this.thread = thread;
            this.wakeTime = wakeTime;
            this.cond = cond;
            this.lock = lock;
        }

        public int compareTo(AlarmThread thread){
            if(this.wakeTime<thread.wakeTime)
                return -1;
            else if(this.wakeTime>thread.wakeTime)
                return 1;
            else return 0;
        }
    }

    private Lock lock;
    private Condition2 cond;
    private PriorityQueue<AlarmThread> waitQueue = new PriorityQueue<>();
}
