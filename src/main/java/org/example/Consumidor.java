package org.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalTime;

public class Consumidor {
    public static void main(String[] args) throws Exception {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        Connection conexao = connectionFactory.newConnection();
        Channel canal = conexao.createChannel();
        String NOME_FILA = "plica" + "ComDurable";
        canal.queueDeclare(NOME_FILA, true, false, false, null);
        DeliverCallback callback = (consumerTag, delivery) -> {
            try {
                String[] mensagem = new String(delivery.getBody()).split("-", 2);
                Timestamp tempo = new Timestamp(Long.parseLong(mensagem[1]));
                System.out.println(mensagem[0] + "-" + compararTempo(tempo));
            } finally {
                canal.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
        };

        // fila, noAck, callback, callback em caso de cancelamento (por exemplo, a fila
        // foi deletada)
        canal.basicConsume(NOME_FILA, false, callback, consumerTag -> {
            System.out.println("Cancelaram a fila: " + NOME_FILA);
        });
    }

    private static long compararTempo(Timestamp tempo) {
        Timestamp agora = new Timestamp(System.currentTimeMillis());
        long tempoComparado = agora.getTime() - tempo.getTime();
        return tempoComparado;
    }
}
