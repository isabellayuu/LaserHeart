### Isabella Yu: Laser Heart 

**Luokka LaserHeart**

Kuvaus: Sydämen keskeltä muodostuu viivoja sydämen reuniin, jolloin kokonainen sydän muodostuu vähitellen. 

Oliorunko, sydämen muodostuminen: 

- Ohjelmalle annetaan sydämen koordinaatit. Jaetaan koordinaatit x ja y koordinaatteihin ja käytetään sydämen muodostumisessa sin ja cos komentoja.  
- Muodostetaan sydämen keskipisteen koordinaatit 
  - Tällä tavalla tiedetään mistä kohdasta laaserit tulevat sydämen reunaan. 

Tietotyypit: 

- Int: taustan leveyden ja korkeuden koordinaatit, aika 

- Double: Sydämen koko, x ja y koordinaatit 

- Color: Taustan väri ja sydämen väri.  

Funktiot: 

- HeartXCoords: 
  - Laskee sydämen x koordinaatin 

- HeartYCoords 
  - Laskee sydämen y koordinaatin 

DrawEffect:
- käydään läpi päätepisteet ja niihin piirretään viivat, jolloin muodostuu vähitellen täytetty sydän. 

Tick:

- Päivittää animaatiota ja kasvattaa clock  

- Laskee sydämen x ja y koordinaatin ja lisää ne päätepisteiden listaan 

Esimerkkejä: 

DrawEffect: 

- Esimerkkinä tilanne, jos endPoints on vain yksi pääpiste, joka on  (300, 400), ruudulla näkyy yksi viiva keskipisteestä päätepisteeseen. Jos laitetaan pisteiden määräksi 200, ruudulla näkyy 200 viivaa, joista muodostuu sydämen kuvio.  

Tick:

- Esimerkiksi jos clock lisätään yksi eli aika menee eteenpäin yhdellä, muodostuu yksi säde keskeltä.  

 

 

 

 

 

 