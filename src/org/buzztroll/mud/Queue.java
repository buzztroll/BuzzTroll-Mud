package org.buzztroll.mud;

import java.util.LinkedList;

public abstract class Queue implements Runnable {

    protected LinkedList list;

    public Queue() {
	list = new LinkedList();
    }
    
    public synchronized void queue(Object obj) {
	list.add(obj);
	notify();
    }
    
    public synchronized Object dequeue() 
	throws InterruptedException {
	if (list.isEmpty()) {
	    wait();
	}
	return list.removeFirst();
    }

    public void start() {
	Thread t = new Thread(this);
	t.setDaemon(true);
	t.start();
    }

    public void run() {
	Object tmp;
	try {
	    while(true) {
		tmp = dequeue();
		if (tmp == null) {
		    return;
		}
		execute(tmp);
	    }
	} catch (InterruptedException e) {
	}
    }
    
    public abstract void execute(Object obj);
}

    
