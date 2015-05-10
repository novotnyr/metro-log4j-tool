package sk.upjs.ics.novotnyr.mlt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class ProloguingAndEpiloguingReader extends Reader {
	private Reader delegate;

	private StringBuilder prologBuffer = new StringBuilder();

	private StringBuilder epilogBuffer = new StringBuilder();

	public ProloguingAndEpiloguingReader(Reader delegate) {
		this(delegate, "", "");
	}

	public ProloguingAndEpiloguingReader(Reader delegate, CharSequence prolog, CharSequence epilog) {
		this.delegate = delegate;
		this.prologBuffer = new StringBuilder(prolog);
		this.epilogBuffer = new StringBuilder(epilog);
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		int bytesRead = exhaustStringBuffer(cbuf, off, len, prologBuffer);
		if(bytesRead >= 0 ) {
			return bytesRead;
		}
		bytesRead = delegate.read(cbuf, off, len);
		if(bytesRead >= 0) {
			return bytesRead;
		}
		return exhaustStringBuffer(cbuf, off, len, epilogBuffer);
	}

	private int exhaustStringBuffer(char[] cbuf, int off, int len, StringBuilder stringBuilder) {
		if(stringBuilder.length() == 0) {
			return -1;
		}
		for (int i = 0; i < len; i++) {
			cbuf[off + i] = stringBuilder.charAt(0);
			stringBuilder.deleteCharAt(0);
			if(stringBuilder.length() == 0) {
				return i + 1;
			}
		}
		return -1;
	}

	@Override
	public void close() throws IOException {
		prologBuffer.setLength(0);
		epilogBuffer.setLength(0);
		delegate.close();
	}

	public static void main(String[] args) throws IOException {
		BufferedReader reader = new BufferedReader(new ProloguingAndEpiloguingReader(new StringReader("")));
		while(true) {
			String line = reader.readLine();
			if(line == null) {
				break;
			}
			System.out.println(line);
		}
	}
}
