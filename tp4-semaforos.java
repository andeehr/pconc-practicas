// 1 
// Utilizar sem´aforos para garantizar que, simult´aneamente, ‘A’ se muestra antes que ‘F’, y ‘F’
// se muestra antes que ‘C’.


global Semaphore permisoF = new Semaphore(0);
global Semaphore permisoC = new Semaphore(0);

thread {
    print ('A');
    permisoF.release();
    print ('B');
    permisoC.acquire();
    print ('C');
}

thread {
    print ('E');
    permisoF.acquire();
    print ('F');
    permisoC.release();
    print ('G');
}

// 2 Utilizar sem´aforos para garantizar que las ´unicas salidas posibles sean ACERO y ACREO

global Semaphore permisoC = new Semaphore(0);
global Semaphore permisoR = new Semaphore(0);

thread {
    permisoC.acquire();
    print ('C');
    permisoR.release();
    print ('E');
}

thread {
    print ('A');
    permisoC.release();
    permisoR.acquire();
    print ('R');
    print ('O');
}

// 3 Utilizar sem´aforos para garantizar que el ´unico resultado impreso ser´a R I O OK OK OK
// (asumimos que print es at´omico).

global Semaphore permisoI = new Semaphore(0);
global Semaphore permisoO = new Semaphore(0);
global Semaphore permisoOK = new Semaphore(0);

thread {
    print ("R");
    permisoI.release();
    permisoOK.acquire();
    print ("OK");
}

thread {
    permisoI.acquire();
    print ("I");
    permisoO.release();
    permisoOK.acquire();
    print ("OK");
}

thread {
    permisoO.acquire();
    print ("O");
    repeat (2) {
        permisoOK.release();
    }
    print ("OK");
}

// 4 Agregar sem´aforos para garantizar que simult´aneamente se den las siguientes condiciones:
// La cantidad de ‘F’ es menor o igual a la cantidad de ‘A’.
// La cantidad de ‘H’ es menor o igual a la cantidad de ‘E’.

global Semaphore permisoF = new Semaphore(0);
global Semaphore permisoH = new Semaphore(0);
global Semaphore permisoC = new Semaphore(0);

thread {
    while(true) {
        print("A");
        permisoF.release();
        print("B");
        permisoC.acquire();
        print("C");
        print("D");
    }
}

thread {
    while(true) {
        print("E");
        permisoH.release();
        permisoF.acquire();
        print("F");
        print("G");
        permisoC.release();
    }
}

thread {
    while(true) {
        permisoH.acquire();
        print("H");
        print("I");
    }
}

// 5 Garantice, por medio del uso de sem´aforos, que la ejecuci´on del programa no pierde sumas
// (es decir, al finalizar la ejecuci´on de los tres threads, el valor final de x debe ser 6). Considere la
// posibilidad de que alguno de los threads no se ejecute, en ese caso los threads restantes deben
// poder finalizar su ejecuci´on sin quedarse bloqueados (y el valor de x debe ser la suma de sus
// incrementos).

global int x = 0;
global Semaphore permisoS = new Semaphore(1);

thread {
    permisoS.acquire();
    x = x + 1;
    permisoS.release();
}

thread {
    permisoS.acquire();
    x = x + 2;
    permisoS.release();
}

thread {
    permisoS.acquire();
    x = x + 3;
    permisoS.release();
}

// 6. Considere los siguientes threads que comparten dos variables y y z.
// a) ¿Cu´ales son los posibles valores finales de x?
//    0, 1, 3
// b) Para cada uno de los valores finales de x posibles, modifique el programa usando sem´aforos
// de forma tal que siempre x tenga ese valor al final de la ejecuci´on (considere que el programa
// modificado siempre debe poder terminar).

// para x = 0;
global Semaphore permisoAsignarYZ = new Semaphore(0);

global int y = 0;
global int z = 0;

thread {
    int x;
    x = y + z;
    print(x);
    permisoAsignarYZ.release();
}

thread {
    permisoAsignarYZ.acquire();
    y = 1;
    z = 2;
}

// para x = 1;
global Semaphore permisoAsignarX = new Semaphore(0);
global Semaphore permisoAsignarZ = new Semaphore(0);

