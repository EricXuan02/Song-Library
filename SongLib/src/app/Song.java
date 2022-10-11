//Eric Xuan (ejx2) and Michael Bazarsky (mab777)

package app;

public class Song implements Comparable<Song>{
	String name;
	String artist;
	String album;
	String year;
	
	public Song(String name, String artist, String album, String year) {
		this.name = name;
		this.artist = artist;
		if (album != null)
			this.album = album;
		this.year = year;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}
	
//	public boolean isEqual(Song other) {
//		return this.name.equalsIgnoreCase(other.getName()) &&
//				this.artist.equalsIgnoreCase(other.getArtist());
//	}
	
	public void setValues(String name, String artist, String album, String year) {
		this.name = name;
		this.artist = artist;
		this.album = album;
		this.year = year;
	}
	
	public String toString() {
		return this.name + "\n" + this.artist;
	}

	@Override
	public int compareTo(Song s) {
		if (this.name.toLowerCase().compareTo(s.name.toLowerCase()) == 0) {
			return this.artist.toLowerCase().compareTo(s.name.toLowerCase());
		}
		return this.name.toLowerCase().compareTo(s.name.toLowerCase());
	}
	
	
}
