/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.greeman.forgettingmap;

import java.util.Random;

/**
 *
 * @author jgreeman
 */
public class TestingThread extends Thread{
    private final ForgettingMap<Integer, String> instance;
    private final Object lock;
    private final Random r    =   new Random();
    private boolean finished    =   false;

    public TestingThread(ForgettingMap<Integer, String> instance, Object lock) {
        this.instance   =   instance;
        this.lock       =   lock;
    }

    public boolean isFinished() {
        return finished;
    }
    
    @Override
    public void run() {
        for (int i = 0; i < 10000; i++){
            instance.add(r.nextInt(100) + 1, "bad value");
        }
        synchronized(lock){
            instance.remove(30);
            instance.add(41, "good value");
        }
        finished    =   true;
    }
    
}
