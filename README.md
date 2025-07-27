# Korupcia

**Čo robí:**  
Dajú sa vyberať rôzne tvary hracej plochy, ukladať rozohratá hra, hlavné menu je resizable.  
Ukladanie hry je automatické, uloží sa do súboru pomenovaného podľa tvaru plochy a dátumu.

**Pravidlá hry:**  
Hráč môže doprava otáčať hexagonmi svojej farby.  
Ak otočený hexagon začne ukazovať na hexagon inej farby, zafarbí ho rekurzívne aj spolu s tými, na ktoré novozafarbený ukazuje.  
Hra končí, ak jeden z hráčov pohltí všetky oponentove hexagony (**mód Obliteration**) alebo pri móde **Domination** obsadí 70% všetkých hexagonov.

Hexagony sa ukladajú vždy do obdĺžnikového gridu — využil som techniku z tejto stránky:  
 [https://www.redblobgames.com/grids/hexagons/](https://www.redblobgames.com/grids/hexagons/)  
Aby grid mal požadovaný tvar, veľa hexagonov je neviditeľných.  
Okrem nej som využíval aj [chatgpt.com](https://chatgpt.com)

**Ako sa spúšťa:**  
Po spustení sa samotná hra začína vybraním **Gameplay Options**, aspoň základného **Board Shape** a **Win Conditions**, až potom sa dá začať kliknutím na **START**.  
Okrem toho sa dá aj bez výberu začať načítaním hry pomocou **Load Game**.

Súbor `Korupcia.java` a `GameBoard.java` sú v zložke:  
`\projektsjavafx\src\main\java\com\example\projektsjavafx`
