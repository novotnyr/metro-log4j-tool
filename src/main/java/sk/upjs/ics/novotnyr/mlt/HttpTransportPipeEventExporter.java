package sk.upjs.ics.novotnyr.mlt;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

import java.io.InputStreamReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HttpTransportPipeEventExporter {
	public static final String TEMPLATE_NAME = "HttpTransportPipeEvent.txt";

	public void export(HttpTransportPipeEvent event, Writer writer) {
		InputStreamReader reader = new InputStreamReader(ClassLoader.getSystemResourceAsStream(TEMPLATE_NAME));
		Template template = Mustache
				.compiler()
				.escapeHTML(false)
				.compile(reader);

		Map<String, Object> context = new HashMap<String, Object>();
		context.put("host", event.getRemoteHostName());
		context.put("timestamp", event.getTimestamp().toString());

		List<Map<String, String>> headersContext = new LinkedList<Map<String, String>>();
		for(Map.Entry<String, String> e : event.getHttpHeaders().entrySet()) {
			Map<String, String> headerContext = new HashMap<String, String>();

			headerContext.put("name", e.getKey());
			headerContext.put("value", e.getValue());

			headersContext.add(headerContext);
		}


		context.put("headers", headersContext);
		context.put("xml", event.getPayloadXmlString());

		template.execute(context, writer);
	}
}
