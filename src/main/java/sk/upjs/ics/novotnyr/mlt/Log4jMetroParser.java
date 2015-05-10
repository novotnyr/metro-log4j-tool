package sk.upjs.ics.novotnyr.mlt;

import nu.xom.*;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Log4jMetroParser {

	public static final String XML_PAYLOAD_INTRODUCTORY_CHARACTER = "<";

	public static final String PROLOG = "<log4j:events xmlns:log4j=\"urn:log4j\">";

	public static final String EPILOG = "</log4j:events>";

	private Date sinceDateFilter;

	public void setSinceDate(Date sinceDate) {
		this.sinceDateFilter = sinceDate;
	}

	public Date getSinceDate() {
		return sinceDateFilter;
	}

	private static class HttpTransportPipeEventCollector extends NodeFactory {
		private static final Nodes EMPTY_NODES = new Nodes();

		private List<HttpTransportPipeEvent> httpTransportPipeEvents = new LinkedList<HttpTransportPipeEvent>();

		private HttpTransportPipeEvent httpTransportPipeEvent;

		private Date sinceDateFilter;

		@Override
		public Nodes makeComment(String data) {
			return EMPTY_NODES;
		}

		@Override
		public Nodes makeProcessingInstruction(String target, String data) {
			return EMPTY_NODES;
		}

		@Override
		public Element startMakingElement(String name, String namespace) {
			if("log4j:event".equals(name)) {
				httpTransportPipeEvent = new HttpTransportPipeEvent();
			}
			return super.startMakingElement(name, namespace);
		}

		@Override
		public Nodes finishMakingElement(Element element) {
			if("event".equals(element.getLocalName())) {
				Date timestamp = new Date(Long.parseLong(element.getAttributeValue("timestamp")));
				if(timestamp.after(this.sinceDateFilter)) {
					httpTransportPipeEvent.setTimestamp(timestamp);
					httpTransportPipeEvent.setThreadName(element.getAttributeValue("thread"));
					httpTransportPipeEvents.add(httpTransportPipeEvent);
				}
				return EMPTY_NODES;
			}
			if("message".equals(element.getLocalName())) {
				String messageText = element.getValue();
				if(isHttpResponse(messageText)) {
					httpTransportPipeEvent.setType(HttpTransportPipeEvent.Type.RESPONSE);

					String RESPONSE_FIRST_LINE_REGEXP = "^---\\[HTTP response - (\\S+) - (\\d+)\\]---";
					Pattern RESPONSE_FIRST_LINE_PATTERN = Pattern.compile(RESPONSE_FIRST_LINE_REGEXP);
					Matcher matcher = RESPONSE_FIRST_LINE_PATTERN.matcher(messageText);
					if(matcher.find()) {
						httpTransportPipeEvent.setRemoteHostName(matcher.group(1));
						httpTransportPipeEvent.setHttpStatus(matcher.group(2));
					}

					parseHttpBody(messageText, httpTransportPipeEvent);

					return EMPTY_NODES;
				}
				if(isHttpRequest(messageText)) {
					httpTransportPipeEvent.setType(HttpTransportPipeEvent.Type.REQUEST);

					String REQUEST_FIRST_LINE_REGEXP = "^---\\[HTTP request - (\\S+)\\]---";
					Pattern REQUEST_FIRST_LINE_PATTERN = Pattern.compile(REQUEST_FIRST_LINE_REGEXP);
					Matcher matcher = REQUEST_FIRST_LINE_PATTERN.matcher(messageText);
					if(matcher.find()) {
						httpTransportPipeEvent.setRemoteHostName(matcher.group(1));
					}

					parseHttpBody(messageText, httpTransportPipeEvent);

					return EMPTY_NODES;

				}
			}
			return super.finishMakingElement(element);
		}

		private void parseHttpBody(String messageText, HttpTransportPipeEvent httpTransportPipeEvent) {
			Scanner scanner = new Scanner(messageText);
			String line = scanner.nextLine();
			line = scanner.nextLine(); //move past first "---" line
			Map<String, String> headers = new LinkedHashMap<String, String>();
			while(!line.startsWith(XML_PAYLOAD_INTRODUCTORY_CHARACTER)) {
				String[] split = line.split(": ");
				if(split.length != 2) {
					throw new IllegalStateException("Malformed header on line " + line);
				}
				headers.put(split[0], split[1]);
				line = scanner.nextLine();
			}
			httpTransportPipeEvent.setHttpHeaders(headers);
			StringBuilder xmlSb = new StringBuilder(line);
			while(scanner.hasNextLine()) {
				xmlSb.append(scanner.nextLine());
			}
			String xmlString = xmlSb.toString();
			if(xmlString.endsWith("--------------------")) {
				xmlString = xmlString.replace("--------------------", "");
			}
			httpTransportPipeEvent.setPayloadXml(xmlString);


		}

		private boolean isHttpResponse(String messageText) {
			return messageText.startsWith("---[HTTP response");
		}

		private boolean isHttpRequest(String messageText) {
			return messageText.startsWith("---[HTTP request");
		}

		public List<HttpTransportPipeEvent> getHttpTransportPipeEvents() {
			return httpTransportPipeEvents;
		}

		public void setSinceDateFilter(Date sinceDateFilter) {
			this.sinceDateFilter = sinceDateFilter;
		}

		public Date getSinceDateFilter() {
			return sinceDateFilter;
		}
	}

	public List<HttpTransportPipeEvent> parse(File file) throws LogRecordParsingException {
		Reader reader = null;
		try {
			reader = new ProloguingAndEpiloguingReader(new FileReader(file), PROLOG, EPILOG);

			HttpTransportPipeEventCollector collector = new HttpTransportPipeEventCollector();
			collector.setSinceDateFilter(this.sinceDateFilter);
			Builder xomBuilder = new Builder(collector);
			Document doc = xomBuilder.build(reader);

			return collector.getHttpTransportPipeEvents();

		} catch (ParsingException e) {
			throw new LogRecordParsingException("Unable to parse Log4j XML", e);
		} catch (IOException e) {
			throw new LogRecordParsingException("Unable to read Log4j XML due to I/O error", e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}

	}



	public static void main(String[] args) throws FileNotFoundException, XMLStreamException {
		File file = new File("c:\\Temp\\ais2-upvs-ws-trace-log2.xml");

		Log4jMetroParser parser = new Log4jMetroParser();
		List<HttpTransportPipeEvent> parse = parser.parse(file);

		for(HttpTransportPipeEvent e : parse) {
			System.out.println(e.getType() + " " + e.getHttpStatus() + " " + e.getPayloadXmlString());
		}

	}
}
