package org.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ConsumidorDois {
    public static void main(String[] args) throws Exception {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        List<Timestamp> times = new ArrayList<Timestamp>();
        String NOME_FILA = "canalDoisComDurable";

        Connection conexao = connectionFactory.newConnection();
        Channel canal = conexao.createChannel();
        canal.queueDeclare(NOME_FILA, true, false, false, null);
        DeliverCallback callback = (consumerTag, delivery) -> {

            try {
                String[] mensagem = new String(delivery.getBody()).split("-", 2);
                Timestamp tempo = new Timestamp(Long.parseLong(mensagem[1]));

                System.out.println(tempo.toString());
                times.add(tempo);
                if (times.size() == 2) {
                    System.out.println(compararTempo(times));
                }
            } finally {
                canal.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }

        };
        canal.basicConsume(NOME_FILA, false, callback, consumerTag -> {
            System.out.println("Cancelaram a fila: " + NOME_FILA);
        });

        // fila, noAck, callback, callback em caso de cancelamento (por exemplo, a fila
        // foi deletada)
    }

    private static long compararTempo(List<Timestamp> times) {
        long tempoComparado = times.get(1).getTime() - times.get(0).getTime();
        return tempoComparado;
    }
}
