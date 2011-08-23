package info.caiiiycuk.stail;

import info.caiiiycuk.stail.impl.AjaxServlet;
import info.caiiiycuk.stail.impl.InfoServlet;
import info.caiiiycuk.stail.impl.ResourceServlet;
import info.caiiiycuk.stail.impl.Tail;

import java.io.File;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartServer {

    private static final Logger logger = LoggerFactory.getLogger(StartServer.class);
	
	public static void main(String[] args) {
		if (args.length == 0) {
			logger.error("Usage: java -jar stail.jar config.js");
			return;
		}
		
		logger.info("Parsing server configuration: " + args[0]);
		
		try {
			ConfigurationHolder.create(args[0]);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return;
		}
		
		QueuedThreadPool threadPool = new QueuedThreadPool(20);
		threadPool.setName("Thread Pool");
		
		SelectChannelConnector selectChannelConnector = new SelectChannelConnector();
		selectChannelConnector.setPort(ConfigurationHolder.c().getServerConfig().getPort());

		selectChannelConnector.setAcceptors(2);
		selectChannelConnector.setAcceptQueueSize(100);

		final Server server = new Server();
		server.setConnectors(new Connector[] { selectChannelConnector });
		server.setThreadPool(threadPool);

		Context root = new Context(server,"/",Context.SESSIONS);
		
		root.addServlet(new ServletHolder(new InfoServlet()), "/info");
		
		root.addServlet(new ServletHolder(ResourceServlet.resource("json2.js")), "/json2.js");
		root.addServlet(new ServletHolder(ResourceServlet.resource("easyXDM.min.js")), "/easyXDM.min.js");
		root.addServlet(new ServletHolder(ResourceServlet.resource("easyXDM.debug.js")), "/easyXDM.debug.js");
		root.addServlet(new ServletHolder(ResourceServlet.resource("json2.js")), "/json2.js");
		
		root.addServlet(new ServletHolder(ResourceServlet.resource("cors-index.html")), "/cors/");
		root.addServlet(new ServletHolder(ResourceServlet.file(args[0])), "/config.js");
		
		root.addServlet(new ServletHolder(new AjaxServlet()), "/cors/dispacher");
		
		if (ConfigurationHolder.c().getServerConfig().haveFrontend()) {
			File file = new File(ConfigurationHolder.c().getServerConfig().getFrontend());
			
			if (!file.exists() || !file.isDirectory()) {
				logger.error("File " + file.getAbsoluteFile().toString() + " not exsist, or not directory");
				return;
			}
			
			addFile(root, file, "/");
		}
		
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
            	try {
					server.stop();
					
					for (Tail tail: ConfigurationHolder.c().getTails()) {
						tail.getProcess().cancle();
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
            }
        }));
        
		try {
			server.start();
			server.join();				
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	private static void addFile(Context root, File file, String path) {
		if (file.isDirectory()) {
			for (File sub: file.listFiles()) {
				addFile(root, sub, path + file.getName() + "/");
			}
		} else {
			String realFile = file.getAbsoluteFile().toString();
			String mappedUrl = path + file.getName();
			
			root.addServlet(
				new ServletHolder(ResourceServlet.file(realFile)),
				mappedUrl);
			
			logger.info("Mapped: " + realFile + " to " + mappedUrl);
		}
	}
	
}