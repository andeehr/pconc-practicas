// 1A
global Channel channel = new Channel();

process Server : {
    int i = 0;
    String message = channel.receive();
    while(true) {
        if(message == "cuenta") {
            print(i);
            i = 0;
        } else {
            i++;
        }
        message = channel.receive();
    }
}

// 1B
global Channel clientToServer = new Channel();
global Channel serverToClient = new Channel();

process Server : {
    int i = 0;
    String message = clientToServer.receive();
    while(true) {
        if(message == "cuenta") {
            serverToClient.send(i);
            i = 0;
        } else {
            i++;
        }
        message = clientToServer.receive();
    }
}

process Client : {
    String message = read(); // "sigue" o "cuenta"
    clientToServer.send(message);
    print(serverToClient.receive());
}

// 2
global Channel clientToServer = new Channel();

process Server : {
    while(true) {
        r1 = clientToServer.receive();
        r2 = clientToServer.receive();
        print(r1 + r2);
    }
}

process ClientA : {
    while(true) {
        clientToServer.send("Juan");
        //sleep
    }
}

process ClientB : {
    while(true) {
        clientToServer.send("Carlos");
        //sleep
    }
}

//4A
global Channel clientToServer = new Channel();
global Channel serverToClient = new Channel();

process Server : {
    while(true) {
        String s = clientToServer.receive();
        s = s.trim();
        serverToClient.send(s);
    }
}

process Client(String s) : {
    clientToServer.send(s);
    String trimmed = serverToClient.receive();
    print(trimmed);
}

//4B
global Channel clientToServer = new Channel();

process Server : {
    while(true) {
        Request req = clientToServer.receive();
        thread(req) {
            String s = req.string.trim();
            req.channel.send(s);
        }
    }
}

process Client(String s) : {
    Request req = new Request {
        string = s,
        channel = new Channel()
    };
    clientToServer.send(req);
    String trimmed = req.channel.receive();
    print(trimmed);
}

//5
global Channel clientToServer = new Channel();

process Server : {
    while(true) {
        Request req = clientToServer.receive();
        thread(req) {
            int random = new Random(0,10); // genera un random entre 0 y 10
            int intento = req.intentos.receive();
            while(intento != random) {
                req.respuestas.send(false);
                intento = req.intentos.receive();
            }
            req.intentos.send(true);
        }
    }
}

process Client : {
    Request req = new Request(); 
    req.intentos = new Channel();
    req.respuestas = new Channel();
    clientToServer.send(req);
    
    int number = read(); //input por consola
    req.intentos.send(number); //el primer envío tiene que ser fuera del while para no caer en un deadlock
    Boolean esCorrecto = req.respuestas.receive();
    while(!esCorrecto) {
        int number = read(); //input por consola
        req.intentos.send(number);
        esCorrecto = req.respuestas.receive();
    }
    print("Adiviné :)");
}

// 6a
global Channel clientToT = new Channel();
global Channel tToServiceR = new Channel();

process ServicioT : {
    while(true) {
        String message = clientToT.receive();
        String encoded = codificar(message);
        tToServiceR.send(encoded);
    }
}

// 6b

global Channel clientToT = new Channel();

process ServicioT : {
    while(true) {
        Request req = clientToT.receive();

        thread(req) {
            Channel channelClientToT = req.channelClient;
            Channel remoteServiceChannel = req.remoteServiceChannel;
            while(true) {
                String message = channelClientToT.receive();
                String encoded = codificar(message);
                remoteServiceChannel.send(encoded);
            }
        }
    }
}

process Client : {
    //Ejemplo de uso de Client
    Request req = new Request();
    Channel myChannel = new Channel();
    Channel remoteChannel = nextChannel(); // un canal random de servicio remoto
    req.channelClient = myChannel;
    req.remoteServiceChannel = remoteChannel;
    clientToT.send(req);
}

// 6c

global Channel clientToT = new Channel();
global Channel serviceKToT = new Channel();

process ServicioT : {
    while(true) {
        Request req = clientToT.receive();

        thread(req) {
            Channel channelClientToT = req.channelClient;
            Channel remoteServiceChannel = req.remoteServiceChannel;
            while(true) {
                String message = channelClientToT.receive();
                String key = serviceKToT.receive();
                String encoded = codificar(message, key);
                remoteServiceChannel.send(encoded);
            }
        }
    }
}

// 8a

global Channel canal1 = new Channel();
global Channel canal2 = new Channel();
global Channel canal3 = new Channel();
global Channel canal4 = new Channel();

process ProxyP : {
    while(true) {
        Calculo calculo = canal3.receive();
        canal1.send(calculo);
        int resultado = canal2.receive();
        canal4.send(resultado);
    }
}

//8b

global Channel canal5 = new Channel();
process ProxyP : {

    repeat(N) canal5.send("token");

    while(true) {
        Calculo calculo = canal3.receive();
        canal5.receive();

        thread {
            canal1.send(calculo);
            int resultado = canal2.receive();
            canal4.send(resultado);
            canal5.send("token");
        }
    }
}
