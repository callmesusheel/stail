package info.caiiiycuk.stail.impl;

import java.util.Arrays;
import java.util.Map;

public class Tail {

	private final String    alias;
	private final int      size;
	private final String[]  command;
	
	private final transient ProcessHolder processHolder;
	
	public Tail(String command) {
		this(command, 300, command);
	}
	
	public Tail(String command, int size) {
		this(command, size, command);
	}
	
	public Tail(String command, int size, String alias) {
		this.alias = alias;
		this.size = size;
		
		this.command = command.split("\\s+");
		this.processHolder = new ProcessHolder(
				this.command, 
				this.size);
	}
	
	public static Tail makeTail(Map<String, Object> argumentMap) {
		if (!argumentMap.containsKey("command")) {
			throw new IllegalArgumentException("Argument map must have command key");
		}
		
		String command = (String) argumentMap.get("command");
		String alias = argumentMap.containsKey("alias") ?
				(String) argumentMap.get("alias") :
				command;
		int size = argumentMap.containsKey("size") ? 
				((Number) argumentMap.get("size")).intValue() :
				300;
		
		return new Tail(command, size, alias);
	}

	public String getAlias() {
		return alias;
	}

	public int getSize() {
		return size;
	}

	public String[] getCommand() {
		return command;
	}
	
	@Override
	public String toString() {
		return 
			"alias: " + alias + 
			", command: " + Arrays.toString(command) + 
			", size:" + size + 
			", status: " + (processHolder.isDone() ? "died" : "ok");
	}

	public ProcessHolder getProcess() {
		return processHolder;
	}

}
