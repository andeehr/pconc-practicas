// simulacro 
// 1a
public class Encoder {
    private Frame[] buffer;

    public Buffer(int M) {
        this.buffer = new ArrayList();
    }

    public synchronized void putRawFrame(frame) throws InterruptedException {
        while(isFull()) {
            wait();
        }
        frames.add(frame);
        notify();
    }

    public synchronized Frame[] getPack() throws InterruptedException {
        while(!canGetPack()) {
            wait();
        }
        Frame[] result = frames.remove(P) // asumimos que saca todos
        notifyAll();
        return result;
    }

    private synchronized boolean isFull() {
        return frames.size() == M;
    }

    private synchronized boolean canGetPack() {
        return frames.size() >= P;
    }
}

//1b
public class Encoder {
    private Frame[] buffer;

    public Buffer(int M) {
        this.buffer = new ArrayList();
    }

    public synchronized void putRawFrame(frame) throws InterruptedException {
        while(isFull()) {
            wait();
        }
        frames.add(frame);
        notifyAll();
    }

    public synchronized Frame[] getPack(int p) throws InterruptedException {
        while(!canGetPack(p)) {
            wait();
        }
        Frame[] result = frames.remove(p) // asumimos que saca todos
        notifyAll();
        return result;
    }

    private synchronized boolean isFull() {
        return frames.size() == M;
    }

    private synchronized boolean isEmpty(int p) {
        return frames.size() >= p;
    }
}

//a
process Agencia : {
    while(true) {
        req = agencia.receive();
        reqService = new Request();
        reqService.fecha = req.fecha;
        reqService.chRta = new Channel();
        bool respuesta = true;

        vuelos.send(reqService);
        autos.send(reqService);
        hotel.send(reqService);
        repeat 3 : respuesta && reqService.chRta.receive();
        req.channel.send(respuesta);
    }
}

//b
process Agencia : {
    while(true) {
        req = agencia.receive();
        thread(req) {
            reqService = new Request();
            reqService.fecha = req.fecha;
            reqService.chRta = new Channel();
            bool respuesta = true;

            vuelos.send(reqService);
            autos.send(reqService);
            hotel.send(reqService);
            repeat 3 : respuesta && reqService.chRta.receive();
            req.channel.send(respuesta);
        }
    }
}


//c me mamé
process Agencia : {
    while(true) {
        req = agencia.receive();
        thread(req) {
            
            rtasVuelos = new Channel();
            rtasAutos = new Channel();
            rtasHoteles = new Channel();
            rta = new Channel();

            // lanzo un thread por cada ws de vuelos
            for (v in vuelos) {
                thread {
                    reqService = new Request();
                    reqService.fecha = req.fecha;
                    reqService.chRta = new Channel();
                    v.send(reqService);
                    bool rta = reqService.chRta.receive();
                    rtasVuelos.send(rta); //voy guardando las rtas en un canal dedicado para las rtas de vuelos               
                }
            }

            // idem lógica vuelos
            for (a in autos) {
                thread {
                    reqService = new Request();
                    reqService.fecha = req.fecha;
                    reqService.chRta = new Channel();
                    a.send(reqService);
                    bool rta = reqService.chRta.receive();
                    rtasAutos.send(rta); 
                }
            }

            // idem lógica vuelos
            for (h in hotel) {
                thread {
                    reqService = new Request();
                    reqService.fecha = req.fecha;
                    reqService.chRta = new Channel();
                    h.send(reqService);
                    bool rta = reqService.chRta.receive();
                    rtasHoteles.send(rta); 
                }
            }

            //lanzo un thread por tipo de ws, cada uno manda la rta de su tipo a un canal en común
            thread {
                int i = 0;
                bool hayVuelo = false;
                // mientras no haya consultado todos los ws voy sacando las rtas
                // ante la primer respuesta verdadera, seteo la variable hayVuelo en true con la intención de no seguir chequeando vuelos
                while(!hayVuelo && vuelos.length > i) {
                    hayVuelo = hayVuelo || rtasVuelos.receive();
                    i++;
                }
                rta.send(hayVuelo);
            }
            
            thread {
                // idem lógica vuelos
                int i = 0;
                bool hayAutos = false;
                while(!hayAutos && autos.length > i) {
                    hayAutos = hayAutos || rtasAutos.receive();
                    i++;
                }
                rta.send(hayAutos);
            }

            thread {
                // idem lógica vuelos
                int i = 0;
                bool hayHotel = false;
                while(!hayHotel && hotel.length > i) {
                    hayHotel = hayHotel || rtasHoteles.receive();
                    i++;
                }
                rta.send(hayHotel);
            }

            bool puedeViajar = true;
            int i = 3;
            // en el canal rta tengo las 3 rtas que llegan de forma asincrónica.
            // esto es para cortar el receive ante la primer rta negativa de algún tipo de ws (autos, hotel o vuelos)
            while(puedeViajar && i > 0) {
                puedeViajar = puedeViajar && rta.receive();
                i--
            }
            
            // por último devuelvo la rta
            req.channel.send(puedeViajar);
        }
    }
}

public void sendToSupplier(Channel sendChannel, Channel responseChannel, DateTime date) {
    Request req = new Request();
    req.fecha = date;
    req.channel = responseChannel;
    sendChannel.send(req);
}

