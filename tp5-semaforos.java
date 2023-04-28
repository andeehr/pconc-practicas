// Ejercicio 1. Modelar con sem ́aforos un transbordador entre dos costas. El transbordador tiene
// capacidad para N personas y funciona de la siguiente manera:
// Empieza en la costa este.
// Espera en una costa hasta llenarse.
// Viaja hasta la costa oeste.
// Amarra, permitiendo que los pasajeros desciendan.
// Repite el procedimiento desde el principio en la costa actual.
// Considere que:
// El transbordador debe ser modelado como un thread.
// Cada persona debe ser modelada como un thread (independiente del transbordador).
// El resto de los elementos del problema pueden ser modelados con sem ́aforos (capacidad,
// permiso para abordar, permiso para descender, etc).

global int N = 50;
global Semaphore[] permisoAbordar = new Semaphore[2];
global Semaphore permisoAbordar[0] = new Semaphore(0);
global Semaphore permisoAbordar[1] = new Semaphore(0);
global Semaphore permisoDescender = new Semaphore(0);
global Semaphore asientosVacios = new Semaphore(N);
global Semaphore asientosOcupados = new Semaphore(0);

thread Transbordador {
    int costa = 0; 
    while(true) {
        repeat(N): permisoAbordar[costa].release();
        costa = (costa + 1) % 2; //destino

        repeat(N): asientosOcupados.acquire();
        viajar(costa);
        repeat(N): permisoDescender.release();
        repeat(N): asientosOcupados.release();
    }
}

thread Persona(costaOrigen) {

    permisoAbordar[costaOrigen].acquire();
    subir();
    asientosOcupados.release();

    permisoDescender.acquire();
    bajar();
    asientosOcupados.acquire();
}

//hydra
global int N = 10;
global Semaphore[] permisoAbordar = new Semaphore[2];
permisoAbordar[0] = new Semaphore(0);
permisoAbordar[1] = new Semaphore(0);
global Semaphore permisoDescender = new Semaphore(0);
global Semaphore asientosOcupados = new Semaphore(0);

thread Transbordador: {
    int costa = 0; 
    while(true) {
        
        repeat(N) permisoAbordar[costa].release();
        costa = (costa + 1) % 2; //destino

        print("Aguardando pasajeros...");
        repeat(N) asientosOcupados.acquire();
        //viajar(costa);
        print("Viajando...");
        repeat(N) permisoDescender.release();
        repeat(N) asientosOcupados.release();
    }
}

public void Persona (int costaOrigen, String nombre) {
  thread {

    print(nombre + " Aguarando para subir a costa " + costaOrigen);
    permisoAbordar[costaOrigen].acquire();
    //subir();
    print(nombre + " subio al barco desde " + costaOrigen);
    asientosOcupados.release();

    print(nombre + " aguardando para descender... ");
    permisoDescender.acquire();
    //bajar();
    print(nombre + " descendio ");
    asientosOcupados.acquire();
    }
}

Persona(0, "Aldo");
Persona(0, "Beto");
Persona(0, "Carla");
Persona(0, "Daniela");
Persona(0, "Eugenia");
Persona(1, "Franco");
Persona(1, "Gabriela");
Persona(0, "Horacio");
Persona(1, "Ines");
Persona(0, "Jemina");
Persona(1, "Karina");
Persona(0, "Lien");
Persona(0, "Marta");
Persona(1, "Nora");
Persona(1, "Omar");
Persona(0, "Qori");
Persona(1, "Roberta");
Persona(0, "Santiago");
Persona(0, "Teresa");
Persona(0, "Ulises");
Persona(0, "Victoria");
Persona(0, "Walter");
Persona(0, "Ximena");
Persona(0, "Yvonne");
Persona(0, "Zaira");

// Ejercicio 2. En un gimnasio hay cuatro aparatos, cada uno para trabajar un grupo muscular
// distinto. Los aparatos son cargados con discos (el gimnasio cuenta con 20 discos, todos del mismo
// peso). Cada cliente del gimnasio posee una rutina que le indica qu ́e aparatos usar, en qu ́e orden
// y cuanto peso utilizar en cada caso (asuma que la rutina es una lista de tuplas con el n ́umero de
// aparato a usar y la cantidad de discos cargar, la rutina podr ́ıa incluir repeticiones de un mismo
// aparato). Como norma el gimnasio exige que cada vez que un cliente termina de utilizar un
// aparato descargue todos los discos y los coloque en el lugar destinado a su almacenamiento (lo
// que incluye usos consecutivos del mismo aparato).
// a) Indique cuales son los recursos compartidos y roles activos.
// b) Escriba un c ́odigo que simule el funcionamiento del gimnasio, garantizando exclusi ́on mutua
// en el acceso a los recursos compartidos y que est ́e libre de deadlock y livelock.
// Ayuda: Considere modelar a los clientes como threads (cada uno con su propia rutina) y a
// los aparatos como un arreglo de sem ́aforos.

