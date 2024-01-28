import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BombermanServidor implements Bomberman {
    boolean Partida_Iniciada = false;
    Bomberman_Game Tablero = new Bomberman_Game();
    
    String[] avataresBomberman = {"A", "B", "C", "D"}; 
    ArrayList<Integer> Indices_Ocupados = new ArrayList<>();
    
    // Implementación del servidor
    
    public BombermanServidor() {
        //super();
        // Inicializar variables del servidor
    }

    /**
     * Método para obtener el estado actual del campo de juego en el servidor Bomberman.
     * 
     * @return Matriz que representa el campo de juego.
     * @throws RemoteException Si ocurre un error de comunicación remota.
     */
    public String[][] Obtener_Campo() throws RemoteException {
        // Devuelve la matriz que representa el campo de juego desde el objeto Tablero en el servidor
        return Tablero.Tablero;
    }

    /**
     * Método para validar si la partida ya ha sido iniciada en el servidor Bomberman.
     * 
     * @return `true` si la partida ha sido iniciada, `false` si no.
     * @throws RemoteException Si ocurre un error de comunicación remota.
     */
    public boolean Validar_Partida() throws RemoteException {
        // Verificar si la partida ha sido iniciada
        return Partida_Iniciada;
    }

    /**
     * Método para crear una nueva partida en el servidor Bomberman.
     * 
     * @param numJugadores Número de jugadores para la nueva partida.
     * @return `true` si se crea la nueva partida con éxito, `false` si ya hay una partida en curso.
     * @throws RemoteException Si ocurre un error de comunicación remota.
     */
    public boolean nuevaPartida(int numJugadores) throws RemoteException {
        // Verificar si ya hay una partida en curso
        if (Partida_Iniciada){
            System.out.println("Ya hay una partida en curso ...");
            return false;
        }

        // Crear una nueva partida
        System.out.println("Creando nueva partida...");
        System.out.println(numJugadores);
        
        // Inicializar el campo de juego
        Tablero.Campo_Juego();
        Tablero.Limitar();
        Tablero.Crear_Obstaculos();
        Tablero.Visualizar_Campo();

        // Configurar el estado de la partida
        Partida_Iniciada = true;
        Tablero.Numeros_Jugadores = numJugadores;
        Tablero.Jugadores_Registrados = -1;
        
        return true;
    }

    /**
     * Método para registrar un nuevo jugador en el servidor Bomberman.
     * 
     * @param nombre Nombre del nuevo jugador.
     * @return El ID asignado al nuevo jugador.
     * @throws RemoteException Si ocurre un error de comunicación remota.
     */
    public int nuevoJugador(String nombre) throws RemoteException {
        // Incrementar el contador de jugadores registrados
        Tablero.Jugadores_Registrados++;

        // Generar un índice aleatorio para el avatar del jugador
        Random random = new Random();
        int Indice_Emoji;
        do {
            Indice_Emoji = random.nextInt(avataresBomberman.length);

            // Romper el bucle si el índice no está ocupado
            if (!Indices_Ocupados.contains(Indice_Emoji)){
                break;
            }
        } while (Indices_Ocupados.contains(Indice_Emoji));
        
        // Generar posiciones aleatorias para el jugador en el campo de juego
        int X = random.nextInt((Tablero.Tablero.length-2) - 1 + 1) + 1;
        int Y = random.nextInt((Tablero.Tablero.length-2) - 1 + 1) + 1;

        while (Tablero.Tablero[X][Y].equals("1")) {
            X = random.nextInt((Tablero.Tablero.length-2) - 1 + 1) + 1;
            Y = random.nextInt((Tablero.Tablero.length-2) - 1 + 1) + 1;
        }
        

        // Crear un nuevo jugador con la información generada
        Jugador Jugador = new Jugador(Tablero.Jugadores_Registrados, nombre, avataresBomberman[Indice_Emoji], X , Y);

        // Agregar el jugador a la lista de jugadores en el campo de juego
        Tablero.Jugadores.add(Jugador);

        // Registrar el índice del emoji como ocupado
        Indices_Ocupados.add(Indice_Emoji);
        
        // Devolver el ID asignado al nuevo jugador
        return Tablero.Jugadores_Registrados;
    }

    /**
     * Método para verificar si se ha alcanzado el límite de jugadores en el servidor Bomberman.
     * 
     * @return `true` si aún hay espacio para más jugadores, `false` si se alcanzó el límite.
     * @throws RemoteException Si ocurre un error de comunicación remota.
     */
    public boolean Limite_Jugadores() throws RemoteException {
        // Verificar si se ha alcanzado el límite de jugadores
        if (Tablero.Jugadores_Registrados == Tablero.Numeros_Jugadores-1){
            System.out.println("Ya se alcanzo el limite de jugadores ....");
            return false;
        }
        return true;
    }

    /**
     * Método para verificar si la partida está lista para comenzar en el servidor Bomberman.
     * 
     * @return `true` si la partida está lista para comenzar, `false` si aún no se han registrado todos los jugadores.
     * @throws RemoteException Si ocurre un error de comunicación remota.
     */
    public boolean partidaLista() throws RemoteException {
        // Verificar si se han registrado todos los jugadores necesarios para la partida
        if (Tablero.Jugadores_Registrados == Tablero.Numeros_Jugadores-1){
            return true;
        }else{
            return false;
        }
    }

    /**
     * Método para obtener la lista de jugadores listos en el servidor Bomberman.
     * 
     * @return Lista de jugadores listos para la partida.
     * @throws RemoteException Si ocurre un error de comunicación remota.
     */
    public ArrayList<Jugador> Jugadores_Listos() throws RemoteException{
        // Devolver la lista de jugadores del campo de juego
        return Tablero.Jugadores;
    }

    /**
     * Método para obtener el número total de jugadores en el servidor Bomberman.
     * 
     * @return Número total de jugadores para la partida.
     * @throws RemoteException Si ocurre un error de comunicación remota.
     */
    public int Numero_Jugadores() throws RemoteException{
        // Devolver el número total de jugadores configurado en el campo de juego
        return Tablero.Numeros_Jugadores;
    }

    /**
     * Método para obtener el estado actual de los jugadores en el servidor Bomberman.
     * 
     * @return Lista de jugadores con su estado actual.
     * @throws RemoteException Si ocurre un error de comunicación remota.
     */
    public ArrayList<Jugador> obtenerEstado_Jugadores() throws RemoteException {
        // Devolver la lista de jugadores del campo de juego
        return Tablero.Jugadores;
    }

    /**
     * Método para obtener el estado actual del juego en el servidor Bomberman.
     * 
     * @return Lista de estados del juego, incluyendo información sobre los jugadores.
     * @throws RemoteException Si ocurre un error de comunicación remota.
     */
    public ArrayList<Estado> Obtener_Estado() throws RemoteException {
        // Crear un nuevo objeto Estado y agregar la información de los jugadores
        Estado Estado = new Estado();
        Estado.Jugadores.addAll(Tablero.Jugadores);

        // Agregar el estado actual a la lista de estados en el campo de juego
        Tablero.Estado.add(Estado);

        // Devolver la lista de estados del campo de juego
        return Tablero.Estado;
    }

    /**
     * Método para actualizar la posición de un jugador en el servidor Bomberman.
     * 
     * @param id Identificador único del jugador.
     * @param x Nueva coordenada X de la posición del jugador.
     * @param y Nueva coordenada Y de la posición del jugador.
     * @throws RemoteException Si ocurre un error de comunicación remota.
     */
    public void movimiento(int id, int x, int y) throws RemoteException {
        // Actualizar la posición del jugador con el ID proporcionado
        for (Jugador Jugador : Tablero.Jugadores) {
            if (Jugador.ID == id){
                Jugador.Posicion.X = x;
                Jugador.Posicion.Y = y;
            }
        }
    }

    /**
     * Método principal para iniciar el servidor Bomberman.
     * 
     * @param args Los argumentos de la línea de comandos (no se utilizan en este ejemplo).
     */
    public static void main(String[] args) {
        try {
            // Crear una instancia del servidor Bomberman
            BombermanServidor obj = new BombermanServidor();

            // Exportar el objeto para que pueda recibir invocaciones remotas
            Bomberman stub = (Bomberman) UnicastRemoteObject.exportObject(obj, 0);

            // Obtener el registro RMI existente o crear uno nuevo en el puerto 1099
            Registry registry = LocateRegistry.getRegistry(1099);

            // Vincular el objeto remoto en el registro con el nombre "BombermanServer"
            registry.bind("BombermanServer", stub);

            System.out.println("✅ Servidor listo...");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ponerBomba(int id, int x, int y) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'ponerBomba'");
    }

    @Override
    public void eliminacion(int id) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'eliminacion'");
    }
}

