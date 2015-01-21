#bubblesort test
addi R1,R1,1
addi R4,R0,4
addi R10,R0,10
add R11,R0,R28

sw R10,R0,R11
add R11,R11,R4
rsub R10,R1,R10
sw R10,R0,R11
add R11,R11,R4
rsub R10,R1,R10
sw R10,R0,R11
add R11,R11,R4
rsub R10,R1,R10
sw R10,R0,R11
add R11,R11,R4
rsub R10,R1,R10
sw R10,R0,R11
add R11,R11,R4
rsub R10,R1,R10
sw R10,R0,R11
add R11,R11,R4
rsub R10,R1,R10
sw R10,R0,R11
add R11,R11,R4
rsub R10,R1,R10
sw R10,R0,R11
add R11,R11,R4
rsub R10,R1,R10
sw R10,R0,R11
add R11,R11,R4
rsub R10,R1,R10
sw R10,R0,R11
#test end

#programm start
addi R2,R0,10
add R3,R0,R28
#R2 = laenge array
#R3 = adresse pointer array

#R4 = 1. Schleife
#R5 = Pointer 1. Platz
#R6 = Pointer 2. Platz
#R7 = 1. Platz
#R8 = 2. Platz
#R9 = 2. Schleife
#R10 = tmp

bubble_sort:
#Anfangslanege in um eins dekrementieren, speichern als erste Zaehlvariable und schauen ob > 0, sonst springe zu ende
	addi R1,R0,1
	rsub R4,R1,R2
	beqi R4,bubble_sort_ende

bubble_sort_re_init:
#Lade die adresse des erste bytes in ein register und des darauffolgenden, anschließend sichere die laenge der ersten schleife fuer die zweite
	add R5,R0,R3
	addi R6,R5,4
	add R9,R0,R4
bubble_sort_loop:
#Lade die bytes von den adressen und vergleiche ihre groese
	lw R7,R5,R0
	lw R8,R6,R0
#wenn byte1 > byte0, dann nicht tauschen
	rsub R10,R7,R8
	bgei R10,bubble_sort_loop_2
#sonst tausche beide werte 
	add R10,R0,R7
	add R7,R0,R8
	add R8,R0,R10
bubble_sort_loop_2:
	sw R7,R5,R0
	sw R8,R6,R0
#erhohe beide zeiger, sodass nun byte1 und byte2 verglichen werden
	addi R5,R5,4
	addi R6,R6,4
#dekrementiere den schleifenzeiger und pruefe ob dieser null ist, falls ja,
	rsub R9,R1,R9
	bgti R9,bubble_sort_loop
#dekrementiere die dekrementierte anfangslaenge und preufe ob diese null ist, falls ja
	rsub R4,R1,R4
	bgti R4,bubble_sort_re_init
bubble_sort_ende:
	nop
