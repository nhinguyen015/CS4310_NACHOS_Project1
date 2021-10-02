# CS4310_NACHOS_Project1
# First assignment for CS4310 Operating System using NACHOS

## Task 1: Condition Variables (5%)  
* Implement condition variables directly by using interrupt enable and disable to provide atomicity. 
* The implementation of condition variables included in the system uses semaphores to achieve this, so your implementation MUST NOT use semaphores (however, you may use locks).
* Your new implementation must reside on nachos.threads.Condition2.  

## Task 2: KThread (5%)  
* Implement KThread.join() using condition variables. 
* Keep in mind that to implement this functionality you may add code to / modify other parts of the KThread class. 
* But you must not break existing functionality.  

## Task 3: Alarms (10%)  
* Complete the implementation of the Alarm class, by implementing the waitUntil(long x) method using condition variables. 
* A thread calls waitUntil to suspend its own execution until time has advanced to at least now + x. 
* This is useful for threads that operate in real-time, for example, for blinking the cursor once per second. 
* There is no requirement that threads start running immediately after waking up; just put them on the ready queue in the timer interrupt handler after they have waited for at least the right amount of time. 
* Do not fork any additional threads to implement waitUntil(); you need only modify waitUntil() and the timer interrupt handler. 
* waitUntil is not limited to one thread; any number of threads may call it and be suspended at any one time.  

## Task 4: Communicator (30%)  
* Implement synchronous send and receive of one word messages (also known as Ada-style rendezvous), using condition variables (don’t use semaphores!). 
* Implement the Communicator class with operations, void speak(int word) and int listen(). 
* speak() atomically waits until listen() is called on the same Communicator object, and then transfers the word over to listen(). 
* Once the transfer is made, both can return. 
* Similarly, listen() waits until speak() is called, at which point the transfer is made, and both can return (listen() returns the word). 
* This means that neither thread may return from listen() or speak() until the word transfer has been made. 
* Your solution should work even if there are multiple speakers and listeners for the same Communicator (note: this is equivalent to a zero-length bounded buffer; since the buffer has no room, the producer and consumer must interact directly, requiring that they wait for one another). 
* Each communicator should only use exactly one lock. 
* If you’re using more than one lock, you’re making things too complicated.  

### Priority Scheduler 
* In this project module you are required to implement a Priority Scheduler. 
* An issue with priority scheduling is a phenomenon called priority inversion: if a high priority thread needs to wait for a low priority thread (for example, for a resource held by the low priority thread), and a medium priority thread is on the ready list, the high priority thread needs to wait for medium priority thread to finish, because the low priority thread won't be scheduled until then. 
* A way to solve this is to implement something called priority donation,  where the high priority thread donates its priority to the low priority thread while the resource is held, so that it can be scheduled. 
* Implementing this implies dealing with two issues: multiple donation (multiple threads waiting on the same resource), and nested donation (process A is waiting for B, and B is waiting for C, so A should donate its priority to both B and C).  
* For the project report, answer these questions:  When does priority donation happen? When a thread A needs access to a resource held by thread B, and A has higher priority than B. 
* When a thread acquires a resource previously held by another thread, and there are other priority threads waiting on the same resource. 
* All of the above. When does a thread restore its original priority? Never. Not necessary. When it releases the resource previously held. 

## Task 5: Priority Scheduling (50%)  
* Implement Priority Scheduling by completing the PriorityScheduler class. 
* Remember to use the nachos.conf file that uses the PriorityScheduler class. 
* When implementing priority scheduling, you will find a point where you can easily compute the effective priority of thread, bu the computation takes a long time. 
* To get full credit, you need to speed this up by caching the effective priority and only recalculating it when it may be possible for it to change.  
* As before, do not modify any classes to solve this task.
* The solution should involve creating a subclass of ThreadQueue that will work with the existing Lock, Semaphore, and Condition classes. 
* You should implement priority donation first, then add donation, and finally restoration.
