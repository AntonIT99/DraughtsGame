
import sdljava.SDLMain;
import sdljava.event.SDLEvent;
import sdljava.event.SDLKey;
import sdljava.event.SDLKeyboardEvent;
import sdljava.event.SDLMouseButtonEvent;
import sdljava.ttf.SDLTTF;
import sdljava.SDLException;
import sdljava.video.*;


public class Game 
{

	public static void main(String[] args) throws SDLException, InterruptedException 
	{

		//**INITIALIZATION**//
		
		//résolution écran
		int ResX = 1920;
		int ResY = 1080;
		
		//initialisation librairie SDL et chargment des images
		SDLMain.init(SDLMain.SDL_INIT_EVERYTHING);
		SDLTTF.init();
		SDLSurface screen = SDLVideo.setVideoMode(ResX, ResY, 32, SDLVideo.SDL_FULLSCREEN | SDLVideo.SDL_DOUBLEBUF | SDLVideo.SDL_HWSURFACE | SDLVideo.SDL_HWACCEL | SDLVideo.SDL_ENABLE);
		SDLVideo.wmSetCaption("Dames", null);
		SDLSurface board = Display.loadImage("board.png");
		SDLSurface pionBlanc = Display.loadImage("Pion1.png");
		SDLSurface pionNoir = Display.loadImage("Pion2.png");
		SDLSurface dameBlanche = Display.loadImage("Dame1.png");
		SDLSurface dameNoire = Display.loadImage("Dame2.png");
		SDLSurface selection = Display.loadImage("selection.png");
		SDLSurface selectionDame = Display.loadImage("selectionDame.png");
		SDLSurface mouvement = Display.loadImage("mouvement.png");
		SDLSurface J1Turn = Display.loadImage("J1Turn.png");
		SDLSurface J2Turn = Display.loadImage("J2Turn.png");
		
		//initialisation des pions
		/*  On déclare le monde  */
		Pion [][] Monde = new Pion[10][10]; //Monde[y][x]
		Pion [] ListPion = new Pion [40];
		
		/*	 On initialise le monde et les pions	 */
		int nb_cases = 0, nb_pions = 0; 										
		
		for (int y = 0; y < Monde.length; y++)
		{
			if (y%2 == 0) //ligne paire => case noire décalée de 1
			{
				nb_cases = 1;
			}
			else if (y%2 == 1) //ligne impaire => case noire non décalée
			{
				nb_cases = 0;
			}
			
			for(int x = 0; x < Monde[0].length; x++)
			{
				//si la case est noire et fait partie des 4 premières lignes, on place un pion noir dessus 
				if((nb_cases % 2) == 0 && (y >= 0 && y <= 3))
				{
					ListPion[nb_pions] = new Pion(x, y, Pion.Colour.NOIR);
					Monde[y][x] = ListPion[nb_pions];
					
					nb_pions++;
				}
				
				//si la case est noire et fait partie des 4 dernières lignes, on place un pion blanc dessus 
				if((nb_cases % 2) == 0 && (y >= 6 && y <= 9))
				{
					ListPion[nb_pions] = new Pion(x, y, Pion.Colour.BLANC);
					Monde[y][x] = ListPion[nb_pions];
		
					nb_pions++;
				}
				
				nb_cases++;
			}
			
		}
		//**END INITIALIZATION**//
		
		//**GAME**//
		
		boolean gameIsRunning = true;
		boolean pionSelected = false;
		boolean movementDone = false;
		boolean ricochet = false;
		boolean testMange = false;
		int selectedPionIndex = -1; //indice du dernier pion selectioné, -1 : aucun pion
		int playerTurn = 1; //tour joueur 1 ou 2. Joueur 1 = blanc, joueur 2 = noir.
		int winnerIndex = 1; //indice du joueur gagnant. Joueur 1 = blanc, joueur 2 = noir.
		
		//on indique au J1 que c'est son tour
		Display.displayImage(screen, J1Turn, 350, ResY-85);
		
		while (gameIsRunning)
		{
			SDLEvent event = SDLEvent.waitEvent();
			
			/*si clique sur la croix*/
			if (event.getType() == SDLEvent.SDL_QUIT)
            {
				gameIsRunning = false;
            }
			
            /*si touche appuyée*/
			if (event instanceof SDLKeyboardEvent) 
			{
				SDLKeyboardEvent eventK = (SDLKeyboardEvent) event;
				switch (eventK.getSym()) 
				{
					case SDLKey.SDLK_ESCAPE:	
						gameIsRunning = false;	
						break;
				}
			}
			
			/*si clique souris*/
			if (event.getType() == SDLEvent.SDL_MOUSEBUTTONDOWN && event instanceof SDLMouseButtonEvent)
			{
					SDLMouseButtonEvent eventM = (SDLMouseButtonEvent) event;
					/*si clique gauche*/
					if (eventM.getButton() == SDLEvent.SDL_BUTTON_LEFT )
					{
						int[] caseClic = Locate.setCaseFromCoord(eventM.getX(), eventM.getY(), ResX/2, ResY/2, board);
						
						//si le pion bougé précédemment doit effectuer un ricochet
						if (ricochet)
						{
							//on balaye les mouvements possibles
							for (int k = 0; k < ListPion[selectedPionIndex].mouvementPossible(Monde, true).length; k++)
							{
								//si la case cliqué correspond à un mouvement possible du pion sélectionné
								if (ListPion[selectedPionIndex].mouvementPossible(Monde, true)[k][0] == caseClic[0] && ListPion[selectedPionIndex].mouvementPossible(Monde, true)[k][1] == caseClic[1])
								{
									
									int xMange = coordMange(Monde, ListPion[selectedPionIndex].getx(), ListPion[selectedPionIndex].gety(), caseClic[0], caseClic[1])[0];
									int yMange = coordMange(Monde, ListPion[selectedPionIndex].getx(), ListPion[selectedPionIndex].gety(), caseClic[0], caseClic[1])[1];
										
									//recherche de l'indice du pion mangé
									for (int a = 0; a < ListPion.length; a++)
									{
										if (ListPion[a].getx() == xMange && ListPion[a].gety() == yMange)
										{
												ListPion[a].setExiste(false);
										}
									}
									
									//déplacement du pion sélectionné et suppression du pion mangé
									Monde[ListPion[selectedPionIndex].gety()][ListPion[selectedPionIndex].getx()] = null;
									Monde[yMange][xMange] = null;
									ListPion[selectedPionIndex].setx(caseClic[0]);
									ListPion[selectedPionIndex].sety(caseClic[1]);
									Monde[ListPion[selectedPionIndex].gety()][ListPion[selectedPionIndex].getx()] = ListPion[selectedPionIndex];
									ListPion[selectedPionIndex].testDame();
									
									//test si le pion peut faire un ricochet
									if (ListPion[selectedPionIndex].canDoRicochet(Monde))
									{
										ricochet = true;
										movementDone = false;
									}
									else
									{
										ricochet = false;
										movementDone = true;
									}
									
								}
										
							}
						}
						//si un pion est sélectionné
						else if (pionSelected) 
						{

							//si on clique sur le pion déjà sélectionné
							if (ListPion[selectedPionIndex].getx() == caseClic[0] && ListPion[selectedPionIndex].gety() == caseClic[1])
							{
								//on déselectionne le pion
								ListPion[selectedPionIndex].deselect();
								pionSelected = false;
								selectedPionIndex = -1;
							}
							else //si on clique ailleurs
							{
								if (!movementDone) //si aucun mouvement n'a déjà été effectué ce tour ci
								{
									for (int k = 0; k < ListPion[selectedPionIndex].mouvementPossible(Monde, false).length; k++)
									{
										//si la case cliqué correspond à un mouvement possible du pion sélectionné
										if (ListPion[selectedPionIndex].mouvementPossible(Monde, false)[k][0] == caseClic[0] && ListPion[selectedPionIndex].mouvementPossible(Monde, false)[k][1] == caseClic[1])
										{												
											int xMange = coordMange(Monde, ListPion[selectedPionIndex].getx(), ListPion[selectedPionIndex].gety(), caseClic[0], caseClic[1])[0];
											int yMange = coordMange(Monde, ListPion[selectedPionIndex].getx(), ListPion[selectedPionIndex].gety(), caseClic[0], caseClic[1])[1];
												
											if (xMange !=-1 && yMange != -1)
											{
												//recherche de l'indice du pion mangé
												for (int a = 0; a < ListPion.length; a++)
												{
													if (ListPion[a].getx() == xMange && ListPion[a].gety() == yMange)
													{
														ListPion[a].setExiste(false);
													}
												}
												Monde[yMange][xMange] = null;
												testMange = true;
												
											}
											
											//déplacement du pion sélectionné
											Monde[ListPion[selectedPionIndex].gety()][ListPion[selectedPionIndex].getx()] = null;
											movementDone = true;
											ListPion[selectedPionIndex].setx(caseClic[0]);
											ListPion[selectedPionIndex].sety(caseClic[1]);
											Monde[ListPion[selectedPionIndex].gety()][ListPion[selectedPionIndex].getx()] = ListPion[selectedPionIndex];
											ListPion[selectedPionIndex].testDame();
											
											//test si le pion peut faire un ricochet
											if (ListPion[selectedPionIndex].canDoRicochet(Monde) && testMange)
											{
												ricochet = true;
												movementDone = false;
												testMange = false;
											}
											else
											{
												ricochet = false;
												movementDone = true;
											}
											
										}
												
									}
								}
							}
						
						} 
						else //si aucun pion n'est sélectionné
						{
							for (int i = 0; i < 40; i++)
							{
								//si on clique sur un pion non sélectionné
								if (ListPion[i].getExiste() && ListPion[i].getx() == caseClic[0] && ListPion[i].gety() == caseClic[1])
								{
									//si la couleur du pion correspond à celle du joueur
									if ((ListPion[i].getCouleur() == Pion.Colour.BLANC && playerTurn == 1) || (ListPion[i].getCouleur() == Pion.Colour.NOIR && playerTurn == 2))
									{
										//on sélectionne le pion
										ListPion[i].select();
										selectedPionIndex = i;
										pionSelected = true;
									}
								}
							}
						}
					}
			}
			
			//***gestion des tours***/
			
			//fin du tour et réinitilisation de la séléction et du mouvement de pions
			//affichage bouton fin du tour appuyé
			
			if(movementDone)
			{
				pionSelected = false;
				movementDone = false;
				testMange = false;
				if (selectedPionIndex != -1)
				{
					ListPion[selectedPionIndex].deselect();
					selectedPionIndex = -1;
				}
				
				//test fin de partie
				switch(fin_partie(ListPion, Monde))
				{
					case 0: //partie en cours
						break;
					
					case 1:
						gameIsRunning = false;
						winnerIndex = 1;
						break;
					
					case 2:
						gameIsRunning = false;
						winnerIndex = 2;
						break;
				}
				
				//changement de joueur
				if (playerTurn == 1)
				{
					playerTurn = 2;
					//on indique au J2 que c'est son tour
					Display.displayImage(screen, J2Turn, 350, ResY-85);
				}
				else if (playerTurn == 2)
				{
					playerTurn = 1;
					//on indique au J1 que c'est son tour
					Display.displayImage(screen, J1Turn, 350, ResY-85);
				}
			}
			
			//***affichage des graphismes****//
			Display.displayImage(screen, board, ResX/2, ResY/2);
			
			//affichage des pions sur le tableau
			for (int i = 0; i < ListPion.length; i++)
			{
				if (ListPion[i].getExiste())
				{
					int posX = Locate.getCaseCenterX(ListPion[i].getx(), board, ResX/2);
					int posY = Locate.getCaseCenterY(ListPion[i].gety(), board, ResY/2);
					
					if (ListPion[i].isSelected())
					{
						if (ListPion[i].dame()) 
						{
							Display.displayImage(screen, selectionDame, posX, posY);
						} 
						else
						{
							Display.displayImage(screen, selection, posX, posY);
						}
						
						if (!movementDone && i == selectedPionIndex)
						{
							int[][] deplacement = ListPion[i].mouvementPossible(Monde, ricochet);
							for (int k = 0; k < deplacement.length; k++)
							{
								if (Locate.isIncluded(deplacement[k][0], 0 , 10) && Locate.isIncluded(deplacement[k][1], 0 , 10)) 
								{
									int X = Locate.getCaseCenterX(deplacement[k][0], board, ResX/2);
									int Y = Locate.getCaseCenterY(deplacement[k][1], board, ResY/2);
									Display.displayImage(screen, mouvement, X, Y);
								}
							}
						}
					}
					else if (ListPion[i].getCouleur() == Pion.Colour.NOIR) 
					{
						if (ListPion[i].dame())
						{
							Display.displayImage(screen, dameNoire, posX, posY);
						}
						else
						{
							Display.displayImage(screen, pionNoir, posX, posY);							
						}
					}
					else if (ListPion[i].getCouleur() == Pion.Colour.BLANC)
					{
						if (ListPion[i].dame())
						{
							Display.displayImage(screen, dameBlanche, posX, posY);
						}
						else
						{
							Display.displayImage(screen, pionBlanc, posX, posY);							
						}
					}
				}
			}
			//affichage des pions éliminés
			int baseXNoir = 50;
			int baseXBlanc = ResX - 50;
			int nbPionsNoirs = 0;
			int nbPionsBlancs = 0;
			for (int i = 0; i < ListPion.length; i++)
			{
				if (ListPion[i].getExiste() == false)
				{
					if (ListPion[i].getCouleur() == Pion.Colour.NOIR)
					{
						if (nbPionsNoirs > 7) 
						{
							baseXNoir += 125;
							nbPionsNoirs = 0;
						}
						if (ListPion[i].dame())
						{
							Display.displayImage(screen, dameNoire, baseXNoir, 100 + nbPionsNoirs*125);
						}
						else
						{
							Display.displayImage(screen, pionNoir, baseXNoir, 100 + nbPionsNoirs*125);							
						}
						nbPionsNoirs++;
					}
					else if (ListPion[i].getCouleur() == Pion.Colour.BLANC)
					{
						if (nbPionsBlancs > 7) 
						{
							baseXBlanc -= 125;
							nbPionsBlancs = 0;
						}
						if (ListPion[i].dame())
						{
							Display.displayImage(screen, dameBlanche, baseXBlanc, 100 + nbPionsBlancs*125);
						}
						else
						{
							Display.displayImage(screen, pionBlanc, baseXBlanc, 100 + nbPionsBlancs*125);							
						}
						Display.displayImage(screen, pionBlanc, baseXBlanc, 100 + nbPionsBlancs*125);
						nbPionsBlancs++;
					}
				}
				
			}
			
			screen.flip();
			//****fin affichage des graphismes****//
			
		}
		
		//affichage Victoire
		screen.fillRect(screen.mapRGB(0,0,0));
		screen.flip();
		Display.displayText(screen, "Victoire du Joueur " + Pion.Colour.values()[winnerIndex-1], 100, ResX/2, ResY/2);
		pause();
		
		//**END GAME**//
		mouvement.freeSurface();
		selection.freeSurface();		
		pionBlanc.freeSurface();
		pionNoir.freeSurface();
		dameBlanche.freeSurface();
		dameNoire.freeSurface();
		board.freeSurface();
		screen.freeSurface();
		selectionDame.freeSurface();
		J1Turn.freeSurface();
		J2Turn.freeSurface();
		SDLMain.quit();
		
	}
	
