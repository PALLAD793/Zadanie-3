package tictactoe;

import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.Queue;

public class PTPProducer {

	public void sendQueueMessages(String character, int x, int y, int winner, boolean end) {
		ConnectionFactory connectionFactory = new com.sun.messaging.ConnectionFactory();

		try {
			((com.sun.messaging.ConnectionFactory) connectionFactory)
				.setProperty(com.sun.messaging.ConnectionConfiguration.imqAddressList, "localhost:7676/jms");
			JMSContext jmsContext = connectionFactory.createContext();
			JMSProducer jmsProducer = jmsContext.createProducer();
			Queue queue = new com.sun.messaging.Queue("ATJQueue");
	
			Message message = jmsContext.createTextMessage();

			try {
				message.setStringProperty("character", character);
				message.setIntProperty("X", x);
				message.setIntProperty("Y", y);
				message.setIntProperty("WIN", winner);
				message.setBooleanProperty("END", end);
				

			} catch (JMSException e) {
				e.printStackTrace();
			}

			jmsProducer.send(queue, message);

			jmsContext.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}

	}
}