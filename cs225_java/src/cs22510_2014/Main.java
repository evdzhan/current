package cs22510_2014;

public class Main {

	public static void main(String[] args) {

		String file1 = "/home/evdjoint/gps_1.dat";
		String file2 = "/home/evdjoint/gps_2.dat";

		Controller controller = new Controller(file1, file2);

		controller.synchronizeTimes();

		String line;
		do {
			line = controller.strm2.read();
			if (!controller.strm2.satelitesOK) {
				System.out.println(controller.strm2.currTime.toString());

			}
		} while (!line.equals("EOF"));
		System.out.println(controller.strm2.currTime.toString());

	}
}