	//méthode qui test si la partie est terminée ou non
	//return 0: partie en cours
	//return 1: J1 a gagné (blanc)
	//return 2: J2 a gagné (noir)
	public static int fin_partie (Pion [] ListPion, Pion[][] Monde)
	{
		int etat_partie = 0, compteurB = 0, compteurN = 0; 
		
		//on compte les pions noirs restants
		for (int i = 0; i < ListPion.length/2; i++)
		{
			if(ListPion[i].getExiste() == true && ListPion[i].mouvementPossible(Monde, false)[0][0] != -1)
			{
				compteurN++;
			}
		}
		
		//on compte les pions blancs restants
		for (int i = (ListPion.length/2)-1; i < ListPion.length; i++)
		{
			if(ListPion[i].getExiste() == true && ListPion[i].mouvementPossible(Monde, false)[0][0] != -1)
			{
				compteurB++;
			}
		}
		
		// si compteur pion noir = 0 c'est que le J2 n'a plus de pions
		//J1 déclaré gagnant
		if(compteurN == 0){
			etat_partie = 1;
		}
		
		// si compteur pion blanc = 0 c'est que le J1 n'a plus de pions
		//J2 déclaré gagnant
		if(compteurB == 0){
			etat_partie = 2;
		}
		
		return etat_partie;
	}
	
