//Eric Xuan (ejx2) and Michael Bazarsky (mab777)

package view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import app.Song;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SceneController {

	private ObservableList<Song> obsList;
	@FXML ListView<Song> listView;

	@FXML TextField detailName;
	@FXML TextField detailArtist;
	@FXML TextField detailAlbum;	
	@FXML TextField detailYear;
	private TextField[] detailFields;
	private Song currentSong;

	@FXML Button currentSongEdit;
	@FXML Button currentSongDelete;

	@FXML TextField addName;
	@FXML TextField addArtist;
	@FXML TextField addAlbum;
	@FXML TextField addYear;
	private TextField[] addFields;

	@FXML Button newSongAdd;
	@FXML Button newSongClear;

	public void start(Stage mainStage) {
		this.detailFields = new TextField[] {detailName, detailArtist, detailAlbum, detailYear};
		this.addFields = new TextField[] {addName, addArtist, addAlbum, addYear};

		// create an ObservableList 
		// from an ArrayList
		generateListView(mainStage);
		listView.setItems(obsList);

		// set listener for the items
		listView
		.getSelectionModel()
		.selectedIndexProperty()
		.addListener(
				(obs, oldVal, newVal) -> {
					setCurrentSong(mainStage);
				});

		//Set all the button actions
		setCurrentSongEditAction(mainStage);
		setCurrentSongDeleteAction(mainStage);

		setNewSongAddAction(mainStage);
		setNewSongClearAction(mainStage);

		//Set all the visuals
		// select the first item
		listView.getSelectionModel().select(0);
		setCurrentSong(mainStage);
	}

	private void setEditing() {
		for (TextField detail : detailFields) {
			detail.setEditable(true);
			detail.setStyle("");
		}
	}
	
	private void setNotEditing() {
		for (TextField detail : detailFields) {
			detail.setEditable(false);
			detail.setStyle("-fx-background-color: transparent");
		}
	}

	private void setCurrentSongEditAction(Stage mainStage) {
		EventHandler<ActionEvent> currentSongEditEvent = new EventHandler<ActionEvent> () {
			public void handle(ActionEvent e) {
				if (currentSongEdit.getText().equals("Edit")) {
					if (currentSong == null) {
						return;
					}
					currentSongEdit.setText("Save");
					System.out.println("current song: " + currentSong + "\n");
					setEditing();
				}
				else {
					String newName = detailName.getText();
					String newArtist = detailArtist.getText();
					String newAlbum = detailAlbum.getText();
					String newYear = detailYear.getText();
					
					if (revealInvalidValues(mainStage, newName, newArtist, newAlbum, newYear, true, detailFields)) {
						return;
					}
					
					if (confirmEditAlert(mainStage).equals("Cancel")) return;
					
					//Change the values of the current song
					currentSong.setValues(newName, newArtist, newAlbum, newYear);
					
					//Set another pointer to the song
					Song currentEditingSong = currentSong;
					
					//Remove the song, this sets another value into currentSong
					obsList.remove(currentSong);
					
					//Insert the old song
					insertSong(mainStage, currentEditingSong);
					
					currentSongEdit.setText("Edit");
					setNotEditing();
				}
			}
		};
		
		currentSongEdit.setOnAction(currentSongEditEvent);
	}

	private String confirmEditAlert(Stage mainStage) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.initOwner(mainStage);
		alert.setTitle("Are you sure?");
		alert.setHeaderText("Are you sure you want to save these changes?");
		
		alert.showAndWait();
		
		return alert.getResult().getText();
	}
	
	private String confirmDeleteAlert(Stage mainStage) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.initOwner(mainStage);
		alert.setTitle("Are you sure?");
		alert.setHeaderText("Are you sure you want to delete this entry?");