//4 aparatos
//20 discos

global Semaphore[] puedeUsarAparato = new Semaphore[4];
puedeUsarAparato[0] = new Semaphore(1);
puedeUsarAparato[1] = new Semaphore(1);
puedeUsarAparato[2] = new Semaphore(1);
puedeUsarAparato[3] = new Semaphore(1);
global Semaphore puedeUsarDiscos = new Semaphore(20);
global Semaphore mutexDiscos = new Semaphore(1);

public void Cliente (ArrayList rutinas, String nombre) {
  thread {
                //(id, discos)
    //rutinaEj = [(0, 4), (1, 6), (2, 8)];
    int index = 0;
    repeat(rutinas.length) {
        Rutina current = rutinas[index];
        puedeUsarAparato[current.getAparatoId()].acquire();
        mutexDiscos.acquire();
        repeat(current.getCantidadDiscos()) puedeUsarDiscos.acquire();
        mutexDiscos.release();
        //ejercicio
        mutexDiscos.acquire();
        repeat(current.getCantidadDiscos()) puedeUsarDiscos.release();
        mutexDiscos.release();
        puedeUsarAparato[current.getAparatoId()].release();
    }
  }
}

// debuger casero
//4 aparatos
//20 discos
global Semaphore[] puedeUsarAparato = new Semaphore[4];
puedeUsarAparato[0] = new Semaphore(1);
puedeUsarAparato[1] = new Semaphore(1);
puedeUsarAparato[2] = new Semaphore(1);
puedeUsarAparato[3] = new Semaphore(1);
global Semaphore puedeUsarDiscos = new Semaphore(20);
global Semaphore mutexDiscos = new Semaphore(1);

public void Cliente (ArrayList rutinas, String nombre) {
  thread {
                //(id, discos)
    //rutinaEj = [(0, 4), (1, 6), (2, 8)];
    int index = 0;
    repeat(rutinas.length) {
        Rutina current = rutinas[index];
        puedeUsarAparato[current.getAparatoId()].acquire(); <- t2
        mutexDiscos.acquire();
        repeat(current.getCantidadDiscos()) puedeUsarDiscos.acquire(); <- t4 (faltan 2)
        mutexDiscos.release();
        //ejercicio <- t1, t3
        mutexDiscos.acquire();
        repeat(current.getCantidadDiscos()) puedeUsarDiscos.release();
        mutexDiscos.release();
        puedeUsarAparato[current.getAparatoId()].release();
    }
  }
}

t1 aparato 0, 8 discos
t2 aparato 0, 4 discos
t3 aparato 1, 10 discos
t4 aparato 2, 4 discos

// Ejercicio 3. Para balancear el g ́enero entre sus asistentes, un boliche decidi ́o implementar el
// siguiente mecanismo de control de acceso: No se permitir ́a que la diferencia entre cantidades de
// hombres y mujeres en el establecimiento sea mayor que 1. Cada persona que llegue al boliche
// debe asegurarse de que tiene permitido el acceso. Si no lo tiene, esperar ́a hasta que finalmente
// pueda hacerlo. Una vez que una persona entr ́o al boliche, se quedar ́a bailando indefinidamente.
// a) Modele este comportamiento utilizando Sem ́aforos.

//a)

global Semaphore[] puedeIngresar = new Semaphore[2];
puedeIngresar[0] = new Semaphore(1);
puedeIngresar[1] = new Semaphore(1);

public void Persona (int genero, String nombre) {
  thread {
    int otro = (genero + 1) % 2
    puedeIngresar[genero].acquire();
    puedeIngresar[otro].release();
    while(true) {
        //bailar
    }
  }
}

// hydra
global Semaphore[] puedeIngresar = new Semaphore[2];
puedeIngresar[0] = new Semaphore(1);
puedeIngresar[1] = new Semaphore(0);

