package info.caiiiycuk.stail;

import info.caiiiycuk.stail.impl.Configuration;
import info.caiiiycuk.stail.impl.ServerConfig;
import info.caiiiycuk.stail.impl.Tail;

import java.io.IOException;
import java.io.InputStream;

import javax.script.ScriptException;

import org.junit.Assert;
import org.junit.Test;

public class ConfigTest {

	@Test
	public void sampleServerConfig() throws ScriptException {
		InputStream resourceStream = ConfigTest.class.getResourceAsStream("config.js");
		Configuration configuration = new Configuration(resourceStream);
		
		ServerConfig serverConfig = configuration.getServerConfig();
		
		Assert.assertNotNull(serverConfig);
		Assert.assertEquals(8080, serverConfig.getPort());
		
		try {
			resourceStream.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Test
	public void sampleTailsConfig() throws ScriptException {
		InputStream resourceStream = ConfigTest.class.getResourceAsStream("config.js");
		Configuration configuration = new Configuration(resourceStream);

		Tail[] tails = configuration.getTails();
		
		Assert.assertEquals(3, tails.length);
		
		Assert.assertEquals("dmesg", tails[0].getAlias());
		Assert.assertEquals(400, tails[0].getSize());
		Assert.assertEquals("tail", tails[0].getCommand()[0]);
		Assert.assertEquals("-f", tails[0].getCommand()[1]);
		Assert.assertEquals("/var/log/dmesg", tails[0].getCommand()[2]);
		
		Assert.assertEquals("syslog", tails[1].getAlias());
		Assert.assertEquals(800, tails[1].getSize());
		Assert.assertEquals("tail", tails[1].getCommand()[0]);
		Assert.assertEquals("-f", tails[1].getCommand()[1]);
		Assert.assertEquals("/var/log/syslog", tails[1].getCommand()[2]);
		
		Assert.assertEquals("tail -f /var/log/udev", tails[2].getAlias());
		Assert.assertEquals(300, tails[2].getSize());
		Assert.assertEquals("tail", tails[2].getCommand()[0]);
		Assert.assertEquals("-f", tails[2].getCommand()[1]);
		Assert.assertEquals("/var/log/udev", tails[2].getCommand()[2]);
		
		try {
			resourceStream.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
}