public bool hasSupplier(int length, Channel responses) {
    int i = 0;
    bool hasSupplier = false;
    while(!hasSupplier && length > i) {
        hasSupplier = hasSupplier || responses.receive();
        i++;
    }
    return hasSupplier;  
}

//c simplificada
process Agencia : {
    while(true) {
        req = agencia.receive();
        thread(req) {
            
            rtasVuelos = new Channel();
            rtasAutos = new Channel();
            rtasHoteles = new Channel();
            rta = new Channel();

            for (v in vuelos) {
                sendToSupplier(v, rtasVuelos, req.fecha);       
            }

            for (a in autos) {
                sendToSupplier(a, rtasAutos, req.fecha);
            }

            for (h in hotel) {
                sendToSupplier(h, rtasHoteles, req.fecha);
            }

            thread {
                bool hayVuelo = hasSupplier(vuelos.length, rtasVuelos);
                rta.send(hayVuelo);
            }
            
            thread {
                bool hayAutos = hasSupplier(autos.length, rtasAutos);
                rta.send(hayAutos);
            }

            thread {
                bool hayHotel = hasSupplier(hotel.length, rtasHoteles);
                rta.send(hayHotel);
            }

            bool puedeViajar = true;
            int i = 3;
            // en el canal rta tengo las 3 rtas que llegan de forma asincrónica.
            // esto es para cortar el receive ante la primer rta negativa de algún tipo de ws (autos, hotel o vuelos)
            while(puedeViajar && i > 0) {
                puedeViajar = puedeViajar && rta.receive();
                i--
            }
            
            // por último devuelvo la rta
            req.channel.send(puedeViajar);
        }
    }
}

//Ej 3A
public class Telescopio {

    private int observadores = 0;
    private bool calibrando = false; // alcanza un bool, porque solo uno puede calibrar concurrentemente

    public synchronized void iniciarObservacion() {
        while(!puedeObservar()) {
            wait();
        }
        observadores++;
    }

    public synchronized void finalizarObservacion() {
        observadores--;
        notify(); //alcanza un notify, porque no puede haber observadores bloqueados si se inició una observación,
        // ya que la única forma de bloquearse es cuando se está calibrando, y para eso, observadores tiene que ser 0
        // En definitiva, este notify, siempre va a despertar a un calibrador
    }

    public synchronized void iniciarCalibracion() {
        while(!puedeCalibrar()) {
            wait();
        }
        calibrando = true;
    }

    public synchronized void finalizarCalibracion() {
        calibrando = false;
        notify(); //alcanza un notify, porque da lo mismo a quien despierte, ya que cualquiera podría entrar
    }

    private bool puedeCalibrar() {
        return observadores == 0 && !calibrando;
    }

    private bool puedeObservar() {
        return !calibrando;
    }
}


//Ej 3B
public class Telescopio {

    private int observadores = 0;
    private bool calibrando = false;
    private int posicionActual = 1; // inicial

    public synchronized void iniciarObservacion(int posicion) {
        while(!puedeObservar(posicion)) {
            wait();
        }
        posicionActual = posicion;
        observadores++;
    }

    public synchronized void finalizarObservacion() {
        observadores--;
        notify();
        // Sigue alcanzando un notify, ya que mientras observadores > 0 se van a seguir bloqueando por la condición
        // Solo cuando observadores sea = 0, se podría cambiar de posición el telescopio o iniciar una calibración
        // y cualquiera de los dos que se despierten (calibrador u observador) va a poder continuar su ejecución
    }

    public synchronized void iniciarCalibracion() {
        while(!puedeCalibrar()) {
            wait();
        }
        calibrando = true;
    }

    public synchronized void finalizarCalibracion() {
        calibrando = false;
        notify(); //alcanza un notify, porque da lo mismo a quien despierte, ya que cualquiera podría entrar
        // si estoy finalizando una calibración, necesariamente observadores = 0, por eso cualquiera podría continuar su ejecución
    }

    private bool puedeCalibrar() {
        return observadores == 0 && !calibrando;
    }

    private bool puedeObservar(int posicion) {
        return !calibrando && (posicion == posicionActual || observadores == 0);
    }
}

//Ej 3C
public class Telescopio {

    private int observadores = 0;
    private bool calibrando = false;
    private int posicionActual = 1;
    private int calibradoresEsperando = 0;

    public synchronized void iniciarObservacion(int posicion) {
        while(!puedeObservar(posicion)) {
            wait();
        }
        posicionActual = posicion;
        observadores++;
    }

    public synchronized void finalizarObservacion() {
        observadores--;
        notifyAll(); // Ahora sí necesito un notifyAll, porque si llegase a despertar a un observador, este podría volver a 
        // bloquearse ycaer en un deadlock. Entonces necesito despertar al calibrador.
    }

    public synchronized void iniciarCalibracion() {
        calibradoresEsperando++;
        while(!puedeCalibrar()) {
            wait();
        }
        calibradoresEsperando--;
        calibrando = true;
    }

    public synchronized void finalizarCalibracion() {
        calibrando = false;
        notify(); // idem justificación punto B
    }

    private bool puedeCalibrar() {
        return observadores == 0 && !calibrando;
    }

    private bool puedeObservar(int posicion) {
        return !calibrando && (posicion == posicionActual || observadores == 0) && (calibradoresEsperando == 0 || posicion == posicionActual);
    }
}