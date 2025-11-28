package jms.client;

import car.CarServer;
import jms.command.SerializableCommand;
import jms.command.SerializableReturn;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQObjectMessage;

import javax.jms.*;
import java.util.Random;

/**
 * @author : Alex
 * @created : 07.05.2021, пятница
 **/
public class JmsSimpleClient {
    public static String url = "tcp://185.188.181.184:8080";
    public static String commandChannel = "COMMANDS";
    public static String retChannel = "RETS";




    public static void main(String[] args) throws JMSException {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        Connection connection = connectionFactory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Topic commands = session.createTopic(commandChannel);

        MessageProducer producer = session.createProducer(commands);
        connection.start();

        Topic returns = session.createTopic(retChannel);
        MessageConsumer consumer = session.createConsumer(returns);

        Random random = new Random();

        int carIndex  = random.nextInt(Integer.MAX_VALUE);
        SerializableCommand command = new SerializableCommand(carIndex, "CREATECAR"," ");
        SerializableReturn ret = executeCommand(command, session, producer, consumer);
        carIndex = Integer.valueOf(ret.ret.toString());

        command = new SerializableCommand(carIndex, "SETNAME","Mike");
        ret = executeCommand(command, session, producer, consumer);

        //command = new SerializableCommand(carIndex, "DOWN","8");
        //ret = executeCommand(command, session, producer, consumer);

        CarServer.Direction direction = CarServer.Direction.DOWN;

        while(true){
            command = new SerializableCommand(carIndex, direction.name(), "1");
            ret = executeCommand(command, session, producer, consumer);
            if(!((Boolean)ret.ret)){
            }
        }

    }

    static SerializableReturn executeCommand(SerializableCommand command, Session session, MessageProducer producer, MessageConsumer consumer) throws JMSException{
        Message message = session.createObjectMessage(command);
        producer.send(message);

        SerializableReturn ret = null;
        do {
            message = consumer.receive(10000);
            if (message instanceof ObjectMessage) {
                //fix security issue
                ActiveMQObjectMessage mapMessage = (ActiveMQObjectMessage) message;
                mapMessage.setTrustAllPackages(true);
                //end fix security issue

                Object o = ((ObjectMessage) message).getObject();
                ret = (SerializableReturn) o;
            }
        }while (ret.carIndex!=command.carIndex);

        System.out.println("ret="+ret);

        return ret;

    }
}
