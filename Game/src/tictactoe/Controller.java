package tictactoe;

import java.util.Optional;

import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.management.remote.JMXServiceURL;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Controller {

	private Button[][] buttonTable;
	private Label infoLabel;
	private Label userCharacterLabel;
	private Boolean turn;
	private int character;
	private int winner;
	private boolean endGame = false;
	private PTPProducer producer;

	public Controller() {
		initButtons();
		turn = new Boolean(true);

		
		startWindow();
		receiveQueueMessageAsynch();
		producer = new PTPProducer();
	}

	private void startWindow() {
		Alert alert = new Alert(AlertType.CONFIRMATION);

		alert.setHeaderText("Do you want to start new game or join your friend?");
		alert.setContentText("Choose your option:");

		ButtonType buttonCreate = new ButtonType("Create game");
		ButtonType buttonJoin = new ButtonType("Join game");

		alert.getButtonTypes().setAll(buttonCreate, buttonJoin);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == buttonCreate) {
			character = 1;
			turn = true;
		} else if (result.get() == buttonJoin) {
			character = 2;
			turn = false;
		}

		infoLabel = new Label(infoLabelFill());
		userCharacterLabel = new Label(userCharacterLabelFill());
	}

	public int getCharacter() {
		return character;
	}

	public void setCharacter(int character) {
		this.character = character;
	}

	public boolean getTurn() {
		return turn;
	}

	public void setTurn(boolean turn) {
		this.turn = turn;
	}

	public Label getInfoLabel() {
		return infoLabel;
	}

	public Scene initWindow() {

		HBox hboxFirstRow = new HBox();
		HBox hboxSecondRow = new HBox();
		HBox hboxThirdRow = new HBox();

		// userCharacterLabel = new Label("Twój znacznik to: " + getCharacter());
		userCharacterLabel.setPadding(new Insets(5, 5, 5, 60));

		if (winner == 0)
			// infoLabel = new Label(infoLabelFill());
			infoLabel.setPadding(new Insets(0, 0, 5, 60));

		hboxFirstRow.getChildren().addAll(buttonTable[0][0], buttonTable[0][1], buttonTable[0][2]);
		hboxSecondRow.getChildren().addAll(buttonTable[1][0], buttonTable[1][1], buttonTable[1][2]);
		hboxThirdRow.getChildren().addAll(buttonTable[2][0], buttonTable[2][1], buttonTable[2][2]);

		VBox vbox = new VBox();
		vbox.setPadding(new Insets(0, 0, 0, 22));
		vbox.getChildren().addAll(userCharacterLabel, infoLabel, hboxFirstRow, hboxSecondRow, hboxThirdRow);

		Scene scene = new Scene(vbox, 300, 320);

		if (turn.booleanValue() == false)
			lockButtons();

		return scene;
	}

	private void initButtons() {
		buttonTable = new Button[3][3];
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				buttonTable[i][j] = new Button();
				buttonTable[i][j].setPrefSize(85, 85);
				buttonTable[i][j].setOnAction(e -> buttonChange(e));
			}
		}
	}

	private String getPlayerCharacter() {
		if (character == 1)
			return "O";
		else if (character == 2)
			return "X";
		else
			return "";
	}

	private String getOpponentCharacter() {
		if (character == 1)
			return "X";
		else if (character == 2)
			return "O";
		else
			return "";
	}

	public String infoLabelFill() {
		if (turn.equals(true))
			return "Twoja tura";
		else
			return "Tura przeciwnika";
	}

	private String userCharacterLabelFill() {
		return "Twój znacznik to:" + getPlayerCharacter();
	}

	private void buttonChange(ActionEvent e) {
		boolean win = false;
		boolean loose = false;
		int x = -1, y = -1;
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				if (e.getSource() == buttonTable[i][j]) {
					buttonTable[i][j].setText(getPlayerCharacter());
					buttonTable[i][j].setFont(new Font(40));
					win = checkWin(i, j);
					loose = checkLoose();
					x = i;
					y = j;
					buttonTable[i][j].setDisable(true);
					break;
				}
			}
		}
		if (win) {
			winner = (turn.equals(true) ? 1 : 2);
			winInfo();
		} else if (loose) {
			endGame = true;
			looseInfo();
		}

		if (winner == 0 && endGame == false)
			infoLabel.setText(infoLabelFill());

		//System.out.println(getPlayerCharacter());
	//	System.out.println(x);
		producer.sendQueueMessages(getPlayerCharacter(), x, y);

		lockButtons();
		turn = !turn;
		infoLabel.setText(infoLabelFill());
		// character = (character == 1 ? 2 : 1);
	}

	private boolean checkWin(int i, int j) {
		int count = 0;
		String charac = getPlayerCharacter();
		for (int x = 0; x < 3; ++x) {
			if (buttonTable[i][x].getText().equals(charac))
				++count;
		}
		if (count == 3)
			return true;
		else
			count = 0;
		for (int y = 0; y < 3; ++y) {
			if (buttonTable[y][j].getText().equals(charac))
				++count;
		}
		if (count == 3)
			return true;
		else if ((j == 1 && (i == 0 || i == 2)) || (i == 1 && (j == 0 || j == 2)))
			return false;
		else {
			if (buttonTable[1][1].getText().equals(charac)) {
				if (buttonTable[0][0].getText().equals(charac) && buttonTable[2][2].getText().equals(charac))
					return true;
				if (buttonTable[2][0].getText().equals(charac) && buttonTable[0][2].getText().equals(charac))
					return true;
			}
			return false;
		}

	}

	private boolean checkLoose() {
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				if (buttonTable[i][j].getText().equals(""))
					return false;
			}
		}
		return true;
	}

	private void winInfo() {
		lockButtons();
		String w = (winner == 1 ? "O" : "X");
		infoLabel.setText("WYGRAŁ " + w);
		infoLabel.setPadding(new Insets(0, 0, 0, 90));
		infoLabel.setTextFill(Color.RED);

	}

	private void looseInfo() {
		lockButtons();
		infoLabel.setText("REMIS");
		infoLabel.setPadding(new Insets(0, 0, 0, 100));
		infoLabel.setTextFill(Color.BLUE);
	}

	private void lockButtons() {
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				buttonTable[i][j].setDisable(true);
			}
		}
	}

	public void unlockButtons() {
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				// System.out.println(buttonTable[i][j].getText());
				if (!buttonTable[i][j].getText().equals(null))
					buttonTable[i][j].setDisable(false);
			}
		}
	}

	public void fillOpponentsMove(String sign, Integer x, Integer y) {
		System.out.println(x.intValue() + " " + y.intValue());
		buttonTable[x.intValue()][y.intValue()].setText(sign);
		buttonTable[x.intValue()][y.intValue()].setFont(new Font(40));
	}

	public void receiveQueueMessageAsynch() {

		ConnectionFactory connectionFactory = new com.sun.messaging.ConnectionFactory();
		JMSContext jmsContext = connectionFactory.createContext();
		try {
			((com.sun.messaging.ConnectionFactory) connectionFactory)
					.setProperty(com.sun.messaging.ConnectionConfiguration.imqAddressList, "localhost:7676/jms");

			Queue queue = new com.sun.messaging.Queue("ATJQueue");
			JMSConsumer jmsConsumer = jmsContext.createConsumer(queue);

			System.out.println(character);
			if (character == 1)
				jmsConsumer = jmsContext.createConsumer(queue, "character = 'X'");
			else if (character == 2)
				jmsConsumer = jmsContext.createConsumer(queue, "character = 'O'");

			jmsConsumer.setMessageListener(new QueueAsynchConsumer(this));

			// jmsConsumer.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
		// jmsContext.close();
	}

}