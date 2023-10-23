package pruebas;

import org.apache.log4j.Logger;

/**
 *
 * @author alberto.sanchez
 */
public class LogSimple {
    static final Logger log = Logger.getLogger(LogSimple.class);

    public static void main(String[] args) {
        log.debug("Prueba Debug");
        log.info ("Prueba Info");
        log.warn ("Prueba Alerta");
        log.error("Prueba Error");
        log.fatal("Prueba Fatal");
    }
}
