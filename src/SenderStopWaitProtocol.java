import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.LinkedBlockingQueue;
import javax.swing.Timer;

public class SenderStopWaitProtocol {
	
	private LinkedBlockingQueue<Frame> messageQueue;
	private Frame current; //last message sent
	private Frame acknowledgement;
	private Thread sendLoop;
	private int sequence = 0;
	private Object lock;
	private Boolean isFirstMessage = true;
	private CommunicationLayer c;
	private Timer t = new Timer(1000, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(acknowledgement == null) {
				//testing
				System.out.println("No acknowledgement received!");
				c.send(current);
				t.restart();
				t.start();
			}
		}
	});
	
	public SenderStopWaitProtocol(int source, String destAddress, int destPort) {
		messageQueue = new LinkedBlockingQueue<>();
		c = new CommunicationLayer(source, destAddress, destPort);
		lock = new Object();
		c.start();
		sendLoop = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					while(true) {
						synchronized (lock) {
							if(t.isRunning()) lock.wait();
							send();
						}
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		});
		c.receive(new ReceiveCallback() {
			@Override
			public void onReceive(Frame frame) {
				synchronized (lock) {
					acknowledgement = frame;
					//for testing purposes!
					System.out.println("Acknowledgement of message: " + acknowledgement.getAck());
					t.stop();
					lock.notify();
				}
			}
		});
	}
	
	public void enqueue(String message) {
		Frame f = new Frame();
		f.setData(message.getBytes());
		messageQueue.add(f);
	}
	
	public void start() {
		sendLoop.start();
	}
	
	private void send() {
		try {
			current = messageQueue.take();
			current.setSeq(sequence);
			if(isFirstMessage) {
				current.setAck(-1);
				isFirstMessage = false;
			}
			sequence = (sequence == 0) ? 1 : 0;
			acknowledgement = null;
			System.out.println("Message sent!");
			c.send(current);
			t.start();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