public void Persona (int genero, String nombre) {
  thread {
    int otro = (genero + 1) % 2;
    puedeIngresar[genero].acquire();
    print(nombre + " ingreso al cheboli");
    puedeIngresar[otro].release();
    while(true) {
        //baila forever
    }
  }
}

Persona(0, "Aldo");
Persona(0, "Beto");
Persona(1, "Carla");
Persona(1, "Daniela");
Persona(1, "Eugenia");
Persona(0, "Franco");
Persona(1, "Gabriela");
Persona(0, "Horacio");
Persona(1, "Ines");
Persona(1, "Jemina");
Persona(1, "Karina");
Persona(1, "Lien");
Persona(1, "Marta");
Persona(1, "Nora");
Persona(0, "Omar");
Persona(0, "Qori");
Persona(1, "Roberta");
Persona(0, "Santiago");
Persona(1, "Teresa");
Persona(0, "Ulises");
Persona(1, "Victoria");
Persona(0, "Walter");
Persona(1, "Ximena");
Persona(1, "Yvonne");
Persona(1, "Zaira");

// b) Extienda la soluci ́on anterior contemplando que el boliche tiene una capacidad de 50 per-
// sonas. Cada persona que logra entrar al boliche ocupa un lugar, y como estos nunca se
// liberan, en alg ́un momento el boliche estar ́a lleno. En ese momento, toda persona que lle-
// gue deber ́a retirarse sin tener la posibilidad de ingreso. Aseg ́urese, tambi ́en, que ninguna
// persona se quede esperando indefinidamente.
//b)
// hydra
global int capacidad = 50;
global int personas = 0;
global Semaphore[] puedeIngresar = new Semaphore[2];
global Semaphore[] permisoChequear = new Semaphore[2];
puedeIngresar[0] = new Semaphore(1);
puedeIngresar[1] = new Semaphore(0);
permisoChequear[0] = new Semaphore(1);
permisoChequear[1] = new Semaphore(0);
global Semaphore mutexSuma = new Semaphore(1);

public void Persona (int genero, String nombre) {
  thread {
    int otro = (genero + 1) % 2;

    permisoChequear[genero].acquire();
    print("capacidad: " + capacidad);
    print("personas: " + personas);
    if(capacidad == personas) {
        print(nombre + " se fue");
        return;
    }

    puedeIngresar[genero].acquire();
    mutexSuma.acquire();
    personas++;
    mutexSuma.release();
    permisoChequear[otro].release();
    print(nombre + " ingreso al cheboli");
    puedeIngresar[otro].release();
    while(true) {
        //baila forever
    }
  }
}

// Ejercicio 4. Se desea implementar un sistema de control para una estaci´on de servicio con 6
// puestos de carga. El sistema debe garantizar que en ning´un momento puede haber m´as de 6
// veh´ıculos cargando combustible y que la atenci´on se produce en orden de llegada. Adem´as, la
// estaci´on cuenta con 1 puesto para el abastecimiento de combustible que es provisto por camiones
// que arriban con mucha menor frecuencia que los clientes. Por lo tanto se requiere garantizar que
// no m´as de un cami´on pueda descargar combustible al mismo tiempo.
// a) Identifique los roles activos y los recursos compartidos.
// b) De una soluci´on considerando que el abastecimiento de combustible no puede hacerse al
// mismo tiempo que la carga a clientes y que los camiones tienen prioridad por sobre los
// veh´ıculos. ¿Es su soluci´on libre de inanici´on?

global Semaphore mutexP = new Semaphore(1);
global Semaphore permisoV = new Semaphore(1);
global Semaphore permisoC = new Semaphore(1);
global Semaphore mutexV = new Semaphore(1);
global Semaphore mutexC = new Semaphore(1);
global Semaphore permisoCombustible = new Semaphore(6);
global int vehiculos = 0;
global int camiones = 0;

public void Vehiculo (String marca) {
  thread {

    mutexP.acquire();
    permisoV.acquire();
    mutexV.acquire();
    vehiculos++;
    if (vehiculos == 1) {
      permisoC.acquire();
    }
    mutexV.release();
    permisoV.release();
    mutexP.release();
    permisoCombustible.acquire();
    // cargar combustible
    print(marca + " esta cargando combustible");
    permisoCombustible.release();
    mutexV.acquire();
    vehiculos--;
    if (vehiculos == 0) {
      permisoC.release();
    }
    mutexV.release();

  }
}

