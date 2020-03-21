import java.util.concurrent.TimeUnit;

public class Main {
	public static void main(String[] args) {
		
		//THIS IS ALL JUST ME TESTING
		CommunicationLayer c1 = new CommunicationLayer(3000, "127.0.0.1", 5000);
		CommunicationLayer c2 = new CommunicationLayer(5000, "127.0.0.1", 3000);
		
		c1.start();
		c2.start();
		
		c2.receive(new ReceiveCallback() {
			
			@Override
			public void onReceive(Frame frame) {
				// TODO Auto-generated method stub
				System.out.println(new String(frame.getData()));
				System.out.println(frame.getSeq());
			}
		});
		
		for(int i = 0; i<20; i++) {
			System.out.println(i);
			Frame f = new Frame();
			f.setData("Hi".getBytes());
			f.setAck(i);
			f.setSeq(i);
			c1.send(f);
		}
	}
}
