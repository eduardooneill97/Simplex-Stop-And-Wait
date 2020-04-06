
public class ReceiverStopWaitProtocol {
	
	private Frame acknowledgement;
	private CommunicationLayer c;
	private MessageCallback callback;
	private boolean wasAcknowledged;
	
	public ReceiverStopWaitProtocol(int source, String destAddress, int destPort) {
		c = new CommunicationLayer(source, destAddress, destPort);
		c.start();
		wasAcknowledged = false;
		c.receive(new ReceiveCallback() {
			@Override
			public void onReceive(Frame frame) {
				if(acknowledgement == null || frame.getSeq() != acknowledgement.getAck()) {
					send(frame.getSeq());
					if(callback != null) 
						callback.onReceive(new String(frame.getData()));
				} else {
					send(frame.getSeq());
				}
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