public void Camion (String marca) {
  thread {

    mutexC.acquire();
    camiones++;
    if (camiones == 1) {
      permisoV.acquire();
    }
    mutexC.release();

    permisoC.acquire();
    //abastecer
    print(marca + " esta abasteciendo");
    permisoC.release();

    mutexC.acquire();
    camiones--;
    if (camiones == 0) {
      permisoV.release();
    }
    mutexC.release();

  }
}

//para visualizar en hydra
global Semaphore mutexP = new Semaphore(1);
global Semaphore permisoV = new Semaphore(1);
global Semaphore permisoC = new Semaphore(1);
global Semaphore mutexV = new Semaphore(1);
global Semaphore mutexC = new Semaphore(1);
global Semaphore mutexVC = new Semaphore(1);
global Semaphore permisoCombustible = new Semaphore(6);
global int vehiculos = 0;
global int camiones = 0;
global int vehiculosCargando = 0;

public void Vehiculo (String marca) {
  thread {

    mutexP.acquire();
    permisoV.acquire();
    mutexV.acquire();
    vehiculos++;
    if (vehiculos == 1) {
      permisoC.acquire();
    }
    mutexV.release();
    permisoV.release();
    mutexP.release();
    permisoCombustible.acquire();
    mutexVC.acquire();
    vehiculosCargando++;
    mutexVC.release();
    print(vehiculosCargando);
    // cargar combustible
    print(marca + " esta cargando combustible");
    permisoCombustible.release();
    mutexVC.acquire();
    vehiculosCargando--;
    mutexVC.release();
    mutexV.acquire();
    vehiculos--;
    if (vehiculos == 0) {
      permisoC.release();
    }
    mutexV.release();

  }
}

public void Camion (String marca) {
  thread {

    mutexC.acquire();
    camiones++;
    if (camiones == 1) {
      permisoV.acquire();
    }
    mutexC.release();

    permisoC.acquire();
    //abastecer
    print(marca + " esta abasteciendo");
    permisoC.release();

    mutexC.acquire();
    camiones--;
    if (camiones == 0) {
      permisoV.release();
    }
    mutexC.release();

  }
}

Vehiculo("Aldo");
Vehiculo("Beto");
Vehiculo("Carla");
Vehiculo("Daniela");
Vehiculo("Eugenia");
Vehiculo("Franco");
Vehiculo("Gabriela");
Vehiculo("Horacio");
Vehiculo("Ines");
Vehiculo("Jemina");
Vehiculo("Karina");
Vehiculo("Lien");
Vehiculo("Marta");
Vehiculo("Nora");
Vehiculo("Omar");
Vehiculo("Qori");
Vehiculo("Roberta");
Vehiculo("Santiago");
Vehiculo("Teresa");
Vehiculo("Ulises");
Vehiculo("Victoria");
Vehiculo("Walter");
Vehiculo("Ximena");
Vehiculo("Yvonne");
Vehiculo("Zaira");

Camion("Aldo");
Camion("Beto");
Camion("Carla");
Camion("Daniela");
Camion("Eugenia");
Camion("Franco");
Camion("Gabriela");
Camion("Horacio");
Camion("Ines");
Camion("Jemina");
Camion("Karina");
Camion("Lien");
Camion("Marta");
Camion("Nora");
Camion("Omar");
Camion("Qori");
Camion("Roberta");
Camion("Santiago");
Camion("Teresa");
Camion("Ulises");
Camion("Victoria");
Camion("Walter");
Camion("Ximena");
Camion("Yvonne");
Camion("Zaira");

// Ejercicio 5. En una oficina hay un ba˜no unisex con 8 toiletes. A lo largo del d´ıa, distintas
// personas entran a utilizarlo. Si sucede que en ese momento todos los toiletes est´an ocupados,
// las personas esperan hasta que alguno se libere. Por otra parte, peri´odicamente el personal de
// limpieza debe pasar a mantener las instalaciones en condiciones. La limpieza del ba˜no no se
// puede hacer mientras haya gente dentro del mismo, por lo que si en ese momento hay personas
// utilizando alg´un toilete o esperando que se libere alguno, el personal de limpieza debe esperar a
// que el ba˜no se vac´ıe completamente. En contraparte, si hay un empleado de limpieza trabajando
// en el ba˜no, las personas que quieran utilizarlo deber´an esperar a que termine.
// a) Modele esta situaci´on utilizando sem´aforos como mecanismo de sincronizaci´on (puede modelar al personal de limpieza como un ´unico thread).
// b) Modifique la soluci´on anterior para contemplar el caso donde el personal de limpieza tiene
// prioridad. Es decir, si hay un empleado de limpieza esperando para hacer el mantenimiento,
// las siguientes personas que lleguen deben esperar a que logre terminar la limpieza.

