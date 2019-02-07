/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package threads;

/**
 *
 * @author hcadavid
 */
public class CountThreadsMain {
    
    public static void main(String a[]){
        CountThread  countUno = new CountThread(0,99);
        CountThread  countDos= new CountThread(99,199);
        CountThread  countTres= new CountThread(200,299);
        
        
    }
    
}