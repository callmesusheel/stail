package info.caiiiycuk.stail.impl;

public class ServerConfig {

	private final int port;
	private final String frontend;

	public ServerConfig(int port, String frontend) {
		this.port = port;
		this.frontend = frontend;
	}
	
	public int getPort() {
		return port;
	}
	
	public String getFrontend() {
		return frontend;
	}
	
	public boolean haveFrontend() {
		return frontend != null && frontend.length() > 0;
	}
	
}
