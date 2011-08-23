package info.caiiiycuk.stail.impl;

public interface MarkerState {

	Marker applyState(Marker marker);
	
	Marker saveState(Marker marker);
	
}
