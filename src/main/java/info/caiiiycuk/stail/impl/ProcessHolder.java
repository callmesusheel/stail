package info.caiiiycuk.stail.impl;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ProcessHolder {
	
	private final static int BUFF_SIZE = 4096;

	private int bufferSize;
	private int indexOffset;
	private final List<String> lines;
	private final List<TailChanged> changeListeners;
	
	private volatile boolean cancled;
	private volatile boolean done;
	
	public ProcessHolder(String[] command) {
		this(command, Integer.MAX_VALUE);
	}
	
	public ProcessHolder(final String[] command, int bufferSize) {
		this.bufferSize = bufferSize;
		this.lines 	= Collections.synchronizedList(new LinkedList<String>());
		this.changeListeners = Collections.synchronizedList(new ArrayList<TailChanged>());
		this.indexOffset = 0;
		this.cancled = false;
		this.done = false;
		
		Runnable processor = new Runnable() {
			
			@Override
			public void run() {
				try {
					ProcessBuilder processBuilder = new ProcessBuilder(command);
					processBuilder.redirectErrorStream(true);
					
					Process process = processBuilder.start();
					BufferedReader prcessReader = 
						new BufferedReader(new InputStreamReader(
										process.getInputStream()));

					char[] buff = new char[BUFF_SIZE];
					
					StringBuilder builder = new StringBuilder();
					
					while (!cancled) {
						if (!prcessReader.ready()) {
							synchronized (this) {
								wait(100);
							}
							continue;
						}
						
						int readCount = prcessReader.read(buff);
						
						for (int i=0; i<readCount; ++i) {
							if (buff[i] == '\n' || buff[i] == '\r') {
								append(builder.toString());
								builder.setLength(0);
							} else {
								builder.append(buff[i]);
							}
						}
						
						if (readCount == -1) {
							break;
						}
					}
					
					append(builder.toString());
					
					process.destroy();
				} catch (Throwable t) {
					ProcessHolder.this.bufferSize = Integer.MAX_VALUE;
					
					append("Exception occured in info.caiiiycuk.stail.impl.ProcessHolder, see stack trace bellow:");
					
					append(t.getMessage());
					for (StackTraceElement element: t.getStackTrace()) {
						append(element.toString());
					}
				} finally {
					done = true;
				}
			}
		};
		
		ThreadPool.execute(processor);
	}

	public int linesCount() {
		synchronized (lines) {
			return lines.size();
		}
	}
	
	public int maxIndex() {
		synchronized (lines) {
			return lines.size() + indexOffset;
		}
	}
	
	public int minIndex() {
		synchronized (lines) {
			return indexOffset;
		}
	}

	public void cancle() {
		cancled = true;
		
		synchronized (this) {
			notify();
		}
	}
	
	public boolean isDone() {
		return done;
	}
	
	private void append(String line) {
		if (line == null || line.length() == 0) {
			return;
		}
		
		synchronized (lines) {
			lines.add(line);
			if (lines.size() > bufferSize) {
				indexOffset++;
				lines.remove(0);
			}
		}
		
		synchronized (changeListeners) {
			Iterator<TailChanged> iterator = changeListeners.iterator();
			while (iterator.hasNext()) {
				iterator.next().onChange();
			}
			
			changeListeners.clear();
		}
	}
	
	public Marker makeMarker() {
		return new ProcessHolderMarker(this);
	}

	public String getLine(int index) {
		synchronized (lines) {
			int realIndex = index - indexOffset;
			
			if (realIndex >= 0 && realIndex < lines.size()) { 
				return lines.get(realIndex);
			}
			
			return "Index is out of range index: " + index + 
				", indexOffset: " + indexOffset +
				", linesCount: " + lines.size();
		}
	}

	public void onChange(TailChanged tailChanged) {
		synchronized (changeListeners) {
			changeListeners.add(tailChanged);
		}
	}
	
	
}
