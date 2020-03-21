import java.util.concurrent.TimeUnit;

public class Main {
	public static void main(String[] args) {
		
		//THIS IS ALL JUST ME TESTING
		CommunicationLayer c1 = new CommunicationLayer(3000, "127.0.0.1", 5000);
		CommunicationLayer c2 = new CommunicationLayer(5000, "127.0.0.1", 3000);
		
		c1.start();
		c2.start();
		
		try {
			TimeUnit.SECONDS.sleep(2);
			c2.receive(new ReceiveCallback() {
				
				@Override
				public void onReceive(Frame frame) {
					// TODO Auto-generated method stub
					System.out.println(new String(frame.getData()));
				}
			});
			
			Frame f = new Frame();
			f.setData("Hi".getBytes());
			f.setAck(0);
			f.setSeq(0);
			c1.send(f);
			c1.send(f);
			c1.send(f);
			c1.send(f);
			c1.send(f);
			c1.send(f);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
