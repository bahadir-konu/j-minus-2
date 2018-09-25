package program.message;

/**
 * Author: Bahadir Konu (bah.konu@gmail.com)
 */
public interface MessageProducer {

    public void addMessageListener(MessageListener listener);

    public void removeMessageListener(MessageListener listener);

    public void sendMessage(Message message);
}
