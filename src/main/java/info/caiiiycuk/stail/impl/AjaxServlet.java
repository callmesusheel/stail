package info.caiiiycuk.stail.impl;

import info.caiiiycuk.stail.ConfigurationHolder;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.nio.SelectChannelConnector.RetryContinuation;
import org.mortbay.util.ajax.ContinuationSupport;

import com.google.gson.Gson;

public class AjaxServlet extends HttpServlet {

	private static final long serialVersionUID = -290743310120522399L;
	
	private static final String ACTION_REQUEST_TAILS = "requestTails";
	private static final String ACTION_PROCEED_LOG = "proceedLog";

	private static final String PARAMETER_ID = "id";
	private static final String PARAMETER_INDEX = "index";

	private static final int MAX_LINES_PER_REQUEST = 500;
	
	private final Gson gson;
	
	public AjaxServlet() {
		gson = new Gson();
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		setHeaders(resp);
		
		String action = req.getParameter("action");
		
		if (ACTION_REQUEST_TAILS.equals(action)) {
			writeTails(resp);
 		} else if(ACTION_PROCEED_LOG.equals(action)) {
 			writeLog(
 					Integer.parseInt(req.getParameter(PARAMETER_ID)), 
 					Integer.parseInt(req.getParameter(PARAMETER_INDEX)),
 					req, resp);
 		}
	}

	private void setHeaders(HttpServletResponse resp) {
		resp.setHeader( "Pragma", "no-cache" );
		resp.addHeader( "Cache-Control", "must-revalidate,no-cache,no-store" );
		resp.setDateHeader("Expires", 0);
		resp.setContentType("text/xml; charset=UTF-8");
	}

	private void writeLog(int id, int index, 
			HttpServletRequest req, HttpServletResponse resp) throws IOException {
		ProcessHolder tailProcess = ConfigurationHolder.c().getTails()[id].getProcess();
		
		Marker marker = new IndexedMarkerState(index).applyState(
				tailProcess.makeMarker());
		
		String log = extractLog(marker);
		
		if (log.length() == 0) {
			//NO LOG DATA AVAILABLE
			final RetryContinuation continuation = (RetryContinuation) 
				ContinuationSupport.getContinuation(req, null);

			if (continuation.isExpired() || continuation.isResumed()) {
				log = extractLog(marker);
			} else {
				tailProcess.onChange(new TailChanged() {
					@Override
					public void onChange() {
						continuation.resume();
					}
				});
				continuation.suspend(60 * 1000);
			}
		}
		
		resp.getWriter().println(gson.toJson(new LogResponse(marker, log.toString())));
	}
	
	private String extractLog(Marker marker) {
		StringBuilder log = new StringBuilder();
		
		int lineCounter = MAX_LINES_PER_REQUEST;
		while (marker.next() && lineCounter > 0) {
			log.append(marker.toString()).append("\n");
			lineCounter --;
		}
		
		return log.toString();
	}

	private void writeTails(HttpServletResponse resp) throws IOException {
		Tail[] tails = ConfigurationHolder.c().getTails();
		
		resp.getWriter().write(gson.toJson(tails));
	}
	
	@SuppressWarnings("unused")
	private static class LogResponse {
		private Marker marker;
		private String data;
		
		public LogResponse(Marker marker, String data) {
			this.marker = marker;
			this.data = data;
		}
	}

}
