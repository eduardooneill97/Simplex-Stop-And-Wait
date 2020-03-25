
public class ReceiverStopWaitProtocol {
	
	private Frame acknowledgement;
	private CommunicationLayer c;
	private MessageCallback callback;
	
	public ReceiverStopWaitProtocol(int source, String destAddress, int destPort) {
		c = new CommunicationLayer(source, destAddress, destPort);
		c.setSendsAck(true);
		c.start();
		c.receive(new ReceiveCallback() {
			@Override
			public void onReceive(Frame frame) {
				System.out.println("Frame received sequence: " + frame.getSeq());
				if(acknowledgement == null || frame.getSeq() != acknowledgement.getAck() || frame.getAck() == -1) {
//					System.out.println(new String(frame.getData()));
					send(frame.getSeq());
					System.out.println("receiver: acknowledgement sent!");
					if(callback != null) 
						callback.onReceive(new String(frame.getData()));
				}
				// do nothing
			}
		});
	}
	
	public void receive(MessageCallback callback) {
		this.callback = callback;
	}
	
	private void send(int seq) {
		acknowledgement = new Frame();
		acknowledgement.setAck(seq);
		acknowledgement.setSeq(-1);
		c.send(acknowledgement);
	}
}
