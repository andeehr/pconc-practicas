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