global int y = 0;
global int z = 0;

thread {
    int x;
    permisoAsignarX.acquire();
    x = y + z;
    print(x);
    permisoAsignarZ.release();
}

thread {
    y = 1;
    permisoAsignarX.release();
    permisoAsignarZ.acquire();
    z = 2;
}

thread {
    permisoAsignarYZ.acquire();
    y = 1;
    z = 2;
}

// para x = 3;
global Semaphore permisoAsignarX = new Semaphore(0);

global int y = 0;
global int z = 0;

thread {
    int x;
    permisoAsignarX.acquire();
    x = y + z;
    print(x);
}

thread {
    y = 1;
    z = 2;
    permisoAsignarX.release();
}

// 7. Considere los siguientes dos threads:
// a) Utilice sem´aforos para garantizar que en todo momento la cantidad de A y B difiera como
// m´aximo en 1.

global Semaphore permisoA = new Semaphore(1);
global Semaphore permisoB = new Semaphore(1);

thread {
    while(true) {
        permisoA.acquire();
        print("A");
        permisoB.release();
    }
}

thread {
    while(true) {
        permisoB.acquire();
        print("B");
        permisoA.release();
    }
}

// b) Modifique la soluci´on para que la ´unica salida posible sea ABABABABAB...
global Semaphore permisoA = new Semaphore(1);
global Semaphore permisoB = new Semaphore(0);

thread {
    while(true) {
        permisoA.acquire();
        print("A");
        permisoB.release();
    }
}

thread {
    while(true) {
        permisoB.acquire();
        print("B");
        permisoA.release();
    }
}

// 8. Los siguientes threads cooperan para calcular el valor N2 que es la suma de los
// primeros N n´umeros impares. Los procesos comparten las variables N y N2 inicializadas de la
// siguiente manera: 
// D´e una soluci´on que utilizando sem´aforos garantice que se muestra el valor correcto de N2.

global int n = 50;
global int n2 = 0;
global Semaphore permisoSumar = new Semaphore(0);
global Semaphore permisoDisminuirN = new Semaphore(1);

thread {
    while (n > 0) {
        permisoDisminuirN.acquire();
        n = n - 1;
        permisoSumar.release();
    }

    print (n2);
}

thread {
    while (true) {
        permisoSumar.acquire();
        n2 = n2 + 2 * n + 1;
        permisoDisminuirN.release();
    }
}

// v2

global int n = 7;
global int n2 = 0;
global Semaphore permisoSumar = new Semaphore(0);
global Semaphore permisoDisminuirN = new Semaphore(1);
global Semaphore permisoImprimir = new Semaphore(0);

thread {
    while (n > 0) {
        permisoDisminuirN.acquire();
        n = n - 1;
        permisoSumar.release();
        permisoImprimir.acquire();
    }

    print (n2);
}

thread {
    while (true) {
        permisoSumar.acquire();
        n2 = n2 + 2 * n + 1;
        permisoImprimir.release();
        permisoDisminuirN.release();
    }
}

// v3

global int n = 7;
global int n2 = 0;
global Semaphore permisoSumar = new Semaphore(0);
global Semaphore permisoDisminuirN = new Semaphore(1);
global Semaphore permisoImprimir = new Semaphore(0);

thread {
    int counter = n;
    while (n > 0) {
        permisoDisminuirN.acquire();
        n = n - 1;
        permisoSumar.release();
    }

    repeat(counter) {
        permisoImprimir.acquire();
    }
 
    print (n2);
}

thread {
    while (true) {
        permisoSumar.acquire();
        n2 = n2 + 2 * n + 1;
        permisoImprimir.release();
        permisoDisminuirN.release();
    }
}

// sincrónico

global int n = 50;
global int result = 0;

thread {
    int i = 0;

    while (i < n) {
        if (i % 2 == 0) {
            result += i;
        }
        i++;
    }
    print (result);
}

global int n = 7;
global int n2 = 0;

thread {
    while (n > 0) {
        n = n - 1;
        n2 = n2 + 2 * n + 1;
    }
    print (n2);
}