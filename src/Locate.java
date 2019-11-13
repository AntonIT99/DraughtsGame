import sdljava.video.SDLSurface;

public class Locate {
	
	//donne la position X du centre d'une case
	public static int getCaseCenterX (int CaseX, SDLSurface board, int boardCenterX)
	{
		return boardCenterX - board.getWidth()/2 + CaseX*100 + 50;
	}
	
	//donne la position Y du centre d'une case
	public static int getCaseCenterY (int CaseY, SDLSurface board, int boardCenterY)
	{
		return boardCenterY - board.getHeight()/2 + CaseY*100 + 50;
	}
	
	//retrouve les coordonnées d'une case à partir des coordonnées en pixels d'un point de l'écran
	public static int[] setCaseFromCoord(int PosX, int PosY, int boardCenterX, int boardCenterY, SDLSurface board)
	{
		int[] caseSet = new int [2];
		caseSet[0] = (PosX - (boardCenterX - board.getWidth()/2))/100;
		caseSet[1] = (PosY - (boardCenterY - board.getHeight()/2))/100;
		
		return caseSet;
	}
	
	//vérifie si un nombre n est inclu dans l'intervalle [inf;sup]
	public static boolean isIncluded(int n, int inf, int sup)
	{
		if (n >= inf && n <= sup)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

}
