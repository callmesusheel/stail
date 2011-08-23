package info.caiiiycuk.stail.impl;


public class IndexedMarkerState implements MarkerState {

	private int index;
	
	public IndexedMarkerState(int index) {
		this.index = index -1;
	}

	@Override
	public Marker applyState(Marker marker) {
		((ProcessHolderMarker) marker).setIndex(index);
		return marker;
	}

	@Override
	public Marker saveState(Marker marker) {
		this.index = ((ProcessHolderMarker) marker).getIndex();
		return marker;
	}

}
