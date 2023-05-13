
// 1a
thread tcp : {
    while(true) {
        hayPedido.acquire();
        //ir
        listo.release();
        solicitud.acquire();
        //cumple
        finalizado.release();
    }
}

thread pedido: {
    hayPedido.release();
    listo.acquire();
    //explicar
    solicitud.release();
    finalizado.acquire();
}

//1b

thread tcp : {
    while(true) {
        hayPedido.acquire();
        //ir
        listo.release();
        solicitud.acquire();
        //cumple
        finalizado.release();
    }
}

thread pedido(clase): {

    mutexCabina[clase].acquire();
    pedidos[clase]++;
    if(pedidos[clase] == 1) {
        atencionTCP.acquire(); //un solo semaforo mutex para ambas clases
    }

    hayPedido.release();
    listo.acquire();
    //explicar
    solicitud.release();
    finalizado.acquire();

    mutexCabina[clase].acquire();
    pedidos[clase]--;
    if (pedidos[clase] == 0) {
        atencionTCP.release();
    }
    mutexCabina[clase].release();

}

//1c

thread tcp : {
    while(true) {
        hayPedido.acquire();
        //ir
        listo.release();
        solicitud.acquire();
        //cumple
        finalizado.release();
    }
}

thread pedido(clase): {

    if (clase == turista) {
        mutexP.acquire();
        permisoTurista.acquire();
    }

    mutexCabina[clase].acquire();
    pedidos[clase]++;
    if(pedidos[clase] == 1) {
        if(clase == primera) {
            permisoTurista.acquire();
        }
        atencionTCP.acquire(); //un solo semaforo mutex para ambas clases
    }
    mutexCabina[clase].release();

    if(clase == turista) {
        permisoTurista.release();
        mutexP.release();
    }

    hayPedido.release();
    listo.acquire();
    //explicar
    solicitud.release();
    finalizado.acquire();

    mutexCabina[clase].acquire();
    pedidos[clase]--;
    if (pedidos[clase] == 0) {
        if(clase == primera) {
            permisoTurista.release();
        }
        atencionTCP.release();
    }
    mutexCabina[clase].release();

}
