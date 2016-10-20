

import java.util.ArrayList;
import java.util.Random;

import javax.swing.JPanel;

/** "Mundo" del juego del coche.
 * Incluye las físicas para el movimiento y los choques de objetos.
 * Representa un espacio 2D en el que se mueven el coche y los objetos de puntuación.
 * @author Andoni Eguíluz Morán
 * Facultad de Ingeniería - Universidad de Deusto
 */
public class MundoJuego {
	private JPanel panel;  // panel visual del juego
	CocheJuego miCoche;    // Coche del juego
	public ArrayList<JLabelEstrella> Estrellas = new ArrayList<JLabelEstrella>();
	public Random r;
	public static final double TIEMPO_ENTRE_ESTRELLAS = 1200L;
	private static long UltimaEstrella = 0L;
	public int estrellasperdidas =0;

	
	
	/** Construye un mundo de juego
	 * @param panel	Panel visual del juego
	 */
	public MundoJuego( JPanel panel ) {
		this.panel = panel;
	}

	/** Crea un coche nuevo y lo añade al mundo y al panel visual
	 * @param posX	Posición X de pixel del nuevo coche
	 * @param posY	Posición Y de píxel del nuevo coche
	 */
	public void creaCoche( int posX, int posY ) {
		// Crear y añadir el coche a la ventana
		miCoche = new CocheJuego();
		miCoche.setPosicion( posX, posY );
		panel.add( miCoche.getGrafico() );  // Añade al panel visual
		miCoche.getGrafico().repaint();  // Refresca el dibujado del coche
		r = new Random();
	}
	
	/** Devuelve el coche del mundo
	 * @return	Coche en el mundo. Si no lo hay, devuelve null
	 */
	public CocheJuego getCoche() {
		return miCoche;
	}

	/** Calcula si hay choque en horizontal con los límites del mundo
	 * @param coche	Coche cuyo choque se comprueba con su posición actual
	 * @return	true si hay choque horizontal, false si no lo hay
	 */
	public boolean hayChoqueHorizontal( CocheJuego coche ) {
		return (coche.getPosX() < JLabelCoche.RADIO_ESFERA_COCHE-JLabelCoche.TAMANYO_COCHE/2 
				|| coche.getPosX()>panel.getWidth()-JLabelCoche.TAMANYO_COCHE/2-JLabelCoche.RADIO_ESFERA_COCHE );
	}
	
	/** Calcula si hay choque en vertical con los límites del mundo
	 * @param coche	Coche cuyo choque se comprueba con su posición actual
	 * @return	true si hay choque vertical, false si no lo hay
	 */
	public boolean hayChoqueVertical( CocheJuego coche ) {
		return (coche.getPosY() < JLabelCoche.RADIO_ESFERA_COCHE-JLabelCoche.TAMANYO_COCHE/2 
				|| coche.getPosY()>panel.getHeight()-JLabelCoche.TAMANYO_COCHE/2-JLabelCoche.RADIO_ESFERA_COCHE );
	}

	/** Realiza un rebote en horizontal del objeto de juego indicado
	 * @param coche	Objeto que rebota en horizontal
	 */
	public void rebotaHorizontal( CocheJuego coche ) {
		// System.out.println( "Choca X");
		double dir = coche.getDireccionActual();
		dir = 180-dir;   // Rebote espejo sobre OY (complementario de 180)
		if (dir < 0) dir = 360+dir;  // Corrección para mantenerlo en [0,360)
		coche.setDireccionActual( dir );
	}
	
	/** Realiza un rebote en vertical del objeto de juego indicado
	 * @param coche	Objeto que rebota en vertical
	 */
	public void rebotaVertical( CocheJuego coche ) {
		// System.out.println( "Choca Y");
		double dir = miCoche.getDireccionActual();
		dir = 360 - dir;  // Rebote espejo sobre OX (complementario de 360)
		miCoche.setDireccionActual( dir );
	}
	
	/** Calcula y devuelve la posición X de un movimiento
	 * @param vel    	Velocidad del movimiento (en píxels por segundo)
	 * @param dir    	Dirección del movimiento en grados (0º = eje OX positivo. Sentido antihorario)
	 * @param tiempo	Tiempo del movimiento (en segundos)
	 * @return
	 */
	public static double calcMovtoX( double vel, double dir, double tiempo ) {
		return vel * Math.cos(dir/180.0*Math.PI) * tiempo;
	}
	
