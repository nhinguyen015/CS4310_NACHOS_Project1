package nachos.threads;

import nachos.machine.*;

import java.util.PriorityQueue;

/**
 * A <i>communicator</i> allows threads to synchronously exchange 32-bit
 * messages. Multiple threads can be waiting to <i>speak</i>,
 * and multiple threads can be waiting to <i>listen</i>. But there should never
 * be a time when both a speaker and a listener are waiting, because the two
 * threads can be paired off at this point.
 */
public class Communicator {
    /**
     * Allocate a new communicator.
     */
    public Communicator() {
        lock = new Lock();
        speaker = new Condition2(lock);
        listener = new Condition2(lock);
        speakerQ = new PriorityQueue();
        listenerQ = new PriorityQueue();
    }

    /**
     * Wait for a thread to listen through this communicator, and then transfer
     * <i>word</i> to the listener.
     *
     * <p>
     * Does not return until this thread is paired up with a listening thread.
     * Exactly one listener should receive <i>word</i>.
     *
     * @param	word	the integer to transfer.
     */
    public void speak(int word) {
        lock.acquire();
        while (!listenerQ.isEmpty()) {
            listener.wake();
            //how to send word to listener?
        }
        if (listenerQ.isEmpty()) {
            speakerQ.add(speaker);
            speaker.sleep();
        }
    }

    /**
     * Wait for a thread to speak through this communicator, and then return
     * the <i>word</i> that thread passed to <tt>speak()</tt>.
     *
     * @return	the integer transferred.
     */    
    public int listen() {
        lock.acquire();
        while (!speakerQ.isEmpty()) {
            speaker.wake();
            //how to receive word from speaker?
        }
        if (speakerQ.isEmpty()) {
            listenerQ.add(listener);
            listener.sleep();
        }
        lock.release();

        return 0;
    }

    private Lock lock;
    private Condition2 listener, speaker;
    private PriorityQueue<Condition2> speakerQ, listenerQ;
}
