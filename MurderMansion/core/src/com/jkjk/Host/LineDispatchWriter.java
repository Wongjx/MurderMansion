package com.jkjk.Host;

import java.io.IOException;
import java.io.Writer;

public class LineDispatchWriter extends Writer {
	private final LineConsumer consumer;
	private final StringBuilder buffer;

	public LineDispatchWriter(LineConsumer consumer) {
		this.consumer = consumer;
		this.buffer = new StringBuilder();
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		for (int i = off; i < off + len; i++) {
			char ch = cbuf[i];
			if (ch == '\r') {
				continue;
			}
			if (ch == '\n') {
				flushBuffer();
			} else {
				buffer.append(ch);
			}
		}
	}

	private void flushBuffer() throws IOException {
		if (buffer.length() == 0) {
			return;
		}
		consumer.accept(buffer.toString());
		buffer.setLength(0);
	}

	@Override
	public void flush() throws IOException {
		flushBuffer();
	}

	@Override
	public void close() throws IOException {
		flushBuffer();
	}

	public interface LineConsumer {
		void accept(String line) throws IOException;
	}
}
