package modelo;

import java.io.Serializable;

public enum Eventos implements Serializable {
    NOTIFICACION_POZO,
    NOTIFICACION_NUEVA_PARTIDA,
    NOTIFICACION_GANADOR,
    NOTIFICACION_ROBO_CASTIGO,
    NOTIFICACION_HUBO_ROBO_CASTIGO,
    NOTIFICACION_PUNTOS,
    NOTIFICACION_COMIENZO_RONDA,
    NOTIFICACION_CORTE_RONDA,
    NOTIFICACION_JUGADORES_ACTUALIZADOS,
    NOTIFICACION_BAJO_JUEGO,
    NOTIFICACION_CAMBIO_TURNO,
    NOTIFICACION_NUMERO_JUGADOR,
    NOTIFICACION_PARTIDA_GUARDADA,
    NOTIFICACION_ELEGIR_JUGADOR,
    NOTIFICACION_FIN_PARTIDA,
    NOTIFICACION_ESPERA,
    NOTIFICACION_TERMINA_ESPERA
}