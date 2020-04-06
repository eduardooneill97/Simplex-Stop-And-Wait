import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		int input = 0;
		String message = "";
		SenderStopWaitProtocol s = new SenderStopWaitProtocol(3000, "127.0.0.1", 5000);
		ReceiverStopWaitProtocol r = new ReceiverStopWaitProtocol(5000, "127.0.0.1", 3000);

		System.out.println("Two Layer Communication System\n"); 
		while(input == 0 || input != 1 || input != 2){
			System.out.println("Are you the (1) Receiver or (2) Sender?");
			input = Integer.parseInt(scanner.nextLine());
			if(input != 1 || input != 2){
				System.out.println("Invalid argument. Try again.\n");
			}
		}

		if(input == 1){
			System.out.println("*** RECEIVER TERMINAL ***\n");
			while(true){
				r.receive(new MessageCallback() {
			
					@Override
					public void onReceive(String message) {
						System.out.println("Received: "+ message);
					}
				});
			}
		}else{
			s.start();
			
			System.out.println("*** SENDER TERMINAL ***\n");
			while(true){
				System.out.println("Write message: ");
				message = scanner.nextLine();
				s.enqueue(message);
			}

		}
		
	}
}
