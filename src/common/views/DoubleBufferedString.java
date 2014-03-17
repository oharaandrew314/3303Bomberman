package common.views;

/**
 * A very simple Double Buffer for Strings.
 * Each buffer is simply a single String since the entire state is
 * represented as such.
 */
public class DoubleBufferedString {
	
	/**
	 * Represents the state of a buffer. Because of the simplicity of
	 * this double buffer, buffers are always either full or empty.
	 */
	private enum BufferState {
		Full, Empty
	}
	
	/**
	 * A simple struct-like class to hold the value and state of a buffer.
	 */
	private class Buffer {
		public String value = "";
		public BufferState state = BufferState.Empty;
	}
	
	private Buffer readBuffer = new Buffer();
	private Buffer writeBuffer = new Buffer();
	
	/**
	 * Writes a string to the writeBuffer. If the writeBuffer is full,
	 * the thread is blocked until the buffers are swapped.
	 * @param value The value to write
	 */
	public void write(String value) {
		synchronized(writeBuffer) {
			while (writeBuffer.state == BufferState.Full) { 
				try {
					writeBuffer.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
	
			writeBuffer.value = value;
			
			if (readBuffer.state == BufferState.Empty) {
				synchronized(readBuffer) {
					swapBuffers();
				}
			} else {
				writeBuffer.state = BufferState.Full;
			}
		}
	}
	
	/**
	 * Reads a string from the readBuffer. If the readBuffer is empty,
	 * the thread is blocked until the buffers are swapped.
	 * @return the contents of the readBuffer.
	 */
	public String read() {
		String value = null;
		synchronized(readBuffer) {
			while (readBuffer.state == BufferState.Empty){
				try {
					readBuffer.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			value = readBuffer.value;
			
			if (writeBuffer.state == BufferState.Full) {
				synchronized(writeBuffer) {
					swapBuffers();
				}
			} else {
				readBuffer.state = BufferState.Empty;
			}
		}
		
		return value;
	}
	

	
	/**
	 * Performs the actual buffer swap and notifies all threads that are waiting.
	 */
	private void swapBuffers() {
		readBuffer.value = writeBuffer.value;
		readBuffer.state = BufferState.Full;
		
		writeBuffer.value = "";
		writeBuffer.state = BufferState.Empty;
		
		readBuffer.notifyAll();
		writeBuffer.notifyAll();
	}
}
