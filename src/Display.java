import sdljava.SDLException;
import sdljava.image.SDLImage;
import sdljava.ttf.SDLTTF;
import sdljava.ttf.SDLTrueTypeFont;
import sdljava.video.*;

public class Display 
{
	//fonction initialisation d'une image (paramètre : nom de l'image avec son extension)
	public static SDLSurface loadImage(String file) throws SDLException
	{
		return SDLImage.load("assets/" + file);
	}
	
	//fonction affichage d'une image/surface (à partir de la position de son centre)
	public static void displayImage(SDLSurface screen, SDLSurface image, int positionX, int positionY) throws SDLException
	{
		SDLRect position = new SDLRect(positionX - image.getWidth()/2, positionY - image.getHeight()/2);
		image.blitSurface(screen, position);
	}
	
	//fonction affichage d'un texte (à partir de la position de son centre)
	public static void displayText(SDLSurface screen, String text, int size, int posX, int posY) throws SDLException
	{
		SDLTrueTypeFont font = SDLTTF.openFont("assets/fonts/arial.ttf", size);
		SDLColor color = new SDLColor(255,255,255); //texte blanc (code RVB)
		SDLSurface surfaceText = font.renderTextBlended(text, color);
		displayImage(screen, surfaceText, posX, posY);
		screen.flip();
		font.closeFont();
		surfaceText.freeSurface();
	}

}
