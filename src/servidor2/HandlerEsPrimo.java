
package servidor2;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Clase para manejar el contexto "/primo"
 * @author David Jiménez Riscardo
 * @version 1.0
 */
public class HandlerEsPrimo implements HttpHandler {
    
    public HandlerEsPrimo() {
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String numero = null, respuesta = null;
        
        //Obteneos la petición http del cliente
        URI uri = exchange.getRequestURI();
        //Obtenemos una cadena a partir de la interrogación(no incluida)
        String peticion = uri.getQuery();
              
        System.out.println("["+utilidades.Utilidades.getFechaHoraActualFormateada()+"] Atendiendo la petición: "+peticion);
               
        //Comprobamos si la peticion se ciñe a un patrón
        if(esCorrecta(peticion)){          
            //Tratar la petición
            try {              
                Map<String, String> values = getUrlValues(peticion);
                numero = values.get("numero"); 
                if(utilidades.Utilidades.esPrimo(Long.parseLong(numero))) 
                    respuesta = "El número "+numero+" es primo";
                else
                    respuesta = "El número "+numero+" NO es primo";
            } catch (UnsupportedEncodingException ex) {
                System.out.println("Codificación no soportada: "+ex.getMessage());
            }             
        }else         
            respuesta = "Petición no válida";
                      
        //Emitimos una respuesta al cliente
        exchange.getResponseHeaders().add("Content-Type", "text/html;charset=utf-8");       
        exchange.sendResponseHeaders(200, respuesta.getBytes().length);
        OutputStream os = exchange.getResponseBody(); 
        os.write(respuesta.getBytes()); 
        os.close();
        
        //Mensaje informativo en el servidor
        System.out.println("["+utilidades.Utilidades.getFechaHoraActualFormateada()+"] Respuesta a la petición: "+peticion+" -> "+respuesta);
    }
    
    /**
     * Volcar el contenido de una cadena en una estructura de datos con pares "clave/valor"
     * @param peticion Peticion del cliente
     * @return Estructura de datos con pares "clave/valor"
     * @throws UnsupportedEncodingException 
     */
    public Map<String, String> getUrlValues(String peticion) throws UnsupportedEncodingException {       
        //System.out.println(peticion);
        Map<String, String> paramsMap = new HashMap<>();
        String params[] = peticion.split("&");
        for (String param : params) {
            String temp[] = param.split("=");
            paramsMap.put(temp[0], java.net.URLDecoder.decode(temp[1], "UTF-8"));
        }     
        return paramsMap;
    }
    
    /**
     * Comprobamos si la petición coincide el patrón "numero=xxx"
     * @param peticion Peticion del cliente
     * @return true si coincide, false en caso contrario
     */
    public boolean esCorrecta(String peticion){       
         //Comprobamos si la petición tiene el formato "nombre=xxx&apellido=yyy"
        Pattern p = Pattern.compile("numero=[0-9]+");
        Matcher m = p.matcher(peticion);
        if(m.matches()){
            return true;
        }else
            return false;
    }
    
}

