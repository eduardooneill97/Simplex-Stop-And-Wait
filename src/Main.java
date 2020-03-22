
public class Main {
	public static void main(String[] args) {
		
		//THIS IS ALL JUST ME TESTING
		SenderStopWaitProtocol s = new SenderStopWaitProtocol(3000, "127.0.0.1", 5000);
		ReceiverStopWaitProtocol r = new ReceiverStopWaitProtocol(5000, "127.0.0.1", 3000);
		
		s.start();
		
		for(int i = 0; i<20; i++) {
			s.enqueue("Message " + i + ".");
		}
	}
}
