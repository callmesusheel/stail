package info.caiiiycuk.stail.impl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import sun.org.mozilla.javascript.internal.NativeArray;
import sun.org.mozilla.javascript.internal.NativeObject;

@SuppressWarnings("restriction")
public class Configuration {

	private Tail[] tails;
	private ServerConfig serverConfig;
	
	public Configuration(InputStream resourceStream) throws ScriptException {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("js");
		
		engine.eval(new InputStreamReader(resourceStream));
		Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
		
		{ // read tails config
			NativeArray jsTails = (NativeArray) bindings.get("tails");
			
			this.tails = new Tail[(int) jsTails.getLength()];
			
			Map<String, Object> argumentMap = new HashMap<String, Object>();
			
			for (int i=0; i<jsTails.getLength(); ++i) {
				NativeObject object = (NativeObject) jsTails.get(i, null);
				
			
				argumentMap.clear();
				for (Object id: object.getIds()) {
					argumentMap.put(id.toString(), object.get(id.toString(), null));
				}
				
				this.tails[i] = Tail.makeTail(argumentMap);
			}
		}
		
		{ // read server config
			NativeObject jsServer = (NativeObject) bindings.get("server");
			int port = ((Number) jsServer.get("port", null)).intValue();
			
			Object jsFrontend = jsServer.get("frontend", null);
			
			if (jsFrontend != null) {
				serverConfig = new ServerConfig(port, jsFrontend.toString());
			} else {
				serverConfig = new ServerConfig(port, null);
			}
		}
	}

	public Tail[] getTails() {
		return tails;
	}

	public ServerConfig getServerConfig() {
		return serverConfig;
	}

}
