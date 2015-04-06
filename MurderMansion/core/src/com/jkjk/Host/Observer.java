package com.jkjk.Host;

import java.io.PrintWriter;

public class Observer {
	private final PrintWriter writer;
	public Observer(PrintWriter writer){
		this.writer=writer;
	}
	public void update(String message){
		writer.println(message);
		writer.flush();
	}
}
