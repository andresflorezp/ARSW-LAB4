/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blackList;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */

public class HostBlackListsValidator {

	private static final int BLACK_LIST_ALARM_COUNT = 5;

	/**
	 * Check the given host's IP address in all the available black lists, and
	 * report it as NOT Trustworthy when such IP was reported in at least
	 * BLACK_LIST_ALARM_COUNT lists, or as Trustworthy in any other case. The search
	 * is not exhaustive: When the number of occurrences is equal to
	 * BLACK_LIST_ALARM_COUNT, the search is finished, the host reported as NOT
	 * Trustworthy, and the list of the five blacklists returned.
	 *
	 * @param ipaddress suspicious host's IP address.
	 * @return Blacklists numbers where the given host's IP address was found.
	 */
	public List<Integer> checkHost(String ipaddress, int threads) {

		LinkedList<Integer> blackListOcurrences = new LinkedList<>();

		int ocurrencesCount = 0;
		ArrayList<ThreadCheckList> searchers = new ArrayList<ThreadCheckList>();
		HostBlacklistsDataSourceFacade skds = HostBlacklistsDataSourceFacade.getInstance();
		int checkedListsCount = 0;
		int size = skds.getRegisteredServersCount() / threads;
		int actual = 0;

		for (int i = 0; i < threads; i++) {
			if (i == threads - 1) {
				searchers.add(new ThreadCheckList(skds, actual, skds.getRegisteredServersCount(),
						BLACK_LIST_ALARM_COUNT, ipaddress));
			} else {
				searchers.add(new ThreadCheckList(skds, actual, actual + size, BLACK_LIST_ALARM_COUNT, ipaddress));
			}
			searchers.get(i).start();
			actual += size;

		}
		boolean notAllFound = false;
		while (notAllFound) {
			synchronized (this) {
				for (int i = 0; i < threads; i++) {
					if (searchers.get(i).getOcurrences() >= BLACK_LIST_ALARM_COUNT) {
						notAllFound = false;
						for (int j = 0; j < threads; j++) {
							searchers.get(j).stop();
						}
					}
				}
			}
		}
		for (int i = 0; i < threads; i++) {
			try {
				searchers.get(i).join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			ocurrencesCount += searchers.get(i).getOcurrences();
			blackListOcurrences.addAll(searchers.get(i).getBlackListOcurrences());
		}

		if (ocurrencesCount >= BLACK_LIST_ALARM_COUNT) {
			skds.reportAsNotTrustworthy(ipaddress);
		} else {
			skds.reportAsTrustworthy(ipaddress);
		}
		LOG.log(Level.INFO, "Checked Black Lists:{0} of {1}",
				new Object[] { checkedListsCount, skds.getRegisteredServersCount() });

		return blackListOcurrences;
	}

	private static final Logger LOG = Logger.getLogger(HostBlackListsValidator.class.getName());

}