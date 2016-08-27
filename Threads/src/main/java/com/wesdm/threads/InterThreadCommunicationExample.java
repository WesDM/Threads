package com.wesdm.threads;

import java.util.LinkedList;
import java.util.Queue;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

public class InterThreadCommunicationExample {

    public static void main(String args[]) {
    	BasicConfigurator.configure(); //setup basic logging to console

        final Queue<Integer> sharedQ = new LinkedList<>();

        Thread producer = new Thread(new ProducerThread(sharedQ),"Producer");
        Thread consumer = new Thread(new ConsumerThread(sharedQ),"Consumer");

        producer.start();
        consumer.start();

    }
}

class ProducerThread implements Runnable {
    private static final Logger logger = Logger.getLogger(ProducerThread.class);
    private final Queue<Integer> sharedQ;

    public ProducerThread(Queue<Integer> sharedQ) {
        this.sharedQ = sharedQ;
    }

    public void run() {

        for (int i = 0; i < 4; i++) {

            synchronized (sharedQ) {
                //waiting condition - wait until Queue is not empty
                while (sharedQ.size() >= 1) {
                    try {
                        logger.debug("Queue is full, waiting");
                        sharedQ.wait();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                logger.debug("producing : " + i);
                sharedQ.add(i);
                sharedQ.notify();
            }
        }
    }
}

class ConsumerThread implements Runnable {
    private static final Logger logger = Logger.getLogger(ConsumerThread.class);
    private final Queue<Integer> sharedQ;

    public ConsumerThread(Queue<Integer> sharedQ) {
        this.sharedQ = sharedQ;
    }

    public void run() {
        while(true) {

            synchronized (sharedQ) {
                //waiting condition - wait until Queue is not empty
                while (sharedQ.size() == 0) {
                    try {
                        logger.debug("Queue is empty, waiting");
                        sharedQ.wait();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                int number = (Integer)sharedQ.poll();
                logger.debug("consuming : " + number );
                sharedQ.notify();
              
                //termination condition
                if(number == 3){break; }
            }
        }
    }
}