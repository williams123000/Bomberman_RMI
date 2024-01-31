import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
/**
 * Interfaz remota para el servicio Bomberman.
 */
public interface Bomberman extends Remote {
    // Métodos remotos para acciones específicas de Bomberman...

    public boolean nuevaPartida(int numJugadores) throws RemoteException;
    public int nuevoJugador(String nombre) throws RemoteException;
    public boolean Limite_Jugadores() throws RemoteException;
    public boolean partidaLista() throws RemoteException;
    public void movimiento(int id, int x, int y) throws RemoteException;
    public void ponerBomba(int id, int x, int y) throws RemoteException;
    public ArrayList<Jugador> obtenerEstado_Jugadores() throws RemoteException;
    public ArrayList<Estado> Obtener_Estado() throws RemoteException;
    public void eliminacion(String Simbolo) throws RemoteException;
    public String[][] Obtener_Campo() throws RemoteException;
    public boolean Validar_Partida() throws RemoteException;
    public int Numero_Jugadores() throws RemoteException;
    public ArrayList<Jugador> Jugadores_Listos() throws RemoteException;
}

class Jugador implements java.io.Serializable {
    // Definir la clase Jugador
    int ID;
    String Nombre;
    String Simbolo;
    boolean Estado;
    Posicion Posicion = new Posicion();   


    /**
     * Constructor de la clase Jugador.
     *
     * @param ID Identificación única del jugador.
     * @param Nombre Nombre del jugador.
     * @param Simbolo Símbolo asociado al jugador.
     * @param X Coordenada X inicial de la posición del jugador.
     * @param Y Coordenada Y inicial de la posición del jugador.
     */
    Jugador(int ID, String Nombre, String Simbolo, int X, int Y){
        this.ID = ID;
        this.Nombre = Nombre;
        this.Simbolo = Simbolo;
        this.Estado = true;
        this.Posicion.X = X;
        this.Posicion.Y = Y;
    }
}

/**
 * La clase Bomba representa una bomba en el juego.
 * Implementa la interfaz Serializable para permitir su serialización.
 */

class Bomba implements java.io.Serializable{
    // Identificador único de la bomba
    int ID_Bomba;
    // Identificador del jugador que colocó la bomba
    int ID_Player;
    // Posición en la que se encuentra la bomba en el tablero
    Posicion Posicion = new Posicion();
    // Momento en que la bomba fue creada
    LocalDateTime Hora_Creacion;
    // Momento en que la bomba explotará
    LocalDateTime Hora_Explosion;
    // Momento en que la bomba estallará
    LocalDateTime Hora_Estallido;
    // Estado de la bomba (activa o inactiva)
    Boolean Estado_Bomba;
    
    /**
     * Constructor de la clase Bomba.
     *
     * @param ID_Bomba    Identificador único de la bomba.
     * @param ID_Player   Identificador del jugador que colocó la bomba.
     * @param X           Coordenada X de la posición de la bomba en el tablero.
     * @param Y           Coordenada Y de la posición de la bomba en el tablero.
     */
    Bomba(int ID_Bomba, int ID_Player, int X, int Y){
        // Inicialización de los atributos
        this.ID_Bomba = ID_Bomba;
        this.ID_Player = ID_Player;
        this.Posicion.X = X;
        this.Posicion.Y = Y;
        this.Hora_Creacion = LocalDateTime.now();
        this.Hora_Explosion = this.Hora_Creacion.plus(4, ChronoUnit.SECONDS);
        this.Hora_Estallido = this.Hora_Creacion.plus(3, ChronoUnit.SECONDS);
        this.Estado_Bomba = true;// La bomba se crea en un estado activo
    }
}
class Posicion implements java.io.Serializable {
    int X;
    int Y;
}

class Estado implements java.io.Serializable {
    // Lista de jugadores en el estado actual del juego
    ArrayList<Jugador> Jugadores = new ArrayList<>(); 
    ArrayList<Bomba> Bombas = new ArrayList<>();
}