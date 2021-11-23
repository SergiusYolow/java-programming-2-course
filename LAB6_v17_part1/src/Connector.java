import java.io.*;

public class Connector {

	private String filename;

	public Connector( String filename ) {
		this.filename = filename;
	}

	public void write( Route[] band) throws IOException {
		FileOutputStream fos = new FileOutputStream (filename);
		try ( ObjectOutputStream oos = new ObjectOutputStream( fos )) {
			oos.writeInt( band.length );
			for ( int i = 0; i < band.length; i++) {
				oos.writeObject( band[i] );		
			}
			oos.flush();
		}
	}

	public Route[] read() throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(filename);
		try ( ObjectInputStream oin = new ObjectInputStream(fis)) {
			int length = oin.readInt();
			Route[] result = new Route[length];
			for ( int i = 0; i < length; i++ ) {
				result[i] = (Route) oin.readObject();
			}
			return result;	
		}
	}

}
