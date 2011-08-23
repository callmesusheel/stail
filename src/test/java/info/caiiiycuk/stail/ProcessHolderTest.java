package info.caiiiycuk.stail;

import info.caiiiycuk.stail.impl.IndexedMarkerState;
import info.caiiiycuk.stail.impl.Marker;
import info.caiiiycuk.stail.impl.ProcessHolder;

import java.net.URL;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

public class ProcessHolderTest extends TestCase {

	private ProcessHolder processHolderFull;
	private ProcessHolder processHolderFive;
	
	public void createHolders() {
		URL testResource = ProcessHolderTest.class.getResource("test.log");
		Assert.assertNotNull(testResource);
		
		String testFile = testResource.getFile();
		Assert.assertNotNull(testFile);
		
		String[] command = new String[] {
				"tail", "-f", "-n 1000", testFile
		};
		
		processHolderFull = new ProcessHolder(command);
		processHolderFive = new ProcessHolder(command, 5);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
	}
	
	@Test
	public void testMarkerState() {
		createHolders();
		
		Marker marker;
		
		marker = processHolderFull.makeMarker();
		
		new IndexedMarkerState(0).applyState(marker).next();
		assertEquals('Q', marker.toString().charAt(0));
		
		new IndexedMarkerState(24).applyState(marker).next();
		assertEquals('N', marker.toString().charAt(0));
		
		marker = processHolderFive.makeMarker();
		
		new IndexedMarkerState(0).applyState(marker).next();
		assertEquals('X', marker.toString().charAt(0));
		
		new IndexedMarkerState(20).applyState(marker).next();
		assertEquals('X', marker.toString().charAt(0));
		
		new IndexedMarkerState(24).applyState(marker).next();
		assertEquals('N', marker.toString().charAt(0));
		
		cancleHolders();
	}
	
//	@Test
//	public void testDelayedMarker() {
//		URL testResource = ProcessHolderTest.class.getResource("test.log");
//		Assert.assertNotNull(testResource);
//		
//		String testFile = testResource.getFile();
//		Assert.assertNotNull(testFile);
//		
//		String[] command = new String[] {
//				"tail", "-f", "-n 1000", testFile
//		};
//
//		MarkerState markerState = new IndexedMarkerState(6);
//		
//		ProcessHolder processHolderFive = new ProcessHolder(command, 5);
//		Marker marker = processHolderFive.makeMarker();
//		
//		Assert.assertEquals(false, marker.isValid());
//		
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//		}
//		
//		processHolderFive.cancle();
//		
////		try {
////			Thread.sleep(100);
////		} catch (InterruptedException e) {
////		}
////		
////		processHolderFive.cancle();
////		
////		while (!processHolderFive.isDone()) {
////			try {
////				Thread.sleep(100);
////			} catch (InterruptedException e) {
////			}
////		}
////		
////		Marker marker = processHolderFive.makeMarker(4);
////		
////		if (marker.next()) {
////			assertEquals('M', marker.toString().charAt(0));
////		}
//	}
	
	@Test
	public void testFiveHolder() {
		URL testResource = ProcessHolderTest.class.getResource("test.log");
		Assert.assertNotNull(testResource);
		
		String testFile = testResource.getFile();
		Assert.assertNotNull(testFile);
		
		String[] command = new String[] {
				"tail", "-f", "-n 1000", testFile
		};
		
		ProcessHolder processHolderFive = new ProcessHolder(command, 5);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		
		processHolderFive.cancle();
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}
		
		processHolderFive.cancle();
		
		while (!processHolderFive.isDone()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
		
		Marker marker = new IndexedMarkerState(25).applyState(processHolderFive.makeMarker());
		
		if (marker.next()) {
			assertEquals('M', marker.toString().charAt(0));
		}
		
	}

	@Test
	public void testFullLinesCount() {
		createHolders();
		
		assertEquals(25, processHolderFull.linesCount());
		assertEquals(5, processHolderFive.linesCount());
		
		cancleHolders();
	}
	
	@Test
	public void testMarkers() {
		createHolders();
		
		String full = "QWERTYUIOPASDFGHJKLZXCVBNM";
		
		Marker marker;
		
		{
			marker = new IndexedMarkerState(0).applyState(processHolderFull.makeMarker());
			
			int index = 0;
			while (marker.next()) {
				assertEquals(1, marker.toString().length());
				assertEquals(full.charAt(index), marker.toString().charAt(0));
				index++;
			}
			
			assertEquals(25, index);
		}
		
		{
			marker = new IndexedMarkerState(15).applyState(processHolderFull.makeMarker());
			
			int index = 15;
			while (marker.next()) {
				assertEquals(1, marker.toString().length());
				assertEquals(full.charAt(index), marker.toString().charAt(0));
				index++;
			}
			
			assertEquals(25, index);
		}
		
		{
			marker = new IndexedMarkerState(20).applyState(processHolderFive.makeMarker());
			
			int index = 20;
			while (marker.next()) {
				assertEquals(1, marker.toString().length());
				assertEquals(full.charAt(index), marker.toString().charAt(0));
				index++;
			}
			
			assertEquals(25, index);
		}
		
		cancleHolders();
	}

	@Test
	public void testStackTrace() {
		ProcessHolder ph = new ProcessHolder(null, 0);
		
		assertNotSame(0, ph.linesCount());
	}
	
	public void cancleHolders() {
		processHolderFull.cancle();
		processHolderFive.cancle();
	}
	
}
