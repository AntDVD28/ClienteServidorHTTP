
package servidor2;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servidor HTTP Concurrente
 * @author David Jiménez Riscardo
 * @version 1.0
 */
public class ServidorHttp {

    /**
     * Método principal
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        //Gestión del puerto
        int puerto = 80;      
        if(args.length!=1 || !isNumeroPositivo(args[0])){
            System.out.println("Parámetros erróneos. Iniciando servidor en el puerto 80.\n");
        }else {
            puerto = Integer.parseInt(args[0]);
        }
        
        System.out.println("SERVIDOR HTTP");
        System.out.println("=============");
              
        try {
            //Instanciamos al servidor
            HttpServer server = HttpServer.create(new InetSocketAddress(puerto), 0);
            System.out.println("["+utilidades.Utilidades.getFechaHoraActualFormateada()+"] Servidor HTTP iniciado en el puerto "+puerto);
            
            //Agregamos los contextos al servidor
            server.createContext("/saludar", (HttpHandler) new servidor2.HandlerSaludar());
            server.createContext("/primo", (HttpHandler) new servidor2.HandlerEsPrimo());
            
            //Incorporamos la gestión multihilo
            server.setExecutor(Executors.newCachedThreadPool());
            
            //Iniciar server
            server.start();
        } catch (BindException ex){    
            System.out.println("Puerto no disponible. Revise si el puerto está siendo ya utilizado por otro programa o no tiene permisos.");
        } catch (IOException ex) {
            System.out.println("Error de E/S");
        }
    }
    
     /**
     * Método para comprobar si una cadena recibida es un número positivo
     * @param cadena Cadena recibida
     * @return Valor booleano, true si la cadena es un número positivo, false en caso contrario
     */
    public static boolean isNumeroPositivo(String cadena) {

        boolean resultado = false;

        try {
            if(Integer.valueOf(cadena)>0)
                resultado = true;
        } catch (NumberFormatException excepcion) {
                resultado = false;
        }
        return resultado;
    }
    
}

