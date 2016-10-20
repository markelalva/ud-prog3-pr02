

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.*;

import javax.swing.*;

import org.omg.Messaging.SyncScopeHelper;

/** Clase principal de minijuego de coche para Práctica 02 - Prog III
 * Ventana del minijuego.
 * @author Andoni Eguíluz
 * Facultad de Ingeniería - Universidad de Deusto (2014)
 */
public class VentanaJuego extends JFrame {
	private static final long serialVersionUID = 1L;  // Para serialización
	JPanel pPrincipal;         // Panel del juego (layout nulo)
	MundoJuego miMundo;        // Mundo del juego
	CocheJuego miCoche;        // Coche del juego
	private JLabel Mensaje;
	MiRunnable miHilo = null;  // Hilo del bucle principal de juego	
	boolean teclas [] = new boolean[4]; // Array para las teclas.
	public int estrellasquitadas =0;
	
	

	/** Constructor de la ventana de juego. Crea y devuelve la ventana inicializada
	 * sin coches dentro
	 */
	public VentanaJuego() {
		// Liberación de la ventana por defecto al cerrar
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		// Creación contenedores y componentes
		pPrincipal = new JPanel();
		JPanel pBotonera = new JPanel();
		Mensaje = new JLabel (" ");
		pBotonera.add(Mensaje);
	    add(pBotonera, "South");
		
		// Formato y layouts
		pPrincipal.setLayout( null );
		pPrincipal.setBackground( Color.white );
		// Añadido de componentes a contenedores
		getContentPane().add( pPrincipal, BorderLayout.CENTER );
		getContentPane().add( pBotonera, BorderLayout.SOUTH );
		// Formato de ventana
		setSize( 1000, 750 );
		setResizable( false );
		
		// Añadido para que también se gestione por teclado con el KeyListener
		pPrincipal.addKeyListener( new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
					case KeyEvent.VK_UP: {
					
						teclas[0] = true;
						break;
					}
					case KeyEvent.VK_DOWN: {
				
						teclas[1] = true;
						break;
					}
					case KeyEvent.VK_LEFT: {
						teclas[2] = true;
						break;
					}
					case KeyEvent.VK_RIGHT: {
						teclas[3] = true;
						break;
					}
				}
			}
			
			public void keyReleased(KeyEvent e) {
				switch (e.getKeyCode()){
				
				case KeyEvent.VK_UP: {
					teclas[0] = false;
					break;
					
				}
				case KeyEvent.VK_DOWN: {
					teclas[1] = false;
					break;
					
				}
				case KeyEvent.VK_LEFT: {
					teclas[2] = false;
					break;
					
				}
				case KeyEvent.VK_RIGHT: {
					teclas[3] = false;
					break;
					
				}
				}
				
				
			}
		});
		pPrincipal.setFocusable(true);
		pPrincipal.requestFocus();
		pPrincipal.addFocusListener( new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				pPrincipal.requestFocus();
			}
		});
		// Cierre del hilo al cierre de la ventana
		addWindowListener( new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (miHilo!=null) miHilo.acaba();
			}
		});
	}
	
	/** Programa principal de la ventana de juego
	 * @param args
	 */
	public static void main(String[] args) {
		// Crea y visibiliza la ventana con el coche
		try {
			final VentanaJuego miVentana = new VentanaJuego();
			SwingUtilities.invokeAndWait( new Runnable() {
				@Override
				public void run() {
					miVentana.setVisible( true );
				}
			});
			miVentana.miMundo = new MundoJuego( miVentana.pPrincipal );
			miVentana.miMundo.creaCoche( 150, 100 );
			miVentana.miCoche = miVentana.miMundo.getCoche();
			miVentana.miCoche.setPiloto( "Fernando Alonso" );
			// Crea el hilo de movimiento del coche y lo lanza
			miVentana.miHilo = miVentana.new MiRunnable();  // Sintaxis de new para clase interna
			Thread nuevoHilo = new Thread( miVentana.miHilo );
			nuevoHilo.start();
		} catch (Exception e) {
			System.exit(1);  // Error anormal
		}
	}
	
	/** Clase interna para implementación de bucle principal del juego como un hilo
	 * @author Andoni Eguíluz
	 * Facultad de Ingeniería - Universidad de Deusto (2014)
	 */
	class MiRunnable implements Runnable {
		boolean sigo = true;
		
		@Override
		public void run() {
			
			// Bucle principal forever hasta que se pare el juego...
			while (sigo) {
				 double fuerzaAceleracion = 0.0D;
				// Mover coche
				if (teclas[0] == true){
					fuerzaAceleracion = VentanaJuego.this.miCoche.fuerzaAceleracionAdelante();

					
				}
				else if (teclas[1]== true){
					 fuerzaAceleracion = -VentanaJuego.this.miCoche.fuerzaAceleracionAtras();
				}
				 MundoJuego.aplicarFuerza(fuerzaAceleracion, VentanaJuego.this.miCoche);
				if (teclas [2] == true){
			
					miCoche.gira(+10);
				}
				else if (teclas[3] == true){
					miCoche.gira(-10);
				}
				miCoche.mueve( 0.040 );
				
				 int haperdido = miMundo.quitayRotaEstrellas(6000L);

				 if (haperdido !=0){
					 Mensaje.setText("Puntuacion: " + 10* estrellasquitadas + " Estrellas Perdidas: " + miMundo.estrellasperdidas);
				 }
				
				miMundo.creaEstrella();
			
				// Chequear choques
				
				int haquitado = miMundo.ComprobarChoques();
				if (haquitado !=0){
					estrellasquitadas += haquitado;
					Mensaje.setText("Puntuacion: " + 10* estrellasquitadas + " Estrellas Perdidas: " + miMundo.estrellasperdidas);
				}
				// (se comprueba tanto X como Y porque podría a la vez chocar en las dos direcciones (esquinas)
				if (miMundo.hayChoqueHorizontal(miCoche)) // Espejo horizontal si choca en X
					miMundo.rebotaHorizontal(miCoche);
				if (miMundo.hayChoqueVertical(miCoche)) // Espejo vertical si choca en Y
					miMundo.rebotaVertical(miCoche);
				
				//Comprobamos las estrellas quitadas
				
				sigo = miMundo.Comprobar();
				
				if (!sigo){
					Mensaje.setText("GRACIAS POR JUGAR, HA OBTENIDO: " + estrellasquitadas*10 + " puntos. ");
				}

				
				// Dormir el hilo 40 milisegundos
				try {
					Thread.sleep( 40 );
				} catch (Exception e) {
				}
			}
		}
		/** Ordena al hilo detenerse en cuanto sea posible
		 */
		public void acaba() {
		this.sigo = false;
	
}
		
	}
}

