
public class ReceiverStopWaitProtocol {
	
	private Frame acknowledgement;
	private CommunicationLayer c;
	
	public ReceiverStopWaitProtocol(int source, String destAddress, int destPort) {
		c = new CommunicationLayer(source, destAddress, destPort);
		c.start();
		c.receive(new ReceiveCallback() {
			@Override
			public void onReceive(Frame frame) {
				if(acknowledgement == null || frame.getSeq() != acknowledgement.getAck()) {
					System.out.println(new String(frame.getData()));
					send(frame.getSeq());
				}
				//otherwise do nothing
			}
		});
	}
	
	private void send(int seq) {
		acknowledgement = new Frame();
		acknowledgement.setAck(seq);
		c.send(acknowledgement);
	}
}
