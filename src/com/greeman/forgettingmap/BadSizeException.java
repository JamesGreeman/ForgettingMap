/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.greeman.forgettingmap;

/**
 * Exception to be used when there is an invalid size in an object
 * @author jgreeman
 */
public class BadSizeException extends RuntimeException{
    public BadSizeException(){
        this("There is a problem with the size of the ArrayLists in this Map");
    }
    public BadSizeException(String message) { 
        super(message);
    }
}
