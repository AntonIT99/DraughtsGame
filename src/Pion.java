public class Pion
{
	public enum Colour {
		BLANC, NOIR;
	};
	
	private int x;				//coordonn�e du pion sur l'axe x 
	private int y;				//coordonn�e du pion sur l'axe y
	private Colour couleur;		//d�finit la couleur du pion
	private int coupDefaut;		//d�finit le coup par d�faut (sens d'avancement dans le tableau) pour simplifier les calculs 
	private boolean estDame;	//d�finit si le pion est une dame (true) ou un pion simple(false)
	private boolean existe;		// d�finit si le pion est encore sur le plateau de jeu ou non
	private boolean estSelectione; // d�finit si le pion est s�lectionn� par le joueur ou non
	
	
	/* 	  	Constructeur 	  */
	
	public Pion (int x, int y, Colour couleur){
		this.x = x;
		this.y = y;
		this.couleur = couleur;
		this.estDame = false;
        this.existe = true;			//au d�but du jeu tous les pions sont sur le plateau donc par d�faut ils existent
        this.estSelectione = false;
		
        if(couleur == Pion.Colour.NOIR)
        {
			this.coupDefaut = 1;
		}
		else
		{
			this.coupDefaut = -1;
		}
	}
	
	
	 /*		 Accesseurs		 */
	
	public int getx(){
		return this.x;
	}
	
	public int gety(){
		return this.y;
	}
	
	public Colour getCouleur(){
		return this.couleur;
	}
	
	public int getCoupDefaut(){
		return this.coupDefaut;
	}
	
	public boolean getExiste(){
		return this.existe;
	}
	
	public boolean isSelected(){
		return this.estSelectione;
	}
	
	public boolean dame(){
		return this.estDame;
	}
	
	
	/*		Mutateurs		*/
	
	public void setx(int coordonnee){
		this.x = coordonnee;
	}
	
	public void sety(int coordonnee){
		this.y = coordonnee;
	}
	
	public void setExiste(boolean e){
		this.existe = e;
	}
	
	public void select(){
		this.estSelectione = true;
	}
	
	public void deselect(){
		this.estSelectione = false;
	}
	
	//autres m�thodes
	
	//transforme un pion en dame si celui-ci arrive au bout du plateau
	public void testDame()
	{
		if (this.couleur == Colour.NOIR && this.y == 9)
		{
			this.estDame = true;
		}
		else if (this.couleur == Colour.BLANC && this.y == 0)
		{
			this.estDame = true;
		}
	}
	
	public boolean canDoRicochet(Pion [][] Monde)
	{
		System.out.println("\n\ncoups:");
		for (int i = 0; i<3; i++)
		{
			System.out.println("coup " + i + ": " + mouvementPossible(Monde, true)[i][0] + ";" + mouvementPossible(Monde, true)[i][1]);
		}
		if (this.mouvementPossible(Monde, true)[0][0] != -1 && this.mouvementPossible(Monde, true)[0][1] != -1)
		{
			System.out.println("ricochet");
			return true;
		}
		else
		{
			System.out.println("pas de ricochet");
			return false;
		}
		
	}
	
	public int[][] mouvementPossible(Pion [][] Monde, boolean ricoche)
	{
		int[][] recap_mouvement = null;
		int x = this.getx(), y = this.gety(), k = 0;
		
		if(this.estDame == true)
		{
			recap_mouvement = new int[18][3];
			
			//par d�faut le premier coup est (-1;-1) si aucun d�placement n'est possible
			recap_mouvement[0][0] = -1;
			recap_mouvement[0][1] = -1;
			
			boolean mange = false, fin = false;
			int deplacement_x, deplacement_y;
			
			for(int diagonale = 1; diagonale <= 4; diagonale++)
			{
				switch (diagonale)
				{
					//diagonale en haut � gauche
					case 1:
						deplacement_x = -1;
						deplacement_y = -1;
						break;
					//diagonale en haut � droite
					case 2:
						deplacement_x = 1;
						deplacement_y = -1;
						break;
					//diagonale en bas � gauche
					case 3:
						deplacement_x = -1;
						deplacement_y = 1;
						break;
						
					//diagonale en bas � droite
					default:
						deplacement_x = 1;
						deplacement_y = 1;
				}
				if (!ricoche) {
					//tant qu'on est dans le plateau de jeu 
					while((x >= 0 && x < Monde[0].length) && (y >= 0 && y < Monde.length) && fin == false)
					{
						//on initialise les variables dont on a besoin
						x = x + deplacement_x;
						y = y + deplacement_y;
						
						//on verrifie que le nouveau coup teste est bien dans le tableau
						if((x >= 0 && x < Monde[0].length) && (y >= 0 && y < Monde.length))
						{
							//si la case est vide et qu'on ne peut pas manger
							if(Monde[y][x] == null && mange == false)
							{
								recap_mouvement[k][0] = x;
								recap_mouvement[k][1] = y;
								recap_mouvement[k][2] = 0;		//0: ce d�placement ne permet pas de manger
								
								k++;
							}
							//si la case est vide et que l'on peut manger
							else if(Monde[y][x] == null && mange == true)
							{
								recap_mouvement[k][0] = x;
								recap_mouvement[k][1] = y;
								recap_mouvement[k][2] = 1;		//1: ce d�placement permet de manger
								
								k++;
							}
							//si la case contient un pion
							else
							{
								//si le pion est de la couleur oppos�e alors on peut peut etre manger. mange devient true et on va regarder la case d'apres
								if( Monde[y][x].getCouleur() != this.getCouleur() && mange == false)
								{
									mange = true;
								}
								//sinon c'est que le pion est de la meme couleur ou que l'on a deja rencontre un pion de la couleur oppos�
								else
								{
									fin = true;
								}
							}
						}
							 
						
					}
				}
				else //d�placements lors d'un ricochet avec une dame
				{
					
					/*    Possibilit� 1: devant � gauche			*/
					//coordon�es de la case
					x = this.getx() - 1; 
					y = this.gety() - 1;
					
					//On test s'il y a pion de la couleur oppos�e sur la case
					if(x >= 0 && y >= 0 && Monde[y][x] != null  && Monde[y][x].getCouleur() != this.getCouleur())
					{
						//si le pion est bien de la couleur oppos�e on verrifie que la case suivante est bien vide pour pouvoir manger.
						//si elle est vide alors on mange
						if(x-1 >= 0 && y-1 >= 0 && Monde[y-1][x-1] == null)
						{
							recap_mouvement[k][0] = x-1;
							recap_mouvement[k][1] = y-1;
							recap_mouvement[k][2] = 1;		//1: signifie qu'en allant sur cette case le pion a mang�
						
							k++;							//on incr�mente le coup pour le prochain test
						}
					}
					
					/*    Possibilit� 2: devant � droite			*/
					//coordon�es de la case
					x = this.getx() + 1; 
					y = this.gety() - 1;
					
					//On test s'il y a pion de la couleur oppos�e sur la case
					if(x < Monde[0].length && y >= 0 && Monde[y][x] != null  && Monde[y][x].getCouleur() != this.getCouleur())
					{
						//si le pion est bien de la couleur oppos�e on verrifie que la case suivante est bien vide pour pouvoir manger.
						//si elle est vide alors on mange
						if(x+1 < Monde[0].length && y-1 >= 0 && Monde[y-1][x+1] == null)
						{
							recap_mouvement[k][0] = x+1;
							recap_mouvement[k][1] = y-1;
							recap_mouvement[k][2] = 1;		//1: signifie qu'en allant sur cette case le pion a mang�
						
							k++;							//on incr�mente le coup pour le prochain test
						}
					}
					
					/*    Possibilit� 3: derri�re � gauche			*/
					//coordon�es de la case
					x = this.getx() - 1; 
					y = this.gety() + 1;
					
					//On test s'il y a pion de la couleur oppos�e sur la case
					if(x >= 0 && y < Monde.length && Monde[y][x] != null  && Monde[y][x].getCouleur() != this.getCouleur())
					{
						//si le pion est bien de la couleur oppos�e on verrifie que la case suivante est bien vide pour pouvoir manger.
						//si elle est vide alors on mange
						if(x-1 >= 0 && y+1 < Monde.length && Monde[y+1][x-1] == null)
						{
							recap_mouvement[k][0] = x-1;
							recap_mouvement[k][1] = y+1;
							recap_mouvement[k][2] = 1;		//1: signifie qu'en allant sur cette case le pion a mang�
						
							k++;							//on incr�mente le coup pour le prochain test
						}
					}
					
					/*    Possibilit� 4: derri�re � droite			*/
					//coordon�es de la case
					x = this.getx() + 1; 
					y = this.gety() + 1;
					
					//On test s'il y a pion de la couleur oppos�e sur la case
					if(x < Monde[0].length && y < Monde.length && Monde[y][x] != null  && Monde[y][x].getCouleur() != this.getCouleur())
					{
						//si le pion est bien de la couleur oppos�e on verrifie que la case suivante est bien vide pour pouvoir manger.
						//si elle est vide alors on mange
						if(x+1 < Monde[0].length && y+1 < Monde.length && Monde[y+1][x+1] == null)
						{
							recap_mouvement[k][0] = x+1;
							recap_mouvement[k][1] = y+1;
							recap_mouvement[k][2] = 1;		//1: signifie qu'en allant sur cette case le pion a mang�
						
							k++;							//on incr�mente le coup pour le prochain test
						}
					}
					
					
				}
				fin = false;
				mange = false;
				x = this.getx();
				y = this.gety();
			}
			
		}
		
		//alors le pion est un pion simple
		else
		{
			recap_mouvement = new int[4][3];
			
			//par d�faut le premier coup est (-1;-1) si aucun d�placement n'est possible
			recap_mouvement[0][0] = -1;
			recap_mouvement[0][1] = -1;
			
			y = y + this.getCoupDefaut();
		
			/* 		possibilit� 1: on se d�place sur la gauche		*/
			x = x - 1;
			
			//Si la cellule existe, et ne contient pas de pion, le mouv est possible et le pion ne mange pas
			if(x >= 0 && (y >= 0 && y < Monde.length) && Monde[y][x] == null && !ricoche)
			{
				recap_mouvement[k][0] = x;
				recap_mouvement[k][1] = y;
				recap_mouvement[k][2] = 0;		//0: pas de pion sur la case donc il ne mange pas 
				
				k++;							//on incr�mente le coup pour le prochain test
			}
			else if (x >= 0 && (y >= 0 && y < Monde.length) && Monde[y][x] != null)
			{
				//On test s'il y a pion de la couleur oppos�e sur la case
				if(Monde[y][x].getCouleur() != this.getCouleur())
				{
					//si le pion est bien de la couleur oppos�e on verrifie que la case suivante est bien vide pour pouvoir manger.
					//si elle est vide alors on mange
					if(x-1 >= 0 && (y+getCoupDefaut() >= 0 && y+getCoupDefaut() < Monde.length) && Monde[y+getCoupDefaut()][x-1] == null)
					{
						recap_mouvement[k][0] = x-1;
						recap_mouvement[k][1] = y+getCoupDefaut();
						recap_mouvement[k][2] = 1;		//1: signifie qu'en allan t sur cette case le pion a mang�
						k++;							//on incr�mente le coup pour le prochain test
					}
				}
			}
			
			
			
			
			/*		possibilt� 2: on se d�place sur la droite (on proc�de de la m�me mani�re)		*/
			
			x = x + 2;
			
			if(x < Monde[0].length && (y >= 0 && y < Monde.length) && Monde[y][x] == null && !ricoche)
			{
				recap_mouvement[k][0] = x;
				recap_mouvement[k][1] = y;
				recap_mouvement[k][2] = 0;		 
				
				k++;							
			}
			else if(x < Monde[0].length && (y >= 0 && y < Monde.length) && Monde[y][x] != null)
			{
				if (Monde[y][x].getCouleur() != this.getCouleur())
				{
					if(x+1 < Monde[0].length && (y+getCoupDefaut() >= 0 && y+getCoupDefaut() < Monde.length) && Monde[y+getCoupDefaut()][x+1] == null)
					{
						recap_mouvement[k][0] = x+1;
						recap_mouvement[k][1] = y+getCoupDefaut();
						recap_mouvement[k][2] = 1;		
						k++;							
					}
				}
			}
			
			/*			On regarde si le pion peut manger en arri�re		*/
			
			//on reset les param�tres
			x = this.getx();
			y = this.gety() - this.getCoupDefaut();
			
			/*    Possibilit� 1: � gauche			*/
			
			x = x-1;
			
			//On test s'il y a pion de la couleur oppos�e sur la case
			if(x >= 0 && (y >= 0 && y < Monde.length) && Monde[y][x] != null  && Monde[y][x].getCouleur() != this.getCouleur())
			{
				//si le pion est bien de la couleur oppos�e on verrifie que la case suivante est bien vide pour pouvoir manger.
				//si elle est vide alors on mange
				if(x-1 >= 0 && (y-getCoupDefaut() >= 0 && y-getCoupDefaut() < Monde.length) && Monde[y-getCoupDefaut()][x-1] == null)
				{
					recap_mouvement[k][0] = x-1;
					recap_mouvement[k][1] = y-getCoupDefaut();
					recap_mouvement[k][2] = 1;		//1: signifie qu'en allant sur cette case le pion a mang�
				
					k++;							//on incr�mente le coup pour le prochain test
				}
			}
			
			/*    Possibilit� 1: � droite			*/
			
			x = x+2;
			
			//On test s'il y a pion de la couleur oppos�e sur la case
			if(x < Monde[0].length && (y >= 0 && y < Monde.length) && Monde[y][x] != null  && Monde[y][x].getCouleur() != this.getCouleur())
			{
				//si le pion est bien de la couleur oppos�e on verrifie que la case suivante est bien vide pour pouvoir manger.
				//si elle est vide alors on mange
				if(x+1 < Monde[0].length && (y-getCoupDefaut() >= 0 && y-getCoupDefaut() < Monde.length) && Monde[y-getCoupDefaut()][x+1] == null)
				{
					recap_mouvement[k][0] = x+1;
					recap_mouvement[k][1] = y-getCoupDefaut();
					recap_mouvement[k][2] = 1;		//1: signifie qu'en allan t sur cette case le pion a mang�
				
					k++;							//on incr�mente le coup pour le prochain test
				}
			}

		}
		
		//si k < nb_coups possibles alors on stock des mouvs imposssibles (-1;-1)
			while(k < recap_mouvement.length)
			{
				recap_mouvement[k][0] = -1;
				recap_mouvement[k][1] = -1;
				recap_mouvement[k][2] = 0;		
			
				k++;	
			}
			
			//au jeu de dame on doit obligatoirement manger. Si possibilit� de manger on retire tous les mouvs o� on mange pas	
			for(int i = 0; i < recap_mouvement.length ; i++)
			{
				//on test si on mange ou si on vient de ricocher
				if(recap_mouvement[i][2] == 1 || (ricoche == true))
				{
					//on parcours notre tableau pour trouver les coups qui ne mangent pas
					for(int j = 0; j < recap_mouvement.length; j++)
					{
						//si on mange pas
						if(i !=j && recap_mouvement[j][2] == 0)
						{
							//si le coup suivant  peut manger on remplace le coup actuel avec le coup suivant (on remet les coups dans l'ordre)  
							if (j+1 < recap_mouvement.length)
							{
								if (recap_mouvement[j+1][2] == 1) 
								{
									recap_mouvement[j][0] = recap_mouvement[j+1][0];
									recap_mouvement[j][1] = recap_mouvement[j+1][1];
									recap_mouvement[j][2] = 1;
									recap_mouvement[j+1][0] = -1;
									recap_mouvement[j+1][1] = -1;
									recap_mouvement[j+1][2] = 0;
								}
							}
							//sinon on efface le coup (on retest pas le coup test� � la boucle for pr�c�dente)
							else
							{
								recap_mouvement[j][0] = -1;
								recap_mouvement[j][1] = -1;								
							}
						}
					}
				}
			}
		
		return recap_mouvement;
	}
			
	
	
}
