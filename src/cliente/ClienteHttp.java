
package cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Clase Cliente HTTP
 * @author David Jiménez Riscardo
 * @version 1.0
 */
public class ClienteHttp extends Thread {
    
    private final String cadenaURL;

    /**
     * Constructor de la clase
     * @param cadenaURL Dirección URL
     */
    public ClienteHttp(String cadenaURL){
        
        this.cadenaURL = cadenaURL;
        
    }
    
    public void run(){
        //Vector dónde guardaré la url troceada
        String vectorUrl[] = new String[3];
        //Para guardar el protocolo
        String protocolo = null;
        String dominio = null;
 
        try {
            
            System.out.println("\nCliente HTTP del alumno David Jiménez Riscardo");
            System.out.println("==============================================");
            System.out.println("Conectándose a la URL: "+cadenaURL+"\n");
            
            //Obtenemos el protocolo y el dominio utilizado, la función getProtocol solo funciona para protocolos existentes
            vectorUrl=dividirURL(cadenaURL);
            protocolo = vectorUrl[0];
            dominio = vectorUrl[1];
            
            //crea objeto url
            URL url = new URL(cadenaURL);           
            //obtiene una conexión al recurso URL
            URLConnection conexion = url.openConnection();
            //se conecta pudiendo interactuar con parámetros
            conexion.connect();
            //obtiene el tipo de contenido
            String contentType = conexion.getContentType();
            
           
            //mostrarCabecera(conexion);
                      
          
            //Mostramos la respuesta del servidor
            System.out.println("Respuesta: "+obtenerRespuestaServidor(conexion));
            
            
            //Tratamiento del contenido recibido
            if (contentType.startsWith("text/html")) {
                String[] datosPagina=obtenerInformacionPagina(conexion);
                System.out.println("Fecha de la última modificación: "+datosPagina[0]);
                System.out.println("Valor de la cookie: "+datosPagina[1]);
                System.out.println("Idioma (charset): "+datosPagina[2]);
                
            } 
            else if (contentType.startsWith("image/png")) {
                String[] datosImagen=obtenerInformacionImagen(conexion);             
                System.out.println("Fecha de la última modificación: "+datosImagen[0]);  
                System.out.println("Tamaño de imagen: " + datosImagen[1] + " bytes");  
                System.out.println("Tipo de imagen: "+datosImagen[2]);
            }
            else {
              System.out.println("No se trata de ningún archivo");
            }

        } catch (MalformedURLException e) {
               System.out.println("ERROR. URL no válida, protocolo desconocido: "+protocolo);
        } catch (IOException e) {
               System.out.println("Error de E/S: "+dominio);
        } finally {
          //termina la aplicación
          System.exit(0);
        }
    }//fin del método run
    
    /**
     * Método main
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        //Casos a probar
        //String cadenaURL="http://www.iesaguadulce.es/centro/index.php/oferta-formativa/formacion-profesional-a-distancia/dam-modalidad-distancia";
        //String cadenaURL="http://www.iesaguadulce.es/centro/templates/dd_toysshop_34/images/logo_ies_aguadulce.png";
        //String cadenaURL="http://www.iesaguadulce.es/centro/images/Documentos_oficiales/PlanesDeCentro/202021/planes2021_22/ProyectoFormacinProfesionalaDistanciav1_01.pdf";
        //String cadenaURL="httx://help.me";
        //String cadenaURL="http://www.google.es";
        //String cadenaURL="http://www.hijk22.com/";
       
        InputStreamReader is = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(is);
        String cadenaURL = null;
        
        System.out.println("Introduzca una url:");
        try {
            cadenaURL=br.readLine();
        } catch (IOException ex) {
            Logger.getLogger(ClienteHttp.class.getName()).log(Level.SEVERE, null, ex);
        } 
        ClienteHttp cliente = new ClienteHttp(cadenaURL);
        cliente.start();      
    }
    
    /**
     * Método auxiliar para ayudarnos a ver la cabecera completa
     * @param conexion Conexión al recurso URL
     */
    public void mostrarCabecera(URLConnection conexion){
             
        System.out.println(conexion.getHeaderFields());      
        //Otra forma de mostrar toda la cabecera
        //for (Entry<String, List<String>> header : conexion.getHeaderFields().entrySet()) {
        //    System.out.println(header.getKey() + "=" + header.getValue());
        //}
    }
    
    /**
     * Método para obtener la respuesta del servidor
     * @param conexion Conexión al recurso URL
     * @return Respuesta del servidor
     */
    public String obtenerRespuestaServidor(URLConnection conexion){
        return conexion.getHeaderField(0)+"\n";
    }
    
    
    /**
     * Método para obtener toda la información solicitada de una página
     * @param conexion Conexión al recurso URL
     * @return Vector con la información
     */
    public String[] obtenerInformacionPagina(URLConnection conexion){       
        String[] vector = new String[3];        
        vector[0] = conexion.getHeaderField("Last-Modified");
        vector[1] = conexion.getHeaderField("Set-Cookie").substring(0, 59);
        vector[2] = conexion.getContentEncoding() != null ? conexion.getContentEncoding() : "utf-8";         
        return vector;
    }
    
    /**
     * Método para obtener la información solicitada de una imagen
     * @param conexion Conexión al recurso URL
     * @return Vector con la información
     */
    public String[] obtenerInformacionImagen(URLConnection conexion){       
        String[] vector = new String[3];      
        vector[0] = conexion.getHeaderField("Last-Modified");
        vector[1] = Long.toString(conexion.getContentLengthLong());
        vector[2] = conexion.getContentType().substring(6);              
        return vector;
    }
    
    /**
     * Método para trocear las partes de una URL
     * @param url Cadena que representa una URL
     * @return Vector con las 3 partes de una url (protocolo, dominio y ruta)
     */
    public String[] dividirURL(String url){      
        //Variables para el regex
        String regex = "^(?:([^:]*):(?://)?)?([^/]*)(/.*)?";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);
        
        //Vector dónde guardaré las tres partes de la url, protocolo, dominio y ruta
        String[] vector = new String[3];

        //Ver si coincide el regex
        if (matcher.find()) {
            //Obtener el texto capturado por cada conjunto de paréntesis
            String protocolo = matcher.group(1);
            String dominio   = matcher.group(2);
            String ruta      = matcher.group(3);

            vector[0]=protocolo;
            vector[1]=dominio;
            vector[2]=ruta;
        }       
        return vector;
    }
}
