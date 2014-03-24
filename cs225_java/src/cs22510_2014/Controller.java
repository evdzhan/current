package cs22510_2014;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.LinkedList;

public class Controller {
	protected GPSReader strm1, strm2; // stream 1 and stream 2
	protected LinkedList<GPSReader.Location> locs; // the place to store the
													// locations
	protected long latOffset;
	protected long lngOffset;
	private final static double MIL = 1000000.0;

	protected Controller(String file1, String file2) {
		strm1 = new GPSReader(file1);
		strm2 = new GPSReader(file2);
		locs = new LinkedList<>();
	}

	public boolean synchronizeTimes() {
		String data = "";
		while (!data.equals(GPSReader.GPS_TIME) && !data.equals(GPSReader.EOF)) {
			data = strm1.read();

		}
		if (data.equals(GPSReader.EOF)) {
			return false;
		}

		data = "";
		while (!data.equals(GPSReader.GPS_TIME) && !data.equals(GPSReader.EOF)) {
			data = strm2.read();
		}
		if (data.equals(GPSReader.EOF)) {
			return false;
		}

		int time = strm1.currTime.compareTo(strm2.currTime);

		if (time == -1) {
			do {
				data = strm1.read();
				if (data == GPSReader.EOF)
					return false;
				if (data.equals(GPSReader.GPS_TIME)) {
					time = strm1.currTime.compareTo(strm2.currTime);
				}

			} while (time != 0);

		} else if (time == 1) {
			do {
				data = strm2.read();
				if (data == GPSReader.EOF)
					return false;
				if (data.equals(GPSReader.GPS_TIME)) {
					time = strm1.currTime.compareTo(strm2.currTime);

				}
			} while (time != 0);

		}

		return true;
	}

	private void getoffset(GPSReader.Location one, GPSReader.Location two) {

		this.latOffset = (long) (one.latitude * MIL)
				- (long) (two.latitude * MIL);

		this.lngOffset = (long) (one.longitude * MIL)
				- (long) (two.longitude * MIL);

	}

	private void addoffset(GPSReader.Location goodFix, GPSReader.Location badFix) {

		badFix.latitude = Math.round(goodFix.latitude * MIL + this.latOffset)
				/ MIL;

		badFix.longitude = Math.round(goodFix.longitude * MIL + this.lngOffset)
				/ MIL;

	}

	protected void start() {
		this.synchronizeGPS();
		this.synchronizeTimes();
		while (true) {

			if (strm1.satelitesOK) {

				locs.add(strm1.currLoc);
				if (strm2.satelitesOK)
					this.getoffset(strm1.currLoc, strm2.currLoc);

				else
					this.addoffset(strm1.currLoc, strm2.currLoc);

			} else if (strm2.satelitesOK) {
				locs.add(strm2.currLoc);
				this.addoffset(strm2.currLoc, strm1.currLoc);

			}
			String lineRead = "";
			do {
				lineRead = strm1.read();
				if (lineRead.equals(GPSReader.EOF))
					return;

			} while (!lineRead.equals(GPSReader.GPS_TIME));
			lineRead = "";
			do {
				lineRead = strm2.read();
				if (lineRead.equals(GPSReader.EOF))
					return;

			} while (!lineRead.equals(GPSReader.GPS_TIME));

		}

	}

	private boolean synchronizeGPS() {
		String data = "";
		if (!strm1.satelitesOK) {
			while (!strm1.satelitesOK) {
				data = strm1.read();
				if (data == GPSReader.EOF)
					return false;
			}
		}
		if (!strm2.satelitesOK) {
			while (!strm2.satelitesOK) {
				data = strm2.read();

				if (data == GPSReader.EOF)
					return false;
			}
		}

		return true;
	}

	static void go() {

		String file1 = "/home/evdjoint/gps_1.dat";
		String file2 = "/home/evdjoint/gps_2.dat";

		Controller controller = new Controller(file1, file2);

		controller.synchronizeTimes();

		controller.start();

		PrintWriter pw = null;
		try {

			pw = new PrintWriter("data.gpx");
			pw.write("<?xml version=\"1.0\"?>\n"
					+ "<gpx\n"
					+ " version=\"1.0\"\n"
					+ " creator=\"Evdzhan Mustafa\"\n"
					+ " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n");

			for (GPSReader.Location l : controller.locs) {
				pw.write("<wpt lat=\"" + l.latitude + "\" lon=\"" + l.longitude
						+ "\">\n" + "<time>" + l.date.toString() + "</time>"
						+ "\n</wpt>\n");
			}

			pw.write("</gpx>");

		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		pw.close();

	}

}
