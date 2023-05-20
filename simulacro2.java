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


//c pendiente
process Agencia : {
    while(true) {
        req = agencia.receive();
        thread(req) {
            bool respuesta = true;
            for (v in vuelos) {
                v.send(req)
            }
            for (a in autos) {
                a.send(req)
            }
            for (h in hotel) {
                h.send(req)
            }
            
            while(respuesta) {
                for (v in vuelos) {
                    respuesta = respuesta && v.receive(req);
                    if(!respuesta) {
                        req.channel.send(respuesta);
                    }
                }
            }

            respuesta = respuesta && vuelos.receive();
            respuesta = respuesta && autos.receive();
            respuesta = respuesta && hotel.receive();
            req.channel.send(respuesta);
        }
    }
}
