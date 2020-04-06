import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		String input = "";
		String message = "";
		SenderStopWaitProtocol s;
		ReceiverStopWaitProtocol r;

		System.out.println("Two Layer Communication System\n"); 
		while(!(input.equals("1") || input.equals("2"))){
			System.out.println("Are you the (1) Receiver or (2) Sender?");
			input = scanner.nextLine();
			if(!(input.equals("1") || input.equals("2"))){
				System.out.println("Invalid option. Try again.\n");
			}
		}

		if(input.equals("1")){
			String receiver_ip = "";
			System.out.println("*** RECEIVER TERMINAL ***\n");
			System.out.println("(Local: 127.0.0.1) Enter Receiver IP Address:");

			receiver_ip = scanner.nextLine();
			r = new ReceiverStopWaitProtocol(5000, receiver_ip, 3000);

			r.receive(new MessageCallback() {
				@Override
				public void onReceive(String message) {
					System.out.println("Received: "+ message);
				}
			});
		}else{
			String sender_ip = "";
			System.out.println("*** SENDER TERMINAL ***\n");
			System.out.println("(Local: 127.0.0.1) Enter Sender IP Address:");
			
			sender_ip = scanner.nextLine();
			s = new SenderStopWaitProtocol(3000, sender_ip, 5000);

			s.start();
			while(true){
				System.out.println("Write message: ");
				message = scanner.nextLine();
				s.enqueue(message);
			}

		}
		
	}
}