// Ejercicio 6. Se desea modelar el control de tr´ansito de un puente que conecta dos ciudades.
// Dado que el puente es muy estrecho se debe evitar que dos autos circulen al mismo tiempo en
// direcci´on opuesta, dado que quedar´ıan atascados.
// Resuelva los siguientes problemas usando sem´aforos, modelando cada coche como un thread
// independiente que desea atravesar el puente en alguna de las dos direcciones posibles. Tenga
// en cuenta que atravesar el puente no es una acci´on at´omica, y por lo tanto, requiere de cierto
// tiempo.

// a) De una soluci´on que permita que varios coches que se desplazan en la misma direcci´on
// puedan circular simult´aneamente.
// b) Modifique la soluci´on anterior para que como m´aximo 3 coches puedan circular por el
// puente al mismo tiempo.
// c) Indique si la soluci´on propuesta en el punto b es libre de inanici´on. Justifique su respuesta.

//a
global int autosCirculando = 0;
global Semaphore[] permisoCircular = new Semaphore[2];
permisoCircular[0] = new Semaphore(1);
permisoCircular[1] = new Semaphore(1);
global Semaphore mutexSuma = new Semaphore(1);

public void Vehiculo (int direccion, String conductor) {
  //0: Norte, 1: Sur
  int opuesta = (direccion + 1) % 2;
  thread {
    permisoCircular[direccion].acquire();
    mutexSuma.acquire();
    autosCirculando++;
    if (autosCirculando == 1) {
      permisoCircular[opuesta].acquire();
    }
    permisoCircular[direccion].release();
    mutexSuma.release();

    //atravesar el puente
    print(conductor + " esta atravesando el puente en direccion " + direccion + " con " + (autosCirculando-1) + " autos mas");

    mutexSuma.acquire();
    autosCirculando--;
    if (autosCirculando == 0) {
      permisoCircular[opuesta].release();
    }
    mutexSuma.release();
  }
}

//b

global int autosCirculando = 0;
global Semaphore[] permisoCircular = new Semaphore[2];
permisoCircular[0] = new Semaphore(1);
permisoCircular[1] = new Semaphore(1);
global Semaphore mutexSuma = new Semaphore(1);
global Semaphore permisoCantidad = new Semaphore(3);

public void Vehiculo (int direccion, String conductor) {
  //0: Norte, 1: Sur
  int opuesta = (direccion + 1) % 2;
  thread {
    permisoCircular[direccion].acquire();
    permisoCantidad.acquire();
    mutexSuma.acquire();
    autosCirculando++;
    if (autosCirculando == 1) {
      permisoCircular[opuesta].acquire();
    }
    permisoCircular[direccion].release();
    mutexSuma.release();

    //atravesar el puente
    print(conductor + " esta atravesando el puente en direccion " + direccion + " con " + (autosCirculando-1) + " autos mas");

    permisoCantidad.release();
    mutexSuma.acquire();
    autosCirculando--;
    if (autosCirculando == 0) {
      permisoCircular[opuesta].release();
    }
    mutexSuma.release();
  }
}

Vehiculo(0, "Aldo");
Vehiculo(0, "Beto");
Vehiculo(0, "Carla");
Vehiculo(0, "Daniela");
Vehiculo(0, "Eugenia");
Vehiculo(1, "Franco");
Vehiculo(1, "Gabriela");
Vehiculo(0, "Horacio");
Vehiculo(1, "Ines");
Vehiculo(0, "Jemina");
Vehiculo(1, "Karina");
Vehiculo(0, "Lien");
Vehiculo(0, "Marta");
Vehiculo(1, "Nora");
Vehiculo(1, "Omar");
Vehiculo(0, "Qori");
Vehiculo(1, "Roberta");
Vehiculo(0, "Santiago");
Vehiculo(0, "Teresa");
Vehiculo(0, "Ulises");
Vehiculo(0, "Victoria");
Vehiculo(0, "Walter");
Vehiculo(0, "Ximena");
Vehiculo(0, "Yvonne");
Vehiculo(0, "Zaira");