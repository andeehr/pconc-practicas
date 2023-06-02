// simulacro 
// 1a
public class Encoder {
    private Frame[] frames;

    public Encoder(int M) {
        this.frames = new ArrayList();
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
    private Frame[] frames;

    public Encoder(int M) {
        this.frames = new ArrayList();
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

    private synchronized boolean canGetPack(int p) {
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
        if(observadores == 0) {
            notify(); //alcanza un notify, porque no puede haber observadores bloqueados si se inició una observación,
            // ya que la única forma de bloquearse es cuando se está calibrando, y para eso, observadores tiene que ser 0
            // En definitiva, este notify, siempre va a despertar a un calibrador
            // El if es para evitar despertar un calibrador y que vuelva a bloquearse
        }
    }

    public synchronized void iniciarCalibracion() {
        while(!puedeCalibrar()) {
            wait();
        }
        calibrando = true;
    }

    public synchronized void finalizarCalibracion() {
        calibrando = false;
        notifyAll(); // Es necesario el notifyAll, porque puede haber múltiples observadores esperando y todos podrían continuar su ejecución
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
        if(observadores == 0) {
            notifyAll(); //Ahora es necesario despertar a todos, porque no solo puede haber un calibrador esperando,
            // sino que puede también haber múltiples observadores intentando querer observar en otra dirección
            // El if sigue valiendo, porque cualquiera que se despierte si observadores > 0 se va a volver a bloquear
        }
    }

    public synchronized void iniciarCalibracion() {
        while(!puedeCalibrar()) {
            wait();
        }
        calibrando = true;
    }

    public synchronized void finalizarCalibracion() {
        calibrando = false;
        notifyAll(); //Idem A
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
        if(observadores == 0) {
            notifyAll(); //Idem B. Además si llegase a despertar a un observador, este podría volver a 
        // bloquearse y caer en un deadlock. Entonces necesito despertar al calibrador.
        }
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
        notifyAll(); // Idem A
    }

    private bool puedeCalibrar() {
        return observadores == 0 && !calibrando;
    }

    private bool puedeObservar(int posicion) {
        return !calibrando && (posicion == posicionActual || observadores == 0) && (calibradoresEsperando == 0 || posicion == posicionActual);
    }
}

// Ejercicio 4. [Mensajes] Un sistema de monitoreo de equipos IT funciona mediante la ejecución coordinada de múltiples servicios. En los equipos a monitorear se ejecuta un Agente (con un ID único) que una vez por minuto reporta que esta funcionando. Un Proxy por red local actúa de intermediario entre los agentes y un Servidor central, recibiendo los reportes de los Agentes y reenvíandolos al Servidor. El Servidor almacena un log con la actividad reportada (por simplicidad imprimiendo por pantalla los mensajes a medida que los va recibiendo).
// a) Modele el escenario descrito utilizando intercambio de mensajes por canales (sin utilizar memoria compartida).
// b) Modifique la solución anterior para que el Servidor central responda cada mensaje con un número aleatorio que debe ser sumado al ID del agente en futuros reportes (i.e., manteniendo el esquema de que el Servidor no se comunica directamente con los agentes, sino que pasa a través del Proxy).
// c) Extienda la solución anterior para que el Servidor notifique (i.e., imprimiendo por pantalla) cuando no haya recibido comunicación de algún Agente cualquiera en los últimos 2 minutos (aproximadamente).

// Ej 4A
global Channel agenteToProxy = new Channel();
global Channel proxyToServer = new Channel();

process Agente(id) : {
    while(true) {
        agenteToProxy.send(id);
        Sleep(60000);
    }
}

process Proxy: {
    while(true) {
        int id = agenteToProxy.receive();
        proxyToServer.send(id);
    }
}

process Server : {
    while(true) {
        int id = proxyToServer.receive();
        print("Agente " + id + " está funcionando correctamente");
    }
}

// 4B
global Channel agenteToProxy = new Channel();
global Channel proxyToServer = new Channel();
global Channel serverToProxy = new Channel();
global Channel proxyToAgente = new Channel();

process Agente(id) : {
    int value = id;
    while(true) {
        agenteToProxy.send(value);
        Sleep(60000);
        int random = proxyToAgente.receive();
        value = value + random;
    }
}

process Proxy: {
    while(true) {
        int id = agenteToProxy.receive();
        proxyToServer.send(id);
        int random = serverToProxy.receive();
        proxyToAgente.send(random);
    }
}

process Server : {
    while(true) {
        int id = proxyToServer.receive();
        int random = new Random();
        print("Agente " + id + " está funcionando correctamente");
        serverToProxy.send(random);
    }
}

// 4C

global Channel agenteToProxy = new Channel();
global Channel proxyToServer = new Channel();
global Channel serverToProxy = new Channel();
global Channel proxyToAgente = new Channel();

process Agente(id) : {
    int value = id;
    Channel myChannel = new Channel();
    // creo un canal por cada agente para la comunicación con el servidor
    // esto es para poder lanzar un thread por cada agente y que cada thread
    // pueda chequear si no se ha recibido comunicación de un agente en particular
    while(true) {
        req = new Request();
        req.id = id;
        req.value = value;
        myChannel.send(req);
        agenteToProxy.send(myChannel);
        Sleep(60000);
        int random = proxyToAgente.receive();
        value = value + random;
    }
}

process Proxy: {
    while(true) {
        Channel channel = agenteToProxy.receive();
        proxyToServer.send(channel);
        int random = serverToProxy.receive();
        proxyToAgente.send(random);
    }
}

process Server : {
    Channel serverChannel = new Channel();
    while(true) {
        Channel channel = proxyToServer.receive();
        thread(channel) {
            req = channel.receive();
            thread(req) {
                while(true) {
                    Sleep(120000);
                    bool huboComunicacion = serverChannel.receive();
                    if(!huboComunicacion) {
                        print("No se ha recibido comunicación del Agente " + req.id);
                    }
                    serverChannel.send(false);
                }
            }
            while(true) {
                int random = new Random();
                print("Agente " + req.id + " está funcionando correctamente");
                serverToProxy.send(random);
                serverChannel.send(true); // el bool representa que hubo comunicación
                req = channel.receive(); // espero el nuevo aviso del agente
                serverChannel.receive(); // "robo" el token
            }
        }
    }
}
