// prioridad lectores

global db = new db();

thread Lector {
    db.startRead();
    //read
    db.endRead();
}

thread Lector {
    db.startWrite();
    //read
    db.endWrite();
}

monitor db {

    int lectores = 0;
    int escritores = 0;

    startRead() {

        while(hayEscritor()) {
            wait();
        }
        lectores++;
    }

    startWrite() {

        while(hayLector() || hayEscritor()) {
            wait();
        }
        escritores++;

    }

    endRead() {
        lectores--;
        if (lectores == 0) {
            notify();
        }
    }

    endWrite() {
        escritores--;
        notifyAll();
    }

}

//prioridad escritores

monitor db {

    int lectores = 0;
    int escritores = 0;
    int escritoresEsperando = 0;

    startRead() {

        while(hayEscritor() || hayEscritorEsperando()) {
            wait();
        }
        lectores++;
    }

    startWrite() {

        while(hayLector() || hayEscritor()) {
            wait();
        }
        escritores++;
        escritoresEsperando--;

    }

    endRead() {
        lectores--;
        if (lectores == 0) {
            notifyAll();
        }
    }

    endWrite() {
        escritores--;
        notifyAll();
    }

}