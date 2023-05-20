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

    private synchronized boolean isEmpty() {
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
            bool puedeViajar = true;
            bool hayVuelo = false;
            bool hayAutos = false;
            bool hayHotel = false;

            // lanzo un thread por cada ws
            thread {
                for (v in vuelos) { //envio todos los request a todos los ws de vuelos
                    reqService = new Request();
                    reqService.fecha = req.fecha;
                    reqService.chRta = new Channel();
                    v.send(reqService);
                }
                for (v in vuelos) { //voy guardando las rtas en un canal dedicado para las rtas de vuelos (en otro for para maximizar la conc)
                    thread { //lanzo un thread por cada rta para no tener que esperar sincrónicamente cada una
                        bool rta = reqService.chRta.receive();
                        rtasVuelos.send(rta);
                    }
                }
            }

            thread {
                //idem lógica vuelos
                for (a in autos) {
                    reqService = new Request();
                    reqService.fecha = req.fecha;
                    reqService.chRta = new Channel();
                    a.send(reqService);
                }
                for(a in autos) {
                    thread {
                        bool rta = reqService.chRta.receive();
                        rtasAutos.send(rta);
                    }
                }
            }

            thread {
                //idem lógica vuelos
                for (h in hotel) {
                    reqService = new Request();
                    reqService.fecha = req.fecha;
                    reqService.chRta = new Channel();
                    h.send(reqService);
                }
                for(h in hotel) {
                    thread {
                        bool rta = reqService.chRta.receive();
                        rtasHoteles.send(rta);
                    }
                }
            }

            while(puedeViajar) {

                int iv = 0
                // mientras no haya vuelo y no haya consultado todos los ws voy sacando las rtas
                // ante la primer respuesta verdadera, seteo la variable hayVuelo en true con la intención de no seguir chequeando vuelos
                while(!hayVuelo && vuelos.length > iv) {
                    hayVuelo = hayVuelo || rtasVuelos.receive();
                    iv++;
                }
                puedeViajar = puedeViajar && hayVuelo;
                // si no hay vuelos disponibles, seteo puedeViajar en false
                // por eso en la próxima iteración, pregunto si también puede viajar
                // si no puede viajar, no chequeo autos ni hoteles
                // si conseguí un vuelo, entonces sí chequeo autos y repito la lógica en hoteles
                int ia = 0
                while(!hayAutos && autos.length > ia && puedeViajar) {
                    hayAutos = hayAutos || rtasAutos.receive();
                    ia++;
                }
                puedeViajar = puedeViajar && hayAutos;

                int ih = 0
                while(!hayHotel && hotel.length > ih && puedeViajar) {
                    hayHotel = hayHotel || rtasHoteles.receive();
                    ih++;
                }
                puedeViajar = puedeViajar && hayHotel;
            }
            // por último devuelvo la rta
            req.channel.send(puedeViajar);
        }
    }
}