class Bomberman_Game {
    String [][] Tablero = new String[11][11];

    int Numeros_Jugadores;
    int Jugadores_Registrados;

    ArrayList<Jugador> Jugadores = new ArrayList<>();
    ArrayList<Bomba> Bombas = new ArrayList<>();
    ArrayList<Estado> Estado = new ArrayList<>();

    /**
     * Visualiza el campo de juego en la consola.
     */
    void Visualizar_Campo(){
        for (int i = 0; i < Tablero.length; i++) {
            for (int j = 0; j < Tablero.length; j++) {
                System.out.print(Tablero[i][j]);
            }
            System.out.println();
        }
    }

    /**
     * Inicializa el campo de juego con celdas vacías.
     */
    void Campo_Juego(){
        for (int i = 0; i < Tablero.length; i++) {
            for (int j = 0; j < Tablero.length; j++) {
                Tablero[i][j] = "0";
            }
        }
    }

    /**
     * Limita el campo de juego estableciendo bordes con celdas bloqueadas.
     */
    void Limitar(){
        for (int i = 0; i < Tablero.length; i++) {
            for (int j = 0; j < Tablero.length; j++) {
                if (i == 0 || i == Tablero.length - 1 || j == 0 || j == Tablero.length-1){
                    Tablero[i][j] = "1";
                }
            }
        }
    }

    void Crear_Obstaculos(){
        for (int i = 2; i < Tablero.length - 2; i = i + 2) {
            for (int j = 2; j < Tablero.length - 2; j = j + 2) {    
                Tablero[i][j] = "1";
            }
        }
    }

    /**
     * Obtiene una copia de la lista de jugadores.
     * 
     * @return Lista de jugadores.
     */
    public List<Jugador> obtenerJugadores() {
        // Devolver una copia de la lista de jugadores para evitar problemas de concurrencia
        return new ArrayList<>(Jugadores);
    }
}


