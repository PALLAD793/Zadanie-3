package tictactoe;

import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.Queue;

public class PTPProducer {

	public void sendQueueMessages(String character, int x, int y) {
		ConnectionFactory connectionFactory = new com.sun.messaging.ConnectionFactory();

		try {
			((com.sun.messaging.ConnectionFactory) connectionFactory)
					.setProperty(com.sun.messaging.ConnectionConfiguration.imqAddressList, "localhost:7676/jms");
			JMSContext jmsContext = connectionFactory.createContext();
			JMSProducer jmsProducer = jmsContext.createProducer();
			Queue queue = new com.sun.messaging.Queue("ATJQueue");

			Message message = jmsContext.createTextMessage();

			try {
				//message.setJMSMessageID(character);
				message.setStringProperty("character", character);
				message.setIntProperty("X", x);
				message.setIntProperty("Y", y);
				System.out.println(message.toString());
				System.out.println("to wysłałem");
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