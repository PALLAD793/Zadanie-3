package tictactoe;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class QueueAsynchConsumer implements MessageListener {

	private Controller controller;

	public QueueAsynchConsumer(Controller con) {
		controller = con;
	}

	@Override
	public void onMessage(Message message) {

		// TextMessage textMessage = (TextMessage) message;

		// controller.fillOpponentsMove(sign, x, y);
		try {
			//System.out.println(message.getStringProperty("character"));
			//System.out.println(message.getIntProperty("X"));
			System.out.println(message.toString());
			controller.fillOpponentsMove(message.getStringProperty("character"), message.getIntProperty("X"),
					message.getIntProperty("Y"));
			controller.setTurn(!controller.getTurn());
			controller.getInfoLabel().setText(controller.infoLabelFill());
			controller.unlockButtons();
		} catch (JMSException e) {
			e.printStackTrace();
		}

	}
}
