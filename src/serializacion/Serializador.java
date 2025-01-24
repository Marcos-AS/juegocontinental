package serializacion;

import java.io.*;
import java.util.ArrayList;

public class Serializador implements Serializable {
    private final String fileName;

    public Serializador(String fileName) {
        super();
        this.fileName = fileName;
    }

    public boolean writeOneObject(Object obj) {
        boolean respuesta = false;
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
            oos.writeObject(obj);
            oos.close();
            respuesta = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return respuesta;
    }

    public boolean addOneObject(Object obj) {
        boolean respuesta = false;
        try {
            AddableObjectOutputStream oos = new AddableObjectOutputStream(new FileOutputStream(fileName, true));
            oos.writeObject(obj);
            oos.close();
            respuesta = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return respuesta;
    }

    public Object readFirstObject() {
        Object respuesta = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName));
            respuesta = ois.readObject();
            ois.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("No hay partidas para cargar.");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return respuesta;
    }

    public Object[] readObjects() {
        Object[] respuesta;
        ArrayList<Object> listaObjetos = new ArrayList<Object>();
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName));
            Object r = ois.readObject();
            while (r != null) {
                listaObjetos.add(r);
                r = ois.readObject();
            }
            ois.close();
        } catch (EOFException ignored) {
            //System.out.println("Lectura completada");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!listaObjetos.isEmpty()) {
            respuesta = new Object[listaObjetos.size()];
            int i = 0;
            for (Object o : listaObjetos) respuesta[i ++] = o;
        } else {
            respuesta = null;
        }
        return respuesta;
    }
}
