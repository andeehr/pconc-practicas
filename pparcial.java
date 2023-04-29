
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

//Parcial 2

// Un concurrido club de barrio tiene a su disposici´on 6 canchas de f´utbol que alquila a
// los grupos de gente que se presentan con la intenci´on de jugar un partido. El club no
// tiene un sistema de reservas, por lo que las canchas se asignan a los grupos en orden
// de llegada. A veces la cantidad de personas en el grupo es grande y se le debe asignar
// m´as de una cancha. Una vez que el grupo termina de jugar, deja la(s) cancha(s), se
// acerca al mostrador donde paga por el tiempo usado, y luego puede retirarse. En el
// mostrador se atiende de a un grupo de gente por vez.
// 1. Modele este comportamiento por medio de threads. Considere a cada grupo de
// personas un thread que tiene por par´ametro la cantidad de canchas que va a
// requerir para jugar. Preste atenci´on a que los pedidos siempre puedan cumplirse.
// 2. Modifique la soluci´on anterior considerando que cada vez que una cancha se libera,
// se le debe realizar un mantenimiento m´ınimo antes de poder ser alquilada por el
// siguiente grupo. El resto de las canchas no se ve afectada por el mantenimiento.
// Hay una sola persona dedicada a esta tarea, por lo que cuando un grupo libera
// m´ultiples canchas se les har´a el mantenimiento de a una. No es necesario que las
// canchas est´en identificadas individualmente.
// 3. A veces llega al club un grupo mayor de personas con la intenci´on de armar un
// torneo. Para jugar un torneo se requieren las 6 canchas, por lo que no pueden
// haber torneos en simult´aneo con uso de las canchas por parte de grupos. Los
// torneos tienen prioridad sobre los grupos menores, por lo que si hay al menos un
// torneo en espera los grupos de personas que reci´en lleguen deber´an esperar a que
// todos los torneos terminen para poder jugar. Modele este nuevo comportamiento
// con los threads que considere necesarios.

//1
global Semaphore permisoCanchas = new Semaphore(6); 
global Semaphore permisoAbonar = new Semaphore(1);
global Semaphore mutexCanchas = new Semaphore(1, true); //las canchas hay que pedirlas en exclusión mutua y fuerte para respetar el orden de llegada

public void GrupoPersonas (int canchas) {
  
  thread {

    mutexCanchas.acquire();
    repeat(canchas) permisoCanchas.acquire();
    mutexCanchas.release();
    //jugar
    mutexCanchas.acquire();
    repeat(canchas) permisoCanchas.release();
    mutexCanchas.release();
    permisoAbonar.acquire();
    //abonar
    permisoAbonar.release();
  }
}

//2
global Semaphore permisoCanchas = new Semaphore(6); 
global Semaphore permisoAbonar = new Semaphore(1);
global Semaphore mutexCanchas = new Semaphore(1, true); //las canchas hay que pedirlas en exclusión mutua y fuerte para respetar el orden de llegada
global Semaphore permisoLimpiar = new Semaphore(0);

thread PersonalLimpieza: {
    while(true) {
        
        permisoLimpiar.acquire();
        //Limpiar cancha
        permisoCanchas.release();

    }
}


public void GrupoPersonas (int canchas) {
  
  thread {

    mutexCanchas.acquire();
    repeat(canchas) permisoCanchas.acquire();
    mutexCanchas.release();
    //jugar
    mutexCanchas.acquire();
    repeat(canchas) permisoLimpiar.release();
    mutexCanchas.release();
    permisoAbonar.acquire();
    //abonar
    permisoAbonar.release();
  }
}

//3
global Semaphore permisoCanchas = new Semaphore(6); 
global Semaphore permisoAbonar = new Semaphore(1);
global Semaphore mutexCanchas = new Semaphore(1, true); //las canchas hay que pedirlas en exclusión mutua y fuerte para respetar el orden de llegada
global Semaphore permisoLimpiar = new Semaphore(0);
global Semaphore permisoGrupoMenor = new Semaphore(1);
global Semaphore mutexT = new Semaphore(1);
global Semaphore mutexP = new Semaphore(1);
global int torneos = 0;

thread PersonalLimpieza: {
    while(true) {
        
        permisoLimpiar.acquire();
        //Limpiar cancha
        permisoCanchas.release();

    }
}


public void GrupoPersonas (int canchas) {
  
  thread {

    mutexP.acquire();
    permisoGrupoMenor.acquire();
    mutexCanchas.acquire();
    repeat(canchas) permisoCanchas.acquire();
    mutexCanchas.release();
    permisoGrupoMenor.release();
    mutexP.release();
    //jugar
    mutexCanchas.acquire();
    repeat(canchas) permisoLimpiar.release();
    mutexCanchas.release();
    permisoAbonar.acquire();
    //abonar
    permisoAbonar.release();
  }
}

public void GrupoTorneo() {

    thread {

        mutexT.acquire();
        torneos++;
        if (torneos == 1) {
            permisoGrupoMenor.acquire();
        }
        mutexT.release();

        mutexCanchas.acquire();
        repeat(6) permisoCanchas.acquire();
        mutexCanchas.release();
        //jugar
        repeat(6) permisoLimpiar.release();

        mutexT.acquire();
        torneos--;
        if (torneos == 0) {
            permisoGrupoMenor.release();
        }
        mutexT.release();

    }
}