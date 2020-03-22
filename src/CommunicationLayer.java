import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;

public class CommunicationLayer implements Serializable{
	
	private Object sendLock;
	private byte[] buffer;
	private Thread sender;
	private Thread receiver;
	private ReceiveCallback callback;
	private DatagramSocket socket;
	private int sourcePort;
	private String destinationAddress;
	private int destinationPort;
	private Random rand;

	public CommunicationLayer(int source, String destAddress, int destPort) {
		this.sourcePort = source;
		this.destinationAddress = destAddress;
		this.destinationPort = destPort;
		sendLock = new Object();
		try {
			socket = new DatagramSocket(sourcePort);
			
			sender = new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						while(true) {
							synchronized (sendLock) {
								
								if(buffer == null) sendLock.wait();
								//TODO ADD HERE THE UNRELIABILITY CODE
								DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(destinationAddress), destinationPort);
								if(rand.nextDouble() > .2) {
									if (rand.nextDouble() > .1) {
										socket.send(packet);
									}
									socket.send(packet);
								}
								// else { doNothing(); }
								buffer = null;
								sendLock.notify();
							}
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			
			receiver = new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					while(true) {
						try {
							DatagramPacket response = new DatagramPacket(new byte[512], 512);
							socket.receive(response);//This is blocking until a packet is received.
							byte[] frame = response.getData();
							callback.onReceive(deserializeFrame(frame));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			});
			
		} catch (SocketException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	}
	
	public void start() {
		sender.start();
		receiver.start();
	}
	
	public void send(Frame frame) {
		synchronized (sendLock) {
			try {
				if(buffer != null) sendLock.wait();
				buffer = serializeFrame(frame);
				sendLock.notify();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void receive(ReceiveCallback callback) {
		this.callback = callback;
	}
	
	private byte[] serializeFrame(Frame frame) {
		try {
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(byteStream);
			
			out.writeObject(frame);
			out.flush();
			byte[] res = byteStream.toByteArray();
			out.close();
			
			return res;
		} catch(IOException e) {
			return null;
		}
	}
	
	private Frame deserializeFrame(byte[] frame) {
		try {
			ByteArrayInputStream byteStream = new ByteArrayInputStream(frame);
			ObjectInputStream in = new ObjectInputStream(byteStream);
			
			Frame res = (Frame) in.readObject();
			in.close();
			
			return res;
		} catch(IOException e) {
			return null;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

}
