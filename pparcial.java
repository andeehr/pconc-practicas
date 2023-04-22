
//a
Semaphore[] permisoAvanzar = new Semaphore[5] {0,0,0,0,0}
Semaphore[] permisoLavar = new Semaphore[5] {0,0,0,0,0}
Semaphore[] permisoMaquina = new Semaphore[5] {1,1,1,1,1}

thread maquina(i) {
    while(true) {
        permisoLavar[i].acquire();
        // accion
        permisoAvanzar[i].release();
    }
}

thread auto {
    for (i : range(0,5)) {
        permisoMaquina[i].acquire();
        //avanza a maq i
        if (i > 0) {
            permisoMaquina[i-1].release();
        } 
        permisoLavar[i].release();
        permisoAvanzar.acquire();
    }
    permisoMaquina[4].release();
}

//b

thread robot(j) {
    while(true) {
        permisoSubir[j].acquire();
        //aspirar
        
    }
}

thread maquina(i) {
    while(true) {
        permisoLavar[i].acquire();
        // accion
        permisoAvanzar[i].release();
    }
}

thread auto {
    for (i : range(0,5)) {
        permisoMaquina[i].acquire();
        //avanza a maq i
        if (i > 0) {
            permisoMaquina[i-1].release();
        } 
        permisoLavar[i].release();
        permisoAvanzar.acquire();
    }
    permisoMaquina[4].release();
}

// demostrar gtia de entrada
// 1. hay al menos un thread en sc?
// 2. cada vez que un thraed sale, habilita al menos a uno thread a entrar a la sc?

//1. supongamos que hay un unico thread, entonces como flag izq y flag der estan en false, por lo tanto no entraria al while. supongamos que hay dos threads (o tres) entran por el turno.
//2. Como al salir cada thread apaga su flag, entonces los que esperaban en el while pueden entrar porque el que sale es el izq y el der respectivo de los otros 2 threads. La condición de los que estaban esperando (izq o der) pasa a dar false y puede entrar.