	/** Calcula y devuelve la posición X de un movimiento
	 * @param vel    	Velocidad del movimiento (en píxels por segundo)
	 * @param dir    	Dirección del movimiento en grados (0º = eje OX positivo. Sentido antihorario)
	 * @param tiempo	Tiempo del movimiento (en segundos)
	 * @return
	 */
	public static double calcMovtoY( double vel, double dir, double tiempo ) {
		return vel * -Math.sin(dir/180.0*Math.PI) * tiempo;
		// el negativo es porque en pantalla la Y crece hacia abajo y no hacia arriba
	}
	
	/** Calcula el cambio de velocidad en función de la aceleración
	 * @param vel		Velocidad original
	 * @param acel		Aceleración aplicada (puede ser negativa) en pixels/sg2
	 * @param tiempo	Tiempo transcurrido en segundos
	 * @return	Nueva velocidad
	 */
	public static double calcVelocidadConAceleracion( double vel, double acel, double tiempo ) {
		return vel + (acel*tiempo);
	}
	public static double calcFuerzaRozamiento( double masa, double coefRozSuelo,
			 double coefRozAire, double vel ) {
			 double fuerzaRozamientoAire = coefRozAire * (-vel); // En contra del movimiento
			 double fuerzaRozamientoSuelo = masa * coefRozSuelo * ((vel>0)?(-1):1); // Contra mvto
			 return fuerzaRozamientoAire + fuerzaRozamientoSuelo;
			 } 
	
	public static double calcAceleracionConFuerza( double fuerza, double masa ) {
		 // 2ª ley de Newton: F = m*a ---> a = F/m
		 return fuerza/masa;
		 } 
	
	public static void aplicarFuerza( double fuerza, Coche coche ) {
		 double fuerzaRozamiento = calcFuerzaRozamiento( Coche.MASA ,
		 Coche.COEF_RZTO_SUELO, Coche.COEF_RZTO_AIRE, coche.getVelocidad() );
		 double aceleracion = calcAceleracionConFuerza( fuerza+fuerzaRozamiento, Coche.MASA );
		 if (fuerza==0) {
		 // No hay fuerza, solo se aplica el rozamiento
		 double velAntigua = coche.getVelocidad();
		 coche.acelera( aceleracion, 0.04 );
		 if (velAntigua>=0 && coche.getVelocidad()<0
		 || velAntigua<=0 && coche.getVelocidad()>0) {
		 coche.setVelocidad(0); // Si se está frenando, se para (no anda al revés)
		 }
		 } else {
		 coche.acelera( aceleracion, 0.04 );
		 }
		 
	
		 
	
	}
	public void creaEstrella(){
		if (System.currentTimeMillis() - UltimaEstrella > TIEMPO_ENTRE_ESTRELLAS){
			JLabelEstrella e = new JLabelEstrella();
			e.setLocation(r.nextInt(this.panel.getWidth() - 40), 
			 r.nextInt(this.panel.getHeight() - 40));
			this.panel.add(e);
			this.panel.repaint();
			Estrellas.add(e);
			UltimaEstrella = System.currentTimeMillis();
		
		}
	}
		
	public int quitayRotaEstrellas(long maxTiempo){
		
		double tiempoactual = 0;
		//Tenemos que hacer desde la ultima posicion a la primera.
		for (int i =Estrellas.size() -1; i>-1; i--){
			JLabelEstrella e = Estrellas.get(i);
			tiempoactual = System.currentTimeMillis();
			if ((tiempoactual - e.hora) > maxTiempo){
				this.panel.remove(e);
				this.panel.repaint();
				Estrellas.remove(i);
				estrellasperdidas++;
			}
			else{
				Estrellas.get(i).addGiro(10D);
				Estrellas.get(i).repaint();
			}
		}
		return estrellasperdidas;
		
	}
	
	public boolean Comprobar(){
		if (estrellasperdidas >9){
			return false;
		}
		else
			return true;
	}
	//Con este metodo calculamos si hay una estrella en la posicion actual del coche.
	 private boolean chocaCocheConEstrella(JLabelEstrella est)
	  {
	    double distX = est.getX() + 20 - this.miCoche.getPosX() - 50.0D;
	    double distY = est.getY() + 20 - this.miCoche.getPosY() - 50.0D;
	    double dist = Math.sqrt(distX * distX + distY * distY);
	    return dist <= 52.0D;
	  }
	
	 //Metodo para cmprobar los choques
	public int ComprobarChoques(){
		int numerodeChoques =0;
		for (int i =0; i<Estrellas.size(); i++){
		if (chocaCocheConEstrella(Estrellas.get(i))){
			numerodeChoques++;
			this.panel.remove(Estrellas.get(i));
			Estrellas.remove(i);
		
			
		}
		
		
		
	}
		return numerodeChoques;
		
		
		
		
		
		
	
	
}
}
