/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blackList;

import java.util.List;

/**
 *
 * @author hcadavid
 */
public class Main {
    
    public static void main(String a[]){
        HostBlackListsValidator hblv=new HostBlackListsValidator();
        //int threads = 2*Runtime.getRuntime().availableProcessors();
        int threads = 50;
        System.out.println("threads: "+ threads);
        List<Integer> blackListOcurrences=hblv.checkHost("202.24.34.55", threads);
        System.out.println("The host was found in the following blacklists:"+blackListOcurrences);
        
    }
    
}