//		alert.setContentText("Are you sure you want to delete this entry?");

		alert.showAndWait();

		return alert.getResult().getText();
	}
	
	private void deleteSong(Stage mainStage) {
		Song currSelection = listView.getSelectionModel().getSelectedItem();
		
		String confirmation = confirmDeleteAlert(mainStage);
		
		if (confirmation.equals("Cancel")) return;
		
		int currIndex = listView.getSelectionModel().getSelectedIndex();
		obsList.remove(currSelection);
		listView.getSelectionModel().select(currIndex);
		setCurrentSong(mainStage);
		writeToFile();
		
		currentSongEdit.setText("Edit");
		setNotEditing();
	}
	
	private void setCurrentSongDeleteAction(Stage mainStage) {
		EventHandler<ActionEvent> currentSongDeleteEvent = new EventHandler<ActionEvent> () {
			public void handle(ActionEvent e) {
				if (currentSong != null) deleteSong(mainStage);
			}
		};

		currentSongDelete.setOnAction(currentSongDeleteEvent);
	}

	private void clearAddFields() {
		for (TextField field : addFields) {
			field.clear();
			field.setStyle("");
		}
	}

	private void invalidInputAlert(Stage mainStage, boolean name, boolean artist, boolean album, boolean year, boolean song) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.initOwner(mainStage);
		alert.setTitle("Incorrect input(s)!");
		alert.setHeaderText("Please ensure you input all fields correctly.");

		String content = "Missing/Invalid value(s):\n";
		content += (name) ? "Song name: Must not be empty!\n" : "";
		content += (artist) ? "Song artist: Must not be empty!\n" : "";
		content += (album) ? "Album name\n" : "";
		content += (year) ? "Release year: If included, must be a positive integer!\n" : "";
		content += (song) ? "Name and artist: There is already a song in the list with the same name and artist!\n" : "";

		alert.setContentText(content);

		alert.showAndWait();
	}
	
	private boolean isInvalidName(String name) {return (name.equals("") || name.indexOf('|') >= 0);}
	private boolean isInvalidArtist(String artist) {return (artist.equals("") || artist.indexOf('|') >= 0);}
	private boolean isInvalidAlbum(String album) {return album.indexOf('|') >= 0;}
	private boolean isInvalidYear(String year) {
		if (year.equals("")) return false;
		//Make sure the year is parseable as int
		try {
			int iYear = Integer.parseInt(year);
			return iYear < 1; //Return whether year is non-positive or not
		}
		catch (Exception e) {return true;}
	}
	private boolean isInvalidSong(String name, String artist, boolean isEditing) {	//checks if the name and artist already exist
		//if not editing, we have to look at everything
		if (!isEditing) {
			for (int i = 0; i < obsList.size(); i++) {
				if (obsList.get(i).getName().equals(name) && obsList.get(i).getArtist().equals(artist)) {
					return true;
				}
			}
		}
		else {
			for (int i = 0; i < obsList.size(); i++) {
				if (obsList.get(i) == currentSong) {
					continue;
				}
				if (obsList.get(i).getName().equals(name) && obsList.get(i).getArtist().equals(artist)) {
					return true;
				}
			}
		}
		return false;
	}
	
	//Returns true if any values were invalid, false if all values OK
	private boolean revealInvalidValues(Stage mainStage, String newName, String newArtist, String newAlbum, String newYear, boolean isEditing, TextField[] fields) {
		for (TextField field : fields) field.setStyle("");
		boolean invalidName = false, invalidArtist = false, invalidAlbum = false, invalidYear = false, invalidSong = false;
		
		if (isInvalidName(newName)) {
			invalidName = true;
			fields[0].setStyle("-fx-border-color:red");
		}
		if (isInvalidArtist(newArtist)) {
			invalidArtist = true;
			fields[1].setStyle("-fx-border-color:red");
		}
		if (isInvalidAlbum(newAlbum)) {
			invalidAlbum = true;
			fields[2].setStyle("-fx-border-color:red");
		}
		if (isInvalidYear(newYear)) {
			invalidYear = true;
			fields[3].setStyle("-fx-border-color:red");
		}
		
		if (isInvalidSong(newName, newArtist, isEditing)) {
			invalidSong = true;
			fields[0].setStyle("-fx-border-color:red");
			fields[1].setStyle("-fx-border-color:red");
		}
		
		if (invalidName || invalidArtist || invalidAlbum || invalidYear || invalidSong) {
			invalidInputAlert(mainStage, invalidName, invalidArtist, invalidAlbum, invalidYear, invalidSong);
			return true;
		}
		return false;
	}
	
	private void setNewSongAddAction(Stage mainStage) {
		EventHandler<ActionEvent> newSongAddEvent = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				String newName = addName.getText();
				String newArtist = addArtist.getText();
				String newAlbum = addAlbum.getText();
				String newYear = addYear.getText();
				
				if (revealInvalidValues(mainStage, newName, newArtist, newAlbum, newYear, false, addFields)) return;
				
				//Set empty values to spaces for csv parsing
				if (newAlbum.equals("")) newAlbum = " ";
				if (newYear.equals("")) newYear = " ";
				
				Song newSong = new Song(newName, newArtist, newAlbum, newYear);
				
				//find where to insert the song
				if (confirmAddAlert(mainStage).equals("Cancel")) return;
				insertSong(mainStage, newSong);
			}
		};
		
		newSongAdd.setOnAction(newSongAddEvent);
	}
	
	private void invalidInsertAlert(Stage mainStage) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.initOwner(mainStage);
		alert.setTitle("Incorrect input(s)!");
