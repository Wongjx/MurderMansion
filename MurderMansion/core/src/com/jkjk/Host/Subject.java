package com.jkjk.Host;

import java.util.ArrayList;

public interface Subject {
	public void register(Observer obs);
	public void unregister (Observer obs);	
	/** Update all clients except, the orgin
	 * @param origin id of orgin client; -1 for server
	 */
	public void updateAll(int origin);
}
