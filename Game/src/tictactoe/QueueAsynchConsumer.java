package tictactoe;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;

public class QueueAsynchConsumer implements MessageListener {

	private Controller controller;

	public QueueAsynchConsumer(Controller con) {
		controller = con;
	}

	@Override
	public void onMessage(Message message) {

		if (message.equals(null)) {
			System.out.println("null");
			return;
		} else
			try {
				if(message.getIntProperty("WIN") != 0 ) {
					Platform.runLater(() -> {
						controller.lockButtons();
						String w;
						try {
							w = (message.getIntProperty("WIN") == 1 ? "O" : "X");
							controller.getInfoLabel().setText("WYGRAŁ " + w);
						} catch (JMSException e) {
							e.printStackTrace();
						}
						controller.getInfoLabel().setPadding(new Insets(0, 0, 0, 90));
						controller.getInfoLabel().setTextFill(Color.RED);
					});
				}
				else if(message.getBooleanProperty("END") != false) {
					Platform.runLater(() -> {
						controller.lockButtons();
						controller.getInfoLabel().setText("REMIS");
						controller.getInfoLabel().setPadding(new Insets(0, 0, 0, 100));
						controller.getInfoLabel().setTextFill(Color.BLUE);
					});
				}
				else {
					Platform.runLater(() -> {
						try {
							controller.fillOpponentsMove(message.getStringProperty("character"), message.getIntProperty("X"),
									message.getIntProperty("Y"));
							controller.setTurn(!controller.getTurn());
							controller.getInfoLabel().setText(controller.infoLabelFill());
							controller.unlockButtons();
						} catch (JMSException e) {
							e.printStackTrace();
						}
					});

				}
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}
