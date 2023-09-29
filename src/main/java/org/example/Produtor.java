package org.example;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Produtor {

    public static void main(String[] args) throws Exception {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        try (
                Connection connection = connectionFactory.newConnection();
                Channel canal = connection.createChannel();
                Channel canalDois = connection.createChannel();) {
            AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                    .deliveryMode(2) // 1 para não persistente, 2 para persistente
                    .build();
            String NOME_FILA = "plica" + "ComDurable";
            canal.queueDeclare(NOME_FILA, true, false, false, null);
            canalDois.queueDeclare("canalDoisComDurable", true, false, false, null);
            for (int count = 0; count <= 10000; count++) {
                String mensagem = count + "-" + Long.toString(new Timestamp(System.currentTimeMillis()).getTime());
                if (count == 1 || count == 1000000) {
                    canalDois.basicPublish("", "canalDoisComDurable", false, props, mensagem.getBytes());
                } else {
                    canal.basicPublish("", NOME_FILA, false, false, props, mensagem.getBytes());
                }
            }
        }

    }
}