//		alert.setHeaderText("");

		String content = "There is already a song with the same name and artist!";

		alert.setContentText(content);

		alert.showAndWait();
		
	}
	
	private String confirmAddAlert(Stage mainStage) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.initOwner(mainStage);
		alert.setTitle("Are you sure?");
		alert.setHeaderText("Are you sure you want to add this entry?");

//		alert.setContentText("Are you sure you want to delete this entry?");

		alert.showAndWait();

		return alert.getResult().getText();
	}
	
	private void insertSong(Stage mainStage, Song newSong) {
		if (obsList.size() == 0) {
			obsList.add(newSong);
			listView.getSelectionModel().select(0);
			writeToFile();
			return;
		}
		for (int i = 0; i < obsList.size(); i++) {
//			System.out.println(newSong);
			if (newSong.compareTo(obsList.get(i)) == 0) {
				invalidInsertAlert(mainStage);
				System.out.println("0");
				break;
			}
			if (newSong.compareTo(obsList.get(i)) < 0) {				
				obsList.add(i, newSong);
				listView.getSelectionModel().select(i);
				writeToFile();
				System.out.println("1, selected: " + i);
				clearAddFields();
				break;
			}
			if (i == obsList.size() - 1) {				
				obsList.add(newSong);
				listView.getSelectionModel().select(i + 1);
				writeToFile();
				System.out.println("2, selected: " + i);
				clearAddFields();
				break;
			}
		}
	}
	
	private void writeToFile() {
		File file = new File("songs.txt");
		try {
			file.createNewFile();
			FileWriter fileWriter = new FileWriter("songs.txt");
			
			for (Song song : obsList) {
				fileWriter.write(song.getName() +
						"|" + song.getArtist() +
						"|" + ((song.getAlbum().length() == 0) ? " " : song.getAlbum()) +
						"|" + ((song.getYear().length() == 0) ? " " : song.getYear()) + "\n");
			}
			
			fileWriter.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void setNewSongClearAction(Stage mainStage) {
		EventHandler<ActionEvent> newSongClearEvent = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				clearAddFields();
			}
		};
		
		newSongClear.setOnAction(newSongClearEvent);
	}
	
	//Generate an ObservableList of Song objects
	private ObservableList<Song> generateListView(Stage mainStage) {
		ArrayList<Song> al = new ArrayList<Song>();
		
		File file = new File ("songs.txt");
		try {
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				String[] songData = scanner.nextLine().split("\\|");
//				for (String data : songData) {
//					System.out.print(data + " ");
//				}
//				System.out.println();
				String name = songData[0];
				String artist = songData[1];
				String album = songData[2];
				String year = songData[3];
				Song nextSong = new Song(name, artist, album, year);
				al.add(nextSong);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		//Not sure if this needs to be a global private variable
		obsList = FXCollections.observableArrayList(al);
		return obsList;
	}
	
	public void buttonClick(ActionEvent e) {
		Button b = (Button) e.getSource();
		System.out.println(b.getId());
		addArtist.setText("helo");
		if (b == newSongAdd) {
			isAddValid();
		}
	}
	
	public boolean isAddValid() {
		System.out.println("thing: " + addName.getText());
		return true;
	}

	//Set the details section to be the currently selected song
	private void setCurrentSong(Stage mainStage) {
		this.currentSong = listView.getSelectionModel().getSelectedItem();
		if (currentSong != null) {
			detailName.setText(currentSong.getName());
			detailArtist.setText(currentSong.getArtist());
			detailAlbum.setText(currentSong.getAlbum().equals(" ") ? "" : currentSong.getAlbum());
			detailYear.setText(currentSong.getYear().equals(" ") ? "" : currentSong.getYear());
		}
		else {
			for (TextField field : detailFields) {
				field.setText("No Song Selected");
			}
		}
	}
}