	//fonction pause (appuyer sur une touche pour continuer
	public static void pause() throws SDLException
	{
		while (!(SDLEvent.waitEvent() instanceof SDLKeyboardEvent))
		{
			
		}
	}
	
	
	
	
	/* on utilise habituellement les paramètres ci dessous avec cette fonction :
	int xi = ListPion[selectedPionIndex].getx();
	int yi = ListPion[selectedPionIndex].gety();
	int xf = caseClic[0];
	int yf = caseClic[1];	*/
	//fonction qui détermine les coordonnées du pion mangé (y compris s'il est mangé par une dame)
	//variables (xi;yi) intiales et (xf:yf) finales lors du déplacement
	//par défaut si le pion ne mange pas, la fonction renvoit (-1;-1)
	public static int[] coordMange(Pion[][] Monde, int xi, int yi, int xf, int yf)
	{
		int[] mange = new int[2];
		mange[0] = -1;
		mange[1] = -1;
		
		if (xf > xi && yf > yi) //déplacement direction bas droite
		{
			//System.out.println("bas droite");
			for(int i = xi+1, j = yi+1; i < xf &&  j < yf; i++, j++)
			{
				if (Monde[j][i] != null) //si un point se situe dans cette diagonale, on récupère ses coordonnées
				{
					mange[0] = i;
					mange[1] = j;
				}
			}
		}
		else if (xf > xi && yf < yi) //déplacement direction haut droite
		{
			//System.out.println("haut droite");
			for(int i = xi+1, j = yi-1; i < xf &&  j > yf; i++, j--)
			{
				if (Monde[j][i] != null) //si un point se situe dans cette diagonale, on récupère ses coordonnées
				{
					mange[0] = i;
					mange[1] = j;
				}
			}
		}
		else if (xf < xi && yf < yi) //déplacement direction haut gauche
		{
			//System.out.println("haut gauche");
			for(int i = xi-1, j = yi-1; i > xf &&  j > yf; i--, j--)
			{
				if (Monde[j][i] != null) //si un point se situe dans cette diagonale, on récupère ses coordonnées
				{
					mange[0] = i;
					mange[1] = j;
				}
			}
		}
		else if (xf < xi && yf > yi) //déplacement direction bas gauche
		{
			//System.out.println("bas gauche");
			for(int i = xi-1, j = yi+1; (i > xf) &&  (j < yf); i--, j++)
			{
				if (Monde[j][i] != null) //si un point se situe dans cette diagonale, on récupère ses coordonnées
				{
					mange[0] = i;
					mange[1] = j;
				}
			}
		}
		return mange;
	}
	
}

