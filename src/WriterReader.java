import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class WriterReader {
    public static final Semaphore RW_mutex = new Semaphore(1, true);
    public static final Semaphore R_mutex = new Semaphore(1, true); //Crea un objeto semáforo con 1 permiso
    public static int RC = 0;

    public static void main(String[] args) {
        int wr, re;
        ResourceManage manage = new ResourceManage();
        Scanner input = new Scanner(System.in);
        System.out.print ("Ingrese el numero de escritores:");
        wr = input.nextInt();
        System.out.print ("Ingrese el numero de lectores:");
        re = input.nextInt();

        // Instanciar objetos de lector y escritor y se llama al método Run.
        for (int i = 0; i < wr; i++) {
            new Thread (new Writer (i, manage), "hilo de escritor r" + (i) + "").start();
        }
        for (int i = 0; i < re; i++) {
            new Thread (new Reader (i, manage), "lector hilo r" + (i) + "").start();
        }
    }
}

class Reader implements Runnable {
    private int num;
    private ResourceManage manage; // Creación del objeto Resource Manage.

    public Reader(int num, ResourceManage manage) { // Constructor para el objeto Reader.
        super();
        this.num = num;
        this.manage = manage;
    }

    @Override
    public void run() {
        manage.naps(); // Establece aleatoriamente el tiempo en espera del objeto.
        manage.startRead();
        System.out.println(Thread.currentThread ().getName() + "Lectura ...");
        manage.naps();
        manage.endRead();
    }
}

class Writer implements Runnable {
    private int num;
    private final ResourceManage manage; // Creación del objeto Resource Manage

    public Writer(int num, ResourceManage manage){  // Constructor para el objeto Reader
        super();
        this.num = num;
        this.manage = manage;
    }

    @Override
    public void run() {
        manage.naps(); // Establece aleatoriamente el tiempo en espera del objeto.
        manage.Write();
        System.out.println (Thread.currentThread().getName() + " Escribir un libro ...");
    }
}

class ResourceManage {
    public ResourceManage() { // Constructor para el objeto Resource Manage
        super();
    }

    // Establecer aleatoriamente el tiempo de espera
    public void naps() {
        try {
            Thread.sleep((int) (3000 * Math.random()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Comienza a leer la operación
    public void startRead() {
        try {
            System.out.println("Valor de RC ANTES DEL IF: " + WriterReader.RC);
            WriterReader.R_mutex.acquire();
            if (WriterReader.RC == 0) { // Bloquea el semaforo para no poder acceder a la sección crítica, en este caso el valor es 0

                System.out.println("ENTRAMOS EN EL IF DE startREAD");
                System.out.println("Valor de RC: " + WriterReader.RC);
                WriterReader.RW_mutex.acquire();
            }
            WriterReader.RC++;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("FINALLY DE startREAD");
            System.out.println("Valor de RC: " + WriterReader.RC);
            WriterReader.R_mutex.release(); // Desbloquea y permite que el semáforo pueda incrementar su permiso en uno.
        }
    }

    // El lector se va
    public void endRead() {
        try {
            System.out.println("Valor de RC ANTES DEL IF: " + WriterReader.RC);
            WriterReader.R_mutex.acquire();
            WriterReader.RC--;
            if (WriterReader.RC == 0) {
                System.out.println("ENTRAMOS EN EL IF DE ENDREAD");
                System.out.println("Valor de RC: " + WriterReader.RC);
                WriterReader.RW_mutex.release();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("FINALLY DE ENDREAD");
            System.out.println("Valor de RC: " + WriterReader.RC);
            WriterReader.R_mutex.release();
        }
    }

    // Operación de escritor
    public void Write() {
        try {
            WriterReader.RW_mutex.acquire();
            System.out.println("SE BLOQUEA EL ESCRITO");
            System.out.println("Valor de RC: " + WriterReader.RC);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("FINALLY DE WRITE");
            System.out.println("Valor de RC: " + WriterReader.RC);
            WriterReader.RW_mutex.release();
        }
    }
}