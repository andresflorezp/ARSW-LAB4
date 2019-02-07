/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blackList;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import java.util.LinkedList;

/**
 *
 * @author laboratorio
 */
public class ThreadCheckList extends Thread{

    private int start,end,alarm,ocurrences;
    String ipAddress;
    LinkedList<Integer> blackListOcurrences=new LinkedList<>();
    HostBlacklistsDataSourceFacade skd;
    ThreadCheckList (HostBlacklistsDataSourceFacade skd,int start, int end, int alarm, String ip){
        this.alarm=alarm;
        this.start=start;
        this.end=end;
        this.ipAddress= ip;
        this.skd=skd;
        
    }
    
    @Override
    public void run(){
        //System.out.println("+++++++");
        for (int i =start;i<end && ocurrences <= alarm;i++){
            if (skd.isInBlackListServer(i, ipAddress)){
                ocurrences+=1;
                blackListOcurrences.add(i);
            }
        }
        System.out.println(getOcurrences());
        
    }

    public int getOcurrences() {
        return ocurrences;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int getAlarm() {
        return alarm;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public LinkedList<Integer> getBlackListOcurrences() {
        return blackListOcurrences;
    }

    public HostBlacklistsDataSourceFacade getSkd() {
        return skd;
    }
    
    
}