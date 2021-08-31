package org.jboss.editorial;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.stream.Stream;

public final class IOUtils {

	private IOUtils() {}
	
	public static Stream<String> readContentFrom(URL url) {
		try {
			return new BufferedReader(new InputStreamReader(url.openStream(), Charset.defaultCharset())).lines();	
		} catch ( IOException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
    public static URL buildURL(String url) {
    	try {
    		return new URL(url);
    	} catch ( MalformedURLException e ) {
    		throw new IllegalArgumentException(e);
    	}
    }
}
