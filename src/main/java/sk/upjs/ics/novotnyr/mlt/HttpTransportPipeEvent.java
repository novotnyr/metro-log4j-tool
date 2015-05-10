package sk.upjs.ics.novotnyr.mlt;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.Serializer;

import javax.print.Doc;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

public class HttpTransportPipeEvent extends LogEvent {
	public static final String NO_BASE_URI = null;
	private Date timestamp;

	private String threadName;

	private String httpStatus;

	private String remoteHostName;

	private Type type;

	private Map<String, String> httpHeaders;

	private String payloadXmlString;

	public void setHttpHeaders(Map<String, String> httpHeaders) {
		this.httpHeaders = httpHeaders;
	}

	public Map<String, String> getHttpHeaders() {
		return httpHeaders;
	}

	public enum Type {
		REQUEST, RESPONSE
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = new Date(timestamp);
	}


	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public void setPayloadXml(String payloadXmlString) throws LogRecordParsingException {
		this.payloadXmlString = prettyPrint(payloadXmlString);
	}

	private String prettyPrint(String xml) {
		try {
			Builder builder = new Builder();
			Document document = builder.build(xml, NO_BASE_URI);
			return prettyPrint(document);
		} catch (ParsingException e) {
			throw new LogRecordParsingException("Unable to parse XML payload", e);
		} catch (IOException e) {
			throw new LogRecordParsingException("Unable to read XML payload due to I/O error", e);
		}

	}

	private String prettyPrint(Document document) {
		try {
			String encoding = "UTF-8";
			ByteArrayOutputStream byteBuf = new ByteArrayOutputStream();
			Serializer serializer = new Serializer(byteBuf, encoding);
			serializer.setIndent(4);
			serializer.write(document);

			return byteBuf.toString(encoding);
		} catch (IOException e) {
			throw new LogRecordException("Unable to retrieve XML payload as string", e);
		}
	}

	public String getPayloadXmlString() {
		return this.payloadXmlString;
	}

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	public String getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(String httpStatus) {
		this.httpStatus = httpStatus;
	}

	public String getRemoteHostName() {
		return remoteHostName;
	}

	public void setRemoteHostName(String remoteHostName) {
		this.remoteHostName = remoteHostName;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "HttpTransportPipeEvent{" +
				"timestamp=" + timestamp +
				", payloadXmlString=" + payloadXmlString +
				", threadName='" + threadName + '\'' +
				", httpStatus='" + httpStatus + '\'' +
				", remoteHostName='" + remoteHostName + '\'' +
				", type=" + type +
				", httpHeaders=" + httpHeaders +
				'}';
	}
}
