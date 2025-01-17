package modelo;

import java.io.Serializable;

public enum Eventos implements Serializable {
    FALTAN_JUGADORES,
    INICIAR_PARTIDA,
    PARTIDA_AUN_NO_CREADA,
    NOTIFICACION_ACTUALIZAR_POZO,
    NOTIFICACION_ACTUALIZAR_JUEGOS,
    NOTIFICACION_NUEVA_PARTIDA_PROPIO,
    NOTIFICACION_GANADOR,
    NOTIFICACION_ROBO_CASTIGO,
    NOTIFICACION_HUBO_ROBO_CASTIGO,
    NOTIFICACION_NO_PUEDE_ROBO_CASTIGO,
    NOTIFICACION_PUNTOS,
    NOTIFICACION_COMIENZO_RONDA,
    NOTIFICACION_CORTE_RONDA,
    NOTIFICACION_AGREGAR_OBSERVADOR,
    NOTIFICACION_BAJO_JUEGO,
    NOTIFICACION_CAMBIO_TURNO,
    NOTIFICACION_NUMERO_JUGADOR,
    PUEDE_CORTAR,
    SOBRAN_CARTAS,
    NO_PUEDE_CORTAR
}
