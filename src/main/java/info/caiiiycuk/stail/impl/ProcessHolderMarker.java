package info.caiiiycuk.stail.impl;


public class ProcessHolderMarker implements Marker {

	private int index;
	private transient ProcessHolder processHolder;
	
	public ProcessHolderMarker(ProcessHolder holder) {
		this.index = -1;
		this.processHolder = holder;
	}
	
	@Override
	public boolean isValid() {
		return index >= 0 && index < processHolder.maxIndex();
	}

	@Override
	public boolean next() {
		index++;
		return isValid();
	}

	public void setIndex(int index) {
		this.index = Math.max(index, processHolder.minIndex() -1) ;
	}

	public int getIndex() {
		return index;
	}
	
	@Override
	public String toString() {
		return processHolder.getLine(index);
	}

}
