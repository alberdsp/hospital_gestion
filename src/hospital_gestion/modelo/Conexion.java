
package hospital_gestion.modelo;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/*
 * 
 * clase que maneja la conexión a la BD a través de hibernate y recibe parametro 
 * de la clase a conectar 
 */
public class Conexion<T> {

    public static Session sesion;
    public static SessionFactory factory;
    private Class<T> claseEntidad;

    public Conexion(Class<T> claseEntidad) {
        this.claseEntidad = claseEntidad;
    }

    public void iniciarTransaccion() {
        // Carga la conexión del archivo de Hibernate
        factory = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(claseEntidad).buildSessionFactory();
        sesion = factory.openSession();
        // Inicia una nueva transacción
        sesion.beginTransaction();
    }

    public void terminarTransaccion() {
        // Envía y cierra la transacción actual
        sesion.getTransaction().commit();
        // Cierra la sesión de Hibernate
        sesion.close();
    }
}