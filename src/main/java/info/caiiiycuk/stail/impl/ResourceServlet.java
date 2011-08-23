package info.caiiiycuk.stail.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

public class ResourceServlet extends HttpServlet {

	private static final long serialVersionUID = -3152750601901791397L;
	
	private byte[] content;
	
	private ResourceServlet(String resource, boolean isResource) {
		content = new byte[0];
		
		InputStream resourceStream = null;
		
		if (isResource) {
			resourceStream = ResourceServlet.class.getResourceAsStream(resource);
		} else {
			try {
				resourceStream = new FileInputStream(resource);
			} catch (FileNotFoundException e) {
				return;
			}
		}
		
		
		if (resourceStream != null) {
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			try {
				IOUtils.copy(resourceStream, bytes);
				content = bytes.toByteArray();
				resourceStream.close();
				bytes.close();
			} catch (IOException e) {
			}
		} 
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		ServletOutputStream outputStream = resp.getOutputStream();
		outputStream.write(content);
		outputStream.close();
	}
	
	public static ResourceServlet file(String file) {
		return new ResourceServlet(file, false);
	}
	
	public static ResourceServlet resource(String resource) {
		return new ResourceServlet(resource, true);
	}
}
