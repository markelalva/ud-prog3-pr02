import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class JLabelEstrella extends JLabel {
	private static final long serialVersionUID = 2L;  // Para serialización
	public static final int TAMANYO_ESTRELLA = 40;  // píxels (igual ancho que algo)
	public static final int RADIO_ESFERA_ESTRELLA = 17;  // Radio en píxels del bounding circle del coche (para choques)  // Dibujado (para depuración) del bounding circle de choque del coche
	public  long hora = System.currentTimeMillis();
	public double miGiro = 0.0D;
	
	public JLabelEstrella() {
		// Esto se haría para acceder por sistema de ficheros
		// 		super( new ImageIcon( "bin/ud/prog3/pr00/coche.png" ) );
		// Esto se hace para acceder tanto por recurso (jar) como por fichero
		try {
			setIcon( new ImageIcon( JLabelCoche.class.getResource( "img/estrella.png" ).toURI().toURL() ) );
			
		} catch (Exception e) {
			System.err.println( "Error en carga de recurso: coche.png no encontrado" );
			e.printStackTrace();
		}
		setBounds( 0, 0, TAMANYO_ESTRELLA, TAMANYO_ESTRELLA );
		// Esto sería útil cuando hay algún problema con el gráfico: borde de color del JLabel
		// setBorder( BorderFactory.createLineBorder( Color.yellow, 4 ));
	}
	

	  public void setGiro(double gradosGiro)
	  {
	    this.miGiro = (gradosGiro / 180.0D * 3.141592653589793D);
	    
	    this.miGiro = (-this.miGiro);
	    if (this.miGiro > 6.283185307179586D) {
	      this.miGiro -= 6.283185307179586D;
	    } else if (this.miGiro < 0.0D) {
	      this.miGiro += 6.283185307179586D;
	    }
	  }
	  
	  public void addGiro(double gradosGiro)
	  {
	    this.miGiro -= gradosGiro / 180.0D * 3.141592653589793D;
	  }
	
	public double getHora(){
		
		return hora;
	}
	
	  protected void paintComponent(Graphics g)
	  {
	    Image img = ((ImageIcon)getIcon()).getImage();
	    Graphics2D g2 = (Graphics2D)g;
	    
	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    
	    g2.rotate(this.miGiro, 20.0D, 20.0D);
	    
	    g2.drawImage(img, 0, 0, 40, 40, null);
	  }
	
	
	
	
	
}
